/**
 * 
 */
package packetControl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Semaphore;

/**
 * @author Stanley
 *
 */
public class Network implements Runnable {
	
	public final static int PORT = 9;
	private ServerSocket server;
	static SynchronizedQueue clients; //Queue of sockets representing client connections
	private boolean status;
	private final static int TIMEOUT = 3000;
	final static Semaphore sem = new Semaphore(0);
	
	public Network() throws IOException {
		server = new ServerSocket();
		server.bind(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
		server.setSoTimeout(TIMEOUT);
		clients = new SynchronizedQueue();
		status = false;
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
	
	public String call() {
		while (true) {
			try {
				if (status == true) {
					//System.out.println("network server listening for clients");
					Socket s = server.accept(); //timeouts after TIMEOUT milliseconds
					System.out.println("network server received a client connection");
					clients.addToQueue(s);
					System.out.println("releasing semaphore");
					sem.release();
				} else {
					return Server.SUCCESS;
				}
			} catch (SocketTimeoutException e) {
				//System.out.println("network server timed-out");
				//falls through
			} catch (IOException e) {
				e.printStackTrace();
				return Server.FAILURE;
			}
		}
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
					//System.out.println("network server listening for clients");
					Socket s = server.accept(); //timeouts after TIMEOUT milliseconds
					System.out.println("network server received client connection");
					clients.addToQueue(s);
					System.out.println("releasing semaphore");
					sem.release();
				} else {
					return;
				}
			} catch (SocketTimeoutException e) {
				//System.out.println("network server timed-out");
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
			System.out.println("running network server...");
			n.turnOn();
			n.run();
		} catch (IOException e) {
			System.err.println("Network error");
			e.printStackTrace();
		}
	}

}
