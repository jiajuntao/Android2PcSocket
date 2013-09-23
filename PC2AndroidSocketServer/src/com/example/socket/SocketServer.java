package com.example.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class SocketServer extends Thread {
	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = SocketServer.class.getSimpleName();

	public static final int MSG_FROM_CLIENT = 1;
	public static final int MSG_TO_CLIENT = 2;
	private static final int SERVER_PORT = 9500;

	private ServerSocket mSocketServer = null;
	private Socket client;
	private BufferedWriter mServerWriter = null;
	private BufferedReader mReaderFromClient = null;
	private boolean doRun = true;

	public Handler mMessageHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TO_CLIENT:
				try {
					mServerWriter.write(msg.obj + "\n");
					mServerWriter.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void run() {

		try {
			mSocketServer = new ServerSocket(SERVER_PORT);
			client = mSocketServer.accept();
			if (client == null) {
				if (DEBUG)
					Log.d(TAG, "no client connect");
				return;
			} else {
				if (DEBUG)
					Log.d(TAG, "has client connect : " + getClientInfo());
			}

			if (mCallback != null) {
				mCallback.onClientConnect(getClientInfo());
			}

			InputStream is = client.getInputStream();
			OutputStream os = client.getOutputStream();

			mReaderFromClient = new BufferedReader(new InputStreamReader(is));
			mServerWriter = new BufferedWriter(new OutputStreamWriter(os));
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
			return;
		}
		if (DEBUG)
			Log.d(TAG, "server start...");

		try {
			while (doRun) {
				String msg = "";
				msg = mReaderFromClient.readLine();
				if (DEBUG)
					Log.d(TAG, msg);
				if (msg.equals("exit")) {
					close();
					break;
				} else if (msg != null && msg != "") {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
		if (DEBUG)
			Log.d(TAG, "server close");
	}

	public void close() {
		if (DEBUG)
			Log.d(TAG, "close");
		doRun = false;
		if (mServerWriter != null) {
			try {
				mServerWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (mReaderFromClient != null) {
			try {
				mReaderFromClient.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (mSocketServer != null) {
			try {
				mSocketServer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getLocalIpAddress() {
		String address = "请连接";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// if (!inetAddress.isLoopbackAddress())
					{
						// if(inetAddress.isSiteLocalAddress())
						{
							address += "\nIP: " + inetAddress.getHostAddress()
									+ " : " + mSocketServer.getLocalPort();
						}
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return address;
	}

	public String getClientInfo() {
		if (client == null) {
			return "";
		}
		SocketAddress socketAddress = client.getRemoteSocketAddress();
		String s = socketAddress.toString();
		return s;
	}

	private Callback mCallback;

	public void addCallback(Callback callback) {
		mCallback = callback;
	}

	public static interface Callback {
		void onClientConnect(String msg);
	}
}
