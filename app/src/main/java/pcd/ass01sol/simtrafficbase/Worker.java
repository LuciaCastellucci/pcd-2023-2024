package pcd.ass01sol.simtrafficbase;

import pcd.ass01sol.simengineseq.AbstractEnvironment;

import java.util.Optional;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {

    List<CarAgent> agents;

    CyclicBarrier barrier;
    int dt;
    public Worker(List<CarAgent> agents, int dt, CyclicBarrier barrier) {
        this.agents = agents;
        this.dt = dt;
        this.barrier = barrier;
    }

    public void run() {
        for (CarAgent agent: agents) {
            /* sense */

            AbstractEnvironment env = agent.getEnv();
            agent.currentPercept = (CarPercept) env.getCurrentPercepts(agent.getId());

            /* decide */

            agent.selectedAction = Optional.empty();

            agent.decide(dt);

            /* act */

            if (agent.selectedAction.isPresent()) {
                env.doAction(agent.getId(), agent.selectedAction.get());
            }
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {}
    }
}
