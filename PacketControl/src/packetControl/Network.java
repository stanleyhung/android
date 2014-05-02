/**
 * 
 */
package packetControl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

/**
 * @author Stanley
 *
 */
public class Network implements Runnable {
	
	private final static int PORT = 9;
	private ServerSocket server;
	SynchronizedQueue clients;
	private boolean status;
	private final static int TIMEOUT = 3000;
	
	public Network() throws IOException {
		server = new ServerSocket(PORT);
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
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Socket s = server.accept();
				clients.addToQueue(s);
			} catch (SocketTimeoutException e) {
				//falls through
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
