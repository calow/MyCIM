package com.example.cim.ui.base;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.cim.listener.OnCIMMessageListener;
import com.example.cim.manager.CIMListenerManager;
import com.example.cim.nio.mutual.Message;
import com.example.cim.nio.mutual.ReplyBody;
import com.example.cim.util.MyActivityManager;

public class CIMMonitorFragmentActivity extends FragmentActivity implements
		OnCIMMessageListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CIMListenerManager.registerMessageListener(this, this);
		MyActivityManager.registerActivity(this, this);
	}

	@Override
	public void finish() {
		super.finish();
		CIMListenerManager.removeMessageListener(this);
		MyActivityManager.removeActivity(this);
	}

	@Override
	public void onMessageReceived(Message message) {

	}

	@Override
	public void onReplyReceived(ReplyBody replyBody) {

	}

	@Override
	public void onNetworkChanged(NetworkInfo networkInfo) {

	}

	@Override
	public void onConnectionStatus(boolean isConnected) {

	}

	@Override
	public void onConnectionSucceed() {
		
	}

	@Override
	public void onConnectionClosed() {

	}

	@Override
	public void notifyUIChanged(String flag) {
		
	}

}
