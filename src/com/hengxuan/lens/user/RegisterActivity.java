package com.hengxuan.lens.user;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.lens.R;
import com.hengxuan.lens.http.HttpError;
import com.hengxuan.lens.http.HttpGroup;
import com.hengxuan.lens.http.HttpGroupaAsynPool;
import com.hengxuan.lens.http.HttpResponse;
import com.hengxuan.lens.http.HttpSetting;
import com.hengxuan.lens.http.constant.ConstFuncId;
import com.hengxuan.lens.http.constant.ConstHttpProp;
import com.hengxuan.lens.utils.PreferencesUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	private EditText etName;
	private EditText etPW1;
	private EditText etPW2;
	private EditText etEmail;
	private EditText etPhone;

	private String mName;
	private String mPW1;
	private String mPW2;
	private String mEmail;
	private String mPhone;

	private TextView tvRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		etName = (EditText) findViewById(R.id.et_user_name);
		etPW1 = (EditText) findViewById(R.id.et_pw1);
		etPW2 = (EditText) findViewById(R.id.et_pw2);
		etEmail = (EditText) findViewById(R.id.et_email);
		etPhone = (EditText) findViewById(R.id.et_phone);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void register() {
		getRegisterUserInfo();
		// 输入的资料不符合规则
		if (!inputCheck())
			return;

		JSONObject jsonobject = new JSONObject();
		try {
			jsonobject.put("username", mName);
			jsonobject.put("password", mPW1);
			jsonobject.put("email", mEmail);
			if (!TextUtils.isEmpty(mPhone)) {
				jsonobject.put("phone", mPhone);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// jsonobject.put("uuid", StatisticsReportUtil.readDeviceUUID());
		HttpSetting httpsetting = new HttpSetting();
		httpsetting.setReadTimeout(600);
		httpsetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_REGISTER);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			public void onEnd(HttpResponse httpresponse) {
				Log.d("RegisterActivity",
						"onRegister.HttpGroup.OnAllListener.onEnd");
				if (httpresponse.getJSONObject() != null) {
					final int getcode = httpresponse.getJSONObject()
							.getIntOrNull("code");
					// JSONObjectProxy jsonobjectProxy =
					// httpresponse.getJSONObject().getJSONObject("registerInfo");
					if (getcode == 1) {
						try {
							String pinName = httpresponse.getJSONObject()
									.getJSONObject("registerInfo")
									.getStringOrNull("pin");
							PreferencesUtils.putString2Prefs(RegisterActivity.this, ConstHttpProp.USER_PIN,
									pinName);
							User.setLogin(mName, mPW1, true);
							
							finish();
							if (Log.D)
								Log.d("Register", "successs to register");
						} catch (Exception exception) {
							if (Log.D) {
								StringBuilder stringbuilder = new StringBuilder(
										"error message:");
								String s = stringbuilder.append(
										exception.getMessage()).toString();
								Log.d("Register", s);
							}
						}
					} else {
						String s = getText(
								R.string.register_error_username_repeat)
								.toString();
					}
				} else {
					String s = getText(R.string.register_err_busy).toString();
				}
			}

			public void onError(HttpError httperror) {
				if (Log.D) {
					Log.d("RegisterActivity",
							"onRegister.HttpGroup.OnAllListener.onError");
				}
			}

			public void onProgress(int i, int j) {
			}

			public void onStart() {
				if (Log.D) {
					Log.d("RegisterActivity",
							"onRegister.HttpGroup.OnAllListener.onStart");
				}

			}
		});
		httpsetting.setNotifyUser(true);
		httpsetting.setShowProgress(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(RegisterActivity.this).add(
				httpsetting);
	}

	/**
	 * 检测用户输入的资料
	 * 
	 * @return true-pass,false-failed
	 */
	private boolean inputCheck() {
		boolean flag = true;
		// 检查用户名
		if (TextUtils.isEmpty(mName)) {
			etName.setError(Html.fromHtml("<font color=#00ff00>"
					+ getResources().getString(R.string.login_user_name_hint)
					+ "</font>"));
			flag = false;
		} else {
			if (!nameCheck(mName)) {
				etName.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.user_name_hint)
						+ "</font>"));
				flag = false;
			}
		}
		// 检查邮箱输入格式是否不对
		if (TextUtils.isEmpty(mEmail)) {
			etEmail.setError(Html.fromHtml("<font color=#00ff00>"
					+ getResources().getString(R.string.not_mail_format)
					+ "</font>"));
			flag = false;
		} else {
			if (!mailCheck(mEmail)) {
				etEmail.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.not_mail_format)
						+ "</font>"));
				flag = false;
			}
		}
		// 检测密码是否一致
		if (TextUtils.isEmpty(mPW1)) {
			String s1 = getString(R.string.login_user_password_hint);
			etPW1.setError(Html.fromHtml("<font color=#00ff00>" + s1
					+ "</font>"));
			flag = false;
		} else {
			if (!mPW1.equals(mPW2)) {
				etPW2.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.password_not_same)
						+ "</font>"));
				flag = false;
			}
			if (!passwordCheck(mPW1, 6, 20)) {
				etPW1.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.user_password_hint)
						+ "</font>"));
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param addr
	 * @return true -success,false - fail
	 */
	private boolean mailCheck(String addr) {
		boolean flag = false;
		String s = addr.trim();
		if (!TextUtils.isEmpty(s)) {
			String suffixs = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			flag = Pattern.compile(suffixs).matcher(s).matches();
		}
		return flag;
	}

	/**
	 * 检查用户名是否符合规则
	 * 
	 * @return true
	 */
	private boolean nameCheck(String name) {
		boolean flag = false;
		String s = name.trim();
		if (!TextUtils.isEmpty(s)) {
			int length = s.length();
			if (4 <= length & length < 20) {
				flag = Pattern.compile("[\\w一-龥\\-a-zA-Z0-9_]+").matcher(s)
						.matches();
			}
		}
		return flag;
	}

	protected boolean passwordCheck(String pw, int i, int j) {
		boolean flag = false;
		String s = pw.trim();
		if (!TextUtils.isEmpty(s)) {
			String ss = (new StringBuilder("[a-zA-Z_0-9\\-]{")).append(i)
					.append(",").append(j).append("}").toString();
			flag = Pattern.compile(ss).matcher(s).matches();
		}
		return flag;
	}

	protected void getRegisterUserInfo() {
		mName = etName.getText().toString().trim();
		mEmail = etEmail.getText().toString().trim();
		// sRegPhone = mRegisterPhone.getText().toString();
		mPW1 = etPW1.getText().toString().trim();
		mPW2 = etPW2.getText().toString().trim();
		mPhone = etPhone.getText().toString().trim();

	}
}
