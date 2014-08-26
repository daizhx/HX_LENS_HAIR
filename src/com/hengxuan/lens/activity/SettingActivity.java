package com.hengxuan.lens.activity;

import com.hengxuan.eht.IntentAction;
import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.R;
import com.hengxuan.lens.user.User;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
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
	//退出登录
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
					if(!User.isLogin){
						startActivity(new Intent(IntentAction.LOGIN));
					}else{
						startActivity(new Intent(IntentAction.RESET_PW));
					}
					break;
				case 2:
					break;
				case 3:
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, getResources()
							.getString(R.string.share));
					intent.putExtra(Intent.EXTRA_TEXT, getResources()
							.getString(R.string.sharecontent));
					startActivity(Intent.createChooser(intent, getTitle()));
					break;
				case 4:
					startActivity(new Intent(SettingActivity.this, UserSuggestionActivity.class));
					break;
				case 5:
					//TODO--更多相关应用推荐
					break;
				case 6:
					startActivity(new Intent(SettingActivity.this, AboutActivity.class));
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
				User.Logout();
				startActivity(new Intent(IntentAction.LOGIN));
			}
		});
	}
}
