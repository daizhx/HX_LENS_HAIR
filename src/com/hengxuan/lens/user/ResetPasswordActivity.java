package com.hengxuan.lens.user;

import com.hengxuan.lens.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ResetPasswordActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(User.isLogin){
			setContentView(R.layout.activity_change_pw);
			setTitle(R.string.reset_pw);
			Button btnSubmit = (Button)findViewById(R.id.btn_submit);
			btnSubmit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					changePassword();
				}
			});
		}else{
			setContentView(R.layout.activity_reset_pw);
			setTitle(R.string.find_pw);
			
		}
	}
	
	private void changePassword(){
		//TODO
	}
}
