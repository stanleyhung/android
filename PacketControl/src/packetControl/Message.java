package packetControl;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

//Class that enables communication between Control and Robot Control
public class Message {
	private String myMessage;
	private int[] key;
	
	final static String PLAY = "play";
	final static String NEXT = "next";
	final static String PREVIOUS = "previous";
	final static String QUIT = "quit";
	final static String RANDOM = "random";
	
	private static final Map<String, int[]> validCommands;
	static {
		validCommands = new HashMap<String, int[]>();
		validCommands.put(PLAY, new int[] {KeyEvent.VK_SPACE});
		validCommands.put(NEXT, new int[] {KeyEvent.VK_N});
		validCommands.put(PREVIOUS, new int[] {KeyEvent.VK_P});
		validCommands.put(QUIT, new int[] {KeyEvent.VK_CONTROL, KeyEvent.VK_Q});
		validCommands.put(RANDOM, new int[] {KeyEvent.VK_R});
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
	
	public static boolean isValidCommand(String command) {
		return validCommands.containsKey(command);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
