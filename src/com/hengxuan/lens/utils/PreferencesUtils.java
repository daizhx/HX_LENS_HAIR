package com.hengxuan.lens.utils;

import android.content.Context;

public class PreferencesUtils {

	public static String getStringFromPrefences(Context context, String key){
		return getStringFromPreferences(context, "eht", key, null);
	}
	public static String getStringFromPreferences(Context context, String prefFile, String key, String defaultString){
		return context.getSharedPreferences(prefFile, Context.MODE_PRIVATE).getString(key, defaultString);
	}
}
