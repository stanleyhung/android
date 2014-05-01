package packetControl;

import java.awt.AWTException;
import java.util.LinkedList;


public class Server extends Thread {
	private RobotControl robot;
	private LinkedList<Message> requests;
	
	public void run() {
		try {
			robot = new RobotControl();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		requests = new LinkedList<Message>();
	}
	
	private synchronized void addToQueue(Message m) {
		requests.add(m);
	}
	
	private synchronized Message getNext() {
		return requests.poll();
	}
}
