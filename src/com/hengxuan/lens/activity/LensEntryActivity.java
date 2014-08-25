package com.hengxuan.lens.activity;

import java.lang.reflect.Field;

import com.hengxuan.lens.LensBaseActivity;
import com.hengxuan.lens.LensConstant;
import com.hengxuan.lens.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LensEntryActivity extends Activity {

	private ListView mListView;
	private String[] ss;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lens_entry);
		if (Integer.valueOf(Build.VERSION.SDK) >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		mListView = (ListView) findViewById(R.id.list);
		ss = new String[] { getString(R.string.hair_detection),
				getString(R.string.skin_detection),
				getString(R.string.iris_detection),
				getString(R.string.naevus_detection) };
		if (Integer.valueOf(Build.VERSION.SDK) >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getOverflow();
		}
		mListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item_2, R.id.text, ss));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LensEntryActivity.this, LensBaseActivity.class); 
				switch (position) {
				case 0:
					intent.putExtra(LensBaseActivity.ARG_INDEX, LensConstant.INDEX_HAIR);
					break;
				case 1:
					intent.putExtra(LensBaseActivity.ARG_INDEX, LensConstant.INDEX_SKIN);
					break;
				case 2:
					intent.putExtra(LensBaseActivity.ARG_INDEX, LensConstant.INDEX_IRIS);
					break;
				case 3:
					intent.putExtra(LensBaseActivity.ARG_INDEX, LensConstant.INDEX_NAEVUS);
					break;
				}
				startActivity(intent);
			}
		});
	}

	// 再有物理menu按键时可以强制显示overflow
	private void getOverflow() {
		try {
			ViewConfiguration mConfiguration = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				try {
					menuKeyField.setBoolean(mConfiguration, false);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
