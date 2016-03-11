package com.example.tool;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.testwebview.IWebview;

public abstract class Tool{

	/**
	 * �÷������غʹ�����ҳ�湤�ߣ�������ָ���ع��ߣ���ҳ�棨��Ϊ�����ڲ�ҳ�棩�ϵ��û���������
	 * @param pWebview ������װ��webview����
	 * @param array ��js�˴���java�˵Ĳ����б�
	 */
	public abstract void act(IWebview pWebview, JSONObject jsonObject);

	/**
	 * �÷�������ҳ�湤�ߣ�������ָ���ع��ߣ�����ڷ�����
	 * @param pWebview ������װ��webview����
	 * @param jsonObject ��js�˴���java�˵Ĳ����б�
	 */
	public abstract void toolMain(IWebview pWebview, JSONObject jsonObject);
	
	/**
	 * �÷������ڹ����ڲ�ҳ���ת�򣨱��ع��߿��Ժ��Դ˷�������Ϊҳ��Ŀ¼��������ת��û���أ�
	 * @param pWebview ������װ��webview����
	 * @param jsonObject ��js�˴���java�˵Ĳ����б�
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
	 * �÷������ڵ��ù���ʹ�����У�ִ�й��̾����Ϊ���¼������Ρ�
	 * 1��ִ��һ����ҳ�湤�ߣ�runTool��ֱ���������б����ù��ߣ������������н�����ء�
	 * 2��ִ����ҳ�湤�ߣ�������ֱ���ض��򵽹�������ҳ�档
	 * @param pWebview ������װ��webview����
	 * @param jsonObject ��js�˴���java�˵Ĳ����б�
	 */
	public void runTool(IWebview pWebview, JSONObject jsonObject) {
		
	}
}
