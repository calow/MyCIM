package com.example.cim.ui.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class CommonBaseControl {

	private ProgressDialog progressDialog;

	public Context mContext;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			String hint = bundle.getString("hint");
			Toast toast = Toast.makeText(mContext, hint, Toast.LENGTH_SHORT);
			toast.show();
		}
	};

	public CommonBaseControl(Context context) {
		mContext = context;
	}

	public void showProgressDialog(String title, String message) {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(mContext, title, message,
					true, false);
		} else if (progressDialog.isShowing()) {
			progressDialog.setTitle(title);
			progressDialog.setMessage(message);
		}
		progressDialog.show();
	}

	public void hideProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public void showToast(String hint) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("hint", hint);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}
}
