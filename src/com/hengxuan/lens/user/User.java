package com.hengxuan.lens.user;

import javax.crypto.spec.PSource;

import com.hengxuan.eht.EHTApplication;
import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.utils.PreferencesUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
	public static final String IS_LOGIN = "is_login";
	public static final String USER_PREFS = "user";
	public static final String USER_NAME = "user_name";
	public static final String PW = "pw";
	public static final String GENDER = "gender";
	
	//是否已登录
	private static boolean isLogin = false;
	//用户名-保存在prefs中
	public static String userName = null;
	//用户密码-保存在prefs中
	public static String password = null;
	
	public static String gender;
	
	static{
		Context c = EHTApplication.getInstance();
		isLogin = PreferencesUtils.getBooleanFromPrefs(c,USER_PREFS, IS_LOGIN, false);
		userName = PreferencesUtils.getStringFromPreferences(c,USER_PREFS, USER_NAME, null);
		password = PreferencesUtils.getStringFromPreferences(c,USER_PREFS, PW, null);
		gender = PreferencesUtils.getStringFromPreferences(c,USER_PREFS, GENDER, null);
	}
	
	public static boolean isLogin(){
		return isLogin;
	}
	
	public static String getUserName(){
		return userName;
	}
	
	public static String getPassword(){
		return password;
		
	}

	public static void Logout(){
		Context c = EHTApplication.getInstance();
		SharedPreferences.Editor editor = c.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		isLogin = false;
		userName = null;
		password = null;
		gender = null;
	}
}
