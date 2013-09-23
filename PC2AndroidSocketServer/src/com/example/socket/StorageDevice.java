package com.example.socket;

import android.content.Context;
import android.text.format.Formatter;

/**
 * 记录存储设备信息 大小、剩余空间、路径名称、设备类型
 */
public class StorageDevice {

	/**
	 * 存储设备类型：手机存储、内置存储卡、外置存储卡
	 */
	public static enum StorageDeviceType {
		SYSTEM, INTERNAL, EXTERNAL
	}

	/**
	 * 储存设备类型
	 */
	public StorageDeviceType type;

	/**
	 * 总大小
	 */
	public long totalSize;

	/**
	 * 剩余大小
	 */
	public long freeSize;

	/**
	 * 存储设备系统路径
	 */
	public String path;

	/**
	 * 获取格式化的总量
	 */
	public String getFormatTotalSize(Context context) {
		return Formatter.formatFileSize(context, totalSize);
	}

	/**
	 * 获取格式化的剩余空间
	 */
	public String getFormatFreeSize(Context context) {
		return Formatter.formatFileSize(context, freeSize);
	}

	@Override
	public String toString() {
		return "StorageDevice [type=" + type + ", totalSize=" + totalSize
				+ ", freeSize=" + freeSize + ", path=" + path + "]";
	}
}
