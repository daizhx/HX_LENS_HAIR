package com.hengxuan.eht;


import android.app.Application;

public class EHTApplication extends Application {
	private static EHTApplication instance;
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
