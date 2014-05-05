package packetControl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Handler implements Runnable {

	final static String SUCCESS = "success";
	
	public static String call() {
		while (true) {
			try {
				if (Network.sem == null) {
					//System.out.println("Network.sem is null");
					continue;
				}
				//System.out.println("handler waiting for server to add socket to queue");
				Network.sem.acquire();
				//System.out.println("acquired semaphore");
			} catch (InterruptedException e) {
				System.err.println("handler error");
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
				System.out.println("handler processed message: " + command);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				writer.write(Handler.SUCCESS, 0, Handler.SUCCESS.length());
				writer.newLine();
				writer.flush();
				s.close();
				if (m.getMessage().equals(Message.QUIT)) {
					return Server.SUCCESS;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return Server.FAILURE;
			}
		}
		return Server.FAILURE;
	}
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
					//System.out.println("Network.sem is null");
					continue;
				}
				System.out.println("handler waiting for server to add socket to queue");
				Network.sem.acquire();
				System.out.println("acquired semaphore");
			} catch (InterruptedException e) {
				System.err.println("handler error");
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
				writer.flush();
				s.close();
				if (m.getMessage().equals(Message.QUIT)) {
					return;
				}
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
