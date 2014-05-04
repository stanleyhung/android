package packetControl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Handler implements Runnable {

	private final static int DELAY = 500;
	final static String SUCCESS = "success";
	@Override
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * The Handler thread parses client requests from sockets in Network.clients
	 */
	public void run() {
		while (true) {
			try {
				if (Network.sem == null) {
					continue;
				}
				System.out.println("handler waiting for server to add socket to queue");
				Network.sem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			Object o = Network.clients.getNext();
			if (o == null) {
				System.err.println("Fatal error - not object in queue after semaphore acquired by handler");
				break;
			}
			Socket s = (Socket) o;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String command = reader.readLine();
				if (Message.isValidCommand(command) != true) {
					break;
				}
				Message m = new Message(command);
				Server.requests.addToQueue(m);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				writer.write(Handler.SUCCESS, 0, Handler.SUCCESS.length());
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		Handler h = new Handler();
		System.out.println("running handler...");
		h.run();
	}

}
