package com.hengxuan.lens.hair;

import com.hengxuan.lens.LensBaseActivity;
import com.hengxuan.lens.LensConstant;
import com.hengxuan.lens.LensPhotoList;
import com.hengxuan.lenshair.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class HairEntryActivity extends Activity {
	// capture new photo
	private Button btnNew;
	// review old photo
	private Button btnOld;
	private Button btnHelp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.hair_analysis);
		setContentView(R.layout.lens_entry_menu);
		btnNew = (Button) findViewById(R.id.new_photo);
		btnOld = (Button) findViewById(R.id.old_photo);
		btnHelp = (Button) findViewById(R.id.help);
		// btnHelp.setVisibility(View.GONE);
		btnNew.setText(R.string.new_hair_photo);
		btnOld.setText(R.string.old_hair_photo);
		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HairEntryActivity.this,
						LensBaseActivity.class);
				int index = LensConstant.INDEX_HAIR;
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});

		btnOld.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HairEntryActivity.this,
						LensPhotoList.class);
				int index = LensConstant.INDEX_HAIR;
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});

		btnHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
}
