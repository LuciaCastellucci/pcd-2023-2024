package pcd.lab04.sem.ex;

public class Pinger extends Thread {

	public Pinger() {
	}	
	
	public void run() {
		while (true) {
			try {
				System.out.println("ping!");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}