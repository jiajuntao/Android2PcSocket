package com.example.socket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class SocketClientGui extends JFrame implements ActionListener {

	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = DEBUG ? "SocketClientGui"
			: SocketClientGui.class.getSimpleName();

	private static final String ACTION_COMMAND_CONNECT = "connect";
	private static final String ACTION_COMMAND_DISCONNECT = "disconnect";
	private static final String ACTION_COMMAND_GET_DATA = "get_data";
	private static final String ACTION_COMMAND_SEND_MSG = "send_msg";

	private JButton mConnectBtn;
	private JButton mDisconnectBtn;
	private JButton mSendBtn;
	private SocketClient mSocketClient;
	private JTextField mMsgText;

	private Vector<Vector<String>> mReservationList = new Vector<Vector<String>>();;

	public Vector<Vector<String>> getmReservationList() {
		return mReservationList;
	}

	public SocketClientGui() {
		super("Socket Client Demo");
		JFrame jf = new JFrame("客户端窗口");
		JPanel jp = new JPanel();
		JLabel l_name = new JLabel("手机信息：");
		JLabel l_password = new JLabel("软件列表：");
		mMsgText = new JTextField(10);
		JPasswordField t_password = new JPasswordField(10);

		mConnectBtn = new JButton("连接");
		mConnectBtn.setActionCommand(ACTION_COMMAND_CONNECT);
		mConnectBtn.addActionListener(this);

		mDisconnectBtn = new JButton("断开");
		mDisconnectBtn.setActionCommand(ACTION_COMMAND_DISCONNECT);
		mDisconnectBtn.addActionListener(this);

		mSendBtn = new JButton("发送");
		mSendBtn.setActionCommand(ACTION_COMMAND_SEND_MSG);
		mSendBtn.addActionListener(this);

		jp.add(mConnectBtn);
		jp.add(mDisconnectBtn);
		jp.add(mSendBtn);
		jp.add(l_name);
		jp.add(mMsgText);
		jp.add(l_password);
		jp.add(t_password);
		jf.add(jp);

		jf.setVisible(true);
		// jf.setResizable(false);
		jf.setSize(400, 300);
		jf.setLocation(500, 270);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SocketClientGui rm = new SocketClientGui();
		rm.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (DEBUG)
			System.out.println("actionCommand: " + event.getActionCommand());

		if (ACTION_COMMAND_CONNECT.equals(actionCommand)) {
			mSocketClient = new SocketClient();
			if (mSocketClient.connect()) {
				mConnectBtn.setEnabled(false);
			}
			return;

		}
		if (ACTION_COMMAND_DISCONNECT.equals(actionCommand)) {
			mConnectBtn.setEnabled(true);
			mSocketClient.disconnect();
			return;
		}
		if (ACTION_COMMAND_SEND_MSG.equals(actionCommand)) {
			mSocketClient.sendMsgToServer(mMsgText.getText());
			return;
		}

	}

}
