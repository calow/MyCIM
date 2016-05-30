package com.example.cim.ui;

import com.example.cim.R;
import com.example.cim.manager.CIMPushManager;
import com.example.cim.nio.constant.Constant;
import com.example.cim.test.SDManager;
import com.example.cim.ui.base.CIMMonitorActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.util.SpUtil;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class SplashActivity extends CIMMonitorActivity {

	protected static final String TAG = "SplashActivity";

	boolean initComplete = false;
	private Context mContext;
	private ImageView mImageView;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = View.inflate(this, R.layout.activity_welcome, null);
		setContentView(view);
		mContext = this;
		mImageView = (ImageView) findViewById(R.id.welcome_img);

		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		init();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@SuppressWarnings("static-access")
	private void init() {
		mImageView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				sp = SpUtil.getInstance().getSharedPreference(mContext);
				boolean isFirst = SpUtil.getInstance().isFirst(sp);
				if(!isFirst){
					SDManager manager = new SDManager(mContext);
					manager.moveUserIcon();
					SpUtil.getInstance().setBooleanSharedPerference(sp, "isFirst", true);
				}
				String userName = CIMDataConfig.getString(SplashActivity.this,  CIMDataConfig.KEY_ACCOUNT);
				Intent intent = null;
				if(userName == null){
					intent = new Intent(mContext, LoginActivity.class);
				}else{
					intent = new Intent(mContext, MainActivity.class);
				}
				startActivity(intent);
				finish();
			}
		}, 2000);
	}
}
