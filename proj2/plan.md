### Plan for Gitlet project

#### Data Strucutes

1. XXX Write the Commit class
2. XXX Write the Blob class
3. XXX Write the Branch class

#### Digestable

1. XXX Write the Digestable interface method `digest()`, which loops through the object's fields and produces a SHA-1 hash
2. XXX TEST

#### Folder Manager

1. XXX Write the FolderManager\<T> class, which implements iterable 
2. XXX Write its constructor which takes the path name this will manage
3. XXX Write its three methods
   1. XXX persist(T obj, String filename) persists the given obj to the folder 
   2. XXX read(String filename) returns the deserialized T under the file name
   3. XXX iterator returns an iterator for each object in the folder
   --> ADDED contains and getFolder methods
   --> ADDED capability to auto generate file names from object
4. XXX TEST

#### Static Fields

1. XXX Write the 7 Main public static fields (mostly directories and FolderManagers) of `Main`
2. XXX Add static field `HEAD` to Repository
3. XXX Write methods for setting/getting head (including lazy loading and auto persisting)
4. XXX Add static fields `added` and `removed` to StagingArea
5. XXX Write methods for setting/getting these (including lazy loading and auto persisting)
6. NO TEST

#### Init 

1. XXX Write `Repository.init` method
2. XXX In `Main.main` method, implement the following:
   1. XXX Verifies if a command was provided
   2. XXX If it was `init`, call `Repository.init()`
3. XXX Be sure to try / throw / catch and print error messages
4. TEST

#### Add, Remove and Commit

1. XXX Write StagingArea.add method
2. XXX Write StagingArea.remove method
3. XXX Write StagingArea.clear method
4. XXX Write Repository.commit method
5. In `Repository.main` method, implement the main switch block and add these cases:
   1. XXX add - calls `StagingArea.add`
   2. XXX remove - calls `StagingArea.remove`
   3. XXX commit - calls `Repository.commit`
6. XXX Be sure to try / throw / catch and print appropriate error messages
7. TEST

#### Log

1. XXX Write `Repository.log` method
   1. Todo: Consider merge commits
2. XXX Write `Repository.globalLog` method
3. XXX Write `Repository.status` method
   1. Todo: consider EC
4. XXX Link up these methods in appropriate cases of `Main.main` switch block
5. XXX Be sure to try/ throw / catch and print appropriate error messages
6. TEST

#### Branch

1. XXX Write `Repository.branch` method
2. XXX Link up method in `Main.main`
3. XXX Be sure to try/throw/catch errors
4. TEST

#### Checkout and Reset

1. XXX Write all three `Repository.checkout` methods
2. XXX Link up in `Main.main`
3. XXX Be sure to try/throw/catch errors
4. TEST
5. Write `Repository.reset` method, which mostly just called checkout and setBranch
6. Link up in `Main.main`
7. try/throw/catch
8. TEST

#### Merge

When it's time, it's time......