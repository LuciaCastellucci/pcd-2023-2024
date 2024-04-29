package pcd.ass02part01.simtrafficexamples.version1;

public class StartSynch {

	private boolean started;
	private int steps;
	
	public StartSynch(){
		started = false;
	}
	
	public synchronized int waitStart() {
		while (!started) {
			try {
				wait();
			} catch (InterruptedException ignored) {}
		}
		started = false;
		return steps;
	}

	public synchronized void notifyStarted(int steps) {
		started = true;
		this.steps = steps;
		notifyAll();
	}
}