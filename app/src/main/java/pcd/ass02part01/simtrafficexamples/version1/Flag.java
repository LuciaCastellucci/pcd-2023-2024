package pcd.ass02part01.simtrafficexamples.version1;

public class Flag {

	private boolean flag;
	
	public Flag() {
		flag = false;
	}
	
	public synchronized void reset() {
		flag = false;
	}
	
	public synchronized void set() {
		flag = true;
	}
	
	public synchronized boolean isSet() {
		return flag;
	}
}
