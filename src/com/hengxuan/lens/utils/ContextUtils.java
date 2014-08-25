package com.hengxuan.lens.utils;

import android.content.Context;
import android.util.TypedValue;

public class ContextUtils {

	/*
	 * 应用于老版本
	 */
	public static int getActionBarHeight(Context c){
		int actionBarHeight = 0;
		// Calculate ActionBar height
		TypedValue tv = new TypedValue();
		if (c.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					c.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}
	
	//preferences
	public static String getStringFromPrefences(Context context, String key){
		return getStringFromPreferences(context, "eht", key, null);
	}
	public static String getStringFromPreferences(Context context, String prefFile, String key, String defaultString){
		return context.getSharedPreferences(prefFile, Context.MODE_PRIVATE).getString(key, defaultString);
	}
	
	public static boolean putInt2Preferences(Context context, String key, int value){
		return putInt2Preferences(context, "eht", key, value);
	}
	public static boolean putInt2Preferences(Context context, String preFileName, String key, int value){
		return context.getSharedPreferences(preFileName, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
	}
	public static int getIntFromPreferences(Context context, String key, int defValue){
		return getIntFromPreferences(context, "eht", key, defValue);
	}
	public static int getIntFromPreferences(Context context, String preFileName, String key, int defValue){
		return context.getSharedPreferences(preFileName, Context.MODE_PRIVATE).getInt(key, defValue);
	}
}
