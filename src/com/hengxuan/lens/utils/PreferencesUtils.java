package com.hengxuan.lens.utils;

import android.content.Context;

public class PreferencesUtils {

	public static String getStringFromPreferences(Context context, String prefFile, String key, String defaultString){
		return context.getSharedPreferences(prefFile, Context.MODE_PRIVATE).getString(key, defaultString);
	}
}
