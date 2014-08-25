package com.hengxuan.lens.activity;

import com.hengxuan.lens.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

public class HelpActivity extends Activity {
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Integer.valueOf(Build.VERSION.SDK) >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			setTitle(R.string.lens_help);
		}
	}
}
