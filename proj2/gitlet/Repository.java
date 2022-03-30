package gitlet;

import java.io.File;
import java.util.Date;
import java.util.StringJoiner;
import java.util.TreeMap;

import static gitlet.Utils.*;
import static gitlet.Main.*;

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
    /** The file name containing the head commit */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    /** The message of the initial commit */
    public static final String INITIAL_COMMIT_MSG = "initial commit";
    /** The name of the master branch */
    public static final String MASTER_BRANCH = "master";

    /** The head commit of the repository. Lazily loaded in its getter. */
    private static Branch head;

    public static Branch getHead() {
        if (head == null) {
            String headBranchName = readContentsAsString(HEAD_FILE);
            head = BRANCHES.read(headBranchName);
        }
        return head;
    }

    public static void setHead(String branchName) {
        assert BRANCHES.contains(branchName) : "Tried to set HEAD to a branch that does not exist";
        writeContents(HEAD_FILE, branchName);
        head = BRANCHES.read(branchName);
    }

    /**
     * Initializes a gitlet repository.
     */
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
        Branch master = new Branch(MASTER_BRANCH, initCommit.digest());
        BRANCHES.persist(master);
        setHead(MASTER_BRANCH);
    }


    /** Commits all changes inside the staging area */
    public static void commit(String message) {
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

        // Prepare the new commit's blobs treemap
        Commit parentCommit = getHead().getCommit();
        TreeMap<String, String> blobs = parentCommit.getBlobs();
        for (String removedFilename : StagingArea.getRemoved()) {
            blobs.remove(removedFilename);
        }
        for (String addedFilename : StagingArea.getAdded().navigableKeySet()) {
            blobs.put(addedFilename, StagingArea.getAdded().get(addedFilename));
        }

        // Create and persist the new commit
        Commit newCommit = new Commit(message, new Date(), getHead().getCommitId(), blobs);
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

    public static void globalLog() {
        StringJoiner sj = new StringJoiner("===" + System.lineSeparator(), "===" + System.lineSeparator(), "");
        for (Commit commit : COMMITS) {
            sj.add(commit.toString());
        }
        System.out.print(sj);
    }

    /** Prints the commitIds of all commits with a matching commit message */
    public static void find(String message) {
        for (Commit commit : COMMITS) {
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.digest());
            }
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
        System.out.println();
    }

    public static void checkoutBranch(String branchName) {
        Branch branch = BRANCHES.read(branchName);
        if (branch == null) {
            throw new GitletException("No such branch exists.");
        }
        if (branch.equals(getHead())) {
            throw new GitletException("No need to checkout the current branch.");
        }

        // make sure there is no untracked file in the way
        Commit headCommit = getHead().getCommit();
        for (String filename : plainFilenamesIn(CWD)) {
            if (!headCommit.getBlobs().containsKey(filename)) {
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        // delete all current files
        for (String filename : plainFilenamesIn(CWD)) {
            restrictedDelete(join(CWD, filename));
        }

        // check out all files from the commit
        for (String filename : branch.getCommit().getBlobs().navigableKeySet()) {
            checkoutFileFromCommit(filename, branch.getCommitId());
        }

        // set head
        setHead(branchName);

        // clear staging area
        StagingArea.clear();
    }

    public static void checkoutFile(String fileName) {
        checkoutFileFromCommit(fileName, getHead().getCommitId());
    }

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
}
