package com.example.home;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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

    //TODO: can I make these hard-coded string dynamically assigned?
	private final static String macAddress = "00.1D.60.88.57.46";
	//private final static String macAddress = "7C.E9.D3.20.78.5E";
	private final static int PORT = 9; //Wake-On Lan port
	public final static String LOG_TAG = "HOME";
	private final static String HOME = "\"Stanley\"";
	private final static int TIMEOUT = 100;
	private static String TEMP = "192.168.1.127";
	
	private TextView wakeButton;
	private TextView startRemoteButton;
	private TextView playButton;
	private TextView nextButton;
	private TextView previousButton;
	private TextView quitButton;
	private TextView randomButton;
	private static InetAddress remoteComputer;
	
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

    /**
     * Tries to search for the network address of the macAddress we have for the computer we want to use.
     */
    private void findMacOnNetwork() {
        if (remoteComputer != null) {
            return;
        }
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	if (networkInfo == null ||  !networkInfo.isConnected()) {
    		return;
    	}
    	WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    	int networkAddr = wm.getConnectionInfo().getIpAddress();
    	LinkedList<InetAddress> devices = scanNetwork(networkAddr);
        remoteComputer = getMacFromArpCache(devices);
    }

    /**
     * Modified version of http://www.flattermann.net/2011/02/android-howto-find-the-hardware-mac-address-of-a-remote-host/
     *
     * Try to extract a hardware MAC address from a given IP address using the
     * ARP cache (/proc/net/arp).<br>
     * <br>
     * We assume that the file has this structure:<br>
     * <br>
     * IP address       HW type     Flags       HW address            Mask     Device
     * 192.168.18.11    0x1         0x2         00:04:20:06:55:1a     *        eth0
     * 192.168.18.36    0x1         0x2         00:22:43:ab:2a:5b     *        eth0
     *
     * @return the ipAddress corresponding to the macAddress we are looking for in the ARP cache
     */
    public static InetAddress getMacFromArpCache(LinkedList<InetAddress> addresses) {
        // Convert all the addresses we are interested in to Strings
        HashMap<String, InetAddress> strAddresses = new HashMap<String, InetAddress>();
        for (InetAddress addr : addresses) {
            String ip = addr.getHostAddress();
            strAddresses.put(ip, addr);
        }
        // No interesting addresses
        if (strAddresses.size() == 0) {
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    for (String ip : strAddresses.keySet()) {
                        if (ip.equals(splitted[0])) {
                            // Basic sanity check
                            String mac = splitted[3];
                            if (mac.matches(macAddress.replace('.', ':'))) {
                                return strAddresses.get(ip);
                            } else {
                                return null;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(MainActivity.LOG_TAG, "Error - while reading from arp cache: " + e.toString());
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                // fall through
            }
        }
        return null;
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
				if (deviceAddr.isReachable(TIMEOUT)) {
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
			findMacOnNetwork();
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

    /**
     * A class to asynchronously execute sending a magic wake packet to the remote computer.
     */
    private class ExecuteWake extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
            // Create the magic packet using the macAddress of the computer we're looking for.
			byte[] macAddressBytes = getAddressBytes(macAddress, 16);
			byte[] bytes = new byte[6 + 16*macAddressBytes.length];

			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) 0xff;
			}

			for (int i = 6; i < bytes.length; i++) {
				bytes[i] = macAddressBytes[i % 6];
			}

            findMacOnNetwork();

			try {
				InetAddress ipAddress = remoteComputer;
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
