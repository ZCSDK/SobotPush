package com.sobot.custom.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SharedPreferencesUtil {
	private static SharedPreferences sharedPreferences;
	private static String CONFIG = "shered_config";

	public static void saveStringData(Context context, String key, String value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putString(key, value).commit();
	}

	public static String getStringData(Context context, String key,
			String defValue) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		return sharedPreferences.getString(key, defValue);
	}

	public static void saveBooleanData(Context context, String key, boolean value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static Boolean getBooleanData(Context context, String key,
									   Boolean defValue) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		return sharedPreferences.getBoolean(key, defValue);
	}


	public static void saveIntData(Context context, String key, int value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putInt(key, value).commit();
	}

	public static int getIntData(Context context, String key, int defValue) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sharedPreferences.getInt(key, defValue);
	}

	public static void removeKey(Context context,String key){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().remove(key).commit();
	}
}