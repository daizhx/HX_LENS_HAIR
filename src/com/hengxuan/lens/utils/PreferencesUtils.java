package com.hengxuan.lens.utils;

import android.content.Context;

public class PreferencesUtils {
	private static String PREFS_NAME = "eht";

	public static String getStringFromPreferences(Context context, String key){
		return getStringFromPreferences(context, PREFS_NAME, key, null);
	}
	public static String getStringFromPreferences(Context context, String prefFile, String key, String defaultString){
		return context.getSharedPreferences(prefFile, Context.MODE_PRIVATE).getString(key, defaultString);
	}
	public static boolean putString2Prefs(Context c, String key, String val){
		return putString2Prefs(c, PREFS_NAME, key, val);
	}
	public static boolean putString2Prefs(Context c, String prefsFile, String key, String val){
		return c.getSharedPreferences(prefsFile, Context.MODE_PRIVATE).edit().putString(key, val).commit();
	}
	
	public static boolean putInt2Preferences(Context context, String key, int value){
		return putInt2Preferences(context, PREFS_NAME, key, value);
	}
	public static boolean putInt2Preferences(Context context, String preFileName, String key, int value){
		return context.getSharedPreferences(preFileName, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
	}
	public static int getIntFromPreferences(Context context, String key, int defValue){
		return getIntFromPreferences(context, PREFS_NAME, key, defValue);
	}
	public static int getIntFromPreferences(Context context, String preFileName, String key, int defValue){
		return context.getSharedPreferences(preFileName, Context.MODE_PRIVATE).getInt(key, defValue);
	}
	
	public static boolean getBooleanFromPrefs(Context c,String prefsName, String key, boolean def){
		return c.getSharedPreferences(prefsName, Context.MODE_PRIVATE).getBoolean(key, def);
	}
	
	public static boolean getBooleanFromPrefs(Context c, String key, boolean def){
		return getBooleanFromPrefs(c,PREFS_NAME, key, def);
	}
	public static boolean putBoolean2Prefs(Context c,String prefsName, String key, boolean v){
		return c.getSharedPreferences(prefsName, Context.MODE_PRIVATE).edit().putBoolean(key, v).commit();
	}
	public static boolean putBoolean2Prefs(Context c, String key, boolean v){
		return putBoolean2Prefs(c, PREFS_NAME, key, v);
	}
}
