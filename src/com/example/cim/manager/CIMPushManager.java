package com.example.cim.manager;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.example.cim.nio.constant.CIMConstant;
import com.example.cim.nio.mutual.SentBody;
import com.example.cim.service.CIMPushService;
import com.example.cim.util.CIMDataConfig;

public class CIMPushManager {

	public static String SERVICE_ACTION = "SERVICE_ACTION";

	public static String ACTION_CONNECTION = "ACTION_CONNECTION";

	public static String ACTION_CONNECTION_KEEPALIVE = "ACTION_CONNECTION_KEEPALIVE";

	public static String KEY_SEND_BODY = "KEY_SEND_BODY";

	public static String ACTION_SENDREQUEST = "ACTION_SENDREQUEST";

	public static String ACTION_DISCONNECTION = "ACTION_DISSENDREQUEST";

	public static String ACTION_DESTROY = "ACTION_DESTROY";

	public static String ACTION_CONNECTION_STATUS = "ACTION_CONNECTION_STATUS";
	
	public static String KEY_CIM_CONNECTION_STATUS ="KEY_CIM_CONNECTION_STATUS";

	/**
	 * 重新失败时重新连接调用
	 * @param context
	 */
	public static void init(Context context){
		boolean  isManualStop  = CIMDataConfig.getBoolean(context,CIMDataConfig.KEY_MANUAL_STOP);
		boolean  isManualDestroy  = CIMDataConfig.getBoolean(context,CIMDataConfig.KEY_CIM_DESTROYED);
		if(isManualStop || isManualDestroy){
			return;
		}
		String host = CIMDataConfig.getString(context, CIMDataConfig.KEY_CIM_SERVIER_HOST);
		int port = CIMDataConfig.getInt(context, CIMDataConfig.KEY_CIM_SERVIER_PORT);
		init(context, host, port);
	}
	
	/**
	 * 初始化,连接服务端，在程序启动页或者 在Application里调用
	 * 
	 * @param context
	 * @param host
	 * @param port
	 */
	public static void init(Context context, String host, int port) {
		CIMDataConfig.putBoolean(context, CIMDataConfig.KEY_CIM_DESTROYED,
				false);
		CIMDataConfig.putBoolean(context, CIMDataConfig.KEY_MANUAL_STOP, false);

		Intent serviceIntent = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION);
		serviceIntent.putExtra(CIMDataConfig.KEY_CIM_SERVIER_HOST, host);
		serviceIntent.putExtra(CIMDataConfig.KEY_CIM_SERVIER_PORT, port);

		context.startService(serviceIntent);

		CIMDataConfig
				.putString(context, CIMDataConfig.KEY_CIM_SERVIER_HOST, host);
		CIMDataConfig.putInt(context, CIMDataConfig.KEY_CIM_SERVIER_PORT, port);
	}

	public static void setAccount(Context context) {
		String account = CIMDataConfig.getString(context,
				CIMDataConfig.KEY_ACCOUNT);
		setAccount(context, account);
	}

	public static void setAccount(Context context, String account) {
		boolean isManualDestroy = CIMDataConfig.getBoolean(context,
				CIMDataConfig.KEY_CIM_DESTROYED);
		if (isManualDestroy || account == null || account.trim().length() == 0) {
			return;
		}

		CIMDataConfig.putBoolean(context, CIMDataConfig.KEY_MANUAL_STOP, false);
		CIMDataConfig.putString(context, CIMDataConfig.KEY_ACCOUNT, account);

		String imei = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		imei += context.getPackageName();

		SentBody body = new SentBody();
		body.setKey(CIMConstant.RequestKey.CLIENT_BIND);
		body.put("account", account);
		body.put("deviceId", imei);
		body.put("channel", "android");
		body.put("device", android.os.Build.MODEL);
		sendRequest(context, body);
	}

	public static void sendRequest(Context context, SentBody sentBody) {
		boolean isManualStop = CIMDataConfig.getBoolean(context,
				CIMDataConfig.KEY_MANUAL_STOP);
		boolean isManualDestroy = CIMDataConfig.getBoolean(context,
				CIMDataConfig.KEY_CIM_DESTROYED);

		if (isManualStop || isManualDestroy) {
			return;
		}

		Intent serviceIntent = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_SENDREQUEST);
		serviceIntent.putExtra(KEY_SEND_BODY, sentBody);
		context.startService(serviceIntent);
	}

	public static void stop(Context context) {
		boolean isManualDestroy = CIMDataConfig.getBoolean(context,
				CIMDataConfig.KEY_CIM_DESTROYED);
		if (isManualDestroy) {
			return;
		}
		CIMDataConfig.putBoolean(context, CIMDataConfig.KEY_MANUAL_STOP, true);
		Intent serviceIntent = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_DISCONNECTION);
		context.startService(serviceIntent);
	}

	public static void destory(Context context) {
		CIMDataConfig
				.putBoolean(context, CIMDataConfig.KEY_CIM_DESTROYED, true);
		CIMDataConfig.putString(context, CIMDataConfig.KEY_ACCOUNT, null);
		Intent serviceInten = new Intent(context, CIMPushService.class);
		serviceInten.putExtra(SERVICE_ACTION, ACTION_DESTROY);
		context.startService(serviceInten);
	}

	public static void resume(Context context) {
		boolean isManualDestroy = CIMDataConfig.getBoolean(context,
				CIMDataConfig.KEY_CIM_DESTROYED);
		if (isManualDestroy) {
			return;
		}
		setAccount(context);
	}

	public static void detectIsConnected(Context context) {
		Intent serviceIntent = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION_STATUS);
		context.startService(serviceIntent);
	}
}
