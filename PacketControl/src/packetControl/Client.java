package packetControl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	@Override
	public void run() {
		//Test the Network and Handler classes by simulating a client
		Socket s;
		try {
			Thread.sleep(3000);
			s = new Socket(InetAddress.getLocalHost(), Network.PORT);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.write(new String(Message.PLAY), 0, Message.PLAY.length());
			writer.newLine();
			writer.flush();
			System.out.println("client successfully sent request");
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			System.out.println("client waiting for server response");
			if (reader.readLine() == Handler.SUCCESS) {
				System.out.println("Client success");
				s.close();
			} else {
				System.err.println("Client did not get success message");
				s.close();
			}
		} catch (IOException | InterruptedException e) {
			System.err.println("Client error");
			e.printStackTrace();
			return;
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client c = new Client();
		System.out.println("running client...");
		c.run();
	}

}
