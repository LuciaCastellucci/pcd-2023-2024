package pcd.ass01sol01.simengineseq;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Worker extends Thread {

    List<AbstractAgent> agents;
    int dt;
    CyclicBarrier barrier;

    boolean lastStep;

    public Worker(List<AbstractAgent> agents, int dt, CyclicBarrier barrier) {
        //super(name);
        this.agents = agents;
        this.dt = dt;
        this.barrier =  barrier;
        //this.lastStep = lastStep;
    }

    public Worker(String name, List<AbstractAgent> agents, int dt, CyclicBarrier barrier, int nSteps, int currentStep) {
        super(name);
        this.agents = agents;
        this.dt = dt;
        this.barrier =  barrier;
        this.lastStep = lastStep;
    }

    public void run() {
        for (AbstractAgent agent : agents) {
            agent.step(dt);
        }
        //if (!lastStep) {
        try {
            //log("Wating for other threads to complete");
            barrier.await();
            //log("Go!!!");
        } catch (InterruptedException | BrokenBarrierException ignored) {
        }
        //}
    }

    protected void log(String msg){
        synchronized (System.out){
            System.out.println("[" + this.getName()+ "] " + msg);
        }
    }
}
