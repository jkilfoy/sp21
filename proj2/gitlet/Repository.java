package gitlet;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Utils.*;
import static gitlet.Main.*;
import static gitlet.ModifiedStatus.*;

/**
 * Represents a gitlet repository.
 *
 * @author Jordan Kilfoy
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The file containing the head commit */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    /** The message of the initial commit */
    public static final String INITIAL_COMMIT_MSG = "initial commit";
    /** The name of the master branch */
    public static final String MASTER_BRANCH_NAME = "master";

    /** The head branch of the repository */
    private static Branch head;

    /** Getter for the head branch; lazily loaded */
    public static Branch getHead() {
        if (head == null) {
            String headBranchName = readContentsAsString(HEAD_FILE);
            head = BRANCHES.read(headBranchName);
        }
        return head;
    }

    /** Setter for the head barnch; persists head when changed */
    public static void setHead(String branchName) {
        assert BRANCHES.contains(branchName) : "Tried to set HEAD to a branch that does not exist";
        writeContents(HEAD_FILE, branchName);
        head = BRANCHES.read(branchName);
    }

    /** Initializes a gitlet repository. */
    public static void init() {
        // Create all necessary folders
        assert !GITLET_DIR.exists() : "Tried to initialize a repository that already exists";
        GITLET_DIR.mkdir();
        COMMITS.getFolder().mkdir();
        TRACKED_BLOBS.getFolder().mkdir();
        BRANCHES.getFolder().mkdir();

        // Initialize the staging area
        StagingArea.init();

        // Create the initial commit
        Commit initCommit = new Commit(INITIAL_COMMIT_MSG, new Date(0), "", new TreeMap<>());
        COMMITS.persist(initCommit);

        // Create the master branch and set it as head
        Branch master = new Branch(MASTER_BRANCH_NAME, initCommit.digest());
        BRANCHES.persist(master);
        setHead(MASTER_BRANCH_NAME);
    }


    /** Commits all changes inside the staging area */
    public static void commit(String message, String secondParentId) {
        if (StagingArea.isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }
        if ("".equals(message)) {
            throw new GitletException("Please enter a commit message.");
        }

        // Copy all staged blobs to the tracked blobs directory
        for (Blob blob : STAGED_BLOBS) {
            TRACKED_BLOBS.persist(blob);
        }

        // Prepare the new commit, containing all blobs tracked by the current head
        // plus any additions / removals from the staging area
        Commit parentCommit = getHead().getCommit();
        TreeMap<String, String> blobsToTrack = parentCommit.getBlobs();
        for (String removedFilename : StagingArea.getRemoved()) {
            blobsToTrack.remove(removedFilename);
        }
        for (String addedFilename : StagingArea.getAdded().navigableKeySet()) {
            blobsToTrack.put(addedFilename, StagingArea.getAdded().get(addedFilename));
        }

        // Create and persist the new commit
        Commit newCommit;
        if (secondParentId != null) {
            newCommit = new Commit(message, new Date(), getHead().getCommitId(), secondParentId, blobsToTrack);
        } else {
            newCommit = new Commit(message, new Date(), getHead().getCommitId(), blobsToTrack);
        }
        COMMITS.persist(newCommit);

        // Update the commit of the HEAD branch
        getHead().setCommitId(newCommit.digest());
        BRANCHES.persist(getHead());

        // Clear the staging area
        StagingArea.clear();
    }

    /** Prints a log of all commits in this branch, starting from HEAD and ending at the init commit */
    public static void log() {
        StringJoiner sj = new StringJoiner("===" + System.lineSeparator(), "===" + System.lineSeparator(), "");
        Commit commit = getHead().getCommit();
        while (commit != null) {
            sj.add(commit.toString());
            commit = COMMITS.read(commit.getParentId());
        }
        System.out.print(sj);
    }

    /** Prints a log of all commits in the repository */
    public static void globalLog() {
        StringJoiner sj = new StringJoiner("===" + System.lineSeparator(), "===" + System.lineSeparator(), "");
        for (Commit commit : COMMITS) {
            sj.add(commit.toString());
        }
        System.out.print(sj);
    }

    /** Prints the commitIds of all commits with a matching commit message */
    public static void find(String message) {
        boolean found = false;
        for (Commit commit : COMMITS) {
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.digest());
                found = true;
            }
        }
        if (!found) {
            throw new GitletException("Found no commit with that message.");
        }
    }

    /** Prints the status of the repository; which branches exist, and which files are
     * staged for addition and removal */
    public static void status() {
        System.out.println("=== Branches ===");
        for (Branch branch : BRANCHES) {
            System.out.println((branch.equals(getHead()) ? "*" : "") + branch.getName());
        }
        System.out.println(System.lineSeparator() + "=== Staged Files ===");
        for (String filename : StagingArea.getAdded().navigableKeySet()) {
            System.out.println(filename);
        }
        System.out.println(System.lineSeparator() + "=== Removed Files ===");
        for (String filename : StagingArea.getRemoved()) {
            System.out.println(filename);
        }
        System.out.println(System.lineSeparator() + "=== Modifications Not Staged For Commit ===");
        System.out.println(System.lineSeparator() + "=== Untracked Files ===");
        System.out.println();
    }

    /** Checks out the commit in the branch specified if it exists,
     * then sets head to that branch and clears the staging area */
    public static void checkoutBranch(String branchName) {
        Branch branch = BRANCHES.read(branchName);
        if (branch == null) {
            throw new GitletException("No such branch exists.");
        }
        if (branch.equals(getHead())) {
            throw new GitletException("No need to checkout the current branch.");
        }

        // checkout the commit of the given branch
        checkoutCommit(branch.getCommitId());

        // set head
        setHead(branchName);

        // clear staging area
        StagingArea.clear();
    }

    /** Overwrites or creates the fileName provided into the CWD with the contents
     * of the file as stored in the provided commitId
     * @param fileName The file to checkout
     * @param commitId The commitId of the commit to find the blob in
     */
    public static void checkoutFileFromCommit(String fileName, String commitId) {
        Commit commit = COMMITS.read(commitId);
        if (commit == null) {
            throw new GitletException("No commit with that id exists.");
        }
        if (!commit.getBlobs().containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        }
        Blob fromCommit = TRACKED_BLOBS.read(commit.getBlobs().get(fileName));
        writeContents(join(CWD, fileName), fromCommit.getContents());
    }

    /** Checks out a file from the current head */
    public static void checkoutFileFromHead(String fileName) {
        checkoutFileFromCommit(fileName, getHead().getCommitId());
    }

    /** Overwrites the CWD to the state of the provided commit; fails if
     * a file untracked by the head branch is in the way */
    public static void checkoutCommit(String commitId) {
        Commit commitToCheckout = COMMITS.read(commitId);
        if (commitToCheckout == null) {
            throw new GitletException("No commit with that id exists.");
        }

        failIfChangingUntrackedFile(commitToCheckout.getBlobs().navigableKeySet());

        // delete all current tracked files
        for (String filename : getHead().getCommit().getBlobs().navigableKeySet()) {
            restrictedDelete(join(CWD, filename));
        }

        // check out all files from the commit
        for (String filename : COMMITS.read(commitId).getBlobs().navigableKeySet()) {
            checkoutFileFromCommit(filename, commitId);
        }
    }

    /** Creates a branch with the given name */
    public static void createBranch(String branchName) {
        if (BRANCHES.contains(branchName)) {
            throw new GitletException("A branch with that name already exists.");
        }
        Branch newBranch = new Branch(branchName, getHead().getCommitId());
        BRANCHES.persist(newBranch);
    }

    /** Removes the branch with the given name if it exists and is not HEAD */
    public static void removeBranch(String branchName) {
        if (getHead().getName().equals(branchName)) {
            throw new GitletException("Cannot remove the current branch.");
        }
        if (!BRANCHES.contains(branchName)) {
            throw new GitletException("A branch with that name does not exist.");
        }
        BRANCHES.clear(branchName);
    }

    /** Resets the commit of the head branch to the commit specified; and checks out that commit */
    public static void reset(String commitId) {
        checkoutCommit(commitId);
        getHead().setCommitId(commitId);
        BRANCHES.persist(getHead());
        StagingArea.clear();
    }

    /** Throws an exception if any of the provided filenames is untracked in the current branch  */
    public static void failIfChangingUntrackedFile(Set<String> changingFileNames) {
        if (changingFileNames.stream().distinct().anyMatch(untrackedFileNames()::contains)) {
            throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
        }
    }

    /** Returns a list of names of all untracked files in CWD */
    public static List<String> untrackedFileNames() {
        Commit headCommit = getHead().getCommit();
        return Objects.requireNonNull(plainFilenamesIn(CWD))
                .stream()
                .filter(s -> !headCommit.getBlobs().containsKey(s)) // filter our files tracked by head
                .collect(Collectors.toList());
    }

    public static void merge(String givenBranchName) {
        if (!StagingArea.isEmpty()) {
            throw new GitletException("You have uncommitted changes.");
        }

        Branch givenBranch = BRANCHES.read(givenBranchName);
        if (givenBranch == null) {
            throw new GitletException("A branch with that name does not exist.");
        }
        if (givenBranch.equals(getHead())) {
            throw new GitletException("Cannot merge a branch with itself.");
        }

        // Determine latest common ancestor
        Commit current = getHead().getCommit();
        Commit given = givenBranch.getCommit();
        Commit splitPoint = latestCommonAncestor(current, given);

//        System.out.println("Merging. Current blobs : " + current.getBlobs() + "\nGiven blobs : " + given.getBlobs());

        // Nothing to do if given branch is an ancestor of current branch
        if (given.equals(splitPoint)) {
            throw new GitletException("Given branch is an ancestor of the current branch.");
        }

        // Fastforward if current branch is an ancestor of the given branch
        if (current.equals(splitPoint)) {
            checkoutBranch(givenBranchName);
            throw new GitletException("Current branch fast-forwarded.");
        }

        // Determine all files that will be staged in the merge commit.
        // This treemap maps filenames to Blob ids or the keyword "REMOVED"
        TreeMap<String, String> changes = new TreeMap<>();
        TreeSet<String> conflicts = new TreeSet<>();

        // Loop through all files in the given commit to find changes from split point
        for (String filename : given.getBlobs().navigableKeySet()) {
            switch (checkModifiedStatus(filename, splitPoint, given)) {
                case ADDED, MODIFIED -> changes.put(filename, given.getBlobs().get(filename));
                case REMOVED -> changes.put(filename, "REMOVED");
            }
        }

        // Compare changes with current commit's changes from split point to detect conflicts or identical modifications
//        System.out.println("Changes at first : " + changes);
        Set<String> tempChanges = new HashSet<>(changes.navigableKeySet());
        for (String filename : tempChanges) {
            switch (checkModifiedStatus(filename, splitPoint, current)) {
                case ADDED, MODIFIED -> {
                    // if blob ids are different, this is a conflict
                    if (!current.getBlobs().get(filename).equals(changes.get(filename))) {
                        conflicts.add(filename);
                    }
                    // either way, remove from changes
                    changes.remove(filename);
                }
                case REMOVED -> {
                    // if the change is not "REMOVED", this is a conflict
                    if (!changes.get(filename).equals("REMOVED")) {
                        conflicts.add(filename);
                    }
                    changes.remove(filename);
                }
            }
        }

        // Before merging, check to make sure no untracked working files will be overwritten
        failIfChangingUntrackedFile(changes.keySet());
        failIfChangingUntrackedFile(conflicts);

        // Checkout and stage all changes
        // todo remove:
//        System.out.println("Changes : " + changes);
//        System.out.println("Conflicts : " + conflicts);
        for (String filename : changes.navigableKeySet()) {
            if (changes.get(filename).equals("REMOVED")) {
                StagingArea.remove(filename);
            } else {
                checkoutFileFromCommit(filename, givenBranch.getCommitId());
                StagingArea.add(filename);
            }
        }

        // Create and stage merge conflict files
        for (String filename : conflicts) {
            String currentContents = "";
            if (current.getBlobs().containsKey(filename)) {
                String blobId = current.getBlobs().get(filename);
                currentContents = new String(TRACKED_BLOBS.read(blobId).getContents());
            }

            String givenContents = "";
            if (given.getBlobs().containsKey(filename)) {
                String blobId = given.getBlobs().get(filename);
                givenContents = new String(TRACKED_BLOBS.read(blobId).getContents());
            }

            writeContents(join(CWD, filename), "<<<<<<< HEAD", System.lineSeparator(),
                    currentContents, "=======", System.lineSeparator(), givenContents, ">>>>>>>");

            StagingArea.add(filename);
        }

        // Commit the merge commit
        commit("Merged " + givenBranchName + " into " + getHead().getName() + ".", given.digest());
        if (!conflicts.isEmpty()) {
            throw new GitletException("Encountered a merge conflict.");
        }
    }

    /** Determines the latest common ancestor of two commits by:
     * 1 - finds all common ancestors by taking the intersection of each commit's ancestors
     * 2 - returning the commit with the latest date out of this intersection */
    public static Commit latestCommonAncestor(Commit commit1, Commit commit2) {
        Set<Commit> commonAncestors = commit1.getAllAncestors();
        commonAncestors.retainAll(commit2.getAllAncestors());
        return commonAncestors.stream().max(Comparator.comparing(Commit::getTimestamp)).get();
    }

    /**
     * Returns the {@link ModifiedStatus} of the given file from one commit to another
     * @param filename the file in question
     * @param from the from commit
     * @param to the to commit
     * @return the modified status of the file from one commit to another
     */
    public static ModifiedStatus checkModifiedStatus(String filename, Commit from, Commit to) {
        if (!from.getBlobs().containsKey(filename)) {
            if (to.getBlobs().containsKey(filename)) return ADDED;
            else return DNE;
        }
        if (!to.getBlobs().containsKey(filename)) return REMOVED;
        if (from.getBlobs().get(filename).equals(to.getBlobs().get(filename))) {
            return SAME;
        }
        return MODIFIED;
    }
}
