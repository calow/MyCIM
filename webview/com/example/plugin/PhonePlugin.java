package com.example.plugin;

import org.json.JSONObject;

import com.example.testwebview.IFeature;
import com.example.testwebview.IWebview;

public class PhonePlugin implements IFeature {


	@Override
	public String execute(IWebview pWebView, String action, String callbackId,
			JSONObject pArgs) {
		return null;
	}

}
