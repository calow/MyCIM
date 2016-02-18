package com.example.cim.receiver;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.example.cim.R;
import com.example.cim.cache.FriendListManage;
import com.example.cim.cache.MessageListManage;
import com.example.cim.cache.MessageManage;
import com.example.cim.exception.CIMSessionDisableException;
import com.example.cim.listener.OnCIMMessageListener;
import com.example.cim.manager.CIMConnectorManager;
import com.example.cim.manager.CIMPushManager;
import com.example.cim.nio.constant.CIMConstant;
import com.example.cim.nio.constant.Constant;
import com.example.cim.nio.mutual.Message;
import com.example.cim.nio.mutual.ReplyBody;
import com.example.cim.nio.mutual.SentBody;
import com.example.cim.ui.ChatActivity;
import com.example.cim.ui.MainActivity;
import com.example.cim.util.CIMDataConfig;

public abstract class CIMEnventListenerReceiver extends BroadcastReceiver
		implements OnCIMMessageListener {

	public Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		// ���ӳɹ�
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_CONNECTION_SUCCESS)) {
			dispatchConnectionSucceed();
		}
		// ����ʧ��
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_CONNECTION_FAILED)) {
			Exception exception = (Exception) intent
					.getSerializableExtra("exception");
			onConnectionFailed(exception);
		}
		// ������Ϣ�ɹ�
		if (intent.getAction().equals(CIMConnectorManager.ACTION_SENT_SUCCESS)) {
			SentBody sentBody = (SentBody) intent
					.getSerializableExtra("sentBody");
			onSentSucceed(sentBody);
		}
		// ������Ϣʧ��
		if (intent.getAction().equals(CIMConnectorManager.ACTION_SENT_FAILED)) {
			Exception exception = (Exception) intent
					.getSerializableExtra("exception");
			SentBody sentBody = (SentBody) intent
					.getSerializableExtra("sentBody");
			onSentFailed(exception, sentBody);
		}
		// ����״̬�仯
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_NETWORK_CHANGED)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService("connectivity");
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			onDevicesNetworkChanged(info);
		}
		// �ж�����״̬
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_CONNECTION_STATUS)) {
			boolean isConnected = intent.getBooleanExtra(
					CIMPushManager.KEY_CIM_CONNECTION_STATUS, false);
			onConnectionStatus(isConnected);
		}
		// ���յ���Ϣ
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_MESSAGE_RECEIVED)) {
			Message message = (Message) intent.getSerializableExtra("message");
			filterType999Message(message);
		}
		// ���յ��ظ���Ϣ
		if (intent.getAction()
				.equals(CIMConnectorManager.ACTION_REPLY_RECEIVED)) {
			ReplyBody replyBody = (ReplyBody) intent
					.getSerializableExtra("replyBody");
			// ���û��ɹ���
			String keyName = replyBody.getKey();
			String code = replyBody.getCode();
			//����Ϣ�ظ�
			if(keyName.equals("client_bind") && code.equals(CIMConstant.ReturnCode.CODE_200)){
				// ��ȡδ����Ϣ�б�
				SentBody sentBody = new SentBody();
				sentBody.setKey("client_get_unread_message");
				CIMPushManager.sendRequest(mContext, sentBody);
				// ��ȡ���������б�
				sentBody.setKey("client_get_online_friends");
				CIMPushManager.sendRequest(mContext, sentBody);
			}
			//δ����Ϣ�б�ظ�
			if(keyName.equals("client_get_unread_message") && code.equals(CIMConstant.ReturnCode.CODE_200)){
				String unReadList = replyBody.getMessage();
				if (unReadList != null && !unReadList.equals("") && !unReadList.equals("null")) {
					List<String> messageSetIdList = MessageListManage.getInstance(mContext).saveOrUpdateMessage(unReadList, null);//����δ����Ϣ�б����ݿ�
					if(messageSetIdList != null && messageSetIdList.size() > 0){
						//���·���������Ϣ����״̬
						SentBody sentBody = new SentBody();
						sentBody.setKey("client_update_offline_message");
						JSONArray jsonarray = new JSONArray(messageSetIdList);
						sentBody.put("list", jsonarray.toString());
						CIMPushManager.sendRequest(context, sentBody);
					}
				}
				if(isInBackground(context)){
					//��֪ͨ������ʾ������Ϣ
					
				}
			}
			//�����б�ظ�
			if(keyName.equals("client_get_online_friends") && code.equals(CIMConstant.ReturnCode.CODE_200)){
				String friendList = replyBody.getMessage();
				if (friendList != null && !friendList.equals("") && !friendList.equals("null")) {
					FriendListManage.getInstance(mContext).saveOrUpdate(friendList);
				}
			}
			//��ȡĳ����������Ϣ�б�
			if(keyName.equals("client_get_group_message") && code.equals(CIMConstant.ReturnCode.CODE_200)){
				String message = replyBody.getMessage();
				if (message != null && !message.equals("") && !message.equals("null")) {
					MessageManage.getInstance(mContext).saveOrUpdateGroupMessage(
							message, null);
				}
			}
			onReplyReceived(replyBody);
		}
		// ���ӳ����쳣
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_UNCAUGHT_EXCEPTION)) {
			Exception exception = (Exception) intent
					.getSerializableExtra("exception");
			onUncaughtException(exception);
		}
		// ���ӹر�
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_CONNECTION_CLOSED)) {
			dispatchConnectionClosed();
		}
		// ����UI
		if (intent.getAction().equals(Constant.NOTIFY_UI_CHANGED)) {
			String flag = intent.getStringExtra(Constant.NOTIFY_UI_CHANGED_FLAG);
			notifyUIChanged(flag);
		}
	}

	/**
	 * ���ӳɹ�
	 */
	private void dispatchConnectionSucceed() {
		CIMPushManager.setAccount(mContext);
		onConnectionSucceed();
	}

	/**
	 * ����ʧ��
	 * 
	 * @param exception
	 */
	private void onConnectionFailed(Exception exception) {
		mHandler.sendMessageDelayed(mHandler.obtainMessage(), 30 * 1000);
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			CIMPushManager.init(mContext);
		}

	};

	/**
	 * ��Ϣ����ʧ��
	 * 
	 * @param exception
	 * @param sentBody
	 */
	private void onSentFailed(Exception exception, SentBody sentBody) {

		if (exception instanceof CIMSessionDisableException) {
			// ��������Ͽ����ӣ���������
			CIMPushManager.init(mContext);
		} else {
			// ����ʧ�ܣ����·���
			CIMPushManager.sendRequest(mContext, sentBody);
		}
	}

	/**
	 * �ֻ�����ı�
	 * 
	 * @param info
	 */
	private void onDevicesNetworkChanged(NetworkInfo info) {
		if (info != null) {
			CIMPushManager.init(mContext);
		}

		onNetworkChanged(info);
	}

	/**
	 * �յ���Ϣ���ж��Ƿ����˻��������豸��¼���������յ����յ���Ϣ�ص�����
	 * 
	 * @param message
	 */
	private void filterType999Message(Message message) {
		// �ж��Ƿ����ʺ��������豸��¼����Ϣ
		if (CIMConstant.MessageType.TYPE_999.equals(message.getType())) {
			CIMDataConfig.putBoolean(mContext, CIMDataConfig.KEY_MANUAL_STOP,
					true);
		}
		//����Ϣд�����ݿ�
		long result = saveMessageToDB(message);
		if(result > 0){
			String messageSetId = message.getMessageSetId();
			//���·���������Ϣ����״̬
			SentBody sentBody = new SentBody();
			sentBody.setKey("client_update_offline_message");
			JSONArray jsonarray = new JSONArray();
			jsonarray.put(messageSetId);
			sentBody.put("list", jsonarray.toString());
			CIMPushManager.sendRequest(mContext, sentBody);
		}
		//�����յ���Ϣ�ź�
		onMessageReceived(message);
	}
	
	private long saveMessageToDB(Message message){
		long result = 0;
		if (message != null && !message.equals("") && !message.equals("null")) {
			ContentValues value = new ContentValues();
			value.put("M_MessageID", message.getMid());
			value.put("M_MessageSetID", message.getMessageSetId());
			value.put("M_FromUserID", message.getSender());
			value.put("M_FromUserName", message.getSenderName());
			value.put("M_ToUserID", message.getReceiver());
			value.put("M_Type", message.getType());
			value.put("M_Content", message.getContent());
			value.put("M_ResourceID", message.getFile());
			value.put("M_CreateTime", message.getTimestamp());
			value.put("M_GroupID", message.getGroupId());
			value.put("M_GroupName", message.getGroupName());
			value.put("M_Statu", 0);
			value.put("M_UserID", CIMDataConfig.getString(mContext,
					CIMDataConfig.KEY_ACCOUNT));
			result = MessageManage.getInstance(mContext).saveReceiveMessage(value, null);
		}
		return result;
	}
	
	/**
	 * ���ӳ����쳣
	 * 
	 * @param exception
	 */
	private void onUncaughtException(Exception exception) {

	}

	/**
	 * ���ӶϿ�
	 */
	private void dispatchConnectionClosed() {
		if (CIMConnectorManager.netWorkAvailable(mContext)) {
			CIMPushManager.init(mContext);
		}
		onConnectionClosed();
	}

	/**
	 * �ж�Ӧ���Ƿ��ں�̨����
	 * 
	 * @param context
	 * @return
	 */
	protected boolean isInBackground(Context context) {
		List<RunningTaskInfo> tasksInfo = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
		if (tasksInfo.size() > 0) {

			if (context.getPackageName().equals(
					tasksInfo.get(0).topActivity.getPackageName())) {

				return false;
			}
		}
		return true;
	}

	/**
	 * ��message�н�������Ϣ����
	 * 
	 * @param content
	 * @return
	 */
	public void DecodeAndNotify(String content) {
		if (content != null && !content.equals("") && !content.equals("null")) {
			try {
				JSONArray jsonArray = new JSONArray(content);
				JSONObject jsonObject = null;
				String name = null;
				String messageContent = null;
				String groupType = null;
				String groupName = null;
				String title = null;
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObject = (JSONObject) jsonArray.get(i);
					name = jsonObject.getString("senderNickName");
					messageContent = jsonObject.getString("messageContent");
					groupType = jsonObject.getString("groupType");
					groupName = jsonObject.getString("groupName");
					if (groupType.equals("0")) {
						title = name + "��˽����Ϣ";
					} else if (groupType.equals("1")) {
						title = groupName + "��Ⱥ����Ϣ";
						messageContent = name + ":" + messageContent;
					}
					showMessageNotify(name + ":" + messageContent, title,
							messageContent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void showMessageNotify(String shortNotify, String notifyTitle,
			String notifyContent) {
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext);

		mBuilder.setContentTitle(notifyTitle)
				// ����֪ͨ������
				.setContentText(notifyContent)
				// ����֪ͨ����ʾ����
				.setContentIntent(
						getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) // ����֪ͨ�������ͼ
				// .setNumber(number) //����֪ͨ���ϵ�����
				.setTicker(shortNotify) // ֪ͨ�״γ�����֪ͨ��������������Ч����
				.setWhen(System.currentTimeMillis())// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��
				.setPriority(Notification.PRIORITY_DEFAULT) // ���ø�֪ͨ���ȼ�
				// .setAutoCancel(true)//���������־���û��������Ϳ�����֪ͨ���Զ�ȡ��
				.setOngoing(false)// ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
				.setDefaults(Notification.DEFAULT_VIBRATE)// ��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND ������� //
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.icon);// ����֪ͨСICON
		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(1, notification);
	}

	public PendingIntent getDefalutIntent(int flags) {
		Intent intent = new Intent(mContext, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1024,
				intent, flags);
		return pendingIntent;
	}

	/**
	 * �յ���Ϣ
	 */
	@Override
	public abstract void onMessageReceived(Message message);

	/**
	 * �յ��ظ���Ϣ
	 */
	@Override
	public abstract void onReplyReceived(ReplyBody replyBody);

	/**
	 * ����״̬�ı�
	 */
	@Override
	public abstract void onNetworkChanged(NetworkInfo networkInfo);

	/**
	 * ��Ϣ���ͳɹ�
	 * 
	 * @param body
	 */
	public abstract void onSentSucceed(SentBody body);

}
