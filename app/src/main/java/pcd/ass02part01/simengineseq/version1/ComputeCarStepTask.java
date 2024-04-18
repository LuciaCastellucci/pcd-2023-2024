package pcd.ass02part01.simengineseq.version1;

import java.util.List;

public class ComputeCarStepTask implements Runnable {

    List<AbstractAgent> agents;
    int dt;

    public ComputeCarStepTask(List<AbstractAgent> agents, int dt) {
        this.agents = agents;
        this.dt = dt;
    }
    @Override
    public void run() {
        for (AbstractAgent agent : agents) {
            agent.step(dt);
        }
    }
}
