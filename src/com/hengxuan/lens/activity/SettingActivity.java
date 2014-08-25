package com.hengxuan.lens.activity;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SettingActivity extends Activity {
	private ListView mListView;
	//ÍË³öµÇÂ¼
	private TextView mTvExit;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Integer.valueOf(Build.VERSION.SDK) >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			setTitle(R.string.setting);
		}
		setContentView(R.layout.activity_setting);
		mListView = (ListView)findViewById(R.id.list);
		String[] strs = new String[]{
			getString(R.string.edit_profile),
			getString(R.string.reset_pw),
			getString(R.string.check_update),
			getString(R.string.share),
			getString(R.string.user_suggestion),
			getString(R.string.more_app),
			getString(R.string.about)
		};
		mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_2, R.id.text, strs));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("daizhx", "position="+position);
				switch (position) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				default:
					break;
				}
			}
		});
		mTvExit = (TextView)findViewById(R.id.text);
		mTvExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
