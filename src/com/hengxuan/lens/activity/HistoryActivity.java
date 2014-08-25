package com.hengxuan.lens.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.TextView;

public class HistoryActivity extends Activity {
	
	ExpandableListView expandableListView;
	CheckFileExpandListAdapter treeViewAdapter;

	private List<String> groupStrs = new ArrayList<String>();
	
	private Map<String, ArrayList<String>> groups = new HashMap<String, ArrayList<String>>();
	
	String path = Environment.getExternalStorageDirectory()
	.toString()
	+ File.separator
	+ "dxlphoto";
	
	File[] files = new File(path).listFiles();
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Integer.valueOf(Build.VERSION.SDK) >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			setTitle(R.string.lens_record);
		}
		setContentView(R.layout.activity_history);
		//遍历文件夹
		for(File file: files){
			//解析文件名 -index_time_o.png
			String fileName = file.getName();
			String[] ss = fileName.split("_");
			if(ss.length == 3 && ss[2].equals(".png")){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date dt = new Date(Long.parseLong(ss[1]));
				String timeStr = df.format(dt);
				Log.d("daizhx", "timeStr="+timeStr);
				//防止重复添加相同元素到list中
				boolean has = false;
				for(int j = 0; j < groupStrs.size(); j++){
					if(groupStrs.get(j).equals(timeStr)){
						has = true;
						groups.get(timeStr).add(fileName);
						break;
					}
				}
				if(has == false){
					groupStrs.add(timeStr);
					ArrayList<String> childs = new ArrayList<String>();
					childs.add(fileName);
					groups.put(timeStr, childs);
				}
				
			}
			
		}
		
		//排序
		Collections.sort(groupStrs, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				// TODO Auto-generated method stub
				int yl = Integer.parseInt(lhs.split("-")[0]);
				int ml = Integer.parseInt(lhs.split("-")[1]);
				int dl = Integer.parseInt(lhs.split("-")[2]);
				
				int yr = Integer.parseInt(rhs.split("-")[0]);
				int mr = Integer.parseInt(rhs.split("-")[1]);
				int dr = Integer.parseInt(rhs.split("-")[2]);
				
				if(yl < yr)return 1;
				if(yl > yr)return -1;
				if(ml < mr)return 1;
				if(ml > mr)return -1;
				if(dl < dr)return 1;
				if(dl > dr)return -1;
				return 0;
			}
			
		});
		
		
		expandableListView = (ExpandableListView)findViewById(R.id.expandlist);
		treeViewAdapter = new CheckFileExpandListAdapter(this, groupStrs, groups);
		expandableListView.setAdapter(treeViewAdapter);
		expandableListView.setChildDivider(new ColorDrawable(Color.TRANSPARENT));
		//没有图片时，显示无记录
		if(groupStrs.size() == 0){
			((TextView)findViewById(R.id.text)).setVisibility(View.VISIBLE);
		}
	}
	
}