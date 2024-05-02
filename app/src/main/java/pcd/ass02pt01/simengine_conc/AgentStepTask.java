package pcd.ass02pt01.simengine_conc;

import java.util.List;
import java.util.concurrent.Callable;

public class AgentStepTask implements Callable<Void> {

    private List<AbstractAgent> assignedSimAgents;
    private int dt;
    private Flag stopFlag;
    private String id;

    public AgentStepTask(String id, List<AbstractAgent> assignedSimAgents, int dt, Flag flag) {
        this.id = id;
        this.assignedSimAgents = assignedSimAgents;
        this.dt = dt;
        this.stopFlag = flag;
    }

    //public void run() {
    public Void call() {
        log("running.");
        try {
            if (!stopFlag.isSet()) {
                /* moving on agents */
                for (var ag: assignedSimAgents) {
                    ag.step(dt);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log("done");
        return null;
    }

    private void log(String msg) {
        System.out.println("[" + this.id +"] " + msg);
    }
}
