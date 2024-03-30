package pcd.ass01sol.simtrafficbase;

import pcd.ass01sol.simengineseq.AbstractAgent;
import pcd.ass01sol.simengineseq.AbstractEnvironment;

import java.util.List;

public interface CarSimulationListener {

    /**
     * Called at the beginning of the simulation
     *
     * @param t
     * @param agents
     * @param env
     */
    void notifyInit(int t, List<CarAgent> agents, AbstractEnvironment env);

    /**
     * Called at each step, updater all updates
     * @param t
     * @param agents
     * @param env
     */
    void notifyStepDone(int t, List<CarAgent> agents, AbstractEnvironment env);
}
