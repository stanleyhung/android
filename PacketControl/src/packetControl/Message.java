package packetControl;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

//Class that enables communication between Control and Robot Control
public class Message {
	private String myMessage;
	private int key;
	
	final static String STOP = "stop";
	final static String PLAY = "play";
	final static String PAUSE = "pause";
	final static String NEXT = "next";
	final static String PREVIOUS = "previous";
	
	private static final Map<String, Integer> validCommands;
	static {
		validCommands = new HashMap<String, Integer>();
		validCommands.put(STOP, KeyEvent.VK_S);
		validCommands.put(PLAY, KeyEvent.VK_CLOSE_BRACKET);
		validCommands.put(PAUSE, KeyEvent.VK_OPEN_BRACKET);
		validCommands.put(NEXT, KeyEvent.VK_N);
		validCommands.put(PREVIOUS, KeyEvent.VK_P);
	}
	
	public Message(String message) {
		if(!validCommands.containsKey(myMessage)) {
			throw new IllegalArgumentException();
		}
		myMessage = message;
		key = validCommands.get(message);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
