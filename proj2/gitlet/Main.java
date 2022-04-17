package gitlet;

import java.io.File;

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
    public static  FolderManager<Commit> COMMITS;
    public static  FolderManager<Blob> TRACKED_BLOBS;
    public static  FolderManager<Branch> BRANCHES;
    public static  FolderManager<Blob> STAGED_BLOBS;

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
                case "rm":
                    verifyNumArguments(1, args.length - 1);
                    StagingArea.remove(args[1]);
                    break;
                case "commit":
                    verifyNumArguments(1, args.length - 1);
                    Repository.commit(args[1]);
                    break;
                case "log":
                    Repository.log();
                    break;
                case "global-log":
                    Repository.globalLog();
                    break;
                case "find":
                    verifyNumArguments(1, args.length - 1);
                    Repository.find(args[1]);
                    break;
                case "status":
                    Repository.status();
                    break;
                case "checkout":
                    if (args.length == 2) {
                        Repository.checkoutBranch(args[1]);
                    } else if (args.length == 3 && args[1].equals("--")) {
                        Repository.checkoutFileFromHead(args[2]);
                    } else if (args.length == 4 && args[2].equals("--")) {
                        String commitId = determineFullCommitId(args[1]);
                        Repository.checkoutFileFromCommit(args[3], commitId);
                    } else {
                        throw new GitletException("Incorrect operands.");
                    }
                    break;
                case "branch":
                    verifyNumArguments(1, args.length - 1);
                    Repository.createBranch(args[1]);
                    break;
                case "rm-branch":
                    verifyNumArguments(1, args.length - 1);
                    Repository.removeBranch(args[1]);
                    break;
                case "reset":
                    verifyNumArguments(1, args.length - 1);
                    String commitId = determineFullCommitId(args[1]);
                    Repository.reset(commitId);
                    break;
                case "merge":
                    verifyNumArguments(1, args.length - 1);
                    Repository.merge(args[1]);
                    break;
                default:
                    throw new GitletException("No command with that name exists.");
            }
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    // if a shortened commit id is used,
    // find the first commitId that starts with the input prefix
    private static String determineFullCommitId(String input) {
        if (input.length() < 40) {
            for (Commit commit : COMMITS) {
                if (commit.digest().startsWith(input)) {
                    return commit.digest();
                }
            }
        }
        throw new GitletException("No commit with that id exists.");
    }

    public static void verifyNumArguments(int expected, int actual) {
        if (actual < expected) {
            throw new GitletException("Incorrect operands.");
        }
    }


}
