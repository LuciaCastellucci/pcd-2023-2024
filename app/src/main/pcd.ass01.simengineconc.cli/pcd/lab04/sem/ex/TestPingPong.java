package pcd.lab04.sem.ex;

import java.util.concurrent.Semaphore;

/**
 * Unsynchronized version
 * 
 * @TODO make it sync 
 * @author aricci
 *
 */
public class TestPingPong {
	public static void main(String[] args) {
		Semaphore pingDone = new Semaphore(1,true);
		Semaphore pongDone = new Semaphore(0, true);

		/*
		In alternativa ad inizializzare uno dei due semafori a 1, bastava inizializzarli a 0 e sbloccare uno dei due
		try {
			pongDone.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/

		new Pinger(pingDone, pongDone).start();
		new Ponger(pingDone, pongDone).start();



	}

}
