package com.example.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SocketService extends Service {

	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = SocketService.class.getSimpleName();

	private SocketServer mSocketServer;
	private IBinder mBinder = null;

	public class LocalBinder extends Binder {
		SocketServer getService() {
			return mSocketServer;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (DEBUG)
			Log.d(TAG, "onCreate");

		mSocketServer = new SocketServer();
		mSocketServer.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (DEBUG)
			Log.d(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSocketServer.close();
		if (DEBUG)
			Log.d(TAG, "onDestroy");

	}

	@Override
	public IBinder onBind(Intent intent) {
		if (DEBUG)
			Log.d(TAG, "onBind");
		if (mBinder == null) {
			mBinder = new LocalBinder();
		}
		return mBinder;
	}
}
