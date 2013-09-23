package com.example.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SocketClient {
	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = DEBUG ? "SocketClient"
			: SocketClient.class.getSimpleName();

	private static final int SERVER_PORT = 9500;
	private static final String SERVER_IP = "localhost";
	private String mBaseAdbPath = "/Users/berry/Android/SDK/android-sdk-mac_x86/platform-tools/";

	private Socket mClientSocket = null;

	BufferedWriter mClientWriter = null;
	BufferedReader mMessageFromServerReader = null;

	volatile Integer number = new Integer(0);
	HashMap<Integer, String> waitingList = new HashMap<Integer, String>();

	// 连接socket server 最多尝试10次
	public boolean connect() {
		try {
			Runtime.getRuntime().exec(
					mBaseAdbPath + "adb shell am broadcast -a "
							+ AppEnv.ACTION_START_SOCKET_SERVER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		waitServer(3000);

		try {
			Runtime.getRuntime().exec(
					mBaseAdbPath + "adb forward tcp:9500 tcp:9500");
		} catch (IOException e) {
			e.printStackTrace();
		}
		waitServer(1000);

		int retryCount = 0;
		boolean isConnectToServer = connectToServer();
		while (!isConnectToServer && retryCount < 10) {
			retryCount++;
			if (DEBUG)
				Log.d(TAG, "retry connect: " + retryCount);
			waitServer(3000);
			isConnectToServer = connectToServer();
		}
		if (isConnectToServer) {
			if (DEBUG)
				Log.d(TAG, "has connect server");
			return true;
		} else {
			if (DEBUG)
				Log.d(TAG, "can't connect server");
			return false;
		}
	}

	// 断开socket server 最多尝试10次
	public void disconnect() {
		sendMsgToServer("exit");
		try {
			Runtime.getRuntime().exec(
					mBaseAdbPath + "adb shell am broadcast -a "
							+ AppEnv.ACTION_STOP_SOCKET_SERVER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean connectToServer() {
		try {

			mClientSocket = new Socket(SERVER_IP, SERVER_PORT);
			if (mClientSocket.isConnected()) {
				if (DEBUG)
					Log.d(TAG, "connect server success!");
			} else {
				if (DEBUG)
					Log.e(TAG, "connect server fail!");
				return false;
			}

			InputStream is = mClientSocket.getInputStream();
			OutputStream os = mClientSocket.getOutputStream();

			mMessageFromServerReader = new BufferedReader(
					new InputStreamReader(is));
			mClientWriter = new BufferedWriter(new OutputStreamWriter(os));

			new Thread(new MessageReader()).start();

			return true;
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}
		return false;

	}

	private void printWaitingList() {
		if (DEBUG) {
			System.out
					.println("\n\n***************************************************");
			System.out.println("   Waitings...");
			System.out
					.println("***************************************************");
		}
		Set<Integer> list = waitingList.keySet();
		for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
			Integer key = i.next();
			if (DEBUG) {
				System.out
						.println(" * " + key + "  :  " + waitingList.get(key));
			}
		}
		if (DEBUG) {
			System.out
					.println("***************************************************");
			System.out.print("Select : ");
		}
	}

	public static void main(String[] args) {
		SocketClient ob = new SocketClient();
	}

	private class MessageReader implements Runnable {

		@Override
		public void run() {
			String msgFromServer = null;
			try {
				while (true) {
					msgFromServer = mMessageFromServerReader.readLine();
					if (msgFromServer == null) {
						if (DEBUG)
							Log.e(TAG, "connect server fail!");
						break;
					}
					if (DEBUG)
						Log.d(TAG, "MessageReader:" + msgFromServer);
					// TODO
					SocketImage.saveBase64Image(msgFromServer,
							"src/com/example/socket/android.png");
					if ("REVC_OK".equals(msgFromServer)) {
						if (DEBUG)
							System.out
									.println("\n\n[Device_Message] RECV_OK\n\n");
					} else if (msgFromServer != null) {
						waitingList.put(++number, msgFromServer);
						printWaitingList();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void sendMsgToServer(String msgToServer) {
		try {
			printWaitingList();
			if (DEBUG)
				Log.d(TAG, "dowhile:" + msgToServer);

			Integer thisKey = Integer.parseInt(msgToServer);
			if (waitingList.containsKey(thisKey)) {
				mClientWriter.write(waitingList.get(thisKey) + "\n");
				mClientWriter.flush();
				waitingList.remove(thisKey);
			}
			if ("exit".equals(msgToServer)) {
				try {
					mClientWriter.close();
					mClientSocket.close();
				} catch (Exception e) {
				}
				if (DEBUG) {
					Log.d(TAG, "disconnect socket server");
				}
			}
		} catch (Exception e) {
		}

	}

	private void waitServer(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}