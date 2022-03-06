# Gitlet Design Document

**Name**: Dadodude

## Classes and Data Structures

### Main

The main class that processes gitlet commands. It contains the following public static fields that are shared across all classes:

1. static final File CWD; <br/>
   A reference to the current working directory 
2. static final File GITLET_DIR; <br/>
   A reference to the `.gitlet` directory
3. static final File STAGE_DIR; <br/>
   A reference to the `.gitlet/stage` directory
4. static final FolderManager COMMITS; <br/>
   A FolderManager for managing the commits stored in the `.gitlet/commits` directory
5. static final FolderManager TRACKED_BLOBS; <br/>
   A FolderManager for managing the tracked blobs stored in the `.gitlet/tracked_blobs` directory
6. static final FolderManager BRANCHES; <br/>
   A FolderManager for managing the branches stored in the `.gitlet/branches` directory
7. static final FolderManager STAGGED_BLOBS; <br/>
   A FolderManager for managing the staged blobs stored in the `.gitlet/stage/staged_blobs` directory.

### Repository

A class representing the Gitlet repository.
This is the main point of access for most gitlet commands. 
It contains a reference to the head branch. 

#### Fields

1. static Branch head; <br/>
   The current head branch of the repository, lazy deserialized from `.gitlet/HEAD`

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
   The time this commit was created


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

A class representing the StagingArea in the repository. Tracks which files have been added, modified and removed, and contains utility methods for updating the Staging Area. Maintains a reference to the directory `.gitlet/stage`, and a FolderManager for the blobs in `.gitlet/stage/staged_blobs`.

####Fields

1. static TreeMap<String, String> added; <br/>
   Maps file names to the SHA-1 digests of the blobs to be added/modified in the next commit. Lazy deserialized from `.gitlet/stage/added`.
2. static TreeSet\<String> removed; <br/>
   A set of file names to be removed in the next commit. Lazy deserialized from `.gitlet/stage/removed`.

### FolderManager\<T implements Serializable>

A utility class for managing a folder of Serializable objects. Facilitates common tasks related to reading, writing or finding such Objects within a directory

#### Fields

1. final File folder; <br/>
   The folder in which serialized T objects are stored

### Digestable

An interface for objects that can be digested into a SHA-1 hash. The default hash uses all of the object's declared fields

## Algorithms

This section will discuss the methods used in my data structures.

### Repository

#### void init()

Initializes a gitlet repository. It: 
1. Creates the `.gitlet` directory as well as subdirectories `commits`, `tracked_blobs`, `branches`, `stage`, and `stage/staged_blobs`
2. Creates the master branch which points to the initial commit, and serializes it in the `branches` folder under the branch's name
3. Sets the repository's head field to the master branch
4. Creates an empty TreeMap and serializes it in `stage/added`
5. Creates an empty TreeSet and serializes it in `stage/removed`
6. Finally, creates the initial commit, and serializes it in the `commits` directory under its SHA-1 digest

#### void commit(String message)

Creates a new commit containing all changes in the staging area, whose parent commit is the current head of the repository,
and using the provided commit message and the current timestamp. This commit is serialized and stored under its 
SHA-1 digest in the `commits` directory. Then, the HEAD branch's commit becomes the newly created commit, and the staging area is cleared.

#### void log()

Prints the `Commit.logString()` of the commit referenced by HEAD, then prints the logString() of each commit along the path of parents until the initial commit.

#### void globalLog()

Iterates over every commit provided by the COMMITS FolderManager and logs each one's logString

#### List\<Commit> findCommitsByMessage(String message)

Loops over every commit provided by the COMMITS FolderManager and returns a list of each Commit whose message matches the input message.

#### String getStatusString()

Returns a string containing a text representation of the status of the repository, including:
1. A list of branches, provided from the BRANCHES_DIR FolderManager, with the HEAD branch demarked with a *
2. A list of all file names staged for addition/modification, retrieved from the StagingArea
3. A list of all file names staged for removal, retrieved from the StagingArea
4. (optional) A list of files that have been modified or removed, but are not staged
5. (optional) A list of files that are not tracked

#### void branch(String branchName)

Checks that the branch doesn't already exist, and if so creates it, serializing it in the branches folder. Then sets head to that branch.

#### void removeBranch(String branchName)

Checks that the branch exists, and is not head, and if so removes the branch from the repository.

#### Branch getHead() 

