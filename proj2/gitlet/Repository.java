package gitlet;

import java.io.File;
import java.util.Date;
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
        Commit initCommit = new Commit(INITIAL_COMMIT_MSG, new Date(0), null, null);
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
        Commit newCommit = new Commit(message, new Date(), parentCommit.digest(), blobs);
        COMMITS.persist(newCommit);

        // Update the commit of the HEAD branch
        getHead().setCommitId(newCommit.digest());
        BRANCHES.persist(getHead());

        // Clear the staging area
        StagingArea.clear();
    }
}
