package pcd.ass01sol02.simengineseq.version1;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

    List<AbstractAgent> agents;
    int dt;
    CyclicBarrier barrier;

    public Worker(List<AbstractAgent> agents, int dt, CyclicBarrier barrier) {
        this.agents = agents;
        this.dt = dt;
        this.barrier = barrier;
    }

    public Worker(String name, List<AbstractAgent> agents, int dt, CyclicBarrier barrier) {
        super(name);
        this.agents = agents;
        this.dt = dt;
        this.barrier = barrier;
    }

    public void run() {
        for (AbstractAgent agent : agents) {
            agent.step(dt);
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {}
    }

    protected void log(String msg){
        synchronized (System.out){
            System.out.println("[" + this.getName()+ "] " + msg);
        }
    }
}
