package com.example.testwebview;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;

public interface IWebview {

	public Context getContext();
	public Activity getActivity();
	public WebView getWebview();
}
