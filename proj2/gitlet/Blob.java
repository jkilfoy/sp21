package gitlet;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Main.CWD;

/**
 *  Represents a gitlet blob object, or a file tracked by a commit.
 *  This class contains a byte[] of the full contents of the file, as well as its name.
 *  A Blob is Digestable, and thus all its fields can be used to generate a SHA-1 digest.
 *  A Blob is immutable; none of its fields can change after being created. This is to
 *  preserve the Digestable contract.
 *
 *  @author Jordan Kilfoy
 */
public class Blob implements Digestable, Serializable {

    /** The message of this Commit. */
    private final byte[] contents;

    /** The timestamp of this Commit. */
    private final String name;

    public Blob(byte[] contents, String name) {
        this.contents = contents;
        this.name = name;
    }

    /**
     * Writes the contents of this blob into a file under its name in CWD,
     * creating it if it doesn't exist, and overwriting if it does.
     */
    public void addToCWD() {
        Utils.writeContents(Utils.join(CWD, name), contents);
    }

    // Getters

    public byte[] getContents() {
        return contents;
    }

    public String getName() {
        return name;
    }

    @Override
    public String digest() {
        List<String> stringsToHash = new ArrayList<>();
        stringsToHash.add(new String(contents, StandardCharsets.UTF_8));
        stringsToHash.add(name);
        return Utils.sha1(stringsToHash.toArray(new String[0]));
    }
}
