package com.example.socket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.util.Log;

public class SocketImage {
	private static final boolean DEBUG = AppEnv.DEBUG;
	private static final String TAG = "";

	public static String getBase64Image(String imagePath) {

		File imageFile = new File(imagePath);
		String imageBase64 = "";
		// 对文件的操作
		FileInputStream in = null;
		try {
			in = new FileInputStream(imagePath);
			byte buffer[] = read(in);// 把图片文件流转成byte数组
			byte[] encod = Base64.encode(buffer, Base64.DEFAULT);// 使用base64编码
			imageBase64 = new String(encod);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (DEBUG)
			Log.d(TAG, imagePath + " is exist : " + imageFile.exists()
					+ " \n imageBase64:" + imageBase64);
		return imageBase64;
	}

	public static void saveBase64Image(String base64Image, String imagePath) {
		FileOutputStream fileout;
		try {
			fileout = new FileOutputStream(imagePath);
			fileout.write(Base64.decode(base64Image.getBytes(), Base64.DEFAULT));// 使用base64解码
			fileout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回字节数组
	 * 
	 * @param in输入的流
	 * @return
	 * @throws Exception
	 */

	public static byte[] read(InputStream in) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (in != null) {
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.close();
			in.close();
			return out.toByteArray();
		}
		return null;
	}
}
