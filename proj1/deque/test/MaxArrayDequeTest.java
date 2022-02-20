package deque.test;

import deque.MaxArrayDeque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    @Test
    public void getMaxIntegerCompareTo() {
        MaxArrayDeque<Integer> ad = new MaxArrayDeque<>(Integer::compareTo);
        for (int i = 0; i < 101; i++) {
            int randInt = StdRandom.uniform(2);
            if (randInt == 0) {
                ad.addFirst(i);
            } else {
                ad.addLast(i);
            }
        }
        int max = ad.max();
        assertEquals(max, 100);
    }

    @Test
    public void getMaxInverseCompareTo() {
        MaxArrayDeque<Integer> ad = new MaxArrayDeque<>(Integer::compareTo);
        for (int i = 0; i < 101; i++) {
            int randInt = StdRandom.uniform(2);
            if (randInt == 0) {
                ad.addFirst(i);
            } else {
                ad.addLast(i);
            }
        }
        int max = ad.max(Comparator.reverseOrder());
        assertEquals(max, 0);
    }
}
