package com.sobot.push.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log工具，类似android.util.Log。 tag自动产生，格式:
 * appName:className.methodName(L:lineNumber),
 */
public class LogUtils {

	// 日志显示开关
	public static boolean isDebug = true;
	// 日志本地保存开关
	public static boolean isCache = false;
	// 日志缓存各级别开关
	public static boolean allowD = true;
	public static boolean allowE = true;
	public static boolean allowI = true;
	public static boolean allowV = true;
	public static boolean allowW = true;
	public static boolean allowWtf = true;
	// 存储时长 单位 ：天
	public static int maxTime = 1;

	// 项目名称
	private static String mAppName = null;
	// 日志文件路径
	public static String path = null;
	// 日志文件夹路径
	private static File file = null;

	private LogUtils() {
	}

	public static void init(Context context) {
		// 获取appname
		PackageManager packageManager = context.getApplicationContext()
				.getPackageManager();
		try {
			ApplicationInfo applicationInfo = packageManager
					.getApplicationInfo(context.getPackageName(), 0);
			mAppName = (String) packageManager
					.getApplicationLabel(applicationInfo);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// 获取日志缓存路径
		if (isCache) {
			setSaveDir(context.getCacheDir().getPath());
		}
	}

	/**
	 * 设置日志保存的路径 默认为packagename/cache/...
	 * 
	 * @param dir
	 */
	public static void setSaveDir(String dir) {
		String str = dir + File.separator + mAppName + "_log";
		LogUtils.path = str + File.separator + mAppName + "_"
				+ getCurrentTime("yyyyMMdd") + "_log.txt";
		LogUtils.file = new File(str);
	}

	/**
	 * 设置日志缓存时间
	 */
	public static void setCacheTime(int cacheTime) {
		if (cacheTime < 0) {
			return;
		}
		LogUtils.maxTime = cacheTime;
	}

	/*
	 * 获取tag
	 */
	private static String generateTag() {
		StackTraceElement caller = new Throwable().getStackTrace()[2];
		String tag = "%s.%s(L:%d)";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName
				.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(),
				caller.getLineNumber());
		tag = TextUtils.isEmpty(mAppName) ? tag : "[" + mAppName + "]:" + tag;
		return tag;
	}

	/**
	 * 设置日志开关
	 * 
	 * @param isDebug
	 */
	public static void setIsDebug(boolean isDebug) {
		LogUtils.isDebug = isDebug;
	}

	/**
	 * 设置日志缓存
	 */
	public static void setIsCache(boolean isCache) {
		LogUtils.isCache = isCache;
	}

