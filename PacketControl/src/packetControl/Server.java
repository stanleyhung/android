package packetControl;

import java.awt.AWTException;
import java.util.LinkedList;
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
	private Network network;
	
	
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
	
	public static void main(String[] args) {
		Server s = new Server();
		s.run();
	}
	
}
