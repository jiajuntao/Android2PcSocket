package com.example.socket;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SocketImage {

	public static String getBase64Image(String imagePath) {
		// 对文件的操作
		FileInputStream in = null;
		try {
			in = new FileInputStream(imagePath);
			byte buffer[] = read(in);// 把图片文件流转成byte数组
			byte[] encod = Base64.encode(buffer, Base64.DEFAULT);// 使用base64编码
			return new String(encod);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	
	public static void main(String[] args) {
		FileOutputStream fileout;
		try {
			fileout = new FileOutputStream("src/com/example/socket/android.png");
			fileout.write(Base64.decode(getBase64Image("src/com/example/socket/test.png").getBytes(), Base64.DEFAULT));// 使用base64解码
			fileout.flush();
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
