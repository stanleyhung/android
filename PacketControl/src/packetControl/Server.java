package packetControl;

import java.awt.AWTException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * @author Stanley
 * 
 */
public class Server extends Thread {
	private final static int NUM_THREADS = 4;
	private MediaPlayer robot;
	static SynchronizedQueue requests; // Queue of Messages representing media player actions
	static ExecutorService executor;
	public final static String SUCCESS = "adsklfjklajsklfdjdskl";
	public final static String FAILURE = "adsklfjaklsjfd";
	
	
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
		ArrayList<Future<String>> results = new ArrayList<Future<String>>();
		results.add(executor.submit(new Callable<String>() {
			public String call() {
				return s.robot.call();
			}
		}));
		results.add(executor.submit(new Callable<String>() {
			public String call() {
				return Network.call();
			}
		}));
		results.add(executor.submit(new Callable<String>() {
			public String call() {
				return Handler.call();
			}
		}));
		results.add(executor.submit(new Callable<String>() {
			public String call() {
				return Client.call();
			}
		}));
		
		while (true) {
			System.out.println("Master checking for completion");
			boolean done = true;
			for (Future<String> result : results) {
				try {
					String value = result.get();
					if (value != Server.SUCCESS) {
						done = false;
					}
				} catch (ExecutionException e) {
					done = false;
				}
			}
			if (done == true) {
				break;
			}
		}
		System.out.println("Master shutting down");
		executor.shutdown();
	}
	
}
