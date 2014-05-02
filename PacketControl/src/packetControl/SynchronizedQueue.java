package packetControl;

import java.util.LinkedList;

public class SynchronizedQueue {
	private LinkedList<Object> queue;
	
	public SynchronizedQueue() {
		queue = new LinkedList<Object>();
	}
	
	public synchronized void addToQueue(Object m) {
		queue.add(m);
	}
	
	public synchronized Object getNext() {
		return queue.poll();
	}
}
