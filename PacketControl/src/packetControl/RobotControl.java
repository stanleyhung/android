package packetControl;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;


//RobotControl handles the lower-level logic for interfacing with VLC Media Player through keystrokes
class RobotControl {
	private Robot robot;
	
	public RobotControl() throws AWTException {
		robot = new Robot();
	}
	/**
	 * @param args
	 */
	
	public int handle(Message m) {
		try {
			robot.keyPress(m.getKey());
			robot.keyRelease(m.getKey());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
