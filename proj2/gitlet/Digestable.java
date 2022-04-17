package gitlet;

/**
 * Interface for classes that can be digested into SHA-1 hash.
 * All fields are used in the digestion in lexicographic order,
 * but only some types are reliably supported.
 * Unreliable field types use their toString method for digest.
 */
public interface Digestable {

    /** Returns the SHA-1 digest of the object */
    String digest();
}
