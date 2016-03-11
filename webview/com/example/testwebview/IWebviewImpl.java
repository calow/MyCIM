package com.example.testwebview;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;

public class IWebviewImpl implements IWebview {

	private Context mContext;
	private WebView mWebView;

	public IWebviewImpl(Context context, WebView webView) {
		this.mContext = context;
		this.mWebView = webView;
	}

	@Override
	public Context getContext() {
		return mContext;
	}

	@Override
	public Activity getActivity() {
		Activity activity = (Activity) mContext;
		return activity;
	}

	@Override
	public WebView getWebview() {
		return mWebView;
	}

	@Override
	public void loadUrl(String url) {
		mWebView.loadUrl(url);
	}

}
