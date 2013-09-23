package com.example.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

import android.util.Log;

class SocketServer extends Thread {
	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = SocketServer.class.getSimpleName();

	private static final int SERVER_PORT = 9500;
	public static final int EXIT = -1;
	public static final int GET_PHONE_INFO = 1;
	public static final int GET_APP_INFO = 2;
	public static final int GET_APP_CACHE = 3;
	public static final int SCAN_APP = 4;
	public static final int GET_VIRUS_INFO = 5;
	public static final int CLEAN_VIRUS = 6;
	public static final int SCAN_APP_CACHE = 7;
	public static final int CLEAN_APP_CACHE = 8;

	private ServerSocket mSocketServer;
	private boolean doRun = true;

	@Override
	public void run() {

		try {
			mSocketServer = new ServerSocket(SERVER_PORT);
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
			return;
		}

		if (DEBUG)
			Log.d(TAG, "server start...");
		while (doRun) {
			try {
				Socket socket = mSocketServer.accept();
				dealSocket(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (DEBUG)
			Log.d(TAG, "server close");
	}

	private void dealSocket(Socket socket) {
		if (socket == null) {
			return;
		}
		if (DEBUG)
			Log.d(TAG, "has client connect : " + getClientInfo());
		setClientInfo(socket);
		if (mCallback != null) {
			mCallback.onClientConnect(getClientInfo());
		}
		BufferedWriter bufWriter = null;
		BufferedReader bufReader = null;
		try {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			bufReader = new BufferedReader(new InputStreamReader(is));
			bufWriter = new BufferedWriter(new OutputStreamWriter(os));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while (true) {
				String msg = "";
				msg = bufReader.readLine();
				if (DEBUG)
					Log.d(TAG, "dealSocket:" + msg);
				int command = Integer.valueOf(msg);
				switch (command) {
				case EXIT:
					close();
					break;
				case GET_PHONE_INFO:
					writePhoneInfo(bufWriter);
					break;

				default:
					break;
				}
				if (!doRun) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufWriter != null) {
				try {
					bufWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {
		if (DEBUG)
			Log.d(TAG, "close");
		doRun = false;
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

	private String clietInfo;

	public String getClientInfo() {
		return clietInfo;
	}

	public void setClientInfo(Socket socket) {
		if (socket == null) {
			return;
		}
		SocketAddress socketAddress = socket.getRemoteSocketAddress();
		clietInfo = socketAddress.toString();
	}

	private Callback mCallback;

	public void addCallback(Callback callback) {
		mCallback = callback;
	}

	public static interface Callback {
		void onClientConnect(String msg);
	}

	public void writePhoneInfo(BufferedWriter bufWriter) throws IOException {
		bufWriter.write("getPhoneInfo");
		bufWriter.flush();
	}
}
