package com.example.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = ServiceBroadcastReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (DEBUG)
			Log.d(TAG, "onReceive: " + action);
		if (AppEnv.ACTION_START_SOCKET_SERVER.equalsIgnoreCase(action)) {
			context.startService(new Intent(context, SocketService.class));
			if (DEBUG)
				if (DEBUG)
					Log.i(TAG, "onReceive start socket service");
		} else if (AppEnv.ACTION_STOP_SOCKET_SERVER.equalsIgnoreCase(action)) {
			context.stopService(new Intent(context, SocketService.class));
			if (DEBUG)
				Log.i(TAG, "onReceive stop socket service");
		}
	}

}
