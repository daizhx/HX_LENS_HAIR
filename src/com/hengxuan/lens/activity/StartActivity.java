/**
 * app∆Ù∂Ø“≥
 */

package com.hengxuan.lens.activity;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.R;
import com.hengxuan.lens.utils.PreferencesUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout.LayoutParams;


public class StartActivity extends Activity {
	private boolean isFirstStart = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.lens_start);
		iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(iv);
		isFirstStart = PreferencesUtils.getBooleanFromPrefs(this, "FirstStart", true);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(isFirstStart){
					startActivity(new Intent(StartActivity.this, SplashActivity.class));
				}else{
					startActivity(new Intent(StartActivity.this, MainActivity.class));
				}
				finish();
			}
		}, 3000);
		boolean b = PreferencesUtils.putBoolean2Prefs(this, "FirstStart", false);
		Log.d("daizhx", "b="+ b + "----------"+PreferencesUtils.getBooleanFromPrefs(this, "FirstStart", true));
	}
}
