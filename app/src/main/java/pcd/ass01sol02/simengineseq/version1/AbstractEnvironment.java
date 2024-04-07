package pcd.ass01sol02.simengineseq.version1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *   
 * Base class to define the environment of the simulation
 *   
 */
public abstract class AbstractEnvironment {

	private String id;
	protected List<Action> submittedActions;
	private Lock lock = new ReentrantLock();

	protected AbstractEnvironment(String id) {
		this.id = id;		
		this.submittedActions = new ArrayList<>();
	}
	
	public String getId() {
		return id;
	}
	
	/**
	 * 
	 * Called at the beginning of the simulation
	 */
	public abstract void init();
	
	/**
	 * 
	 * Called at each step of the simulation
	 * 
	 * @param dt
	 */
	public abstract void step(int dt);

	/**
	 * 
	 * Called by an agent to get its percepts 
	 * 
	 * @param agentId - identifier of the agent
	 * @return agent percept
	 */
	public abstract Percept getCurrentPercepts(String agentId);

	/**
	 * 
	 * Called by agent to submit an action to the environment
	 * 
	 * @param act - the action
	 */
	public void submitAction(Action act) {
		try {
			lock.lock();
			submittedActions.add(act);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 
	 * Called at each simulation step to clean the list of actions
	 * submitted by agents
	 * 
	 */
	public void cleanActions() {
		submittedActions.clear();
	}

	/**
	 * 
	 * Called at each simulation step to process the actions 
	 * submitted by agents. 
	 * 
	 */
	public abstract void processActions();
}
