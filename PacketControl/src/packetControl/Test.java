package packetControl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//purely for testing purposes
		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {
			public void run() {
				Network.main(null);
			}
		});
		executor.execute(new Runnable() {
			public void run() {
				Handler.main(null);
			}
		});
		executor.execute(new Runnable() {
			public void run() {
				Client.main(null);
			}
		});
		
		executor.shutdown();

	}

}
