package com.example.cim.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cim.R;
import com.example.cim.network.API;
import com.example.cim.network.HttpRequest;
import com.example.cim.network.HttpRequest.HttpCompliteListener;
import com.example.cim.ui.base.CIMMonitorActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.view.TitleBarView;

public class RegisterInfoActivity extends CIMMonitorActivity {

	private Context mContext;
	private Button btn_complete;
	private TitleBarView mTitleBarView;
	private EditText et_password;
	private EditText et_userName;
	private String userName;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_userinfo);
		mContext = this;
		findView();
		initTitleView();
		init();
	}

	private void findView() {
		mTitleBarView = (TitleBarView) findViewById(R.id.title_bar);
		btn_complete = (Button) findViewById(R.id.register_complete);
		et_password = (EditText) findViewById(R.id.password);
		et_userName = (EditText) findViewById(R.id.name);
	}

	private void initTitleView() {
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE,
				View.GONE);
		mTitleBarView.setBtnLeft(R.drawable.boss_unipay_icon_back,
				R.string.back);
		mTitleBarView.setTitleText(R.string.title_register_info);
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void init() {
		btn_complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doRegister();
			}
		});
	}

	public void doRegister() {
		if (validate()) {
			showProgressDialog("��ʾ", "����ע�ᣬ���Ժ�......");
			Map<String, String> user = new HashMap<String, String>();
			user.put("UNickName", userName);
			user.put("UPassWord", password);
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
						String loginId = null;
						try {
							loginId = jsonObject.getString("loginId");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(mContext,
								RegisterResultActivity.class);
						intent.putExtra("loginId", loginId);
						startActivity(intent);
					}
				}
				
				@Override
				public void onResponseError(int code) {
					hideProgressDialog();
					showToast("ע���쳣:" + code);
				}

				@Override
				public void onRequestException(Exception e) {
					hideProgressDialog();
					showToast("ע�����:" + e);
				}
			});
			request.httpPost(API.USERREGISTER_URL, user);
		}
	}

	public boolean validate() {
		boolean result = true;
		userName = et_userName.getText().toString().trim();
		password = et_password.getText().toString().trim();
		if (userName == null || userName.equals("")) {
			result = false;
			showToast("�û�������Ϊ��");
		}
		if (userName.length() > 20) {
			result = false;
			showToast("�û������ܴ���20���ַ�");
		}
		if (password == null || password.equals("")) {
			result = false;
			showToast("���벻��Ϊ��");
		}
		if (password.length() < 6 && password.length() > 16) {
			result = false;
			showToast("���벻�ܴ���16�ַ�");
		}
		return result;
	}
	
}
