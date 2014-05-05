package com.example.home;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wakeButton = (TextView) findViewById(R.id.myButton);
        startRemoteButton = (TextView) findViewById(R.id.startButton);
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
    
    /* Called when the user clicks to start remote button */
    public void handleStartRemote(View view) {
    	/*
    	if(!checkConnectivity()) {
    		return;
    	}
    	*/
    	ExecuteStartRemote er = new ExecuteStartRemote();
    	er.execute();
    }
    
    private class ExecuteStartRemote extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			InetAddress temp;
			try {
				temp = InetAddress.getByName("10.10.10.69");
				Socket s = new Socket(temp, PORT);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				writer.write(new String(Message.MAGIC), 0, Message.MAGIC.length());
				writer.newLine();
				writer.flush();
			} catch (UnknownHostException e) {
				Log.e(MainActivity.LOG_TAG, "Error - Unknown host");
				return false;
			} catch (IOException e) {
				Log.e(MainActivity.LOG_TAG, "Error - IO Exception");
				return false;
			}
			return true;
		}
		
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				startRemoteButton.setText("Success!");
			} else {
				startRemoteButton.setText("FAILURE - Could not send packet");
			}
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
    	final static String STOP = "stop";
    	final static String PLAY = "play";
    	final static String PAUSE = "pause";
    	final static String NEXT = "next";
    	final static String PREVIOUS = "previous";
    	final static String QUIT = "quit";
    	final static String MAGIC = "Turn On";
    	
    	private String myMessage;
    	
    	public Message(String message) {
    		myMessage = message;
    	}
    	
    	public String getMessage() {
    		return myMessage;
    	}

    }
    
}
