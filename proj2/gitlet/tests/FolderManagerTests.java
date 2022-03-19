package gitlet.tests;

import gitlet.Blob;
import gitlet.Commit;
import gitlet.FolderManager;
import gitlet.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static gitlet.Main.CWD;
import static org.junit.Assert.*;

public class FolderManagerTests {

    File testFolder;
    File stringFolder;
    File commitFolder;
    File blobFolder;
    FolderManager<String> stringFolderManager;
    FolderManager<Commit> commitFolderManager;
    FolderManager<Blob> blobFolderManager;

    @Before
    public void init() {
        testFolder = Utils.join(CWD, "test");
        testFolder.mkdir();
        stringFolder = Utils.join(testFolder, "strings");
        commitFolder = Utils.join(testFolder, "commits");
        blobFolder = Utils.join(testFolder, "blobs");
    }

    @After
    public void cleanup() throws IOException {
        Files.walk(testFolder.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }


    @Test
    public void testCreate() {
        assertFalse("Folder should not exist before FolderManager construction", stringFolder.exists());
        stringFolderManager = new FolderManager<>(stringFolder, String.class);
        assertTrue("Folder should exist after FolderManager construction", stringFolder.exists());
    }

    @Test
    public void testWriteAndReadObject() {
        stringFolderManager = new FolderManager<>(stringFolder, String.class);
        String fileName = "testfile1.txt";
        String fileContents = "This is the file contenteriwuyr q34n2807 ry bbqueiowhfc qiewuh cfuedwbfh iuh fo";
        assertFalse("File should not exist before it's written", stringFolderManager.contains(fileName));
        stringFolderManager.persist(fileContents, fileName);
        assertTrue("File should exist after writing", stringFolderManager.contains(fileName));
        assertEquals("File contents should be readable", fileContents, stringFolderManager.read(fileName));
    }

    @Test
    public void testOverwritingObject() {
        stringFolderManager = new FolderManager<>(stringFolder, String.class);
        String fileName = "file1";
        String fileContentsBefore = "hqwnfiouh2n0r8cxy2b3408r72y43078rcv2347089r 2340789r y2";
        String fileContentsAfter = "qirup92nu r98 23nh4priufhnqwe ";
        stringFolderManager.persist(fileContentsBefore, fileName);
        assertEquals("File should contain its initial contents", fileContentsBefore, stringFolderManager.read(fileName));
        stringFolderManager.persist(fileContentsAfter, fileName);
        assertEquals("File contents should be overwritten", fileContentsAfter, stringFolderManager.read(fileName));
    }

    @Test
    public void testDefaultFileNameUsingToString() {
        stringFolderManager = new FolderManager<>(stringFolder, String.class);
        String fileNameAndContents = "ImTheOne";
        stringFolderManager.persist(fileNameAndContents);
        assertTrue("Folder should contain the file", stringFolderManager.contains(fileNameAndContents));
        assertEquals("File should contain the contents", fileNameAndContents, stringFolderManager.read(fileNameAndContents));
    }

    @Test
    public void testIteratingOverFilemanager() {
        final int NUM_FILES = 500;
        stringFolderManager = new FolderManager<>(stringFolder, String.class, (s) -> "file" + s);
        for (int i = 1; i <= NUM_FILES; i++) {
            stringFolderManager.persist("" + i);
        }
        assertEquals("1000 files should exist", NUM_FILES, stringFolder.list().length);

        for (int i = 1; i <= NUM_FILES; i++) {
            assertEquals("Each files should have correct contents", ""+i, stringFolderManager.read("file"+i));
        }

        Map<String, Boolean> exists = new HashMap<>();
        for (String fileContents : stringFolderManager) {
            exists.put(fileContents, true);
        }
        assertEquals("All files should be iterated over", NUM_FILES, exists.size());
    }

    @Test
    public void nullIfFileDoesntExist() {
        stringFolderManager = new FolderManager<>(stringFolder, String.class);
        assertNull("Should return null if file doesn't exist", stringFolderManager.read("happyFace"));
    }

    @Test
    public void cannotWriteNullValues() {
        stringFolderManager = new FolderManager<>(stringFolder, String.class);
        String fileName = "nullFile";
        stringFolderManager.persist(null, fileName);
        assertFalse("Folder should not contain null file", stringFolderManager.contains(fileName));
        assertNull("Folder should read null from non-existant file", stringFolderManager.read(fileName));
    }

    @Test
    public void readAndWriteCommitObjectWithDigestFilename() {
        commitFolderManager = new FolderManager<>(commitFolder, Commit.class, Commit::digest);
        Commit c1 = new Commit("Message1", new Date(), "12345", new TreeMap<>());
        commitFolderManager.persist(c1);
        assertTrue("Folder should contain the commit by digest fileName", commitFolderManager.contains(c1.digest()));
    }
}
