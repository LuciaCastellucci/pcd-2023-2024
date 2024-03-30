package pcd.ass01sol.simtrafficbase;

import pcd.ass01sol.simengineseq.AbstractAgent;
import pcd.ass01sol.simengineseq.AbstractEnvironment;
import pcd.ass01sol.simengineseq.AbstractSimulation;
import pcd.ass01sol.simengineseq.SimulationListener;


import java.util.*;
import java.util.concurrent.CyclicBarrier;

public abstract class AbstractCarSimulation extends AbstractSimulation {

    CyclicBarrier barrier;

    /* list of the agents */
    protected List<CarAgent> carAgents;

    private List<CarSimulationListener> listeners;

    protected AbstractCarSimulation() {
        carAgents = new ArrayList<CarAgent>();
        listeners = new ArrayList<CarSimulationListener>();
        toBeInSyncWithWallTime = false;
    }

    public void run(int numSteps) {

        startWallTime = System.currentTimeMillis();

        /* initialize the env and the agents inside */
        int t = t0;

        env.init();
        for (var a: carAgents) {
            a.init(env);
        }

        this.notifyReset(t, carAgents, env);

        long timePerStep = 0;
        doSteps(numSteps, t, timePerStep);

        endWallTime = System.currentTimeMillis();
        this.averageTimePerStep = timePerStep / numSteps;

    }

    protected void doSteps(int numSteps, int t, long timePerStep) {
        List<List<CarAgent>> parts = new ArrayList<List<CarAgent>>();
        int nThread = Runtime.getRuntime().availableProcessors();
        int partsSize = carAgents.size() / nThread;
        for (int i = 0; i < carAgents.size(); i += partsSize) {
            parts.add(new ArrayList<CarAgent>(
                    carAgents.subList(i, Math.min(carAgents.size(), i + partsSize)))
            );
        }

        barrier = new CyclicBarrier(parts.size());
        for(List<CarAgent> part : parts) {
            Worker worker = new Worker(part, dt, barrier);
            worker.start();
        }
    }

    private void notifyReset(int t0, List<CarAgent> agents, AbstractEnvironment env) {
        for (var l: this.listeners) {
            l.notifyInit(t0, agents, env);
        }
    }

    protected void addAgent(CarAgent agent) {
        carAgents.add(agent);
    }
}
