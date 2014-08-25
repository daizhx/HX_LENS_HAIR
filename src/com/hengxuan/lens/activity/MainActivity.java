package com.hengxuan.lens.activity;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.LensPhotoList;
import com.hengxuan.lens.R;
import com.hengxuan.lens.user.LoginActivity;
import com.hengxuan.lens.user.User;
import com.hengxuan.lens.utils.PreferencesUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {
	// 检测entry
	private ImageView ivLens;
	// 历史记录entry
	private ImageView ivHistory;
	// 使用帮助entry
	private ImageView ivHelp;
	// 检测报告entry
	private ImageView ivReport;
	// settings
	private ImageView ivSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		ivLens = (ImageView) findViewById(R.id.iv_lens);
		ivLens.setOnClickListener(this);
		ivHistory = (ImageView) findViewById(R.id.iv_record);
		ivHistory.setOnClickListener(this);
		ivHelp = (ImageView) findViewById(R.id.iv_help);
		ivHelp.setOnClickListener(this);
		ivReport = (ImageView)findViewById(R.id.iv_report);
		ivReport.setOnClickListener(this);
		ivSetting = (ImageView) findViewById(R.id.iv_setting);
		ivSetting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_lens:
			startActivity(new Intent(MainActivity.this, LensEntryActivity.class));
			break;
		case R.id.iv_record:
			startActivity(new Intent(MainActivity.this, HistoryActivity.class));
			break;
		case R.id.iv_help:
			startActivity(new Intent(MainActivity.this, HelpActivity.class));
			break;
		case R.id.iv_report:
//			if(User.isLogin(this)){
//				startActivity(new Intent(MainActivity.this, ReportActivity.class));
//			}else{
//				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//				intent.putExtra("destIntent", new Intent(getApplication(), ReportActivity.class));
//				startActivity(intent);
//			}
			startActivity(new Intent(MainActivity.this, ReportActivity.class));
			break;
		case R.id.iv_setting:
			startActivity(new Intent(MainActivity.this, SettingActivity.class));
			break;
		default:
			break;
		}
	}

}
