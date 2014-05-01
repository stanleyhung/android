package packetControl;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;


//RobotControl handles the lower-level logic for interfacing with VLC Media Player through keystrokes
class MediaPlayer implements Runnable{
	private Robot robot;
	private NetworkQueue queue;
	
	//for testing purposes only
	public MediaPlayer() throws AWTException {
		robot = new Robot();
	}
	
	public MediaPlayer(NetworkQueue queue) throws AWTException {
		robot = new Robot();
		this.queue = queue;
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
	
	
	public static void main(String[] args) throws AWTException {
		// TODO Auto-generated method stub
		MediaPlayer player = new MediaPlayer();
		player.run();
	}
	@Override
	public void run() {
		String fileName = "C:/Users/Stanley/Documents/Music/f2.wav";
		String[] cmd = {"\"C:/Program Files (x86)/VideoLAN/VLC/vlc.exe\"", fileName};
		try {
			System.out.println("fileName is: ");
			System.out.print(fileName);
			Process p = Runtime.getRuntime().exec(cmd);
			while (true) {
				if (queue == null) {
					break;
				}
				Message m = queue.getNext();
				if (m != null) {
					
				}
			}
			p.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
