package com.example.cim.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.cim.R;
import com.example.cim.network.API;
import com.example.cim.network.HttpRequest;
import com.example.cim.network.HttpRequest.HttpCompliteListener;
import com.example.cim.ui.base.CIMMonitorActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.util.LogUtils;
import com.example.cim.view.TextURLView;

public class LoginActivity extends CIMMonitorActivity {
	
	private Context mContext;
	private RelativeLayout rl_user;
	private Button login;
	private Button register;
	private TextURLView mTextViewURL;
	
	private EditText account;
	private EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mContext=this;
		findView();
		initTvUrl();
		init();
	}
	
	private void findView(){
		rl_user = (RelativeLayout) findViewById(R.id.rl_user);
		account = (EditText) findViewById(R.id.account);
		password = (EditText) findViewById(R.id.password);
		login = (Button) findViewById(R.id.login);
		register=(Button) findViewById(R.id.register);
		mTextViewURL = (TextURLView) findViewById(R.id.tv_forget_password);
	}
	
	private void initTvUrl(){
		mTextViewURL.setText(R.string.forget_password);
	}
	
	private void init(){
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.login_anim);
		animation.setFillAfter(true);
		rl_user.startAnimation(animation);
		
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doLogin();
			}
		});
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext, RegisterPhoneActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void doLogin() {
		showProgressDialog("提示", "正在登陆，请稍后......");
		Map<String, String> user = new HashMap<String, String>();
		user.put("ULoginId", account.getText().toString().trim());
		user.put("UPassWord", password.getText().toString().trim());
		HttpRequest request = new HttpRequest(new HttpCompliteListener() {
			
			@Override
			public void onResponseSuccess(String json) {
				hideProgressDialog();
				JSONObject jsonObject = null;
				String code = null;
				try {
					jsonObject = new JSONObject(json);
					code = jsonObject.getString("code");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(code != null && code.equals("200")){
					String userName = null;
					try {
						userName = jsonObject.getString("userName");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					CIMDataConfig.putString(LoginActivity.this, CIMDataConfig.KEY_USERNAME, userName);
					Intent intent = new Intent(mContext, MainActivity.class);
					CIMDataConfig.putString(LoginActivity.this, CIMDataConfig.KEY_ACCOUNT,
							account.getText().toString().trim());
					startActivity(intent);
					finish();
				}
			}
			
			@Override
			public void onResponseError(int code) {
				hideProgressDialog();
				showToast("登录异常:" + code);
			}

			@Override
			public void onRequestException(Exception e) {
				hideProgressDialog();
				showToast("登录出错:" + e);
			}
		});
		request.httpPost(API.UserLogin_URL, user);
	}

	@Override
	public void onBackPressed() {
//		CIMPushManager.destory(this);
		this.finish();
	}
	
}
