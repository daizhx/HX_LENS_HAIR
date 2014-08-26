package com.hengxuan.lens.activity;

import com.hengxuan.eht.IntentAction;
import com.hengxuan.lens.R;
import com.hengxuan.lens.hair.HairReportActivity;
import com.hengxuan.lens.iris.IrisReportActivity;
import com.hengxuan.lens.skin.SkinReportActivity;
import com.hengxuan.lens.user.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReportActivity extends Activity {
	private String userName;
	private String password;
	
	private TextView tvName;
	private TextView tvGender;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Integer.valueOf(Build.VERSION.SDK) >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			setTitle(R.string.lens_report);
		}
		setContentView(R.layout.activity_report);
		ListView list = (ListView)findViewById(R.id.list);
		String[] strs = new String[]{
			getString(R.string.hair_report),
			getString(R.string.skin_report),
			getString(R.string.iris_report),
		};
		list.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_2, R.id.text, strs));
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(!User.isLogin){
					Toast.makeText(ReportActivity.this, R.string.not_login, 2000).show();
					return;
				}
				switch (position) {
				case 0:
					startActivity(new Intent(ReportActivity.this, HairReportActivity.class));
					break;
				case 1:
					startActivity(new Intent(ReportActivity.this, SkinReportActivity.class));
					break;
				case 2:
					startActivity(new Intent(ReportActivity.this, IrisReportActivity.class));
					break;

				default:
					break;
				}
			}
		});
		
		tvName = (TextView)findViewById(R.id.tv_name);
		tvGender = (TextView)findViewById(R.id.tv_gender);
		tvGender.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(IntentAction.LOGIN));
			}
		});
		
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(User.isLogin()){
			userName = User.getUserName();
			password = User.getUserName();
		}
		if(userName != null && password != null){
			tvName.setText(userName);
			tvGender.setBackgroundColor(Color.TRANSPARENT);
			tvGender.setText(User.gender);
		}
		
		super.onResume();
	}
}