	/**
	 * 保存文件到本地
	 */
	@SuppressLint("SimpleDateFormat")
	public static void save2Local(String level, String tag, String content,
			Throwable tr) {
		if (!isCache)
			return;
		if (!TextUtils.isEmpty(LogUtils.path)) {
			PrintWriter pw = null;
			String logContent = null;
			String separator = "\t\t";

			if (!file.exists()) {
				file.mkdirs();
			}
			try {

				logContent = level;
				logContent = TextUtils.isEmpty(tag) ? logContent : logContent
						+ separator + tag;
				logContent = TextUtils.isEmpty(content)
						? logContent
						: logContent + separator + content;
				pw = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(LogUtils.path, true), "utf-8"));
				pw.println("--------------------------------------"
						+ getCurrentTime("yyyy-MM-dd hh:mm:ss")
						+ "---------------------------------------");
				pw.println("pid:" + android.os.Process.myPid() + separator
						+ "tid:" + android.os.Process.myTid());
				pw.println(logContent);
				pw.flush();
				if (tr != null) {
					tr.printStackTrace(pw);
				}
				pw.println("--------------------------------------------------------------------------------------------------------");
				pw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pw != null) {
					pw.close();
				}
			}
			clearLog();
		}
	}

	/*
	 * 清理日志
	 */
	private static void clearLog() {
		if (LogUtils.maxTime < 0) {
			return;
		}
		int currentTime = Integer.valueOf(getCurrentTime("yyyyMMdd"));
		File[] tempList = LogUtils.file.listFiles();
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				String tempName = tempList[i].getName().split("_")[1];
				Integer tempDate = Integer.valueOf(tempName);
				if ((currentTime - tempDate) >= maxTime) {
					tempList[i].delete();
				}
			}
		}
	}

	/**
	 * 读取日志文件
	 */
	public static String getLogContent() {
		File file = new File(LogUtils.path);
		if (!file.exists()) {
			return null;
		}
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			String str = null;
			br = new BufferedReader(new FileReader(file));
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/*
	 * 获取日志文件的路径
	 */
	public static String getLogFilePath() {
		return LogUtils.path;
	}

	/*
	 * 根据日期获取日志文件的路径
	 * 
	 * @param date 日期（yyyyMMdd） 20160312
	 */
	public static String getLogFileByDate(String date) {
		if (TextUtils.isEmpty(date)) {
			return null;
		}
		
		File file2 = new File(LogUtils.file,mAppName + "_"
				+ date + "_log.txt");
		if (file2.exists()) {
			return file2.getAbsolutePath();
		} else {
			return null;
		}
	}

	/**
	 * 日志路径输出
	 * 
	 * @return
	 */
	public static void printLogPath() {
		String tag = generateTag();
		Log.i(tag, LogUtils.file.getPath());
	}

	/*
	 * 获取时间并且格式化
	 */
	@SuppressLint("SimpleDateFormat")
	private static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		String currentTime = dateFormat.format(date);
		return currentTime;
	}

	public static void d(String content) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.d(tag, content);
		}

		if (allowD && isCache) {
			save2Local("D", tag, content, null);
		}
	}

	public static void d(String content, Throwable tr) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			if (content.length() > 3072) {
				String log = content.substring(0, 3072);
				Log.d(tag, "分段打印开始"+log);
				String var2 = content.substring(3072, content.length());
				if (var2.length() - 3072 > 3072) {
					i(var2);
				} else {
					Log.d(tag, "分段打印结束"+var2);
				}
			} else {
				Log.d(tag, content);
			}
		}

//		if (allowD && isCache) {
//			save2Local("D", tag, content, tr);
//		}
	}

	public static void e(String content) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.e(tag, content);
		}

		if (allowE && isCache) {
			save2Local("E", tag, content, null);
		}
	}

	public static void e(String content, Throwable tr) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.e(tag, content, tr);
		}

		if (allowE && isCache) {
			save2Local("E", tag, content, tr);
		}
	}

	public static void i(String content) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			if (content.length() > 3072) {
				String log = content.substring(0, 3072);
				Log.i(tag, "分段打印开始"+log);
				String var2 = content.substring(3072, content.length());
				if (var2.length() - 3072 > 3072) {
					i(var2);
				} else {
					Log.i(tag, "分段打印结束"+var2);
				}
			} else {
				Log.i(tag, content);
			}
		}
//		if (allowI && isCache) {
//			save2Local("I", tag, content, null);
//		}
	}

	public static void i(String content, Throwable tr) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.i(tag, content, tr);
		}
		if (allowI && isCache) {
			save2Local("I", tag, content, tr);
		}
	}

	public static void v(String content) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.v(tag, content);
		}
		if (allowV && isCache) {
			save2Local("V", tag, content, null);
		}
	}

	public static void v(String content, Throwable tr) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.v(tag, content, tr);
		}

		if (allowV && isCache) {
			save2Local("V", tag, content, tr);
		}
	}

	public static void w(String content) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.w(tag, content);
		}

		if (allowW && isCache) {
			save2Local("W", tag, content, null);
		}
	}

	public static void w(String content, Throwable tr) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.w(tag, content, tr);
		}

		if (allowW && isCache) {
			save2Local("W", tag, content, tr);
		}
	}

	public static void w(Throwable tr) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.w(tag, tr);
		}

		if (allowW && isCache) {
			save2Local("W", tag, null, tr);
		}
	}

	public static void wtf(String content) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.wtf(tag, content);
		}

		if (allowWtf && isCache) {
			save2Local("WTF", tag, content, null);
		}
	}

	public static void wtf(String content, Throwable tr) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.wtf(tag, content, tr);
		}

		if (allowWtf && isCache) {
			save2Local("WTF", tag, content, tr);
		}
	}

	public static void wtf(Throwable tr) {
		if (!(isCache || isDebug)) {
			return;
		}
		String tag = generateTag();
		if (isDebug) {
			Log.wtf(tag, tr);
		}

		if (allowWtf && isCache) {
			save2Local("WTF", tag, null, tr);
		}
	}

}
