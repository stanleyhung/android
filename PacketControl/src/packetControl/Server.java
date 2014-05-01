package packetControl;

import java.awt.AWTException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server extends Thread {
	private final static int NUM_THREADS = 4;
	private RobotControl robot;
	private LinkedList<Message> requests;
	private ExecutorService executor;
	
	public Server() {
		try {
			robot = new RobotControl();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requests = new LinkedList<Message>();
		executor = Executors.newFixedThreadPool(NUM_THREADS);
	}
	
	private synchronized void addToQueue(Message m) {
		requests.add(m);
	}
	
	private synchronized Message getNext() {
		return requests.poll();
	}
}
