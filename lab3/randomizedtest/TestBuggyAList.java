package randomizedtest;

import afu.org.checkerframework.checker.igj.qual.I;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> al = new AListNoResizing<>();
        BuggyAList<Integer> bugal = new BuggyAList<>();
        al.addLast(1);
        bugal.addLast(1);
        al.addLast(2);
        bugal.addLast(2);
        al.addLast(3);
        bugal.addLast(3);

        assertEquals(al.removeLast(), bugal.removeLast());
        assertEquals(al.removeLast(), bugal.removeLast());
        assertEquals(al.removeLast(), bugal.removeLast());
    }

    @Test
    public void randomizedTesting() {
        AListNoResizing<Integer> al = new AListNoResizing<>();
        BuggyAList<Integer> bugal = new BuggyAList<>();

        int N = 50000;
        for (int i = 0; i < N; ) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                al.addLast(randVal);
                bugal.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
                assertEquals(al.getLast(), bugal.getLast());
            } else if (operationNumber == 1) {
                // size
                int size = al.size();
                int bugalSize = bugal.size();
                System.out.println("size: " + size);
                assertEquals(size, bugalSize);
            } else if (operationNumber == 2) {
                // getLast
                if (al.size() == 0) {
                    continue;
                }
                int res = al.getLast();
                int bugalLast = bugal.getLast();
                System.out.println("getLast: " + res);
                assertEquals(res, bugalLast);
            } else if (operationNumber == 3) {
                // removeLast
                if (al.size() == 0) {
                    continue;
                }
                int res = al.removeLast();
                int bugalLast = bugal.removeLast();
                System.out.println("removeLast: " + res);
                assertEquals(res, bugalLast);
            }
            i += 1;
        }
    }
}
