package com.example.socket;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.socket.SocketServer.Callback;

public class SocketServerMainActivity extends Activity {

	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = SocketServerMainActivity.class
			.getSimpleName();

	private Context mContext;
	private SocketServer mSocketServer;

	private TextView mTextView;
	private Button mSendButton;
	private EditText mEditText;
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mSocketServer = ((SocketService.LocalBinder) service).getService();
			mSocketServer.addCallback(new Callback() {

				@Override
				public void onClientConnect(String msg) {
					mTextView.post(new Runnable() {

						@Override
						public void run() {
							initTextView();
						}
					});
				}
			});
			mTextView.post(new Runnable() {

				@Override
				public void run() {
					initTextView();
				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSocketServer = null;
		}
	};

	private void initTextView() {
		String s = mSocketServer.getLocalIpAddress();
		if (mSocketServer.getClientInfo() != null) {
			s += "\n" + mSocketServer.getClientInfo();
		}
		s += "\n" + getPhoneInfo();
		mTextView.setText(s);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = getApplicationContext();

		mTextView = (TextView) findViewById(R.id.text_field);
		mSendButton = (Button) findViewById(R.id.btn_send);
		mEditText = (EditText) findViewById(R.id.text_message);
		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message temp_msg = Message.obtain();
//				temp_msg.what = SocketServer.MSG_TO_CLIENT;
//				temp_msg.obj = mEditText.getText().toString();
//
//				if (mSocketServer != null) {
//					mSocketServer.mMessageHandler.sendMessage(temp_msg);
//				}
			}
		});

		startService(new Intent(mContext, SocketService.class));
		bindService(new Intent(mContext, SocketService.class),
				mServiceConnection, Service.BIND_AUTO_CREATE);

		if (DEBUG) {
			PhoneInfo phone = new PhoneInfo(mContext);
			Log.d(TAG, "PhoneInfo: " + phone);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
	}

	public String getPhoneInfo() {
		PhoneInfo phoneInfo = new PhoneInfo(mContext);
		String phoneInfoStr = "系统版本：" + phoneInfo.androidVersion + "\n机器型号："
				+ phoneInfo.phoneMode + "\n手机串号：" + phoneInfo.phoneImei
				+ "\n屏幕分辨率：" + phoneInfo.phoneScreen + "\n内存总量："
				+ phoneInfo.memoryTotal + "\n内存剩余：" + phoneInfo.memoryFree
				+ "\nCPU型号：" + phoneInfo.phoneCpu + "\nCPU频率："
				+ phoneInfo.phoneCpuSpeed + "\n内置存储总量："
				+ phoneInfo.systemStorageTotal + "\n内置存储剩余："
				+ phoneInfo.systemStorageFree + "\n外置存储总量："
				+ phoneInfo.sdcardStorageTotal + "\n外置飘出剩余："
				+ phoneInfo.sdcardStorageFree + "\n运行进程数："
				+ phoneInfo.runningProcessNum + "\n缓存数"
				+ phoneInfo.appCacheLength;
		return phoneInfoStr;
	}

}