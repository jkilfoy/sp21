package gitlet;

import java.io.File;
import java.util.TreeMap;
import java.util.TreeSet;
import static gitlet.Utils.*;
import static gitlet.Main.*;

/**
 * Represents the staging area. Contains methods to modify the staging area.
 *
 * @author Jordan Kilfoy
 */
public class StagingArea {

    /** File containing serialized added object */
    private static final File ADDED_FILE = join(STAGE_DIR, "added");
    /** File containing serialized removed object */
    private static final File REMOVED_FILE = join(STAGE_DIR, "removed");

    /** Maps file names to blobIds within the staged_blobs repository, to be added
     * in the next commit. Lazily desrialized from the "added" file */
    private static TreeMap<String, String> added = null;
    /** Set of file names to be removed in the next commit. Lazily deserialized
     * from the "removed" file. */
    private static TreeSet<String> removed = null;

    public static TreeMap<String, String> getAdded() {
        if (added == null) {
            added = readObject(ADDED_FILE, TreeMap.class);
        }
        return added;
    }

    public static TreeSet<String> getRemoved() {
        if (removed == null) {
            removed = readObject(REMOVED_FILE, TreeSet.class);
        }
        return removed;
    }

    // Persists the added and removed objects
    private static void persist() {
        getAdded();
        getRemoved();
        writeObject(ADDED_FILE, added);
        writeObject(REMOVED_FILE, removed);
    }

    private static void resetAddedAndRemoved() {
        added = new TreeMap<>();
        removed = new TreeSet<>();
        persist();
    }

    /** Initializes the staging area */
    public static void init() {
        assert !STAGE_DIR.exists() : "Tried to initialize a staging area inside a repo where it already exists";
        STAGE_DIR.mkdir();
        STAGED_BLOBS.getFolder().mkdir();
        resetAddedAndRemoved();
    }

    /** Adds a file from the CWD to the staging area */
    public static void add(String filename) throws GitletException {
        File file = join(CWD, filename);
        if (!file.exists()) {
            throw new GitletException("File does not exist.");
        }

        // Clear the already staged version of the file if it exists
        if (getAdded().containsKey(filename)) {
            STAGED_BLOBS.clear(getAdded().get(filename));
        }

        // Stage it if the file is different from the currently tracked version
        Blob blobToStage = new Blob(readContents(file), filename);
        if (!TRACKED_BLOBS.contains(blobToStage.digest())) {
            // Persist the blob in the staging area
            STAGED_BLOBS.persist(blobToStage);
            getAdded().put(filename, blobToStage.digest());
        }

        // If the file was staged for removal, un-stage it for removal
        getRemoved().remove(filename);

        // Persist the added and removed objects
        persist();
    }

    /** Removes a file from the staging area, and from the CWD provided
     * it is tracked by the HEAD commit */
    public static void remove(String filename) throws GitletException {

        // If there's no reason to remove file, throw error message
        if (!Repository.getHead().getCommit().getBlobs().containsKey(filename)
        &&  !getAdded().containsKey(filename)) {
            throw new GitletException("No reason to remove the file.");
        }

        // Remove the file from the staging area
        if (getAdded().containsKey(filename)) {
            STAGED_BLOBS.clear(getAdded().get(filename));
            getAdded().remove(filename);
        }

        // Stage the file for removal and remove it from CWD if it's tracked by the head commit
        if (Repository.getHead().getCommit().getBlobs().containsKey(filename)) {
            restrictedDelete(join(CWD, filename));
            getRemoved().add(filename);
        }

        // Persist the added and removed objects
        persist();
    }

    public static boolean isEmpty() {
        return getAdded().isEmpty() && getRemoved().isEmpty();
    }

    public static void clear() {
        STAGED_BLOBS.clearAll();
        resetAddedAndRemoved();
    }
}
