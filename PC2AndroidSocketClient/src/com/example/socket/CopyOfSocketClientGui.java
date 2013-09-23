package com.example.socket;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class CopyOfSocketClientGui extends JFrame implements ActionListener {

	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = DEBUG ? "SocketClientGui"
			: CopyOfSocketClientGui.class.getSimpleName();

	private static final String ACTION_COMMAND_CONNECT = "connect";
	private static final String ACTION_COMMAND_DISCONNECT = "disconnect";
	private static final String ACTION_COMMAND_GET_DATA = "get_data";

	private Container mContainer;
	private JPanel mReservationPannel;
	private JTable mReservationTable;
	private DefaultTableModel mTableModel;
	private JButton mConnectBtn;
	private JButton mDisconnectBtn;

	private Vector<Vector<String>> mReservationList = new Vector<Vector<String>>();;

	public Vector<Vector<String>> getmReservationList() {
		return mReservationList;
	}

	private Vector<String> columnNames = new Vector<String>();

	public CopyOfSocketClientGui() {
		super("Socket Client Demo");
		mContainer = getContentPane();
		mContainer.setLayout(new FlowLayout());
		setSize(460, 400);
		setLocation(400, 250);

		mReservationPannel = new JPanel(new BorderLayout());
		mReservationPannel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Reservation List."));
		mReservationPannel.setPreferredSize(new Dimension(400, 300));

		columnNames.add("No.");
		columnNames.add("Phone Number");

		mTableModel = new DefaultTableModel(mReservationList, columnNames);

		mReservationTable = new JTable(mTableModel);
		mReservationTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		mReservationTable.setRowSelectionAllowed(true);
		mReservationPannel.add(mReservationTable.getTableHeader(),
				BorderLayout.PAGE_START);
		mReservationPannel.add(mReservationTable, BorderLayout.CENTER);
		mContainer.add(mReservationPannel);

		mConnectBtn = new JButton("connect phone");
		mConnectBtn.setActionCommand(ACTION_COMMAND_CONNECT);
		mConnectBtn.setPreferredSize(new Dimension(380, 50));
		mConnectBtn.addActionListener(this);
		mConnectBtn.setEnabled(true);
		mContainer.add(mConnectBtn);

		mDisconnectBtn = new JButton("disconnect phone");
		mDisconnectBtn.setPreferredSize(new Dimension(380, 50));
		mDisconnectBtn.addActionListener(this);
		mDisconnectBtn.setEnabled(true);
		mContainer.add(mDisconnectBtn);

		setResizable(false);
		setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CopyOfSocketClientGui rm = new CopyOfSocketClientGui();
		rm.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (DEBUG)
			System.out.println("actionCommand: " + event.getActionCommand());

		if (ACTION_COMMAND_CONNECT.equals(actionCommand)) {
			Vector<String> aa = new Vector<String>();
			aa.add("1");
			aa.add("222");
			mReservationList.add(aa);
			mTableModel.fireTableDataChanged();
			mReservationTable.requestFocusInWindow();
			new SocketClient();
			mConnectBtn.setEnabled(false);
			return;

		}
		if (ACTION_COMMAND_DISCONNECT.equals(actionCommand)) {
			mConnectBtn.setEnabled(true);
			return;
		}
		if (ACTION_COMMAND_CONNECT.equals(actionCommand)) {
			return;
		}

	}

}
