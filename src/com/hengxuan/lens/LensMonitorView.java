package com.hengxuan.lens;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hengxuan.eht.logger.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LensMonitorView extends SurfaceView implements SurfaceHolder.Callback{

	public static final String TAG = "LensMonitorView";
	private IrisMonitorThread thread;
	private LensMonitorParameter cmPara;
	//最新获得的图片
	private Bitmap capture_bitmap = null;
	private boolean retry = true;
    private HttpURLConnection httpURLconnection;
    private float previewRatio = (float) (16.0/9.0);
    private Context mContext;
    private SurfaceHolder sHolder;
    public int mWidth,mHeight;
    
    private CameraSource cs;
    
    //通过回调接口，来实现上下文对自身状态改变的响应
    public interface OnStateChangeListener{
    	void onGetCaptureImage();
    	void onStopRunning();
    }
    private OnStateChangeListener mOnStateChangeListener;
    
    public void setOnStateChangeListener(OnStateChangeListener f){
    	mOnStateChangeListener = f;
    }
    private boolean isFirstTime = true;
    
	public LensMonitorView(Context context, AttributeSet attrs) {
		super(context,attrs);
		// TODO Auto-generated constructor stub
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mContext = context;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		Log.d(TAG, "LensMonitorView get suggested size:"+width+"x"+height);
		if(width == 0 || height == 0){
			return;
		}
		if(((float)height/(float)width) > previewRatio){
			height = (int)(((float)width)*previewRatio);
		}else if(((float)height/(float)width) < previewRatio){
			width = (int)(((float)height)/previewRatio);
		}
		setMeasuredDimension(width, height);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surfaceChanged:LensMonitorView:"+width+"x"+height);
		thread.setSurfaceSize(width, height);
		mWidth = width;
		mHeight = height;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "LensMonitorView--Created");
        thread = new IrisMonitorThread(holder);
        sHolder = holder;
//		thread.setRunning(true);
//		try{
//			setCmPara(initParam());
//			thread.start();
//		}catch(IllegalThreadStateException e){}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "LensMonitorView--Destroyed");
		thread.closeCameraSource();
		if(null != httpURLconnection)
			httpURLconnection.disconnect(); 
			
		if(capture_bitmap != null) {
			if(!capture_bitmap.isRecycled()){
				capture_bitmap.recycle();   //回收图片所占的内存   
		        System.gc();  //提醒系统及时回收   
			}
		}
//		stop();
	}
	
	public LensMonitorParameter initParam()
	{
		LensMonitorParameter param = new LensMonitorParameter();
		param.setId(1);
		param.setConnectType(0);
		param.setIp("10.10.10.254");
		param.setLocal_dir("/sdcard");
		param.setName("192.168.1.102");
		param.setUsername("aaaaa");
		param.setPassword("123456");
		param.setPort(8080);
		param.setTime_out(2000);
		param.setConnectType(mContext.BIND_AUTO_CREATE);
		return param;
	}
	
	public void setRetry(boolean flag) {
		retry = flag;
	}

	public class IrisMonitorThread extends Thread{
		
		private SurfaceHolder mSurfaceHolder;
		
		private int mCanvasHeight = 1;
		
		private int mCanvasWidth = 1;
		
		public boolean mRun = false;
	    //暂停工作
		private boolean mPause = false;
//	    private CameraSource cs;
	        
	    private Canvas c = null;	    
	    
	    public IrisMonitorThread(SurfaceHolder surfaceHolder) {
			super();
			mSurfaceHolder = surfaceHolder;
		}
	    
	    public void setRunning(boolean b) {
            mRun = b;

            if (mRun == false) {
                
            }
        }
	    public void setPause(boolean b){
	    	mPause = b;
	    }

		@Override
		public void run() {
			// TODO Auto-generated method stub
				URL url;
				try {
					url = new URL("http://"+cmPara.getIp()+":"+cmPara.getPort());
					
					while(mRun){
						//暂停
						while(mPause){
							try {
								sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return;
							}
						}
						
						httpURLconnection = (HttpURLConnection)url.openConnection();
						httpURLconnection.setRequestMethod("GET");
						httpURLconnection.setReadTimeout(2*1000);
						
						// Log.e("isrun", "run capture");
						c = mSurfaceHolder.lockCanvas(null);
						if(captureImage(mCanvasWidth, mCanvasHeight, httpURLconnection)){
							//
						}else{
							//try again?
						}
						
						if (c != null) {
							mSurfaceHolder.unlockCanvasAndPost(c);
							c = null;
						}
						
						
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Log.d(TAG, Log.getStackTraceString(e1));
					mRun = false;
					mOnStateChangeListener.onStopRunning();
					// Toast.makeText(context, text, duration)
				}
		}
	    
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;    
            }
		}
		
		/**
		 * 从socketcamera获取图片并画到surface上
		 * @param width
		 * @param height
		 * @param httpURLconnection
		 * @return true-success false-fail
		 */
		private boolean captureImage(int width, int height,HttpURLConnection httpURLconnection){
			
			cs = new SocketCamera(width, height, true);
//	        cs.capture(c, httpURLconnection); //capture the frame onto the canvas
			if(cs.capture(c, httpURLconnection)){
	     
	        if(isFirstTime){
	        	mOnStateChangeListener.onGetCaptureImage();
	        	isFirstTime = false;
	        }
	        return true;
			}else{
				return false;
			}
		}
		
		public boolean saveImage(){
			
			String now = String.valueOf(System.currentTimeMillis());
			if(cs == null){
				return false;
			}
			cs.saveImage(cmPara.getLocal_dir()+"/ehealthtec/image", now+".PNG");
			
			return true;
		}
		
		public void closeCameraSource(){
			if(null != cs)
				cs.close();
		}
		
		public CameraSource getCameraSource() {
			return cs;
		}
	}
	
	public LensMonitorParameter getCmPara() {
		return cmPara;
	}
	
	public void setCmPara(LensMonitorParameter cmPara) {
		this.cmPara = cmPara;
	}
	
	public void setRunning(boolean b) {
        this.thread.setRunning(b);
    }
	
	public void start(){
		//thread initialized in the construction
		thread.setRunning(true);
		thread.start();
	}
	public void stop(){
		if(thread != null && thread.mRun){
			this.thread.setRunning(false);
			this.thread.interrupt();
			this.thread = null;
		}
	}
	
	public void pause(){
		Log.d(TAG, "pause");
		if(!thread.mPause){
			thread.setPause(true);
			thread.interrupt();
		}
	}
	//暂停后重新开始
	public void restart(){
		Log.d(TAG, "restart");
		if(thread.mPause){
			thread.setPause(false);
//			thread.interrupted();
		}
	}
	
//	public void drawBitmap(Bitmap b){
//		stop();
//		Canvas c = sHolder.lockCanvas();
//		if(mWidth == b.getWidth() && mHeight == b.getHeight()){
//			c.save();
//			c.drawBitmap(b, 0, 0, null);
//			c.restore();
//		}else{
//			Matrix matrix = new Matrix();
//			matrix.postRotate(90);
//			c.save();
//			Bitmap tmpBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
//			c.drawBitmap(tmpBitmap, 0, 0, null);
//			if(!tmpBitmap.isRecycled()){
//				tmpBitmap.recycle();   //回收图片所占的内存   
//		        System.gc();  //提醒系统及时回收   
//			}
//			c.restore();
//		}
//		if(c != null){
//			sHolder.unlockCanvasAndPost(c);
//			thread.notify();
//		}
//	}
//	
//	/**
//	 * 停止预览，选定照片
//	 */
//	public void PickUpImage(){
//		drawBitmap(capture_bitmap);
//	}
	
	public boolean getRunning(){
		return thread.mRun;
	}

	public IrisMonitorThread getThread() {
		return thread;
	}
	
	public Bitmap getCaptureImage()
	{
		return cs.getCaptureImage();
	}

}
