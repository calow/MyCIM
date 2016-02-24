package com.example.testwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.example.cim.view.TitleBarView;
import com.example.manage.WebViewManage;

public class WebviewActivity extends Activity {

	private WebView myWebView;
	private FrameLayout mLayout;
	private TitleBarView mTitleBarView;
	private String title = "";
	private String url;

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int index = msg.arg1;
			Bundle bundle = msg.getData();
			switch (index) {
			case 1:
				WebView view = new WebView(WebviewActivity.this);
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
		setContentView(R.layout.webview_activity);
		getIntentContent();
		findView();
		init();
	}

	public void getIntentContent() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			title = bundle.getString("title");
			url = bundle.getString("url");
		}
	}

	private void findView() {
		mTitleBarView = (TitleBarView) findViewById(R.id.title_bar);
		myWebView = (WebView) findViewById(R.id.my_webview);
		mLayout = (FrameLayout) findViewById(R.id.fl_main);
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void init() {
		// 初始化title
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE,
				View.GONE);
		mTitleBarView.setTitleText(title);
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
			 * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
			 */
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						view.getContext());

				builder.setTitle("对话框").setMessage(message)
						.setPositiveButton("确定", null);

				// 不需要绑定按键事件
				// 屏蔽keycode等于84之类的按键
				builder.setOnKeyListener(new OnKeyListener() {
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						Log.v("onJsAlert", "keyCode==" + keyCode + "event="
								+ event);
						return true;
					}
				});
				// 禁止响应按back键的事件
				builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
				return true;
				// return super.onJsAlert(view, url, message, result);
			}

			/**
			 * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////”
			 */
			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						view.getContext());
				builder.setTitle("对话框")
						.setMessage(message)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNeutralButton("取消",
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

				// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
				builder.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						Log.v("onJsConfirm", "keyCode==" + keyCode + "event="
								+ event);
						return true;
					}
				});
				// 禁止响应按back键的事件
				// builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
				// return super.onJsConfirm(view, url, message, result);
			}
			
			/** 
		     * 覆盖默认的window.prompt展示界面，避免title里显示为“：来自file:////” 
		     * window.prompt('请输入您的域名地址', '618119.com'); 
		     */  
			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {
		        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());  
		                  
		        builder.setTitle("对话框").setMessage(message);  
		                  
		        final EditText et = new EditText(view.getContext());  
		        et.setSingleLine();  
		        et.setText(defaultValue);  
		        builder.setView(et)  
		                .setPositiveButton("确定", new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int which) {  
		                        result.confirm(et.getText().toString());  
		                    }  
		          
		                })  
		                .setNeutralButton("取消", new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int which) {  
		                        result.cancel();  
		                    }  
		                });  
		  
		        // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题  
		        builder.setOnKeyListener(new OnKeyListener() {  
		            public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {  
		                Log.v("onJsPrompt", "keyCode==" + keyCode + "event="+ event);  
		                return true;  
		            }  
		        });  
		  
		        // 禁止响应按back键的事件  
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
			myWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
