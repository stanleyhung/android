package com.example.home;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final static String macAddress = "00.1D.60.88.57.46";
	private final static String networkAddress = "192.168.1.255";
	private final static int PORT = 9; //Wake-On Lan port
	public final static String LOG_TAG = "HOME";
	private final static String HOME = "\"Stanley\"";
	
	private TextView wakeButton;
	private TextView startRemoteButton;
	private TextView playButton;
	private TextView nextButton;
	private TextView previousButton;
	private TextView quitButton;
	private TextView randomButton;
	private InetAddress remoteComputer;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wakeButton = (TextView) findViewById(R.id.myButton);
        startRemoteButton = (TextView) findViewById(R.id.startButton);
        playButton = (TextView) findViewById(R.id.playButton);
        nextButton = (TextView) findViewById(R.id.nextButton);
        previousButton = (TextView) findViewById(R.id.previousButton);
        quitButton = (TextView) findViewById(R.id.quitButton);
        randomButton = (TextView) findViewById(R.id.randomButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private static byte[] getAddressBytes (String address, int radix) throws IllegalArgumentException {
		String[] string = address.split("\\.");
		byte[] bytes = new byte[string.length];

		for (int i = 0; i < string.length; i++) {
			bytes[i] = (byte) Integer.parseInt(string[i], radix);
		}

		return bytes;

	}
    
    private boolean checkConnectivity() {
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	if (networkInfo != null && networkInfo.isConnected()) {
    		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    		String ssid = wm.getConnectionInfo().getSSID();
    		if (!ssid.equals(HOME)) {
    			Log.e(MainActivity.LOG_TAG, "SSID should be: " + HOME + ", but is: " + ssid + ".");
    			return false;
    		}
    		return true;
    	}
    	return false;
    }
    
    /** Called when the user clicks the wake button */
    public void handleWake(View view) {
    	if(!checkConnectivity()) {
    		wakeButton.setText("Failure - Not Connected to 'Stanley' network");
    		return;
    	}
    	ExecuteWake ew = new ExecuteWake();
    	ew.execute();
    }
    
    /*
     *  The following handle*** methods are called when the appropriate button is clicked
     */    
    
    public void handleStartRemote(View view) {
    	sendMessage(Message.MAGIC);
    }
    
    public void handlePlay(View view) {
    	sendMessage(Message.PLAY);
    }
    
    public void handleNext(View view) {
    	sendMessage(Message.NEXT);
    }
    
    public void handlePrevious(View view) {
    	sendMessage(Message.PREVIOUS);
    }
    
    public void handleQuit(View view) {
    	sendMessage(Message.QUIT);
    }
    
    public void handleRandom(View view) {
    	sendMessage(Message.RANDOM);
    }
    
    //initializes the logic to send a packet to the computer
    public void sendMessage(String message) {
    	/*
    	if(!checkConnectivity()) {
    		return;
    	}
    	*/
    	ExecuteMediaControl er = new ExecuteMediaControl();
    	er.execute(message);
    }
    
    private class Output {
    	private String type;
    	private boolean success;
    	
    	public Output(String type, boolean success) {
    		this.type = type;
    		this.success = success;
    	}
    	
    	public String getType() {
    		return type;
    	}
    	
    	public boolean getSuccess() {
    		return success;
    	}
    }
    
    private InetAddress findMacOnNetwork() {
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	if (networkInfo == null ||  !networkInfo.isConnected()) {
    		return null;
    	}
    	WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    	int networkAddr = wm.getConnectionInfo().getIpAddress();
    	LinkedList<InetAddress> devices = scanNetwork(networkAddr);
    	for (InetAddress addr : devices) {
    		if(checkCorrectAddress(macAddress, addr)) {
    			return addr;
    		}
    	}
    	return null;
    }
    
    //checks to see if ipAddr is assigned to device with macAddr
    private boolean checkCorrectAddress(String macAddr, InetAddress ipAddr) {
    	try {
    		NetworkInterface device = NetworkInterface.getByInetAddress(ipAddr);
			byte[] addr = device.getHardwareAddress();
			String addrStr = String.format("%x%x.%x%x.%x%x.%x%x.%x%x.%x%x",
					(addr[0] & 0xf),
					(addr[0] >> 4 & 0xf),
					(addr[1] & 0xf),
					(addr[1] >> 4 & 0xf),
					(addr[2] & 0xf),
					(addr[2] >> 4 & 0xf),
					(addr[3] & 0xf),
					(addr[3] >> 4 & 0xf),
					(addr[4] & 0xf),
					(addr[4] >> 4 & 0xf),
					(addr[5] & 0xf),
					(addr[5] >> 4 & 0xf),
					(addr[6] & 0xf),
					(addr[6] >> 4 & 0xf));
			if (addrStr.equals(macAddr)) {
				return true;
			}
			Log.d(MainActivity.LOG_TAG, "Error - macAddress mismatch: " + addrStr);
			return false;
		} catch (SocketException e) {
			Log.e(MainActivity.LOG_TAG, "Error - SocketException in checkCorrectAddress");
			return false;
		}
    }
    
    //scan a networkAddr for a list of all reachable ipAddresses on that network
    private LinkedList<InetAddress> scanNetwork(int networkAddr) {
    	LinkedList<InetAddress> output = new LinkedList<InetAddress>();
    	for (int i = 0; i < 255; i++) {
    		String deviceAddrStr = String.format("%d.%d.%d.%d",
    				(networkAddr & 0xff),
    				(networkAddr >> 8 & 0xff),
    				(networkAddr >> 16 & 0xff),
    				i);
    		InetAddress deviceAddr;
			try {
				deviceAddr = InetAddress.getByName(deviceAddrStr);
				if (deviceAddr.isReachable(400)) {
					Log.d(MainActivity.LOG_TAG, "Debug - Found device: " + deviceAddr.toString());
	    			output.add(deviceAddr);
	    		}
			} catch (UnknownHostException e) {
				//falls through
			} catch (IOException e) {
				//falls through
			}
    	}
    	return output;
    }
    
    private class ExecuteMediaControl extends AsyncTask<String, Void, Output> {

		@Override
		protected Output doInBackground(String... cmd) {
			//send the cmd message to the remote computer
			try {
				if (remoteComputer == null) {
					Log.e(MainActivity.LOG_TAG, "Error - Could not find remoteComputer on Network");
					return new Output(cmd[0], false);
				}
				Socket connection = new Socket(remoteComputer, PORT);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
				writer.write(cmd[0], 0, cmd[0].length());
				writer.newLine();
				writer.flush();
				connection.close();
			} catch (UnknownHostException e) {
				Log.e(MainActivity.LOG_TAG, "Error - Unknown host");
				return new Output(cmd[0], false);
			} catch (IOException e) {
				Log.e(MainActivity.LOG_TAG, "Error - IO Exception");
				Log.e(MainActivity.LOG_TAG, e.getMessage());
				return new Output(cmd[0], false);
			}
			return new Output(cmd[0], true);
		}
		
		protected void onPostExecute(Output result) {
			TextView correctButton = null;
			String buttonName = result.getType();
			if (buttonName.equals(Message.MAGIC)) {
				correctButton = startRemoteButton;
			} else if(buttonName.equals(Message.PLAY)) {
				correctButton = playButton;
			} else if(buttonName.equals(Message.NEXT)) {
				correctButton = nextButton;
			} else if(buttonName.equals(Message.PREVIOUS)) {
				correctButton = previousButton;
			} else if(buttonName.equals(Message.QUIT)) {
				correctButton = quitButton;
			} else if (buttonName.equals(Message.RANDOM)) {
				correctButton = randomButton;
			} else {
				startRemoteButton.setText("FAILURE - FATAL, BAD BAD BAD");
			}
			if (!result.getSuccess()) {
				correctButton.setText("FAILURE - Could not send packet");
			}			
			//reset button's text after 3 seconds 
			final TextView button = correctButton;
			final String buttonText = buttonName;
			Handler h = new Handler();
    		h.postDelayed(new Runnable() { 
    	         public void run() { 
    	        	 button.setText(buttonText); 
    	         } 
    	    }, 3000);
		}
    	
    }
    
    private class ExecuteWake extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			byte[] macAddressBytes = getAddressBytes(macAddress, 16);
			byte[] bytes = new byte[6 + 16*macAddressBytes.length];

			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) 0xff;
			}

			for (int i = 6; i < bytes.length; i++) {
				bytes[i] = macAddressBytes[i % 6];
			}

			byte[] network = getAddressBytes(networkAddress, 10);
			try {
				InetAddress ipAddress = InetAddress.getByAddress(network);
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length, ipAddress,80);
				DatagramSocket socket = new DatagramSocket();
				socket.send(packet);
				socket.close();
			} catch(UnknownHostException e) {
				Log.e(MainActivity.LOG_TAG, "Error - Unknown host");
				return false;
			} catch(SocketException e) {
				Log.e(MainActivity.LOG_TAG, "Error - Socket Exception");
				Log.e(MainActivity.LOG_TAG, e.getMessage());
				return false;
			} catch(IOException e) {
				Log.e(MainActivity.LOG_TAG, "Error - IO Exception");
				return false;
			}
			return true;
		}
		
		// onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean result) {
        	if (result == true) {
        		wakeButton.setText("Success!");
        		Handler h = new Handler();
        		h.postDelayed(new Runnable() { 
        	         public void run() { 
        	        	 wakeButton.setText("Turn On Computer"); 
        	         } 
        	    }, 3000); 
        	} else {
        		wakeButton.setText("FAILURE - Could not send packet");
        	}
            
       }

    }
    
    //Class for communication with remote computer
    private class Message {
    	final static String PLAY = "play";
    	final static String NEXT = "next";
    	final static String PREVIOUS = "previous";
    	final static String QUIT = "quit";
    	final static String MAGIC = "Turn On";
    	final static String RANDOM = "random";
    	
    	private String myMessage;
    	
    	public Message(String message) {
    		myMessage = message;
    	}
    	
    	public String getMessage() {
    		return myMessage;
    	}

    }
    
}
