package pcd.lab04.sem.ex;

import java.util.concurrent.Semaphore;

public class Pinger extends Thread {

	private Semaphore pingDone;
	private Semaphore pongDone;

	public Pinger(Semaphore pingDone, Semaphore pongDone) {
		this.pingDone = pingDone;
		this.pongDone = pongDone;
	}	
	
	public void run() {

		while (true) {
			try {
				pongDone.acquire();
				System.out.println("ping!");
				pingDone.release();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}