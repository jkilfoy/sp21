### Plan for Gitlet project

#### Digestable

1. Write the Digestable interface method `digest()`, which loops through the object's fields and produces a SHA-1 hash
2. Write the Commit and Blob structues which implement Digestable
3. TEST

#### Folder Manager

1. Write the FolderManager\<T> class, which implements iterable 
2. Write its constructor which takes the path name this will manage
3. Write its three methods
   1. persist(T obj, String filename) persists the given obj to the folder 
   2. read(String filename) returns the deserialized T under the file name
   3. iterator returns an iterator for each object in the folder
4. TEST

#### Static Fields

1. Write the 7 Main public static fields (mostly directories and FolderManagers) of `Main`
2. Add static field `HEAD` to Repository
3. Write methods for setting/getting head (including lazy loading and auto persisting)
4. Add static fields `added` and `removed` to StagingArea
5. Write methods for setting/getting these (including lazy loading and auto persisting)
6. TEST

#### Init 

1. Write `Repository.init` method
2. In `Main.main` method, implement the following:
   1. Verifies if a command was provided
   2. If it was `init`, call `Repository.init()`
3. Be sure to try / throw / catch and print error messages
4. TEST

#### Add, Remove and Commit

1. Write StagingArea.add method
2. Write StagingArea.remove method
3. Write StagingArea.clear method
4. Write Repository.commit method
5. In `Repository.main` method, implement the main switch block and add these cases:
   1. add - calls `StagingArea.add`
   2. remove - calls `StagingArea.remove`
   3. commit - calls `Repository.commit`
6. Be sure to try / throw / catch and print appropriate error messages
7. TEST

#### Log

1. Write `Repository.log` method
2. Write `Repository.globalLog` method
3. Write `Repository.status` method
4. Link up these methods in appropriate cases of `Main.main` switch block
5. Be sure to try/ throw / catch and print appropriate error messages
6. TEST

#### Branch

1. Write `Repository.branch` method
2. Link up method in `Main.main`
3. Be sure to try/throw/catch errors
4. TEST

#### Checkout and Reset

1. Write all three `Repository.checkout` methods
2. Link up in `Main.main`
3. Be sure to try/throw/catch errors
4. TEST
5. Write `Repository.reset` method, which mostly just called checkout and setBranch
6. Link up in `Main.main`
7. try/throw/catch
8. TEST

#### Merge

When it's time, it's time......