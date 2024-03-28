package pcd.lab05.monitors.ex_latch;

import pcd.lab05.monitors.ex_barrier.RealBarrier;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RealLatch implements Latch {

    int counter;

    Lock lock;

    Condition condition;

    public RealLatch(int nWorker) {
        this.counter = nWorker;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }
    @Override
    public void countDown() {
        try {
            lock.lock();
            counter--;
            if (counter == 0) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void await() throws InterruptedException {
        try {
            lock.lock();
            while (counter != 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }

    }
}
