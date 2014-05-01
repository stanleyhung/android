package packetControl;

import java.util.LinkedList;

public class NetworkQueue {
	private LinkedList<Message> queue;
	
	public NetworkQueue() {
		queue = new LinkedList<Message>();
	}
	
	public synchronized void addToQueue(Message m) {
		queue.add(m);
	}
	
	public synchronized Message getNext() {
		return queue.poll();
	}
}
