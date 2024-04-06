package pcd.ass01sol01.simengineseq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pcd.ass01sol01.simtrafficexamples.*;

/**
 * Base class for defining concrete simulations
 * Cuore dell'engine
 * Implementa il pattern Observer poichè al suo interno ha dei listener (vedi metodi notifyReset e notifyNewStep)
 * Tale engine va reso concorrente
 */
public abstract class AbstractSimulation {

	/* environment of the simulation */
	private AbstractEnvironment env;
	
	/* list of the agents */
	private List<AbstractAgent> agents;
	
	/* simulation listeners */
	private List<SimulationListener> listeners;

	/* logical time step */
	private int dt;
	
	/* initial logical time */
	private int t0;

	/* in the case of sync with wall-time */
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;
	
	/* for time statistics*/
	private long currentWallTime;
	private long startWallTime;
	private long endWallTime;
	private long averageTimePerStep;
	private Flag stopFlag;
	private StartSynch synch;

	protected AbstractSimulation(Flag stopFlag, StartSynch sync) {
		agents = new ArrayList<AbstractAgent>();
		listeners = new ArrayList<SimulationListener>();
		toBeInSyncWithWallTime = false;
		this.stopFlag = stopFlag;
		this.synch = sync;
	}
	
	/**
	 * 
	 * Method used to configure the simulation, specifying env and agents
	 * 
	 */
	protected abstract void setup();

	/**
	 * Method running the simulation for a large number of steps,
	 * using a concurrent approach.
	 * It's used only for testing performance using RunTrafficSimulationMassiveTest.
	 * @param numSteps
	 */
	public void run(int numSteps) {
		synch.notifyStarted(numSteps);
		run();
	}
	
	/**
	 * Method running the simulation for a number of steps,
	 * using a concurrent approach
	 */
	public void run() {
		try {
			int numSteps = synch.waitStart();
			notifyStateChanged("Running");

			startWallTime = System.currentTimeMillis();

			/* initialize the env and the agents inside */
			int t = t0;

			env.init();
			for (var a: agents) {
				a.init(env);
			}

			this.notifyReset(t, agents, env);

			long timePerStep = 0;
			int nSteps = 0;

			int nThread = Runtime.getRuntime().availableProcessors();
			int barrierSize = (Math.min(agents.size(), nThread)) + 1;
			CyclicBarrier barrier = new CyclicBarrier(barrierSize);

			while (nSteps < numSteps) {
				currentWallTime = System.currentTimeMillis();

				/* make a step */

				env.step(dt);

				List<List<AbstractAgent>> parts = new ArrayList<List<AbstractAgent>>();
				int agentsSplitted = 0;
				int partsSize = agents.size() / (nThread - 1);
				if (partsSize == 0) {
					partsSize = 1;
				}
				int nParts = Math.min(agents.size(), nThread);
				for (int i = 0; i < nParts; i++) {
					int to = agentsSplitted + partsSize;
					if (i == nThread - 1) {
						to = agents.size();
					}
					parts.add(new ArrayList<AbstractAgent>(
							agents.subList(agentsSplitted, to)));
					agentsSplitted += partsSize;
				}

				for(List<AbstractAgent> part : parts) {
					Worker worker = new Worker(part, dt, barrier);
					worker.start();
				}
				try {
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException ignored) {}

				t += dt;

				if (!stopFlag.isSet()) {
					notifyNewStep(t, agents, env);
				} else {
					notifyStateChanged("Interrupted");
					break;
				}

				nSteps++;
				timePerStep += System.currentTimeMillis() - currentWallTime;

				if (toBeInSyncWithWallTime) {
					syncWithWallTime();
				}
			}

			endWallTime = System.currentTimeMillis();
			this.averageTimePerStep = timePerStep / numSteps;
		} catch (Exception ignored) { }
		notifyStepOver();
	}
	
	public long getSimulationDuration() {
		return endWallTime - startWallTime;
	}
	
	public long getAverageTimePerCycle() {
		return averageTimePerStep;
	}
	
	/* methods for configuring the simulation */
	
	protected void setupTimings(int t0, int dt) {
		this.dt = dt;
		this.t0 = t0;
	}
	
	protected void syncWithTime(int nCyclesPerSec) {
		this.toBeInSyncWithWallTime = true;
		this.nStepsPerSec = nCyclesPerSec;
	}
		
	protected void setupEnvironment(AbstractEnvironment env) {
		this.env = env;
	}

	protected void addAgent(AbstractAgent agent) {
		agents.add(agent);
	}
	
	/* methods for listeners */
	
	public void addSimulationListener(SimulationListener l) {
		this.listeners.add(l);
	}
	
	private void notifyReset(int t0, List<AbstractAgent> agents, AbstractEnvironment env) {
		for (var l: listeners) {
			l.notifyInit(t0, agents, env);
		}
	}

	private void notifyNewStep(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		for (var l: listeners) {
			l.notifyStepDone(t, agents, env);
		}
	}

	private void notifyStateChanged(String message) {
		for (var l: listeners) {
			l.notifyStateChanged(message);
		}
	}

	private void notifyStepOver() {
		for (var l: listeners) {
			l.notifyStepOver();
		}
	}

	/* method to sync with wall time at a specified step rate */
	
	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ignored) {}
	}
}
