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
			s = new Socket(InetAddress.getLocalHost(), Network.PORT);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.write(new String(Message.PLAY), 0, Message.PLAY.length());
			writer.newLine();
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			if (reader.readLine() == Handler.SUCCESS) {
				s.close();
			} else {
				System.err.println("Client did not get success message");
				s.close();
			}
		} catch (IOException e) {
			System.err.println("Client error");
			e.printStackTrace();
			return;
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
