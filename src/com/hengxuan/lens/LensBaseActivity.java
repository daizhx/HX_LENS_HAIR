/**
 * 自动开启wifi连接镜头，预览拍照
 */
package com.hengxuan.lens;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.MyDataBaseContract.ImagesInfo;
import com.hengxuan.lens.hair.HairAnalysisActivity;
import com.hengxuan.lens.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class LensBaseActivity extends FragmentActivity implements LensMonitorView.OnStateChangeListener{

	private static final String TAG = "Lens";
	public static final String ARG_INDEX = "index";
	private WifiManager mWifiManager;
	private WifiInfo currentWifiInfo;
	//拍摄照片的类别
	public int index;
	private int irisIndex;//左眼-1，右眼-2
	//图片保存的路径
	public String savedPath;
	
	private int currentNetWorkId = -1;
	//连接镜头状态
	private boolean isConnectedLens = false;
	private ProgressDialog pd;
	protected LensMonitorView lensMonitorView;
	//拍照按钮
	private ImageView shoot;
	//取消按钮
	private ImageView chacha;
	//确定按钮
	private ImageView confirm = null;
	//view
//	private View settingBar;
	

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				if (connect2Lens()) {
					onConnectSuccess();
				} else {
					onConnectFail();
				}
			} else if (intent.getAction().equals(
					WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				if (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1) == WifiManager.WIFI_STATE_ENABLED) {
					// scan lens ap and connect
					mWifiManager.startScan();
				}
			}

		}
	};

	private boolean connect2Lens() {
		List<ScanResult> APList = mWifiManager.getScanResults();
		for (ScanResult scanResult : APList) {
			if (scanResult.SSID.equals("EHT")) {
				List<WifiConfiguration> list = mWifiManager
						.getConfiguredNetworks();
				for (WifiConfiguration wifiConfiguration : list) {
					Log.d(TAG, "ssid:" + wifiConfiguration.SSID);
					if (wifiConfiguration.SSID != null
							&& wifiConfiguration.SSID.equals("\"" + "EHT"
									+ "\"")) {
						currentWifiInfo = mWifiManager.getConnectionInfo();
						setCurrentNetWorkId(currentWifiInfo
										.getNetworkId());
						mWifiManager.disconnect();
						mWifiManager.enableNetwork(wifiConfiguration.networkId,
								true);
						mWifiManager.reconnect();
						return true;
					}
				}

				// add conf
				WifiConfiguration conf = new WifiConfiguration();
				conf.SSID = "\"" + "EHT" + "\"";
				conf.preSharedKey = "\"" + "12345678" + "\"";
				mWifiManager.addNetwork(conf);
				for (WifiConfiguration wifiConfiguration : list) {
					if (wifiConfiguration.SSID != null
							&& wifiConfiguration.SSID.equals("\"" + "EHT"
									+ "\"")) {
						currentWifiInfo = mWifiManager.getConnectionInfo();
						setCurrentNetWorkId(currentWifiInfo
										.getNetworkId());
						mWifiManager.disconnect();
						mWifiManager.enableNetwork(wifiConfiguration.networkId,
								true);
						mWifiManager.reconnect();
						return true;
					}
				}
				// if found EHT AP,mostly return true,should not return
				// false,there must be something wrong
				return false;
			}
		}
		return false;
	}

	/**
	 * 判读是否连接到镜头
	 * @return
	 */
	public boolean isConnectLens() {
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		ssid = ssid.replaceAll("\"", "");
		if (ssid.length() < 3) {
			// prevent throwing a IndexOutBoundException
			return false;
		}
		if (!TextUtils.isEmpty(ssid) && ssid.substring(0, 3).equals("EHT")) {
			if(ssid.length() >3){
				return false;
			}
			return true;
		}
		return false;
	}

	public void onConnectSuccess() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConnectSuccess");
		unregisterReceiver(mBroadcastReceiver);
		Log.d(TAG, "index=" + index);
		switch (index) {
		case LensConstant.INDEX_IRIS:
			try {
				DatagramSocket s = new DatagramSocket();
				InetAddress local = InetAddress.getByName("10.10.10.254");
				byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
						0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
						0x3D, 0x30, 0x30, 0x30, 0x30, (byte) 0xD2 };
				DatagramPacket p = new DatagramPacket(message, message.length,
						local, 8080);
				s.send(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case LensConstant.INDEX_HAIR:
			try {
				DatagramSocket s = new DatagramSocket();
				InetAddress local = InetAddress.getByName("10.10.10.254");
				byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
						0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
						0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
				DatagramPacket p = new DatagramPacket(message, message.length,
						local, 8080);
				s.send(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case LensConstant.INDEX_SKIN:
			try {
				DatagramSocket s = new DatagramSocket();
				InetAddress local = InetAddress.getByName("10.10.10.254");
				byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
						0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
						0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
				DatagramPacket p = new DatagramPacket(message, message.length,
						local, 8080);
				s.send(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case LensConstant.INDEX_NAEVUS:
			try {
				DatagramSocket s = new DatagramSocket();
				InetAddress local = InetAddress.getByName("10.10.10.254");
				byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
						0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
						0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
				DatagramPacket p = new DatagramPacket(message, message.length,
						local, 8080);
				s.send(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		lensMonitorView.setCmPara(lensMonitorView.initParam());
		lensMonitorView.start();
	}

	public void onConnectFail() {
		Log.d(TAG, "onConnectFail");
		unregisterReceiver(mBroadcastReceiver);
		// TODO Auto-generated method stub
		pd.dismiss();
		AlertDialog alertDialog = new AlertDialog.Builder(LensBaseActivity.this).create();
		alertDialog.setMessage(getString(R.string.open_lens));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
				getString(R.string.confirm),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
//						restoreWifiInfo();
						finish();
					}
				});
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(arg2.getKeyCode() == KeyEvent.KEYCODE_BACK){
					finish();
					return true;
				}
				return false;
			}
		});
		alertDialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lens_base);
		initView();
		if(!isConnectedLens){
			pd = new ProgressDialog(this);
			pd.setMessage(getString(R.string.lens_connecting));
			pd.setCancelable(false);
			pd.show();
//			pd.show(this, null, getString(R.string.lens_connecting), false, true);
		}

		index = getIntent().getIntExtra("index", 0);
		if(index == 0){
			Log.d("entry error");
			return;
		}
		Log.d(TAG, "index = " + index);
		switch (index) {
		case LensConstant.INDEX_HAIR:
			setTitle(R.string.hair_detection);
			break;
		case LensConstant.INDEX_IRIS:
			setTitle(R.string.iris_detection);
			break;
		case LensConstant.INDEX_SKIN:
			setTitle(R.string.skin_detection);
			break;
		case LensConstant.INDEX_NAEVUS:
			setTitle(R.string.naevus_detection);
			break;
		}

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		registerReceiver(mBroadcastReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		if (mWifiManager.isWifiEnabled()) {
			if (isConnectLens()) {
				onConnectSuccess();
				return;
			} else {
				mWifiManager.startScan();
			}
		} else {
			// open wifi
			mWifiManager.setWifiEnabled(true);
		}
		
	}
	
	private void initView(){
		lensMonitorView = (LensMonitorView)findViewById(R.id.lens_monitor_view);
		//第一次获取图像时回调
		lensMonitorView.setOnStateChangeListener(this);
		shoot = (ImageView)findViewById(R.id.shoot);
		shoot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(confirm == null){
					lensMonitorView.pause();
					shoot.setImageResource(R.drawable.save);
					chacha.setVisibility(View.VISIBLE);
					confirm = shoot;
				}else{
//					String imagePath = savePhoto();
//					toAnalysis(index, imagePath);
					savedPath = savePhoto();
					popup();
				}
//				settingBar.setVisibility(View.GONE);
			}
		});
		chacha = (ImageView)findViewById(R.id.chacha);
		chacha.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				lensMonitorView.restart();
				chacha.setVisibility(View.GONE);
				shoot.setImageResource(R.drawable.ic_camera);
				confirm = null;
