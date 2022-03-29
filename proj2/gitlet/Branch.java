package gitlet;

import java.io.Serializable;

/**
 *  Represents a gitlet branch object.
 *  This class contains the branch's name and the commitId for the commit it is pointing to.
 *
 *  @author Jordan Kilfoy
 */
public class Branch implements Serializable {

    /** The name of the branch */
    private final String name;

    /** SHA-1 digest of the commit this branch is currently pointing to */
    private String commitId;

    public Branch(String name, String commitId) {
        this.name = name;
        this.commitId = commitId;
    }

    //Getters and Setters

    public String getName() {
        return name;
    }

    public String getCommitId() {
        return commitId;
    }

    public Commit getCommit() {
        return Main.COMMITS.read(commitId);
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
