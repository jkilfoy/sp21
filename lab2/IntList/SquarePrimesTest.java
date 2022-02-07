package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimesTo100() {
        IntList lst = IntList.of(1);
        List<Integer> squarePrimes = new ArrayList<>();
        squarePrimes.add(1);
        for (int i = 2; i < 1000; i++) {
            if (Primes.isPrime(i)) {
                squarePrimes.add(0, i*i);
            } else {
                squarePrimes.add(0, i);
            }
            lst = new IntList(i, lst);
        }
        boolean changed = IntListExercises.squarePrimes(lst);
        for (int i = 0; i < 999; i++) {
            System.out.println("lst at i: " + lst.get(i) + " -- squarePrimes at i" + squarePrimes.get(i));
            assert lst.get(i) == squarePrimes.get(i);
        }
        assertTrue(changed);

    }
}
