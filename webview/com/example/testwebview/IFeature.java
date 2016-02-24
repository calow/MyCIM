package com.example.testwebview;

import org.json.JSONObject;


public interface IFeature {
	public String execute(IWebview pWebView, String action, String callbackId, JSONObject pArgs);
}
