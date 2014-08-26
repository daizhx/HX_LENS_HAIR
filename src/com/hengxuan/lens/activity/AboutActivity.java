package com.hengxuan.lens.activity;

import com.hengxuan.lens.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout.LayoutParams;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.lens_start);
		iv.setScaleType(ScaleType.FIT_XY);
		iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(iv);
	}
}
