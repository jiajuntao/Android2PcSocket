package com.example.socket;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
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
							mTextView.setText(mSocketServer.getLocalIpAddress()
									+ "\n" + mSocketServer.getClientInfo());
						}
					});
				}
			});
			mTextView.post(new Runnable() {

				@Override
				public void run() {
					mTextView.setText(mSocketServer.getLocalIpAddress());
				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSocketServer = null;
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = getApplicationContext();

		mTextView = (TextView) findViewById(R.id.text_field);
		mSendButton = (Button) findViewById(R.id.btn_send);
		mEditText = (EditText) findViewById(R.id.text_message);
		mEditText.setText(getJsonStr());
		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message temp_msg = Message.obtain();
				temp_msg.what = SocketServer.MSG_TO_CLIENT;
				temp_msg.obj = mEditText.getText().toString();

				if (mSocketServer != null) {
					mSocketServer.mMessageHandler.sendMessage(temp_msg);
				}
			}
		});

		startService(new Intent(mContext, SocketService.class));
		bindService(new Intent(mContext, SocketService.class),
				mServiceConnection, Service.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
	}

	public String getJsonStr() {
		JSONObject jsonObject = null;

		try {
			jsonObject = new JSONObject();
			// 字符串值放入jsonObject中
			// 传图片
			jsonObject.put("image", SocketImage.getBase64Image(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/DCIM/android.png"));
			jsonObject.put("Race", "人类");
			jsonObject.put("Profession", "死亡骑士");
			// 服务器的信息的值是对象，故创建一个对象，然后添加到jsonObject对象中
			JSONObject serverMsg = new JSONObject();
			serverMsg.put("Server_region", "二区");
			serverMsg.put("Server_name", "阿古斯");
			jsonObject.put("Server", serverMsg);
			// 天赋信息的值是数组，故创建一个数组，然后添加到jsonObject对象中
			JSONArray talent = new JSONArray();
			talent.put("鲜血").put("冰霜");
			jsonObject.put("Talent", talent);
			// 整数值放入jsonObject中
			jsonObject.put("Achievement_Point", 12090);
		} catch (Exception e) {
			return null;
		}
		return jsonObject.toString();
	}

}