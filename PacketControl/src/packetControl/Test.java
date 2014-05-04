package packetControl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//purely for testing purposes
		ExecutorService network = Executors.newFixedThreadPool(1);
		network.execute(new Runnable() {
			public void run() {
				Network.main(null);
			}
		});
		ExecutorService handler = Executors.newFixedThreadPool(1);
		handler.execute(new Runnable() {
			public void run() {
				Handler.main(null);
			}
		});
		ExecutorService client = Executors.newFixedThreadPool(1);
		client.execute(new Runnable() {
			public void run() {
				Client.main(null);
			}
		});
		
		network.shutdown();
		handler.shutdown();
		client.shutdown();

	}

}
