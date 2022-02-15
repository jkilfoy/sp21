package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> ops = new AList<>();
        final int OPS = 10000;

        for (int i = 1; i <= 128; i*=2) {
            int n = 1000 * i;
            Stopwatch s = new Stopwatch();

            SLList<Integer> test = new SLList<>();
            for (int op = 0; op < n; op++) {
                test.addLast(op);
            }

            double time = s.elapsedTime();

            ns.addLast(n);
            times.addLast(time);
            ops.addLast(OPS);
        }

        printTimingTable(ns, times, ops);
    }

}
