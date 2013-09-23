package com.example.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.example.socket.StorageDevice.StorageDeviceType;

public class PhoneInfoUtils {

	private static final boolean DEBUG = AppEnv.DEBUG;
	private final static String TAG = DEBUG ? "PhoneInfoUtils"
			: PhoneInfoUtils.class.getSimpleName();

	public static String getPhoneInfo() {
		String phoneInfo = "Product: " + android.os.Build.PRODUCT;
		phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI;
		phoneInfo += ", TAGS: " + android.os.Build.TAGS;
		phoneInfo += ", VERSION_CODES.BASE: "
				+ android.os.Build.VERSION_CODES.BASE;
		phoneInfo += ", MODEL: " + android.os.Build.MODEL;
		phoneInfo += ", SDK: " + android.os.Build.VERSION.SDK;
		phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
		phoneInfo += ", DEVICE: " + android.os.Build.DEVICE;
		phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY;
		phoneInfo += ", BRAND: " + android.os.Build.BRAND;
		phoneInfo += ", BOARD: " + android.os.Build.BOARD;
		phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT;
		phoneInfo += ", ID: " + android.os.Build.ID;
		phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER;
		phoneInfo += ", USER: " + android.os.Build.USER;
		return phoneInfo;
	}

	/** 获取内存信息 */
	public static long getMemoryTotal() {
		long result = 0;
		RandomAccessFile mFile = null;
		try {
			mFile = new RandomAccessFile("/proc/meminfo", "r");
			String line = mFile.readLine();
			String[] divd = line.split(":");
			result = Long.valueOf(divd[1].substring(0, divd[1].length() - 2)
					.trim());

		} catch (Exception ex) {
			if (DEBUG)
				ex.printStackTrace();
		} finally {
			if (mFile != null) {
				try {
					mFile.close();
				} catch (Exception e) {
					if (DEBUG)
						e.printStackTrace();
				}
			}
		}
		if (result <= 0) {

			ProcessBuilder cmd;
			BufferedReader buf = null;
			try {
				String[] args = { "/system/bin/cat", "/proc/meminfo" };
				cmd = new ProcessBuilder(args);
				Process process = cmd.start();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				buf = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				String line = buf.readLine();
				buf.close();
				String[] divd = line.split(":");
				result = Long.valueOf(divd[1]
						.substring(0, divd[1].length() - 2).trim());
			} catch (Exception e) {
			} finally {
				if (buf != null) {
					try {
						buf.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return result * 1024;
	}

	// 获得可用的内存
	public static long getMemoryFree(Context mContext) {
		// 得到ActivityManager
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 创建ActivityManager.MemoryInfo对象

		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);

		// 取得剩余的内存空间
		long memoryFree = mi.availMem;
		return memoryFree;
	}

	/** 获取手机型号 */
	public static String getPhoneMode() {
		return Build.MODEL;
	}

	/** 获取厂商 */
	public static String getPhoneManufacturer() {
		return android.os.Build.MANUFACTURER;
	}

	public static String getPhoneVersion() {
		return Build.VERSION.RELEASE;
	}

	/** 获取miei */
	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/** 获取运营商类型描述 */
	public static String getMobileType(Context context) {
		String type = null;
		TelephonyManager iPhoneManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String iNumeric = iPhoneManager.getSubscriberId();
		if (iNumeric != null) {
			if (iNumeric.startsWith("46000") || iNumeric.startsWith("46002")
					|| iNumeric.startsWith("46007")) {
				type = "中国移动";
			} else if (iNumeric.startsWith("46001")) {
				type = "中国联通";
			} else if (iNumeric.startsWith("46003")) {
				type = "中国电信";
			}
		}
		return type;
	}

	public static DisplayMetrics getScreen(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/** 获取CPU最大速度 */
	public static long readCPUMaxSpeed() {
		long result = -1;
		RandomAccessFile mFile = null;
		try {
			mFile = new RandomAccessFile(
					"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq",
					"r");
			String line = mFile.readLine();
			// DecimalFormat df1 = new DecimalFormat("####");
			result = Long.valueOf(line) / 1000;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (mFile != null) {
				try {
					mFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 如果读不到频率的上下限，则使用/proc/cpuinfo中的BogoMIPS数据作为频率
		if (result < 0) {
			try {
				mFile = new RandomAccessFile("/proc/cpuinfo", "r");
				String line = null;
				while ((line = mFile.readLine()) != null) {
					if (line.startsWith("BogoMIPS")) {
						String[] ss = line.split(":");
						if (ss.length > 1) {
							result = Math.round(Float.valueOf(ss[1].trim()));
						}
						break;
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (mFile != null) {
					try {
						mFile.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return result;
	}

	/** 获取cpu最小速度 */
	public static long readCPUMinSpeed() {
		long result = -1;
		RandomAccessFile mFile = null;
		try {
			mFile = new RandomAccessFile(
					"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq",
					"r");
			String line = mFile.readLine();
			// DecimalFormat df1 = new DecimalFormat("####");
			result = Long.valueOf(line) / 1000;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (mFile != null) {
				try {
					mFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 如果读不到频率的上下限，则使用/proc/cpuinfo中的BogoMIPS数据作为频率
		if (result < 0) {
			try {
				mFile = new RandomAccessFile("/proc/cpuinfo", "r");
				String line = null;
				while ((line = mFile.readLine()) != null) {
					if (line.startsWith("BogoMIPS")) {
						String[] ss = line.split(":");
						if (ss.length > 1) {
							result = Math.round(Float.valueOf(ss[1].trim()));
						}
						break;
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (mFile != null) {
					try {
						mFile.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return result;
	}

	/** 获取cpu当前速度,注意HTC G14因为没有开放权限,无法获取 */
	public static long readCPUCurSpeed() {
		RandomAccessFile mFile = null;
		long result = -1;
		try {
			mFile = new RandomAccessFile(
					"/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq",
					"r");
			String line = mFile.readLine();
			result = Long.valueOf(line) / 1000;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (mFile != null) {
				try {
					mFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/** 是否存在扩展sd卡 */
	public static boolean haveSdCard() {

		try {
			File input = new File("/sys/class/mmc_host/mmc0");
			String cid_directory = null;
			int i = 0;
			File[] sid = input.listFiles();
			for (i = 0; i < sid.length; i++) {
				if (sid[i].toString().contains("mmc0:")) {
					cid_directory = sid[i].toString();
					String type = readFileLine(cid_directory + "/type");
					if (type != null && type.equals("SD") == true) {
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
		}

		try {
			File input = new File("/sys/class/mmc_host/mmc1");
			String cid_directory = null;
			int i = 0;
			File[] sid = input.listFiles();
			for (i = 0; i < sid.length; i++) {
				if (sid[i].toString().contains("mmc1:")) {
					cid_directory = sid[i].toString();
					String type = readFileLine(cid_directory + "/type");
					if (type != null && type.equals("SD") == true) {
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
		}

		try {
			File input = new File("/sys/class/mmc_host/mmc2");
			String cid_directory = null;
			int i = 0;
			File[] sid = input.listFiles();
			for (i = 0; i < sid.length; i++) {
				if (sid[i].toString().contains("mmc2:")) {
					cid_directory = sid[i].toString();
					String type = readFileLine(cid_directory + "/type");
					if (type != null && type.equals("SD") == true) {
						return true;
					}
					break;
				}
			}
		} catch (Exception e) {
		}

		return false;
	}

	/** 读取一行文件数据 */
	public static String readFileLine(String path) {
		BufferedReader brType = null;
		String type = null;
		try {
			brType = new BufferedReader(new FileReader(path));
			type = brType.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				brType.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return type;
	}

	/** 读取cpu信息 */
	public static String readCPUinfo() {
		String result = "";
		RandomAccessFile mFile = null;
		try {

			mFile = new RandomAccessFile("/proc/cpuinfo", "r");
			String line;
			while ((line = mFile.readLine()) != null) {
				if (line.toLowerCase().contains("processor")) {
					String[] tmpList = line.split(":");
					result = tmpList[1].substring(1);
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (mFile != null) {
				try {
					mFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/** 获取cpu利用率,注意此处会sleep一段时间,以保证采样数据的平滑 */
	public static float readCpuUsage() {
		RandomAccessFile reader = null;
		try {
			reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" ");

			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" ");

			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			}
		}

		return 0;
	}

	@SuppressWarnings("unused")
	public static List<StorageDevice> getStorageDevice(Context context) {

		StorageDevice systemStorageDevice = null;
		StorageDevice internalStorageDevice = null;
		StorageDevice externalStorageDevice = null;

		String externalStorageDirectory = Environment
				.getExternalStorageDirectory().getPath();
		Method isExternalStorageRemovableMethod = null;
		boolean isExternalStorageRemovable = false;
		try {
			isExternalStorageRemovableMethod = Environment.class
					.getMethod("isExternalStorageRemovable");
			isExternalStorageRemovable = (Boolean) isExternalStorageRemovableMethod
					.invoke(Environment.class, (Object[]) null);
		} catch (Exception e1) {
			if (DEBUG)
				e1.printStackTrace();
		}

		try {
			ArrayList<String> sds = new ArrayList<String>();
			sds.add(Environment.getExternalStorageDirectory().getAbsolutePath());
			ArrayList<String> storageDevicePathList = sds;
			if (DEBUG)
				Log.d(TAG, "SDPATH:" + storageDevicePathList
						+ "\n getExternalStorageDirectory:"
						+ externalStorageDirectory
						+ " isExternalStorageRemovable:"
						+ isExternalStorageRemovable);
			if (storageDevicePathList == null)
				return null;

			String internalStorageDevicePath = null;
			String externalStorageDevicePath = null;

			int pathSize = storageDevicePathList.size();

			if (pathSize == 2) {
				String path0 = storageDevicePathList.get(0);
				String path1 = storageDevicePathList.get(1);

				if (externalStorageDirectory.equals(path0)
						&& isExternalStorageRemovable) {
					externalStorageDevicePath = path0;
					internalStorageDevicePath = path1;
				} else {
					externalStorageDevicePath = path1;
					internalStorageDevicePath = path0;
				}

			} else if (pathSize == 1) {
				String path0 = storageDevicePathList.get(0);
				if (isExternalStorageRemovable) {
					externalStorageDevicePath = path0;
				} else {
					internalStorageDevicePath = path0;
				}
			}

			if (!TextUtils.isEmpty(internalStorageDevicePath)) {
				// 内置存储卡
				internalStorageDevice = getStorageDevice(internalStorageDevicePath);
				internalStorageDevice.type = StorageDeviceType.INTERNAL;
			}

			if (!TextUtils.isEmpty(externalStorageDevicePath)) {
				// 外置存储卡
				externalStorageDevice = getStorageDevice(externalStorageDevicePath);
				externalStorageDevice.type = StorageDeviceType.EXTERNAL;
			}

			if (DEBUG)
				Log.d(TAG, internalStorageDevice + "\n" + externalStorageDevice);

		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

		// 系统存储
		systemStorageDevice = getStorageDevice(Environment.getDataDirectory()
				.getPath());
		systemStorageDevice.type = StorageDeviceType.SYSTEM;
		if (DEBUG)
			Log.d(TAG, "" + systemStorageDevice);

		// 去重， 避免系统手机存储与内置存储设备重复
		if ((internalStorageDevice != null) && (systemStorageDevice != null)) {
			if ((internalStorageDevice.totalSize == systemStorageDevice.totalSize)
					&& (internalStorageDevice.freeSize == systemStorageDevice.freeSize)) {
				internalStorageDevice = null;
			}
		}

		List<StorageDevice> storageDevices = new ArrayList<StorageDevice>();
		if (systemStorageDevice != null) {
			storageDevices.add(systemStorageDevice);
		}
		if (internalStorageDevice != null) {
			storageDevices.add(internalStorageDevice);
		}
		if (externalStorageDevice != null) {
			storageDevices.add(externalStorageDevice);
		}

		// 有些机器型号会识别出错，用这种方式做兼容
		if ((externalStorageDevice == null) && (internalStorageDevice != null)) {
			if (haveSdCard()) {
				internalStorageDevice.type = StorageDeviceType.EXTERNAL;
			}
		}

		if (DEBUG)
			Log.d(TAG, "" + storageDevices);

		return storageDevices;
	}

	/**
	 * 获取存储卡总空间及可用空间大小
	 * 
	 * @return
	 */
	public static StorageDevice getStorageDevice(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}

		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long availableBlocks = stat.getAvailableBlocks();

		StorageDevice storageDevice = new StorageDevice();
		storageDevice.path = path;
		storageDevice.totalSize = totalBlocks * blockSize;
		storageDevice.freeSize = availableBlocks * blockSize;

		return storageDevice;
	}
}
