package com.hengxuan.lens.http;

import android.content.Context;

import com.hengxuan.lens.http.constant.ConstHttpProp;
import com.hengxuan.lens.http.utils.PooledThread;
import com.hengxuan.lens.http.utils.ThreadPool;

public class HttpGroupaAsynPool extends HttpGroup {
	private Context mContext;
	private static HttpGroupaAsynPool mHttpGroupaAsynPool;

	private HttpGroupaAsynPool(HttpGroupSetting paramHttpGroupSetting) {
		super(paramHttpGroupSetting);
	}

	public static HttpGroupaAsynPool getHttpGroupaAsynPool() {
		if(mHttpGroupaAsynPool == null){
			HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
			localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSON);
			mHttpGroupaAsynPool = new HttpGroupaAsynPool(localHttpGroupSetting);
		}
		
		return mHttpGroupaAsynPool;
	}

	private static HttpGroup getHttpGroupaAsynPool(int paramInt) {
		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
//		localHttpGroupSetting.setMyActivity(this);
		localHttpGroupSetting.setType(paramInt);
		return getHttpGroupaAsynPool(localHttpGroupSetting);
	}

	private static HttpGroup getHttpGroupaAsynPool(
			HttpGroupSetting paramHttpGroupSetting) {
		HttpGroupaAsynPool localHttpGroupaAsynPool = new HttpGroupaAsynPool(paramHttpGroupSetting);
		// addDestroyListener(localHttpGroupaAsynPool);
		return localHttpGroupaAsynPool;
	}

	@Override
	public void execute(final HttpRequest httpRequest) {
		ThreadPool threadpool = PooledThread.getThreadPool();
		int i = httpRequest.getHttpSetting().getPriority();
		threadpool.offerTask(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (httpList.size() < 1)
					onStart();
				httpList.add(httpRequest);
				httpRequest.nextHandler();

			}

		}, i);
	}

}