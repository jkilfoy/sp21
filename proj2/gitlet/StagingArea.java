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

    private static TreeMap<String, String> getAdded() {
        if (added == null) {
            added = readObject(ADDED_FILE, TreeMap.class);
        }
        return added;
    }

    private static TreeSet<String> getRemoved() {
        if (removed == null) {
            removed = readObject(REMOVED_FILE, TreeSet.class);
        }
        return removed;
    }

    // Persists the added and removed objects
    private static void persist() {
        writeObject(ADDED_FILE, added);
        writeObject(REMOVED_FILE, removed);
    }

    /** Initializes the staging area */
    public static void init() {
        assert !STAGE_DIR.exists() : "Tried to initialize a staging area inside a repo where it already exists";
        STAGE_DIR.mkdir();
        STAGED_BLOBS.getFolder().mkdir();
        added = new TreeMap<>();
        removed = new TreeSet<>();
        persist();
    }

    /** Adds a file from the CWD to the staging area */
    public static void add(String filename) throws GitletException {
        File file = join(CWD, filename);
        if (!file.exists()) {
            throw new GitletException("File does not exist.");
        }

        // Clear the already staged version of the file if it exists
        if (getAdded().containsKey(filename)) {
            STAGED_BLOBS.clear(filename);
        }

        // Persist the blob in the staging area
        Blob staged_blob = new Blob(readContents(file), filename);
        STAGED_BLOBS.persist(staged_blob);
        getAdded().put(filename, staged_blob.digest());

        // If the file was staged for removal, un-stage it for removal
        getRemoved().remove(filename);

        // Persist the added and removed objects
        persist();
    }
}