//				settingBar.setVisibility(View.VISIBLE);
			}
		});
//		settingBar = findViewById(R.id.setting);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		lensMonitorView.stop();
		// 断开镜头wifi
		Log.d(TAG, "onDestroy:restore wifi connect");
		if(isConnectLens()){
			restoreWifiInfo();
		}
		
		super.onDestroy();
	}



	public void restoreWifiInfo() {
		Log.d(TAG, "restoreWifiInfo");
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.disconnect();
		int networkId = getCurrentNetWorkId();
		if (networkId >= 0) {
			wifiManager.enableNetwork(networkId, true);
			wifiManager.reconnect();
		} else {
			// 3G
			wifiManager.setWifiEnabled(false);
		}

	}

	public void setCurrentNetWorkId(int i) {
		currentNetWorkId = i;
	}

	public int getCurrentNetWorkId() {
		return currentNetWorkId;
	}
	
	/**
	 * 标记iris是左眼还是右眼
	 * @param index 1-左眼 2-右眼
	 */
	public void setIrisIndex(int index){
		irisIndex = index;
	}

	@Override
	public void onGetCaptureImage() {
		// TODO Auto-generated method stub
		pd.dismiss();
		isConnectedLens = true;
	}
	
	@Override
	public void onStopRunning() {
		// TODO Auto-generated method stub
		AlertDialog aDialog = new AlertDialog.Builder(this).create();
		aDialog.setMessage(getString(R.string.lost_lens));
		aDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.yes), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	/**
	 * 
	 * @return the saved photo Path
	 */
	protected String savePhoto(){
		String filePath = null;
		Bitmap bmp = lensMonitorView.getCaptureImage();
		String savePath = Environment.getExternalStorageDirectory()
		.toString()
		+ File.separator
		+ "dxlphoto"
		+ File.separator;
		
		String prefix = "";
		if(index == LensConstant.INDEX_IRIS){
			if(irisIndex == LensConstant.LEFT_EYE){
				prefix = LensConstant.LEFT_EYE_INDEX + "_";
			}else{
				prefix = LensConstant.RIGHT_EYE_INDEX + "_";
			}
		}else if(index == LensConstant.INDEX_HAIR){
			prefix = LensConstant.INDEX_HAIR+"_";
		}else if(index == LensConstant.INDEX_NAEVUS){
			prefix = LensConstant.INDEX_NAEVUS + "_";
		}else if(index == LensConstant.INDEX_SKIN){
			prefix = LensConstant.INDEX_SKIN + "_";
		}
		
		String filename = prefix + String.valueOf(System.currentTimeMillis())+"_.png";
		File file = new File(savePath.concat(filename));
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if (bmp != null) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(savePath + filename);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				return savePath + filename;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return filePath;
	}
	
	String photoTag;
	protected void popup() {
		// TODO Auto-generated method stub
		
		final EditText ownerEt;
		
		LayoutInflater layoutInflater = getLayoutInflater();
		View v = layoutInflater.inflate(R.layout.popup_photo_confirm, null);
//		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		int displayWidth = outMetrics.widthPixels; 
		final PopupWindow popupWindow = new PopupWindow(v, (int)(displayWidth * 0.8), LayoutParams.WRAP_CONTENT);
		TextView tvTitle = (TextView)v.findViewById(R.id.title);
		switch (index) {
		case LensConstant.INDEX_IRIS:
			tvTitle.setText(R.string.iris_photo);
			RadioButton rb1 = (RadioButton)v.findViewById(R.id.rb1);
			rb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg1){
						//选中左眼
						irisIndex = LensConstant.LEFT_EYE;
						photoTag = getString(R.string.left_eye);
						
					}else{
						irisIndex = LensConstant.RIGHT_EYE;
						photoTag = getString(R.string.left_eye);
					}
				}
			});
			break;
		case LensConstant.INDEX_HAIR:
			tvTitle.setText(R.string.hair_photo);
			((RadioGroup)v.findViewById(R.id.radio_group)).setVisibility(View.GONE);
			((TextView)v.findViewById(R.id.eye_label)).setVisibility(View.GONE);
			photoTag = getString(R.string.hair);
			break;
		case LensConstant.INDEX_SKIN:
			tvTitle.setText(R.string.skin_photo);
			((RadioGroup)v.findViewById(R.id.radio_group)).setVisibility(View.GONE);
			((TextView)v.findViewById(R.id.eye_label)).setVisibility(View.GONE);
			photoTag = getString(R.string.skin);
			break;
		case LensConstant.INDEX_NAEVUS:
			tvTitle.setText(R.string.naevus_photo);
			((RadioGroup)v.findViewById(R.id.radio_group)).setVisibility(View.GONE);
			((TextView)v.findViewById(R.id.eye_label)).setVisibility(View.GONE);
			photoTag = getString(R.string.naevus);
			break;
		default:
			break;
		}
		ownerEt = (EditText)v.findViewById(R.id.et_owner);
