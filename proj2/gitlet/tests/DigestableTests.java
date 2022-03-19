package gitlet.tests;

import gitlet.Blob;
import gitlet.Commit;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class DigestableTests {

    @Test
    public void objectsAreDigestable() {
        Blob blob = new Blob("These are the contents".getBytes(StandardCharsets.UTF_8), "blob1");
        Commit commit = new Commit("init commit", new Date(), "1234", new TreeMap<>());

        String blobId = blob.digest();
        String commitId = commit.digest();
        assertEquals("Digest should be 40 characters", 40, blobId.length());
        assertEquals("Digest should be 40 characters", 40, commitId.length());
        for (int i = 0; i < 100; i++) {
            assertEquals("Digest should always produce the same results", blobId, blob.digest());
            assertEquals("Digest should produce the same results", commitId, commit.digest());
        }
    }

    @Test
    public void sameContentsMeansSameDigest() {
        Blob blob = new Blob("0123456789".getBytes(StandardCharsets.UTF_8), "blob");
        Blob blobCopy = new Blob("0123456789".getBytes(StandardCharsets.UTF_8), "blob");
        assertEquals("Objects with identical fields should give same digest", blob.digest(), blobCopy.digest());

        TreeMap<String, String> map1 = new TreeMap<>();
        TreeMap<String, String> map2 = new TreeMap<>();
        for (int i = 1000; i < 20000; i+= 349) {
            map1.put("" + i, "" + (i/4));
            map2.put("" + i, "" + (i/4));
        }
        Date now = new Date();
        Commit commit = new Commit("same", now, "0123456789", map1);
        Commit commitCopy = new Commit("same", now, "0123456789", map2);
        assertEquals("Objects with identical fields should give same digest", commit.digest(), commitCopy.digest());
    }

    @Test
    public void differentContentsMeansDifferentDigest() {
        Blob blob1 = new Blob("0123456789".getBytes(StandardCharsets.UTF_8), "blob");
        Blob blob2 = new Blob("0123456789".getBytes(StandardCharsets.UTF_8), "slob");
        Blob blob3 = new Blob("1234567890".getBytes(StandardCharsets.UTF_8), "blob");
        assertNotEquals("Objects with different fields should give different digests", blob1.digest(), blob2.digest());
        assertNotEquals("Objects with different fields should give different digests", blob1.digest(), blob3.digest());
        assertNotEquals("Objects with different fields should give different digests", blob3.digest(), blob2.digest());

        Commit commit1 = new Commit("Hello", new Date(), "Uh ok", new TreeMap<>());
        Commit commit2 = new Commit("Hello", new Date(0), "Uh ok", new TreeMap<>());
        assertNotEquals("Objects with different fields should give different digests", commit1.digest(), commit2.digest());

        TreeMap<String, String> map1 = new TreeMap<>();
        TreeMap<String, String> map2 = new TreeMap<>();
        for (int i = 1000; i < 20000; i+= 349) {
            map1.put("" + i, "" + (i/4));
            map2.put("l" + i, "" + (i/4)); // second map is different
        }
        Date now = new Date();
        Commit commit = new Commit("same", now, "0123456789", map1);
        Commit commitCopy = new Commit("same", now, "0123456789", map2);
        assertNotEquals("Objects with different fields should give different digests", commit.digest(), commitCopy.digest());
    }
}
