package com.example.testwebview;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.example.cim.R;
import com.example.cim.fragment.WebViewFragment;
import com.example.cim.ui.MainActivity;
import com.example.cim.ui.base.CIMMonitorFragmentActivity;
import com.example.cim.view.TitleBarView;

public class WebviewActivity extends CIMMonitorFragmentActivity{

	private TitleBarView mTitleBarView;
	private String title = "";
	private String url;
	private WebViewFragment recent;

	private Map<String, WebViewFragment> maps = new HashMap<String, WebViewFragment>();

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int index = msg.arg1;
			Bundle bundle = msg.getData();
			switch (index) {
			case 1:

				String url = bundle.getString("url");
				if (!maps.containsKey(url)) {
					WebViewFragment fragment = new WebViewFragment();
					fragment.setHandler(mHandler);
					Bundle b = new Bundle();
					b.putString("url", url);
					fragment.setArguments(b);
					FragmentManager fm = getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.add(R.id.fl_webview_activity, fragment);
					ft.show(fragment).hide(recent).commit();
					recent = fragment;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview_activity);
		getIntentContent();
		findView();
		init();
	}

	public void getIntentContent() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			title = bundle.getString("title");
			url = bundle.getString("url");
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		initWithNewIntent(intent);
	}

	public void initWithNewIntent(Intent intent) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			title = bundle.getString("title");
			url = bundle.getString("url");
			mTitleBarView.setTitleText(title);
			FragmentManager fm = getSupportFragmentManager();
			WebViewFragment myFragment;
			if (maps.containsKey(url)) {
				myFragment = maps.get(url);
			} else {
				myFragment = new WebViewFragment();
				Bundle b = new Bundle();
				b.putString("url", url);
				myFragment.setArguments(b);
				FragmentTransaction ft = fm.beginTransaction();
				ft.add(R.id.fl_webview_activity, myFragment).commit();
				maps.put(url, myFragment);
			}
			FragmentTransaction ft = fm.beginTransaction();
			if (recent != null) {
				ft.hide(recent);
			}
			ft.show(myFragment);
			ft.commit();
			recent = myFragment;
		}
	}

	private void findView() {
		mTitleBarView = (TitleBarView) findViewById(R.id.title_bar);
	}

	private void init() {
		// ≥ı ºªØtitle
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE,
				View.GONE);
		mTitleBarView.setTitleText(title);
		mTitleBarView.setBtnLeft(R.drawable.boss_unipay_icon_back,
				R.string.back);
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WebviewActivity.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_back, R.anim.activity_finish);
//				finish();
			}
		});
		FragmentManager fm = getSupportFragmentManager();
		WebViewFragment myFragment;
		if (maps.containsKey(url)) {
			myFragment = maps.get(url);
		} else {
			myFragment = new WebViewFragment();
			Bundle bundle = new Bundle();
			bundle.putString("url", url);
			myFragment.setArguments(bundle);
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.fl_webview_activity, myFragment).commit();
			maps.put(url, myFragment);
		}
		FragmentTransaction ft = fm.beginTransaction();
		if (recent != null) {
			ft.hide(recent);
		}
		ft.show(myFragment);
		ft.commit();
		recent = myFragment;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			recent.dispatchGoBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
