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
	private RobotControl robot;
	private NetworkQueue requests;
	private ExecutorService executor;
	private Network network;
	
	
	public Server() {
		try {
			robot = new RobotControl();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requests = new NetworkQueue();
		executor = Executors.newFixedThreadPool(NUM_THREADS);
	}
	
	public void run() {
		
	}
	
	public static void main(String[] args) {
		Server s = new Server();
		s.run();
	}
	
}
