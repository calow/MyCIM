package com.example.cim.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.example.cim.R;
import com.example.testwebview.Demo;

public class WebViewFragment extends Fragment {

	private Context mContext;
	private View mBaseView;
	private WebView myWebView;

	private Handler mHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_webview, null);
		findView();
		init();
		return mBaseView;
	}

	public void findView() {
		myWebView = (WebView) mBaseView.findViewById(R.id.my_webview);
	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	public void init() {
		Bundle bundle = getArguments();
		String url = bundle.getString("url");
		myWebView.addJavascriptInterface(
				new Demo(mContext, myWebView, mHandler), "demo");
		myWebView.requestFocus();
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		myWebView.setWebChromeClient(new WebChromeClient() {
			/**
			 * ����Ĭ�ϵ�window.alertչʾ���棬����title����ʾΪ��������file:////��
			 */
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						view.getContext());

				builder.setTitle("�Ի���").setMessage(message)
						.setPositiveButton("ȷ��", null);

				// ����Ҫ�󶨰����¼�
				// ����keycode����84֮��İ���
				builder.setOnKeyListener(new OnKeyListener() {
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						Log.v("onJsAlert", "keyCode==" + keyCode + "event="
								+ event);
						return true;
					}
				});
				// ��ֹ��Ӧ��back�����¼�
				builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				result.confirm();// ��Ϊû�а��¼�����Ҫǿ��confirm,����ҳ�������ʾ�������ݡ�
				return true;
				// return super.onJsAlert(view, url, message, result);
			}

			/**
			 * ����Ĭ�ϵ�window.confirmչʾ���棬����title����ʾΪ��������file:////��
			 */
			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						view.getContext());
				builder.setTitle("�Ի���")
						.setMessage(message)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNeutralButton("ȡ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								});
				builder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						result.cancel();
					}
				});

				// ����keycode����84֮��İ��������ⰴ�����¶Ի�����Ϣ��ҳ���޷��ٵ����Ի��������
				builder.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						Log.v("onJsConfirm", "keyCode==" + keyCode + "event="
								+ event);
						return true;
					}
				});
				// ��ֹ��Ӧ��back�����¼�
				// builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
				// return super.onJsConfirm(view, url, message, result);
			}

			/**
			 * ����Ĭ�ϵ�window.promptչʾ���棬����title����ʾΪ��������file:////��
			 * window.prompt('����������������ַ', '618119.com');
			 */
			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						view.getContext());

				builder.setTitle("�Ի���").setMessage(message);

				final EditText et = new EditText(view.getContext());
				et.setSingleLine();
				et.setText(defaultValue);
				builder.setView(et)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm(et.getText().toString());
									}

								})
						.setNeutralButton("ȡ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								});

				// ����keycode����84֮��İ��������ⰴ�����¶Ի�����Ϣ��ҳ���޷��ٵ����Ի��������
				builder.setOnKeyListener(new OnKeyListener() {
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						Log.v("onJsPrompt", "keyCode==" + keyCode + "event="
								+ event);
						return true;
					}
				});

				// ��ֹ��Ӧ��back�����¼�
				// builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
				// return super.onJsPrompt(view, url, message, defaultValue,
				// result);
			}
		});
		myWebView.loadUrl(url);
	}
	
	public void dispatchGoBack() {
		if (myWebView.canGoBack()) {
			myWebView.goBack();
		}
	}
	
	public void setHandler(Handler handler){
		mHandler = handler;
	}
}
