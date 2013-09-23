package com.example.socket;

public class Log {

	public static final void d(String TAG, String msg) {
		System.out.println(TAG + " : " + msg);
	}
	public static final void e(String TAG, String msg) {
		System.err.println(TAG + " : " + msg);
	}
}
