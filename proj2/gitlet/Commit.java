package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

/**
 *  Represents a gitlet commit object.
 *  This class contains all meta data pertinent to the commit, including its message,
 *  its timestamp, its parent's id, and a map of filenames to the ids of blobs that hold them.
 *  A commit is Digestable, and thus all its fields can be used to generate a SHA-1 digest.
 *  A commit is immutable; none of its fields can change after being created. This is to
 *  preserve the Digestable contract.
 *
 *  @author Jordan Kilfoy
 */
// TODO : merge commit
public class Commit implements Digestable, Serializable {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);

    /** The message of this Commit. */
    private final String message;

    /** The timestamp of this Commit. */
    private final Date timestamp;

    /** The SHA-1 digest of the parent commit. */
    private final String parentId;

    /** Maps the name of each file in the commit to the corresponding blob id */
    private final TreeMap<String, String> blobs;

    public Commit(String message, Date timestamp, String parentId, TreeMap<String, String> blobs) {
        this.message = message;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.blobs = blobs;
    }

    public String toString() {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
        return "commit " + digest() + System.lineSeparator() +
                "Date: " + formatter.format(timestamp) + System.lineSeparator() +
                message + System.lineSeparator() +
                System.lineSeparator();
    }

    // Getters

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getParentId() {
        return parentId;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }
}
