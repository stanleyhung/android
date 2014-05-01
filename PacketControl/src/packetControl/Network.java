/**
 * 
 */
package packetControl;

/**
 * @author Stanley
 *
 */
public class Network implements Runnable {
	private NetworkQueue queue;
	
	public Network(NetworkQueue queue) {
		this.queue = queue;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
