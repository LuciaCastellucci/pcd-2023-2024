package pcd.lab04.sem.ex;

public class Ponger extends Thread {
	
	public Ponger() {
	}	
	
	public void run() {
		while (true) {
			try {
				System.out.println("pong!");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}