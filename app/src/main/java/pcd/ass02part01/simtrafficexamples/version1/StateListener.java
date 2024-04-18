package pcd.ass02part01.simtrafficexamples.version1;

import pcd.ass02part01.simengineseq.version1.AbstractAgent;
import pcd.ass02part01.simengineseq.version1.AbstractEnvironment;
import pcd.ass02part01.simengineseq.version1.SimulationListener;

import java.util.List;

public class StateListener implements SimulationListener {


    RoadSimView view;

    public StateListener(RoadSimView view) {
        super();
        this.view = view;
    }

    @Override
    public void notifyInit(int t, List<AbstractAgent> agents, AbstractEnvironment env) { }

    @Override
    public void notifyStepDone(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
        view.notifyStepDone(t, agents, env);
    }

    public void notifyStateChanged(String message) {
        view.changeState(message);
    }

    @Override
    public void notifyStepOver() {
        view.stepOver();
    }
}
