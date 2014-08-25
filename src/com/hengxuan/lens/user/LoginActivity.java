package com.hengxuan.lens.user;

import org.json.JSONException;
import org.json.JSONObject;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.R;
import com.hengxuan.lens.http.HttpError;
import com.hengxuan.lens.http.HttpGroup;
import com.hengxuan.lens.http.HttpGroupSetting;
import com.hengxuan.lens.http.HttpGroupaAsynPool;
import com.hengxuan.lens.http.HttpResponse;
import com.hengxuan.lens.http.HttpSetting;
import com.hengxuan.lens.http.constant.ConstFuncId;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	// user name
	private String mUserName;
	private String mPassword;
	private String mLastUserName;
	private String mLastWrongPW;

	private EditText etName;
	private EditText etPW;
	private TextView tvLogin;
	private TextView tvRegister;
	private TextView tvFindPW;

	private TextView tvMessage;

	// µÇÂ¼ºóÌø×ªµ½
	private Intent goIntent;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// µÇÂ¼Ê§°Ü
				Log.d("daizhx", "handle message = " + msg.what);
				tvMessage.setVisibility(View.INVISIBLE);
				break;
			case 2:
				// µÇÂ¼³É¹¦-Ìø×ª
				if (goIntent != null) {
					startActivity(goIntent);
				}
				finish();
				break;
			default:
				break;
			}
		};
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		if (Integer.valueOf(Build.VERSION.SDK) >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			setTitle(R.string.setting);
		}
		initView();
		if (getIntent().getExtras() != null) {
			goIntent = getIntent().getExtras().getParcelable("destIntent");
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		etName = (EditText) findViewById(R.id.et_user_name);
		etName.setOnClickListener(this);
		etPW = (EditText) findViewById(R.id.et_pw);
		etPW.setOnClickListener(this);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvRegister.setOnClickListener(this);
		tvFindPW = (TextView) findViewById(R.id.tv_find_pw);
		tvFindPW.setOnClickListener(this);

		tvMessage = (TextView) findViewById(R.id.tv_message);
		tvLogin = (TextView) findViewById(R.id.tv_login);
		tvLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.et_user_name:

			break;
		case R.id.et_pw:
			break;
		case R.id.tv_register:
			break;
		case R.id.tv_find_pw:
			break;
		case R.id.tv_login:
			Login();
			break;
		default:
			break;
		}
	}

	// ÓÃ»§µã»÷µÇÂ¼°´Å¥
	private void Login() {
		//Òþ²ØÈí¼üÅÌ
		((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken()
				,InputMethodManager.HIDE_NOT_ALWAYS);
		mUserName = etName.getText().toString();
		mPassword = etPW.getText().toString();
		if (mUserName.equals("") || mPassword.equals("")) {
			// ÊäÈëÎª¿Õ
			handle(1, getString(R.string.no_name_pw));
		} else {
			// ÁªÍø²Ù×÷
			HttpSetting httpSetting = new HttpSetting();
			JSONObject jsonParams = new JSONObject();
			try {
				jsonParams.put("password", mPassword);
				jsonParams.put("username", mUserName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpSetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_LOGIN);
			httpSetting.setJsonParams(jsonParams);
			httpSetting.setListener(new HttpGroup.OnAllListener() {

				@Override
				public void onProgress(int i, int j) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onError(HttpError httpError) {
					// TODO Auto-generated method stub
					Log.d("login", "onError.....");
				}

				@Override
				public void onEnd(HttpResponse httpresponse) {
					// TODO Auto-generated method stub
					if (httpresponse.getJSONObject() != null) {
						try {
							String s5 = httpresponse.getJSONObject().getJSONObject("registerInfo").get("pin").toString();
							Log.d("login", "Login pin.." + s5);
						} catch (Exception exception) {
							mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									handle(1, getString(R.string.login_data_error));
								}
							});
//							handle(1, getString(R.string.login_data_error));
							StringBuilder stringbuilder = new StringBuilder("error message:");
							Log.d("login",stringbuilder.append(exception.getMessage()).toString());
						}
					} else {
						Log.d("login", "get empty string.....");
						handle(1, getString(R.string.login_data_error));
					}
				}

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					Log.d("login", "onStart.....");
				}
			});
			httpSetting.setNotifyUser(true);
			httpSetting.setShowProgress(true);
			HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpSetting);
		}
	}

	private void handle(final int code, String msg) {
		// TODO Auto-generated method stub
		Animation showAction = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		showAction.setDuration(500);
		showAction.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(code);
					}
				}, 2000);
			}
		});
		tvMessage.setAnimation(showAction);
		tvMessage.setText(msg);
		tvMessage.setVisibility(View.VISIBLE);

	}
}
