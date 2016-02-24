package com.example.plugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testwebview.IFeature;
import com.example.testwebview.IWebview;
import com.example.testwebview.JSUtil;

public class MediaPlugin implements IFeature {

	@Override
	public String execute(final IWebview pWebView, final String action, final String callbackId, final JSONObject pArgs) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				JSONObject jsonObject = null;
				try {
					System.out.println("name = " + pArgs.get("name") + "--sex = " + pArgs.get("sex") + "--");
					JSONArray education = pArgs.getJSONArray("education");
					JSONObject school;
					for(int i = 0; i < education.length(); i++){
						school = (JSONObject) education.get(i);
						System.out.println("schoolName = " + school.get("schoolName") + "--address = " + school.get("address"));
					}
					jsonObject = new JSONObject();
					jsonObject.put("value1", 111);
					jsonObject.put("value2", 222);
					JSUtil.execCallback(pWebView, callbackId, jsonObject.toString(),
							JSUtil.OK, JSUtil.UNCONTINUED);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return null;
	}

}
