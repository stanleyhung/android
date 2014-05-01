package packetControl;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

//Class that enables communication between Control and Robot Control
public class Message {
	private String myMessage;
	private int[] key;
	
	final static String STOP = "stop";
	final static String PLAY = "play";
	final static String PAUSE = "pause";
	final static String NEXT = "next";
	final static String PREVIOUS = "previous";
	final static String QUIT = "quit";
	
	private static final Map<String, int[]> validCommands;
	static {
		validCommands = new HashMap<String, int[]>();
		validCommands.put(STOP, new int[] {KeyEvent.VK_S});
		validCommands.put(PLAY, new int[] {KeyEvent.VK_CLOSE_BRACKET});
		validCommands.put(PAUSE, new int[] {KeyEvent.VK_OPEN_BRACKET});
		validCommands.put(NEXT, new int[] {KeyEvent.VK_N});
		validCommands.put(PREVIOUS, new int[] {KeyEvent.VK_P});
		validCommands.put(QUIT, new int[] {KeyEvent.VK_CONTROL, KeyEvent.VK_Q});
	}
	
	public Message(String message) {
		if(!validCommands.containsKey(message)) {
			throw new IllegalArgumentException();
		}
		myMessage = message;
		key = validCommands.get(message);
	}
	
	public int[] getKeys() {
		return key;
	}
	
	public String getMessage() {
		return myMessage;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
