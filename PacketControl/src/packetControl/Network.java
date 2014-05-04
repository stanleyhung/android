/**
 * 
 */
package packetControl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Semaphore;

/**
 * @author Stanley
 *
 */
public class Network implements Runnable {
	
	private final static int PORT = 9;
	private ServerSocket server;
	static SynchronizedQueue clients; //Queue of sockets representing client connections
	private boolean status;
	private final static int TIMEOUT = 3000;
	static Semaphore sem;
	
	public Network() throws IOException {
		server = new ServerSocket(PORT);
		server.setSoTimeout(TIMEOUT);
		clients = new SynchronizedQueue();
		status = false;
		sem = new Semaphore(0);
	}
	
	//turns the server on and returns true on success, false if the server was already on
	public synchronized boolean turnOn() {
		if (status == true) {
			return false;
		}
		status = true;
		return true;
	}
	
	//turns the server off and returns true on success, false if the server was already off
	public synchronized boolean turnOff() {
		if (status == false) {
			return false;
		}
		status = false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * The Network thread accepts client connections and adds them to a queue that is shared by Handler threads.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				if (status == true) {
					Socket s = server.accept(); //timeouts after TIMEOUT milliseconds
					clients.addToQueue(s);
					sem.release();
				}
			} catch (SocketTimeoutException e) {
				//falls through
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		Network n;
		try {
			n = new Network();
			n.run();
		} catch (IOException e) {
			System.err.println("Network error");
			e.printStackTrace();
		}
	}

}
