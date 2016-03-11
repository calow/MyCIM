package com.example.tool;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.testwebview.IWebview;

public abstract class Tool{

	/**
	 * 该方法拦截和处理有页面工具（这里特指本地工具）的页面（称为工具内部页面）上的用户操作请求。
	 * @param pWebview 经过封装的webview对象
	 * @param array 从js端传到java端的参数列表
	 */
	public abstract void act(IWebview pWebview, JSONObject jsonObject);

	/**
	 * 该方法是无页面工具（这里特指本地工具）的入口方法。
	 * @param pWebview 经过封装的webview对象
	 * @param jsonObject 从js端传到java端的参数列表
	 */
	public abstract void toolMain(IWebview pWebview, JSONObject jsonObject);
	
	/**
	 * 该方法用于工具内部页面的转向（本地工具可以忽略此方法，因为页面目录可随意跳转而没拦截）
	 * @param pWebview 经过封装的webview对象
	 * @param jsonObject 从js端传到java端的参数列表
	 */
	public void forward(IWebview pWebview, JSONObject jsonObject){
		try {
			String url = jsonObject.getString("destination");
			pWebview.loadUrl(url);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 该方法用于调用工具使其运行，执行过程具体分为以下几种情形。
	 * 1）执行一个无页面工具：runTool将直接启动运行被调用工具，并将工具运行结果返回。
	 * 2）执行有页面工具：将请求直接重定向到工具运行页面。
	 * @param pWebview 经过封装的webview对象
	 * @param jsonObject 从js端传到java端的参数列表
	 */
	public void runTool(IWebview pWebview, JSONObject jsonObject) {
		
	}
}
