package packetControl;

public class Handler implements Runnable {

	private final static int DELAY = 500;
	@Override
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * The Handler thread parses client requests from sockets in Network.clients
	 */
	public void run() {
		while (true) {
			Object o = Network.clients.getNext();
			if (o != null) {
				
			}
		}

	}

}
