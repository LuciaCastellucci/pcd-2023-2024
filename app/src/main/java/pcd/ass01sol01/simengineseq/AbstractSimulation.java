package pcd.ass01sol01.simengineseq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	private Semaphore stepDone;


	protected AbstractSimulation() {
		agents = new ArrayList<AbstractAgent>();
		listeners = new ArrayList<SimulationListener>();
		toBeInSyncWithWallTime = false;
		stepDone = new Semaphore(1, true);
	}
	
	/**
	 * 
	 * Method used to configure the simulation, specifying env and agents
	 * 
	 */
	protected abstract void setup();
	
	/**
	 * Method running the simulation for a number of steps,
	 * using a sequential approach
	 *
	 * Implementazione 1:1 rispetto allo pseudocodice
	 * 
	 * @param numSteps
	 */
	public void run(int numSteps) {		

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
		CyclicBarrier barrier = new CyclicBarrier(nThread);

		while (nSteps < numSteps) {
			currentWallTime = System.currentTimeMillis();

			/* make a step */

			env.step(dt);

			List<List<AbstractAgent>> parts = new ArrayList<List<AbstractAgent>>();
			int partsSize = agents.size() / nThread;
			if (partsSize == 0) {
				nThread = agents.size();
				partsSize = 1;
			}
			for (int i = 0; i < agents.size(); i += partsSize) {
				parts.add(new ArrayList<AbstractAgent>(
						agents.subList(i, Math.min(agents.size(), i + partsSize)))
				);
			}

			//boolean lastStep = nSteps == numSteps - 1;
			//int c = 0;
			for(List<AbstractAgent> part : parts) {
				Worker worker = new Worker(part, dt, barrier);
				//c++;
				//Worker worker = new Worker("Step " + nSteps + " Worker " + c, part, dt, barrier, numSteps);
				worker.start();
			}

			t += dt;

			notifyNewStep(t, agents, env);

			nSteps++;
			timePerStep += System.currentTimeMillis() - currentWallTime;

			if (toBeInSyncWithWallTime) {
				syncWithWallTime();
			}
		}	
		
		endWallTime = System.currentTimeMillis();
		this.averageTimePerStep = timePerStep / numSteps;
		
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

	/* method to sync with wall time at a specified step rate */
	
	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ex) {}		
	}
}
