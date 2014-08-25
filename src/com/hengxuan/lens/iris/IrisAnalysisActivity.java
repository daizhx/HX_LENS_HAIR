package com.hengxuan.lens.iris;

import java.io.File;

import com.hengxuan.lens.LensConstant;
import com.hengxuan.lens.R;
import com.hengxuan.lens.http.utils.DPIUtils;
import com.hengxuan.lens.utils.ContextUtils;
import com.hengxuan.lens.utils.PreferencesUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class IrisAnalysisActivity extends Activity {
	private static final String TAG = "IrisAnalysisActivity";
	// photo image path
	private String imagePath;
	private int iris_index = -1;
	private String[] iris_image_paths = { null };
	//��Ĥ��Ƭ
	private IrisImageView irisImageView;
	private int displayHeight;
	private LinearLayout linearL;
	private View getIrisView;
	private PointF centerPoint;
	private float getStandardR;
	private float getStandardMidR;
	private float getStandardMinR;
	//�������Ƿ�ϲ�������ɵ�����
	private boolean irisMergedTag[] = null;
	//�ϲ���ť
	private Button ivMergeBtn;
	//���ؼ� FrameLayout�Ĵ�С
	public static int mContainerWidth;
	public static int mContainerHeight;
	public static int actionBarHeight;
	public static int statusBarHeight;

	public IrisAnalysisActivity(){
		this.irisMergedTag = new boolean[2];
		//��ʼ��Ϊfalse
		this.irisMergedTag[0] = false;
		this.irisMergedTag[1] = false;
		
	}

	public View getStandardView() {
		float center_x = mContainerWidth / 2;
		float center_y = mContainerHeight / 2;
		View irisView = new CanvasIris(IrisAnalysisActivity.this, iris_index,
				center_x, center_y);
		return irisView;
	}

	private void initView() {
		
		irisImageView = (IrisImageView) findViewById(R.id.irisMyView);
		
		final FrameLayout container = (FrameLayout)findViewById(R.id.container);
		ViewTreeObserver viewTreeObserver = container.getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
				mContainerWidth = container.getWidth();
				mContainerHeight = container.getHeight();
//				Log.d(TAG, "container FL -- width="+width+",height="+height);
				irisImageView.initObjList(iris_image_paths, 0);
				
				getIrisView = getStandardView();
				linearL = (LinearLayout) findViewById(R.id.irisll);
				linearL.addView(getIrisView);
			}
		});
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				mContainerWidth = container.getWidth();
				mContainerHeight = container.getHeight();
				return true;
			}
		});
		
		ivMergeBtn = (Button)findViewById(R.id.merge_btn);
		ivMergeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getStandardR = ((CanvasIris) getIrisView).getMaxR();
				getStandardMinR = ((CanvasIris) getIrisView).getMinR();
				getStandardMidR = ((CanvasIris) getIrisView).getMidR();
				if(0 != getStandardMinR && 0 != getStandardMidR && 0 != getStandardR){
					mergeIrisView();
				}
				
			}
		});
		
		// set default display
		DPIUtils.setDefaultDisplay(super.getWindowManager().getDefaultDisplay());
		DPIUtils.setDensity(super.getResources().getDisplayMetrics().density);
		actionBarHeight = ContextUtils.getActionBarHeight(this);
		statusBarHeight = getStatusBarHeight();
	}
	
	/**
	 * not safety,maybe return 0
	 * @return
	 */
	public int getStatusBarHeight(){
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	      if (resourceId > 0) {
	          result = getResources().getDimensionPixelSize(resourceId);
	      }
	      return result;
	}
	public int getDisplayHeight() {
		if (displayHeight == -1) {
			DisplayMetrics metrics = super.getResources().getDisplayMetrics();
			if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				displayHeight = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			} else {
				displayHeight = Math.max(metrics.widthPixels,
						metrics.heightPixels);
			}
		}
		return displayHeight;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
//		Bundle bundle = intent.getExtras();
//		if(bundle != null){
//			imagePath = bundle.getString("image_path");
//			iris_index = bundle.getInt("iris_image_index");
//			putInt2Preference("currentIndex", iris_index);
//			iris_image_paths[0] = imagePath;
//		}else{
//			//TODO
//			Log.e("iris", "iris analysis can not get args,finished!");
//			finish();
//		}
		imagePath = intent.getStringExtra(LensConstant.PHOTO_PATH);
		iris_index = intent.getIntExtra(LensConstant.EYES_INDEX, -1);
//		putInt2Preference("currentIndex", iris_index);
		PreferencesUtils.putInt2Preferences(this, "currentIndex", iris_index);
		iris_image_paths[0] = imagePath;
		if(iris_index == -1){
			finish();
			return;
		}
		setContentView(R.layout.actvity_iris_analysis);
		setTitle(R.string.iris_analysis);
		initView();
		
	}

	// �ϲ����񵽺�Ĥ��Ƭ��
	private void mergeIrisView() {
		centerPoint = ((CanvasIris) getIrisView).getCenter();
		if (0 != getStandardMinR && 0 != getStandardMidR && 0 != getStandardR) {
			ProgressDialog proDialog = ProgressDialog.show(
					IrisAnalysisActivity.this,
					getResources().getString(R.string.merge_image),
					getResources().getString(R.string.merge_wait));
			new IrisImageHandlerThread(proDialog, 2, getIrisView,
					centerPoint, getStandardR, getStandardMinR, getStandardMidR)
					.start();
			linearL.removeAllViews();
			getIrisView = null;
			linearL = null;
		}

	}
	
	private class IrisImageHandlerThread extends Thread {
		private ProgressDialog progressDlg = null;
		private int index;
		private View getCurrentView;
		private PointF getCenterP = new PointF();
		private float getStandardR, getStandardMinR, getStandardMidR;

		public IrisImageHandlerThread(ProgressDialog progressDialog, int index,
				View view, PointF getCenterP, float getStandardR,
				float getStandardMinR, float getStandardMidR) {
			this.progressDlg = progressDialog;
			this.index = index;
			getCurrentView = view;
			this.getCenterP = getCenterP;
			this.getStandardR = getStandardR;
			this.getStandardMinR = getStandardMinR;
			this.getStandardMidR = getStandardMidR;
		}

		@Override
		public void run() {
			// ����ͼƬ�ϲ�������
			irisImageView.setTouchEventHandler(index,
					iris_index, getCurrentView,
					getCenterP, getStandardR, getStandardMinR, getStandardMidR);
			if (index == 2){
				irisImageView.isCompleted(true);
			}
			// ���úϲ��ı�־
			irisMergedTag[iris_index] = true;
			// ��ʼ����Ĥ��ص���Ϣ
			IrisDataCache.getInstance().initIrisDataByIndex(iris_index);
			// ɾ��������ʾ��
			this.progressDlg.dismiss();
		}

	}
		
}