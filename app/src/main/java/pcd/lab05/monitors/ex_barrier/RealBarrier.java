package pcd.lab05.monitors.ex_barrier;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RealBarrier implements Barrier {
    private int counter;

    private int nPartecipants;
    public RealBarrier(int nPartecipants) {
        this.counter = 0;
        this.nPartecipants = nPartecipants;
    }
    @Override
    public synchronized void hitAndWaitAll() throws InterruptedException {
        counter++;
        if (counter == nPartecipants) {
            notifyAll();
        } else {
            while (counter != nPartecipants) {
                wait();
            }
        }
    }
}
