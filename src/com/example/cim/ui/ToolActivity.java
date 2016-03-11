package com.example.cim.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.example.cim.R;
import com.example.cim.nio.constant.Constant;
import com.example.cim.ui.base.CIMMonitorFragmentActivity;
import com.example.cim.view.TitleBarView;
import com.example.manage.WebViewManage;
import com.example.testwebview.Demo;

public class ToolActivity extends CIMMonitorFragmentActivity implements
		OnClickListener {
	
	private WebView myWebView;
	private FrameLayout mLayout;
	private TitleBarView mTitleBarView;
	private String toolId;
	private String toolName;
	private String description;
	private String tvId;
	private String url;
	
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int index = msg.arg1;
			Bundle bundle = msg.getData();
			switch (index) {
			case 1:
				WebView view = new WebView(ToolActivity.this);
				view.setId(Integer.parseInt(bundle.getString("id")));
				FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				layoutparams.setMargins(0,
						Integer.parseInt(bundle.getString("marginTop")), 0,
						Integer.parseInt(bundle.getString("marginBottom")));
				mLayout.addView(view, layoutparams);
				WebViewManage.getInstance().putWebview(bundle.getString("id"),
						view);
				view.loadUrl(bundle.getString("path"));
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tool_activity);
		getIntentContent();
		findView();
		init();
	}
	
	public void getIntentContent() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			toolId = bundle.getString("toolId");
			toolName = bundle.getString("toolName");
			description = bundle.getString("description");
			tvId = bundle.getString("tvId");
			url = Constant.SERVER_URL + "/tool/tool_run.action?toolId=" + toolId;
		}
	}
	
	private void findView() {
		mTitleBarView = (TitleBarView) findViewById(R.id.title_bar);
		myWebView = (WebView) findViewById(R.id.my_tool_webview);
		mLayout = (FrameLayout) findViewById(R.id.fl_main);
	}
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void init() {
		// ��ʼ��title
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE,
				View.GONE);
		mTitleBarView.setTitleText(toolName);
		mTitleBarView.setBtnLeft(R.drawable.boss_unipay_icon_back,
				R.string.back);
		mTitleBarView.setBtnRight(R.drawable.noq);
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		myWebView.addJavascriptInterface(new Demo(this, myWebView, mHandler),
				"demo");
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
		        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());  
		                  
		        builder.setTitle("�Ի���").setMessage(message);  
		                  
		        final EditText et = new EditText(view.getContext());  
		        et.setSingleLine();  
		        et.setText(defaultValue);  
		        builder.setView(et)  
		                .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int which) {  
		                        result.confirm(et.getText().toString());  
		                    }  
		          
		                })  
		                .setNeutralButton("ȡ��", new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int which) {  
		                        result.cancel();  
		                    }  
		                });  
		  
		        // ����keycode����84֮��İ��������ⰴ�����¶Ի�����Ϣ��ҳ���޷��ٵ����Ի��������  
		        builder.setOnKeyListener(new OnKeyListener() {  
		            public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {  
		                Log.v("onJsPrompt", "keyCode==" + keyCode + "event="+ event);  
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

	@Override
	public void onClick(View v) {
		
	}

}
