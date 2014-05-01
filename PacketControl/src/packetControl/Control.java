package packetControl;

import java.awt.AWTException;

public class Control {
	private Server server;
	
	public Control() {
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
