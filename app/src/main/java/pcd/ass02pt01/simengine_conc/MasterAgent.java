package pcd.ass02pt01.simengine_conc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;

public class MasterAgent extends Thread {
	
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;
	private int numSteps;

	private long currentWallTime;
	
	private AbstractSimulation sim;
	private Flag stopFlag;
	private Semaphore done;
	private int poolSize;
	private ForkJoinPool forkJoinPool;
	
	public MasterAgent(AbstractSimulation sim, int nWorkers, int numSteps, Flag stopFlag, Semaphore done, boolean syncWithTime) {
		toBeInSyncWithWallTime = false;
		this.sim = sim;
		this.stopFlag = stopFlag;
		this.numSteps = numSteps;
		this.done = done;
		this.poolSize = nWorkers;
		this.forkJoinPool = new ForkJoinPool(poolSize);
		
		if (syncWithTime) {
			this.syncWithTime(25);
		}
	}

	public void run() {
		
		log("booted");
		
		var simEnv = sim.getEnvironment();
		var simAgents = sim.getAgents();
		
		simEnv.init();
		for (var a: simAgents) {
			a.init(simEnv);
		}

		int t = sim.getInitialTime();
		int dt = sim.getTimeStep();
		
		sim.notifyReset(t, simAgents, simEnv);
		
		log("creating workers...");

		List<List<AbstractAgent>> assignedSimAgents = new ArrayList<List<AbstractAgent>>();
		int agentsSplitted = 0;
		int partsSize = simAgents.size() / (poolSize - 1);
		if (partsSize == 0) {
			partsSize = 1;
		}
		int nParts = Math.min(simAgents.size(), poolSize);
		for (int i = 0; i < nParts; i++) {
			int to = agentsSplitted + partsSize;
			if (i == poolSize - 1) {
				to = simAgents.size();
			}
			assignedSimAgents.add(new ArrayList<AbstractAgent>(
					simAgents.subList(agentsSplitted, to)));
			agentsSplitted += partsSize;
		}

		log("starting the simulation loop.");

		int step = 0;
		currentWallTime = System.currentTimeMillis();

		try {
			while (!stopFlag.isSet() &&  step < numSteps) {
				
				simEnv.step(dt);
				simEnv.cleanActions();

				List<AgentStepTask> tasks = new ArrayList<>();
				for (int i = 0; i < assignedSimAgents.size(); i++) {
					tasks.add(new AgentStepTask("task-"+i+" for step: "+step, assignedSimAgents.get(i), dt, stopFlag));
				}
				forkJoinPool.invokeAll(tasks);

				/* executed actions */
				simEnv.processActions();
								
				sim.notifyNewStep(t, simAgents, simEnv);
	
				if (toBeInSyncWithWallTime) {
					syncWithWallTime();
				}
				
				/* updating logic time */
				
				t += dt;
				step++;
			}	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		log("done");
		stopFlag.set();

		done.release();
	}

	private void syncWithTime(int nStepsPerSec) {
		this.toBeInSyncWithWallTime = true;
		this.nStepsPerSec = nStepsPerSec;
	}

	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			currentWallTime = System.currentTimeMillis();
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ex) {}
		
	}
	
	private void log(String msg) {
		System.out.println("[MASTER] " + msg);
	}
	
	
}
