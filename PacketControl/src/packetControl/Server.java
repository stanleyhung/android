package packetControl;

import java.awt.AWTException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Stanley
 * 
 */
public class Server extends Thread {
	private final static int NUM_THREADS = 4;
	private MediaPlayer robot;
	static SynchronizedQueue requests; // Queue of Messages representing media player actions
	static ExecutorService executor;
	public final static int SUCCESS = 1;
	public final static int FAILURE = -1;
	
	
	public Server() {
		try {
			robot = new MediaPlayer();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requests = new SynchronizedQueue();
		executor = Executors.newFixedThreadPool(NUM_THREADS);
	}
	
	public void run() {
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		final Server s = new Server();
		ExecutorService executor = Executors.newFixedThreadPool(5);
		executor.execute(new Runnable() {
			public void run() {
				s.robot.run();
			}
		});
		Thread.sleep(3000);
		executor.submit(new Runnable() {
			public void run() {
				Network.main(null);
			}
		});
		executor.submit(new Runnable() {
			public void run() {
				Handler.main(null);
			}
		});
		executor.submit(new Runnable() {
			public void run() {
				Client.main(null);
			}
		});
		
		executor.shutdown();
	}
	
}
