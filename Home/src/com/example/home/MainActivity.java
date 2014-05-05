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
	private TextView stopButton;
	private TextView playButton;
	private TextView nextButton;
	private TextView previousButton;
	private TextView quitButton;
	private Socket connection;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wakeButton = (TextView) findViewById(R.id.myButton);
        startRemoteButton = (TextView) findViewById(R.id.startButton);
        stopButton = (TextView) findViewById(R.id.stopButton);
        playButton = (TextView) findViewById(R.id.playButton);
        nextButton = (TextView) findViewById(R.id.nextButton);
        previousButton = (TextView) findViewById(R.id.previousButton);
        quitButton = (TextView) findViewById(R.id.quitButton);
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
    
    public void handleStop(View view) {
    	sendMessage(Message.STOP);
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
    
    private class ExecuteMediaControl extends AsyncTask<String, Void, Output> {

		@Override
		protected Output doInBackground(String... cmd) {
			//send the cmd message to the remote computer
			InetAddress temp;
			try {
				temp = InetAddress.getByName("10.10.10.69"); //TODO: find some dynamic way to get IP addr
				connection = new Socket(temp, PORT);
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
			} else if(buttonName.equals(Message.STOP)) {
				correctButton = stopButton;
			} else if(buttonName.equals(Message.PLAY)) {
				correctButton = playButton;
			} else if(buttonName.equals(Message.NEXT)) {
				correctButton = nextButton;
			} else if(buttonName.equals(Message.PREVIOUS)) {
				correctButton = previousButton;
			} else if(buttonName.equals(Message.QUIT)) {
				correctButton = quitButton;
			} else {
				startRemoteButton.setText("FAILURE - FATAL, BAD BAD BAD");
			}
			if (result.getSuccess()) {
				correctButton.setText("Sending packet...");
			} else {
				correctButton.setText("FAILURE - Could not send packet");
			}
			
			//additional work needed for special cases
			if (result.getSuccess() && buttonName.equals(Message.MAGIC)) {
				//start magic packet -> need to initialize all other buttons
				stopButton.setText("Stop");
				playButton.setText("Play");
				nextButton.setText("Next");
				previousButton.setText("Previous");
				quitButton.setText("Quit");
			} 
			if (result.getSuccess() && buttonName.equals(Message.QUIT)) {
				//quit -> need to reset all other buttons
				stopButton.setText("Turn On VLC To Show Command");
				playButton.setText("Turn On VLC To Show Command");
				nextButton.setText("Turn On VLC To Show Command");
				previousButton.setText("Turn On VLC To Show Command");
				startRemoteButton.setText("Start VLC On Computer");
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
    	final static String STOP = "stop";
    	final static String PLAY = "play";
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
