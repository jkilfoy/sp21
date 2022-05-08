package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static gitlet.Main.COMMITS;

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
    private final String secondParentId;

    /** Maps the name of each file in the commit to the corresponding blob id */
    private final TreeMap<String, String> blobs;

    public Commit(String message, Date timestamp, String parentId, TreeMap<String, String> blobs) {
        this.message = message;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.secondParentId = "";
        this.blobs = blobs;
    }

    public Commit(String message, Date timestamp, String parentId, String secondParentId,
                  TreeMap<String, String> blobs) {
        this.message = message;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.secondParentId = secondParentId == null ? "" : secondParentId;
        this.blobs = blobs;
    }

    /** Returns a set of all ancestors of this commit, including itself */
    public Set<Commit> getAllAncestors() {
        Set<Commit> ancestors = new HashSet<Commit>();
        List<Commit> trails = new LinkedList<>();
        trails.add(this);
        while (!trails.isEmpty()) {
            List<Commit> next = new LinkedList<>();
            for (Commit trailHead : trails) {
                if (!ancestors.contains(trailHead)) {
                    // add the commit to ancestors, then add each parent to trails
                    ancestors.add(trailHead);
                    Commit firstParent = COMMITS.read(trailHead.getParentId());
                    if (firstParent != null) {
                        next.add(firstParent);
                    }
                    Commit secondParent = COMMITS.read(trailHead.getSecondParentId());
                    if (secondParent != null) {
                        next.add(secondParent);
                    }
                }
            }
            trails = next;
        }
        return ancestors;
    }

    public String toString() {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
        return "commit " + digest() + System.lineSeparator() +
                (!"".equals(secondParentId) ?
                        "Merge: " + shorten(parentId) + " " + shorten(secondParentId) + System.lineSeparator()
                        : ""
                ) +
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

    public String getSecondParentId() {
        return secondParentId;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    @Override
    public String digest() {
        List<String> stringsToHash = new ArrayList<>();
        stringsToHash.add(message);
        stringsToHash.add(timestamp.toString());
        stringsToHash.add(parentId);
        stringsToHash.add(secondParentId);
        stringsToHash.add(blobs.toString());
        return Utils.sha1(stringsToHash.toArray(new String[0]));
    }

    /** Returns the first 7 characters of the input string */
    public static String shorten(String input) {
        return input.length() > 7 ? input.substring(0, 7) : input;
    }
}
