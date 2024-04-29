package pcd.lab01.ex01;

import java.util.Arrays;
import java.util.Random;

public class ConcurrentSort {
    static final int VECTOR_SIZE = 40000000;
        //0; con questo zero vado sempre e comunque in heap of memory
    static final int N_THREADS = 2;

    public static void main(String[] args) {

        log("Generating array...");
        long[] v = genArray(VECTOR_SIZE);

        log("Array generated.");
        log("Sorting (" + VECTOR_SIZE + " elements)...");

        long t0 = System.nanoTime();

        long [] result = new long[0];
        for (int i=0; i< N_THREADS; i++) {
            long [] toOrder = Arrays.copyOfRange(v,
                    (VECTOR_SIZE / N_THREADS) * i,
                    (VECTOR_SIZE / N_THREADS) * (i + 1)
            );
            long [] current = sortArray(toOrder);
            result = mergeArrays(result, current);
        }
        //Arrays.sort(v, 0, v.length);
        long t1 = System.nanoTime();
        log("Done. Time elapsed: " + ((t1 - t0) / 1000000) + " ms");

        // dumpArray(v);
    }

    private static long[] sortArray(long[] v) {
        try {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    log("Sorting (" + v.length + " elements using thread)...");
                    Arrays.sort(v, 0, v.length);
                }
            });
            thread.start();
            thread.join();
        } catch (Exception e) {
            log("Interrupted exception occurred sorting " + v.length + " elements");
        }
        return v;
    }

    private static long[] mergeArrays(long[] foo, long[] bar) {
        int fooLength = foo.length;
        int barLength = bar.length;

        long[] merged = new long[fooLength + barLength];

        int fooPosition, barPosition, mergedPosition;
        fooPosition = barPosition = mergedPosition = 0;

        while(fooPosition < fooLength && barPosition < barLength) {
            if (foo[fooPosition] < bar[barPosition]) {
                merged[mergedPosition++] = foo[fooPosition++];
            } else {
                merged[mergedPosition++] = bar[barPosition++];
            }
        }

        while (fooPosition < fooLength) {
            merged[mergedPosition++] = foo[fooPosition++];
        }

        while (barPosition < barLength) {
            merged[mergedPosition++] = bar[barPosition++];
        }

        return merged;
    }


    private static long[] genArray(int n) {
        Random gen = new Random(System.currentTimeMillis());
        long v[] = new long[n];
        for (int i = 0; i < v.length; i++) {
            v[i] = gen.nextLong();
        }
        return v;
    }

    private static void dumpArray(long[] v) {
        for (long l:  v) {
            System.out.print(l + " ");
        }
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
