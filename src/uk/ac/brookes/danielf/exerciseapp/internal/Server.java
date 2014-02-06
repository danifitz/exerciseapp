package uk.ac.brookes.danielf.exerciseapp.internal;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

/**
 * This class handles connecting to the remote server, submitting and fetching
 * XML documents.
 * 
 * @author danfitzgerald
 * 
 */
public class Server extends AsyncTask<Pair<String, String>, Void, String> {

	public static final int port = 44365;
	public static final String ip = "161.73.245.41";

	public static final String SEARCH = "SEARCH";
	public static final String RETRIEVE = "RETRIEVE";
	public static final String STORE = "STORE";

	@Override
	protected String doInBackground(Pair<String, String>... params) {

		// which method to call and return result
		String result = null;
		if (params[0].first.equals(SEARCH))
			result = searchRoute(params[0].second);
		else if (params[0].first.equals(RETRIEVE))
			result = retrieveRoute(params[0].second);
		else if (params[0].first.equals(STORE))
			result = storeRoute(params[0].second);

		return result;
	}

	private String storeRoute(String xmlDoc) {
		Log.d("Server:storeRoute()", "submitting route to server");
		Socket socket = null;
		DataOutputStream out1 = null;

		// try to open a socket on port
		try {
			socket = new Socket(ip, port);
			out1 = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			Log.d("Server:storeRoute()", "Don't know about host: " + ip);
		} catch (IOException e) {
			Log.d("Server:storeRoute()",
					"Couldn't get I/O for the connection to: " + ip);
		}

		if (socket != null && out1 != null && xmlDoc != null) {
			try {
				byte[] buf = ("STORE " + xmlDoc).getBytes("UTF-8");
				out1.write(buf, 0, buf.length);
				out1.flush();
				out1.close();
				socket.close();
			} catch (IOException e) {
				Log.d("Server", "IOException: " + e);
			}
		}
		return String.valueOf(true);
	}

	private String retrieveRoute(String id) {
		Log.d("Server:retrieveRoute()", "retrieving route: " + id);
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in = null;

		String xmlDoc = null;

		// try to open a socket on port
		try {
			socket = new Socket(ip, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			Log.d("Server:storeRoute()", "Don't know about host: " + ip);
		} catch (IOException e) {
			Log.d("Server:storeRoute()",
					"Couldn't get I/O for the connection to: " + ip);
		}

		if (socket != null && out != null && in != null) {
			try {
				byte[] buf = ("RETRIEVE " + id + "\r\n").getBytes("UTF-8");
				out.write(buf, 0, buf.length);
				out.flush();
				BufferedReader rdr = new BufferedReader(new InputStreamReader(
						in));
				while (!rdr.ready()) {
					Thread.sleep(50);
				}
				if (rdr.ready())
					xmlDoc = rdr.readLine();
				out.close();
				rdr.close(); // if it doesnt work get rid of this
				in.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("IOException!", e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return xmlDoc;
	}

	private String searchRoute(String userid) {
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in = null;

		String idstring = null;

		// try to open a socket on port
		try {
			socket = new Socket(ip, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			Log.d("Server:storeRoute()", "Don't know about host: " + ip);
		} catch (IOException e) {
			Log.d("Server:storeRoute()",
					"Couldn't get I/O for the connection to: " + ip);
		}

		if (socket != null && out != null && in != null) {
			try {
				byte[] buf = ("search " + userid + "\r\n").getBytes("UTF-8");
				String d = new String(buf, 0, buf.length);
				Log.d("Server:searchRoute()", d);
				out.write(buf, 0, buf.length);
				out.flush();
				BufferedReader rdr = new BufferedReader(new InputStreamReader(
						in));
				while (!rdr.ready()) {
					Thread.sleep(50);
				}
				if (rdr.ready())
					idstring = rdr.readLine();
				out.close();
				rdr.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("IOException!", e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (idstring != null) {
			if (idstring.contains("No routes found.")) {
				return null;
			}
			Log.d("Server:searchRoute() - response: ", idstring);
		}

		return idstring;
	}
}