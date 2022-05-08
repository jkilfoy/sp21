package gitlet;

/**
 * Enum that represents all states of modification a file can take between two commits
 */
public enum ModifiedStatus {
    SAME,       // the file is the same in both commits
    MODIFIED,   // the file is different in the later commit
    REMOVED,    // the file is not present in the later commit
    ADDED,      // the file is only present in the later commit
    DNE;        // the file is not in either commit
}
