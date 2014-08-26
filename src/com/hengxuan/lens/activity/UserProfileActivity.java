package com.hengxuan.lens.activity;

import com.hengxuan.lens.R;

import android.app.Activity;
import android.os.Bundle;

public class UserProfileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.edit_profile);
		setContentView(R.layout.activity_profile);
	}
}
