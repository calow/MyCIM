package com.example.cim.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.cim.R;
import com.example.cim.manager.CIMListenerManager;
import com.example.cim.nio.mutual.Message;
import com.example.cim.nio.mutual.ReplyBody;
import com.example.cim.nio.mutual.SentBody;
import com.example.cim.ui.SystemMessageActivity;

public class CustomCIMMessageReceiver extends CIMEnventListenerReceiver {

	private NotificationManager notificationManager;

	/**
	 * ���ӳɹ�ʱ�ص�
	 */
	@Override
	public void onConnectionSucceed() {
		for (int i = 0; i < CIMListenerManager.getCIMListeners().size(); i++) {
			CIMListenerManager.getCIMListeners().get(i).onConnectionSucceed();
		}
	}

	/**
	 * ������Ϣ�ɹ�ʱ�ص�
	 */
	@Override
	public void onSentSucceed(SentBody body) {

	}

	/**
	 * �յ��ظ���Ϣʱ�ص�
	 */
	@Override
	public void onReplyReceived(ReplyBody replyBody) {
		for (int i = 0; i < CIMListenerManager.getCIMListeners().size(); i++) {
			CIMListenerManager.getCIMListeners().get(i)
					.onReplyReceived(replyBody);
		}
	}

	/**
	 * �յ���Ϣʱ�ص�
	 */
	@Override
	public void onMessageReceived(Message message) {
		for (int i = 0; i < CIMListenerManager.getCIMListeners().size(); i++) {
			Log.i(this.getClass().getSimpleName(), "########################"
					+ (CIMListenerManager.getCIMListeners().get(i).getClass()
							.getName() + ".onMessageReceived################"));
			CIMListenerManager.getCIMListeners().get(i)
					.onMessageReceived(message);
		}
		if (message.getType().startsWith("9")) {
			return;
		}
		if (isInBackground(mContext)) {
			showNotify(mContext, message);
		}
	}

	/**
	 * �ж�����״̬ʱ�ص�
	 */
	@Override
	public void onConnectionStatus(boolean isConnected) {
		for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
			CIMListenerManager.getCIMListeners().get(index)
					.onConnectionStatus(isConnected);
		}
	}

	/**
	 * ����״̬�仯ʱ�ص�
	 */
	@Override
	public void onNetworkChanged(NetworkInfo networkInfo) {
		for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
			CIMListenerManager.getCIMListeners().get(index)
					.onNetworkChanged(networkInfo);
		}
	}

	/**
	 * ���ӹر�ʱ�ص�
	 */
	@Override
	public void onConnectionClosed() {
		for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
			CIMListenerManager.getCIMListeners().get(index)
					.onConnectionClosed();
		}
	}

	/**
	 * ��Ϣ����
	 * 
	 * @param context
	 * @param msg
	 */
	private void showNotify(Context context, Message msg) {
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String title = "ϵͳ��Ϣ";

		Notification notification = new Notification(R.drawable.icon_notify,
				title, msg.getTimestamp());
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent notificationIntent = new Intent(context,
				SystemMessageActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		notification.setLatestEventInfo(context, title, msg.getContent(),
				contentIntent);
		notificationManager.notify(R.drawable.icon_notify, notification);
	}

	@Override
	public void notifyUIChanged(String flag) {
		for (int i = 0; i < CIMListenerManager.getCIMListeners().size(); i++) {
			CIMListenerManager.getCIMListeners().get(i).notifyUIChanged(flag);
		}
	}

}
