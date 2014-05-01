package packetControl;

import java.util.HashMap;
import java.util.Map;

//Class that enables communication between Control and Robot Control
public class Message {
	public String myMessage;
	
	final static String STOP = "stop";
	final static String PLAY = "play";
	final static String PAUSE = "pause";
	final static String NEXT = "next";
	final static String PREVIOUS = "previous";
	
	private static final Map<String, Boolean> validCommands;
	static {
		validCommands = new HashMap<String, Boolean>();
		validCommands.put(STOP, true);
		validCommands.put(PLAY, true);
		validCommands.put(PAUSE, true);
		validCommands.put(NEXT, true);
		validCommands.put(PREVIOUS, true);
	}
	
	public Message(String message) {
		if(!validCommands.containsKey(myMessage)) {
			throw new IllegalArgumentException();
		}
		myMessage = message;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
