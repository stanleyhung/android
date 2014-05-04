package packetControl;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;


//RobotControl handles the lower-level logic for interfacing with VLC Media Player through keystrokes
class MediaPlayer implements Runnable{
	private Robot robot;
	
	public MediaPlayer() throws AWTException {
		robot = new Robot();
	}
	
	/**
	 * @param args
	 */
	
	public int handle(Message m) {
		try {
			for (int key : m.getKeys()) {
				robot.keyPress(key);
			}
			for (int key : m.getKeys()) {
				robot.keyRelease(key);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	
	public static void main(String[] args) throws AWTException {		
		MediaPlayer player = new MediaPlayer();
		Message m = new Message(Message.QUIT);
		Server.requests.addToQueue(m);
		player.run();
	}
	@Override
	public void run() {
		String fileName = "C:/Users/Stanley/Documents/Music/f2.wav";
		String[] cmd = {"\"C:/Program Files (x86)/VideoLAN/VLC/vlc.exe\"", fileName};
		try {
			System.out.println("fileName is: ");
			System.out.println(fileName);
			Process p = Runtime.getRuntime().exec(cmd);
			Thread.sleep(5000);
			while (true) {
				if (Server.requests == null) {
					break;
				}
				Message m = (Message) Server.requests.getNext();
				if (m != null) {
					if (handle(m) != 0) {
						System.err.println("error - mediaplayer could not handle message");
						break;
					}
				} else {
					continue;
				}
			}
			System.out.println("Done");
			p.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
