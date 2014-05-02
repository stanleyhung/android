/**
 * 
 */
package packetControl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author Stanley
 *
 */
public class Network implements Runnable {
	
	private final static int PORT = 9;
	private ServerSocket server;
	LinkedList<Socket> clients;
	private boolean status;
	
	public Network() throws IOException {
		server = new ServerSocket(PORT);
		clients = new LinkedList<Socket>();
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
		
	}

}
