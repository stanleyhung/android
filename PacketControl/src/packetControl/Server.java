package packetControl;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * @author Stanley
 * 
 */
public class Server {
	private final static int NUM_THREADS = 4;
	private MediaPlayer robot;
	static SynchronizedQueue requests; // Queue of Messages representing media player actions
	static ExecutorService executor;
	public final static String SUCCESS = "SUCCESS";
	public final static String FAILURE = "FAILURE";
	
	public Server() {
		try {
			robot = new MediaPlayer();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requests = new SynchronizedQueue();
		executor = Executors.newFixedThreadPool(NUM_THREADS);
	}
	
	public void run() throws IOException {
		//listens for the magic packet that instructs the majority of this java app to run
		ServerSocket server = new ServerSocket(Network.PORT);
		System.out.println("master listening for magic packet");
		Socket s = server.accept();
		BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		reader.readLine();
		server.close();
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		final Server s = new Server();
		
		try {
			s.run();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		ArrayList<Future<String>> results = new ArrayList<Future<String>>();
		final Network n = new Network();
		n.turnOn();
		executor.submit(new Callable<String>() {
			public String call() {
				return n.call();
			}
		});
		results.add(executor.submit(new Callable<String>() {
			public String call() {
				return s.robot.call();
			}
		}));
		results.add(executor.submit(new Callable<String>() {
			public String call() {
				return Handler.call();
			}
		}));
		results.add(executor.submit(new Callable<String>() {
			public String call() {
				return Client.call();
			}
		}));
		
		while (true) {
			System.out.println("Master checking for completion");
			boolean done = true;
			for (Future<String> result : results) {
				try {
					String value = result.get();
					if (!value.equals(Server.SUCCESS)) {
						System.err.println(value);
						done = false;
					}
				} catch (ExecutionException e) {
					e.printStackTrace();
					done = false;
				}
			}
			if (done == true) {
				break;
			}
		}
		n.turnOff();
		System.out.println("Master shutting down");
		executor.shutdown();
	}
	
}
