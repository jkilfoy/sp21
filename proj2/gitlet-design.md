# Gitlet Design Document

**Name**: Dadodude

## Classes and Data Structures

### Repository

A class representing everything contained in the Gitlet repository.
This is the main point of access for most gitlet commands, and contains standard methods for CRUDing commits, blobs and branches. This class maintains references to the directories that store all commits and tracked blobs in the repository.

#### Fields

1. Branch head; <br/>
   The current head branch of the repository
2. StagingArea stagingArea; <br>
   The staging area of the repository
3. static final File GITLET_DIR; <br/>
   A reference to the GITLET directory
4. static final FolderManager COMMITS_DIR; <br/>
   A reference to the directory where commits are stored
5. static final FolderManager BLOBS_DIR; <br/>
   A reference to the directory where tracked blobs are stored
6. static final FolderManager BRANCHES_DIR; <br/>
   A reference to the directory where branch information is stored

### Commit

A class representing a Commit object. Contains all relevant meta data of a commit, and can be digested into a SHA-1 hash.

#### Fields

1. TreeMap<String, String> blobs; <br/>
   Maps filenames to the SHA-1 digests of the blobs tracked in this commit
2. String parentId; <br/>
   The SHA-1 digest of the parent commit
3. String message; <br/>
   The message for this commit
4. Date timestamp; <br/>
   The time this commit was added


### Blob

A class representing a file contained in the repository. Contains the file's name and contents and can be digested into a SHA-1 hash.

#### Fields

1. String filename; <br/>
   The name of the file
2. byte[] contents; <br/>
   The contents of the file

### Branch

A class representing a Branch in the repository. Contains a reference to the commit this branch points to, as well as the name of the branch.

####Fields

1. String commitId; <br/>
   The SHA-1 digest of commit this branch is pointing to
2. String name; <br/>
   The name of the branch

### StagingArea

A class representing the StagingArea in the repository. Tracks which files have been added, modified and removed, and contains utility methods for updating the Staging Area. Maintains a reference to the directory containing all staged blobs.

####Fields

1. TreeMap<String, String> added; <br/>
   Maps file names to the SHA-1 digests of the blobs to be added/modified in the next commit
2. TreeSet<String> removed; <br/>
   A set of file names to be removed in the next commit
3. public static final FolderManager STAGED_DIR <br/>
   A reference to the directory where staged blobs are stored

### FolderManager\<T implements Serializable>

A utility class for managing a folder of Serializable objects. Facilitates common tasks related reading, writing or finding such Objects within a directory

#### Fields

1. final File folder; <br/>
   The folder in which serialized T objects are stored

### Digestable

An interface for objects that can be digested into a SHA-1 hash using all their declared fields

## Algorithms

This section will discuss the methods used in my data structures.

### Repository

#### void init()

The `init` method will initialize a repository. It: 
1. Creates the `.gitlet` directory as well as subdirectories `commits`, `blobs`, `branches`, `stagedChanges`.
2. Creates the initial commit, and serializes it in the `commits` directory under its SHA-1 digest
3. Creates the master branch which points to the initial commit, and serializes it in the `branches` folder under the branch's name
4. Creates the StagingArea object and serializes it under `.gitlet/stage` 
5. Sets the repository's head field to the master branch
6. Finally, serializes the Repository object itself under the file `.gitlet/repo`

#### void commit(String message)

The `commit` method will create a new commit containing all changes in the staging area, using the provided commit message and 
the current timestamp, and whose parent commit is the current head of the repository. This commit is serialized and stored under its 
SHA-1 digest in the `commits` directory. Then, the HEAD branch's commit becomes the newly created commit.

#### void log()

Prints the `Commit.logString()` of the commit referenced by HEAD, then prints the logString() of each commit along the path of parents until the initial commit.

#### void globalLog()

Loops over every commit provided by the COMMITS_DIR FolderManager and logs each one's logString

#### List<Commit> findCommitsByMessage(String message)

Loops over every commit provided by the Commits_DIR FolderManager and returns a list of each whose message matches the input message.

#### String getStatusString()

Returns a string containing a text representation of the status of the repository, including:
1. A list of branches, provided from the BRANCHES_DIR FolderManager, with the HEAD branch demarked with a *
2. A list of all file names staged for addition/modification, retrieved from the StagingArea
3. A list of all file names staged for removal, retrieved from the StagingArea
4. (optional) A list of files that have been modified or removed, but are not staged
5. (optional) A list of files that are not tracked


~~ <br/> 
The following methods are convenience static methods used by many classes in the code base for common opeprations <br/>
~~

#### static Commit getCommit(String commitId)

Returns the commit with the provided SHA-1 digest, or null if none found

#### static Branch getBranch(String branchName)

Returns the branch with the branchName, or null if none found

#### static Blob getBlob(String blobId) 

Returns the blob with the provided SHA-1 digest, or null if none fuond



## Persistence

