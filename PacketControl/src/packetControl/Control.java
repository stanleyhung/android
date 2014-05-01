package packetControl;

import java.awt.AWTException;
import java.awt.Robot;

public class Control {
	private Robot robot;
	
	public Control() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			System.err.println("Could not initialize Control Robot");
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
