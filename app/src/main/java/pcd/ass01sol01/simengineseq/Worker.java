package pcd.ass01sol01.simengineseq;

import pcd.ass01sol01.simengineseq.AbstractAgent;

import java.util.List;

public class Worker extends Thread {

    List<AbstractAgent> agents;
    int dt;
    public Worker(List<AbstractAgent> agents, int dt) {
        this.agents = agents;
        this.dt = dt;
    }

    public void run() {
        for (AbstractAgent agent : agents) {
            agent.step(dt);
        }
    }
}
