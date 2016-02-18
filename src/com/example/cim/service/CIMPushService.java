package com.example.cim.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.cim.manager.CIMConnectorManager;
import com.example.cim.manager.CIMPushManager;
import com.example.cim.nio.mutual.SentBody;
import com.example.cim.receiver.KeepAliveReceiver;
import com.example.cim.util.CIMDataConfig;

public class CIMPushService extends Service {

	CIMConnectorManager mConnectorManager;
	AlarmManager mAlarmManager;
	PendingIntent mPendingIntent;
	private IBinder mBinder = new CIMPushService.LocalBinder();

	@Override
	public void onCreate() {
		mConnectorManager = CIMConnectorManager.getManager(this
				.getApplicationContext());
		mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this,
				KeepAliveReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mAlarmManager.cancel(mPendingIntent);
		mAlarmManager.set(AlarmManager.RTC_WAKEUP,
				300000L + System.currentTimeMillis(), mPendingIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action;
		if (intent == null) {
			intent = new Intent(CIMPushManager.ACTION_CONNECTION);
			String host = CIMDataConfig.getString(this,
					CIMDataConfig.KEY_CIM_SERVIER_HOST);
			int port = CIMDataConfig.getInt(this,
					CIMDataConfig.KEY_CIM_SERVIER_PORT);
			intent.putExtra(CIMDataConfig.KEY_CIM_SERVIER_HOST, host);
			intent.putExtra(CIMDataConfig.KEY_CIM_SERVIER_PORT, port);
		}

		action = intent.getStringExtra(CIMPushManager.SERVICE_ACTION);

		if (action.equals(CIMPushManager.ACTION_CONNECTION_KEEPALIVE)) {
			mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP,
					300000L + System.currentTimeMillis(), mPendingIntent);
			// 判断连接是否依然连着，如果没连着则重连
			if (!mConnectorManager.isConnected()) {
				String host = intent
						.getStringExtra(CIMDataConfig.KEY_CIM_SERVIER_HOST);
				int port = intent.getIntExtra(
						CIMDataConfig.KEY_CIM_SERVIER_PORT, 23456);
				mConnectorManager.connect(host, port);
			} else {
				Log.d(CIMPushService.class.getSimpleName(),
						"isConnected() = true ");
			}
		}

		if (action.equals(CIMPushManager.ACTION_CONNECTION)) {
			String host = intent
					.getStringExtra(CIMDataConfig.KEY_CIM_SERVIER_HOST);
			int port = intent.getIntExtra(CIMDataConfig.KEY_CIM_SERVIER_PORT,
					23456);
			mConnectorManager.connect(host, port);
		}

		if (action.equals(CIMPushManager.ACTION_SENDREQUEST)) {
			SentBody sentBody = (SentBody) intent.getSerializableExtra(CIMPushManager.KEY_SEND_BODY);
			mConnectorManager.send(sentBody);
		}
		
		if(action.equals(CIMPushManager.ACTION_DISCONNECTION)){
			mConnectorManager.closeSession();
		}
		
		if(action.equals(CIMPushManager.ACTION_DESTROY)){
			mConnectorManager.destroy();
			this.stopSelf();
    		android.os.Process.killProcess(android.os.Process.myPid());
		}
		
		if(action.equals(CIMPushManager.ACTION_CONNECTION_STATUS)){
			mConnectorManager.deliverIsConnected();
		}

		return Service.START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public CIMPushService getService() {
			return CIMPushService.this;
		}
	}

}
