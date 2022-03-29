package gitlet;

import java.io.File;
import java.util.zip.GZIPOutputStream;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Jordan Kilfoy
 */
public class Main {

    /** Current Working Directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");

    /** Folder for staged objects */
    public static final File STAGE_DIR = Utils.join(GITLET_DIR, "stage");

    /** FolderManagers for managing serialized objects */
    public static final FolderManager<Commit> COMMITS;
    public static final FolderManager<Blob> TRACKED_BLOBS;
    public static final FolderManager<Branch> BRANCHES;
    public static final FolderManager<Blob> STAGED_BLOBS;

    static {
        COMMITS = new FolderManager<>(Utils.join(GITLET_DIR, "commits"), Commit.class, Commit::digest);
        TRACKED_BLOBS = new FolderManager<>(Utils.join(GITLET_DIR, "tracked_blobs"), Blob.class, Blob::digest);
        BRANCHES = new FolderManager<>(Utils.join(GITLET_DIR, "branches"), Branch.class, Branch::getName);
        STAGED_BLOBS = new FolderManager<>(Utils.join(STAGE_DIR, "staged_blobs"), Blob.class, Blob::digest);
    }



    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new GitletException("Please enter a command.");
            }

            // Read the command
            String firstArg = args[0];

            // Initialize the repository if it does not exist
            if (!GITLET_DIR.exists()) {
                if (!firstArg.equals("init")) {
                    throw new GitletException("Not in an initialized Gitlet directory.");
                }
                Repository.init();
                return;
            }

            // Handle all other commands
            switch(firstArg) {
                case "init":
                    throw new GitletException("A Gitlet version-control system already exists in the current directory.");
                case "add":
                    verifyNumArguments(1, args.length - 1);
                    StagingArea.add(args[1]);
                    break;
                case "remove":
                    break;

            }
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    public static void verifyNumArguments(int expected, int actual) {
        if (actual < expected) {
            throw new GitletException("Incorrect operands.");
        }
    }


}
