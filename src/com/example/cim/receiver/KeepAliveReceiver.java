package com.example.cim.receiver;

import com.example.cim.manager.CIMPushManager;
import com.example.cim.service.CIMPushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KeepAliveReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent it) {
		Log.d(KeepAliveReceiver.class.getSimpleName(), "onReceive()");
		Intent intent = new Intent(context, CIMPushService.class);
		intent.putExtra(CIMPushManager.SERVICE_ACTION,
				CIMPushManager.ACTION_CONNECTION_KEEPALIVE);
		context.startService(intent);
	}

}
