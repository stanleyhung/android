package packetControl;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.ForkJoinPool;

public class Control {
	private RobotControl robot;
	private Server server;
	
	public Control() {
		try {
			robot = new RobotControl();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			System.err.println("Could not initialize Control Robot");
			e.printStackTrace();
		}
	}
	
	public void start() throws InterruptedException {
		server.start();
		server.join();
	}
	
	/**w
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Control c = new Control();
		c.start();
	}

}