Gets the current head of the repository, deserializing it from `.gitlet/HEAD` if neceesary

#### void setHead(Branch branch)

Sets the branch to be the head of the repository, serializing the branch name in `.gitlet/HEAD`

#### void checkout(String filename)

Overwrites the current version of filename in CWD with the version in the HEAD commit

#### void checkout(Commit commit, String filename)

Overwrites the current version of filename in CWD with the version in the given commit

#### void checkout(Branch branch)

Overwrites all files in the CWD with the versions in the given branch

#### void reset(Branch branch)

Checksout the branch, then resets HEAD to the branch.

#### void merge(Branch branch)

TODO figure it out.

### StagingArea

#### void add(String fileName)

Verifies the file exists in the CWD, and that its contents differ from the currently tracked version of the file. If so, it serializes the file into `.gitlet/stage/staged_blobs`, and adds an entry to the `added` TreeMap. Also removes the fileName from the `removed` TreeSet if present.

#### void remove(String fileName)

Removes the file from the `added` TreeMap and its corresponding blob from `.gitlet/stage/staged_blobs` if it exists. 
Then, if the file is tracked by HEAD, it adds the filename to the `removed` TreeSet.
Finally, if the file exists in CWD, it is removed.

#### void clear()

Clears the staging area by overwriting the `added` and `removed` files with an empty TreeMap and TreeSet respectively, and clears the `staged_blobs` folder.

#### TreeMap\<String, String> getAdded()

Gets the added treemap, deserializing it from `stage/added` if necessary

#### void setAdded(TreeMap\<String, String>)

Sets the added treemap, serializing it into `stage/added`

#### TreeSet\<String> getRemoved()

Gets the removed treeset, deserializing it from `stage/removed` if necessary

#### void setRemoved(TreeSet\<String>)

Sets the removed treeset, serializing it into `stage/removed`


### FolderManager\<T extends Serializable>

#### void persist(T obj) 

Persists the object in this directory this FolderManager is responsible is for, overwriting or creating the file as necessary

#### T read(String fileName)

Returns the object stored in the directory under the given filename, or null if none is found.

#### Iterator\<T> iterate()

Returns an iterator over all objects stored in the directory

### Digestable

#### default String digest()

Returns the SHA-1 digest (hash) of all declared fields (in lexicographic order) of this Digestable object.

### Branch

This class only has getters and setters

### Commits

This class only has getters, setters, and inherits the digest method from Digestable

### Blob

This class has getters, setters and inherits the digest method from Digestable. In addition, it has:

#### void writeToCWD()

This will write the contents of the Blob to the CWD, overwriting any file that's already present.

## Persistence

When a giitlet repository is initialized, the .gitlet directory is made in the CWD, and contains the following structure.
~~~
.gitlet
|---- HEAD                 // Serialized String of current HEAD branch name
|---- branches             // Folder containing all branches in the repository
      |---- master         // Serialized Branch stored under branch name
      |---- branch2
      ...
|---- commits              // Folder containing all commits in the repository
      |---- 3ux6ehg..      // Serialized Commit, stored under SHA-1 digest
      ...
|---- tracked_blobs        // Folder containing all blobs tracked by any commit in the repository
      |---- nj71uyd..      // Serialized Blob, stored under SHA-1 digest
      ... 
|---- stage                // Folder containing staging area information  
      |---- added          // Serialized TreeMap of filenames to staged_blob ids
      |---- removed        // Serialized TreeSet of filenames of blobs to be removed 
      |---- staged_blobs   // Folder containing blobs to be added/modified in next commit
            |---- j37c..   // Serialized Blob    
            ...  
~~~

There are 3 main serialized Objects in the directory.

1. The branch name of the HEAD branch, stored in `.gitlet/HEAD`. This is used to initialize `Repository.head`.
2. The TreeMap mapping filenames to blobs in the staging area, stored in `.gitlet/stage/added`. This is used to initialize `StagingArea.added`.
3. The TreeSet containing filenames of blobs to remove in the next commit, stored in `.gitlet/stage/removed`. This is used to initialize `StagingArea.removed`.

Each of these variables is lazy loaded by the class that manages them, and accessible via a public getter.

For the remaining 4 folders that contain lists of serialized objects (`branches`, `commits`, `tracked_blobs`, and `stage/staged_blobs`), a FolderManager is used to facilitate persisting and deserializing the objects it manages.