//		ownerEt.setText(UserLogin.getUserName());
		Button btnCancel = (Button)v.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
		((Button)v.findViewById(R.id.btn_confirm)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String name = ownerEt.getText().toString();
				Log.d("daizhx", "name = "+ name);
				recordPhotoInfo(name,photoTag,savedPath,index);
				popupWindow.dismiss();
				toAnalysis(index, savedPath);

			}
		});
		
		
//		popupWindow.setContentView(view);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		popupWindow.update();
		popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}
	protected void recordPhotoInfo(String owner,String photoTag, String photoPath,int photoIndex) {
		// TODO Auto-generated method stub
		
		MyDbHelper myDbHelper = new MyDbHelper(this);
		SQLiteDatabase db = myDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String strdate = simpleDateFormat.format(date);
		
		values.put(ImagesInfo.COLUMN_NAME_OWNER, owner);
		values.put(ImagesInfo.COLUMN_NAME_TAG,photoTag);
		values.put(ImagesInfo.COLUMN_NAME_DATA, photoPath);
		values.put(ImagesInfo.COLUMN_NAME_DATE, strdate);
		values.put(ImagesInfo.COLUMN_NAME_TYPE, photoIndex);
		long newRowId = db.insert(ImagesInfo.TABLE_NAME, null, values);
		Log.d("daizhx", "newRowId="+newRowId);
	}
	
	//进入镜头分析界面
	public void toAnalysis(int index, String imagePath) {
		// TODO Auto-generated method stub
		Log.d(TAG, "To Analysis,index="+index+", imagepath="+imagePath);
		Intent intent = new Intent();
		switch (index) {
		case LensConstant.INDEX_IRIS:
			intent.setAction(LensConstant.ANA_IRIS);
			intent.putExtra("irisIndex", irisIndex);
			break;
		case LensConstant.INDEX_SKIN:
			intent.setAction(LensConstant.ANA_SKIN);
			break;
		case LensConstant.INDEX_HAIR:
			intent.setAction(LensConstant.ANA_HAIR);
			break;
		case LensConstant.INDEX_NAEVUS:
			intent.setAction(LensConstant.ANA_NAEVUS);
			break;
		default:
			return;
		}
		if(imagePath == null || imagePath.equals("")){
			return;
		}
		intent.putExtra(LensConstant.PHOTO_PATH, imagePath);
		startActivity(intent);
		finish();
	}
}
