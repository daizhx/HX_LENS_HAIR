package com.hengxuan.lens.activity;

import com.hengxuan.lens.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout.LayoutParams;

public class SplashActivity extends Activity {
	private static final int PAGES = 4;
	private int[] imageIds = new int[]{
			R.drawable.splash1,
			R.drawable.splash2,
			R.drawable.splash3,
			R.drawable.splash4
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewPager vp = new ViewPager(this);
		vp.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return PAGES;
			}
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				ImageView iv = new ImageView(SplashActivity.this);
				iv.setImageResource(imageIds[position]);
				container.addView(iv);
				if(position == 3){
					iv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							startActivity(new Intent(SplashActivity.this, MainActivity.class));
							finish();
						}
					});
				}
				return iv;
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
				container.removeView((View)object);
//				super.destroyItem(container, position, object);
			}
		});
		vp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(vp);
	}
}
