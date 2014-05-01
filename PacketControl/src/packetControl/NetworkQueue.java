package packetControl;

import java.util.LinkedList;

public class NetworkQueue {
	private LinkedList<Message> queue;
	
	public NetworkQueue() {
		
	}
	
	public synchronized void addToQueue(Message m) {
		queue.add(m);
	}
	
	public synchronized Message getNext(Message m) {
		return queue.poll();
	}
}
