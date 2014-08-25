package com.hengxuan.eht;


import com.hengxuan.lens.user.User;

import android.app.Application;

public class EHTApplication extends Application {
	private static EHTApplication instance;
	private User mUser = null;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
	}
	
	public static EHTApplication getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}
	
}
