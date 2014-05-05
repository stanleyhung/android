package packetControl;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;


//RobotControl handles the lower-level logic for interfacing with VLC Media Player through keystrokes
class MediaPlayer implements Runnable{
	private Robot robot;
	private final static int QUIT = 100;
	
	public MediaPlayer() throws AWTException {
		robot = new Robot();
	}
	
	/**
	 * @param args
	 */
	
	public int handle(Message m) {
		try {
			System.out.println("VLC handling message: " + m.getMessage());
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
		if (m.getMessage().equals(Message.QUIT)) {
			return QUIT;
		}
		return 0;
	}
	
	
	public static void main(String[] args) throws AWTException {		
		MediaPlayer player = new MediaPlayer();
		Message m = new Message(Message.QUIT);
		Server.requests.addToQueue(m);
		player.run();
	}
	
	public String call() {
		String[] cmd = {"\"C:/Program Files (x86)/VideoLAN/VLC/vlc.exe\"", "C:\\Users\\Stanley\\Documents\\Music\\"};
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			//Thread.sleep(5000);
			while (true) {
				if (Server.requests == null) {
					break;
				}
				Message m = (Message) Server.requests.getNext();
				if (m != null) {
					int result = handle(m);
					if (result < 0) {
						System.err.println("error - mediaplayer could not handle message");
						break;
					} else if (result == QUIT) {
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
			return Server.FAILURE;
		}
		return Server.SUCCESS;
	}
	
	@Override
	public void run() {
		String[] cmd = {"\"C:/Program Files (x86)/VideoLAN/VLC/vlc.exe\"", "C:\\Users\\Stanley\\Documents\\Music\\"};
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			Thread.sleep(5000);
			while (true) {
				if (Server.requests == null) {
					break;
				}
				Message m = (Message) Server.requests.getNext();
				if (m != null) {
					int result = handle(m);
					if (result < 0) {
						System.err.println("error - mediaplayer could not handle message");
						break;
					} else if (result == QUIT) {
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
