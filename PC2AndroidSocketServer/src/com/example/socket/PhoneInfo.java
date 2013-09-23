package com.example.socket;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

public class PhoneInfo {

	// 详细说明如下：
	// 基本信息包括：
	// 当前连接设备的型号
	// 系统版本信息
	// 屏幕分辨率
	// 运行内存大小
	// 处理器信息
	// 硬件设备串号（IMEI）
	// 手机卡串号（IMSI）
	// 手机内存（机身ROM存储）
	// 标准SD卡信息（总大小、已用、可用）
	// 当前运行程序的个数
	// 当前可清理的应用缓存

	public String androidVersion;
	public String phoneMode;
	public String phoneImei;
	public String phoneScreen;
	public String memoryTotal;
	public String memoryFree;
	public String phoneCpu;
	public String phoneCpuSpeed;
	public String systemStorageTotal;
	public String systemStorageFree;
	public String sdcardStorageTotal;
	public String sdcardStorageFree;
	public String runningProcessNum;
	public String appCacheLength;

	public PhoneInfo(Context context) {
		init(context);
	}

	public void init(Context context) {
		androidVersion = "Android " + PhoneInfoUtils.getPhoneVersion();
		phoneMode = PhoneInfoUtils.getPhoneMode();
		phoneImei = PhoneInfoUtils.getIMEI(context);
		DisplayMetrics displayMetrics = PhoneInfoUtils.getScreen(context);
		phoneScreen = displayMetrics.heightPixels + " x "
				+ displayMetrics.widthPixels;
		memoryTotal = Formatter.formatFileSize(context,
				PhoneInfoUtils.getMemoryTotal());
		memoryFree = Formatter.formatFileSize(context,
				PhoneInfoUtils.getMemoryFree(context));
		phoneCpu = PhoneInfoUtils.readCPUinfo();
		phoneCpuSpeed = "最高频率：" + PhoneInfoUtils.readCPUMaxSpeed()
				+ "Mhz; 最低频率： " + PhoneInfoUtils.readCPUMinSpeed() + "Mhz";

		long total = 0;
		long free = 0;
		List<StorageDevice> sdcardList = PhoneInfoUtils
				.getStorageDevice(context);
		for (StorageDevice storageDevice : sdcardList) {
			total += storageDevice.totalSize;
			free += storageDevice.freeSize;
		}
		sdcardStorageTotal = Formatter.formatFileSize(context, total);
		sdcardStorageFree = Formatter.formatFileSize(context, free);
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
			jsonObject.put("androidVersion", androidVersion);
			jsonObject.put("phoneMode", phoneMode);
			jsonObject.put("phoneImei", phoneImei);
			jsonObject.put("phoneScreen", phoneScreen);
			jsonObject.put("memoryTotal", memoryTotal);
			jsonObject.put("memoryFree", memoryFree);
			jsonObject.put("phoneCpu", phoneCpu);
			jsonObject.put("phoneCpuSpeed", phoneCpuSpeed);
			jsonObject.put("systemStorageTotal", systemStorageTotal);
			jsonObject.put("systemStorageFree", systemStorageFree);
			jsonObject.put("sdcardStorageTotal", sdcardStorageTotal);
			jsonObject.put("sdcardStorageFree", sdcardStorageFree);
			jsonObject.put("runningProcessNum", runningProcessNum);
			jsonObject.put("appCacheLength", appCacheLength);

			// // 服务器的信息的值是对象，故创建一个对象，然后添加到jsonObject对象中
			// JSONObject serverMsg = new JSONObject();
			// serverMsg.put("Server_region", "二区");
			// serverMsg.put("Server_name", "阿古斯");
			// jsonObject.put("Server", serverMsg);
			// // 天赋信息的值是数组，故创建一个数组，然后添加到jsonObject对象中
			// JSONArray talent = new JSONArray();
			// talent.put("鲜血").put("冰霜");
			// jsonObject.put("Talent", talent);
			// // 整数值放入jsonObject中
			// jsonObject.put("Achievement_Point", 12090);
		} catch (Exception e) {
			return null;
		}
		return jsonObject.toString();
	}

	@Override
	public String toString() {
		return "PhoneInfoUtils [androidVersion=" + androidVersion
				+ ", phoneMode=" + phoneMode + ", phoneImei=" + phoneImei
				+ ", phoneScreen=" + phoneScreen + ", memoryTotal="
				+ memoryTotal + ", memoryFree=" + memoryFree + ", phoneCpu="
				+ phoneCpu + ", phoneCpuSpeed=" + phoneCpuSpeed
				+ ", systemStorageTotal=" + systemStorageTotal
				+ ", systemStorageFree=" + systemStorageFree
				+ ", sdcardStorageTotal=" + sdcardStorageTotal
				+ ", sdcardStorageFree=" + sdcardStorageFree
				+ ", runningProcessNum=" + runningProcessNum
				+ ", appCacheLength=" + appCacheLength + "]";
	}
}
