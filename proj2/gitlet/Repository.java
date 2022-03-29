package gitlet;

import java.io.File;
import java.util.Date;

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

    private static Branch getHead() {
        if (head == null) {
            String headBranchName = readContentsAsString(HEAD_FILE);
            head = BRANCHES.read(headBranchName);
        }
        return head;
    }

    private static void setHead(String branchName) {
        assert BRANCHES.contains(branchName) : "Tried to set HEAD to a commit that does not exist";
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



}
