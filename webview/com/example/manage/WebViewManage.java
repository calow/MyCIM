package com.example.manage;

import java.util.HashMap;
import java.util.Map;

import com.example.testwebview.FeatureManage;

import android.content.Context;
import android.webkit.WebView;

public class WebViewManage {

	public static WebViewManage sManage;

	// webview¶ÔÏóÈÝÆ÷
	public Map<String, WebView> webviewMap = new HashMap<String, WebView>();

	private WebViewManage() {

	}

	public static WebViewManage getInstance() {
		if (sManage == null) {
			synchronized (FeatureManage.class) {
				if (sManage == null) {
					sManage = new WebViewManage();
				}
			}
		}
		return sManage;
	}

	public void putWebview(String id, WebView webView) {
		webviewMap.put(id, webView);
	}

	public WebView getWebView(String id) {
		return webviewMap.get(id);
	}

	public boolean containWebView(String id) {
		boolean result = false;
		if (webviewMap.containsKey(id)) {
			result = true;
		}
		return result;
	}

	public void removeWebView(String id) {
		if (webviewMap.containsKey(id)) {
			webviewMap.remove(id);
		}
	}

	public void clearAll() {
		webviewMap.clear();
	}

}
