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
		// 连接成功
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_CONNECTION_SUCCESS)) {
			dispatchConnectionSucceed();
		}
		// 连接失败
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_CONNECTION_FAILED)) {
			Exception exception = (Exception) intent
					.getSerializableExtra("exception");
			onConnectionFailed(exception);
		}
		// 发送消息成功
		if (intent.getAction().equals(CIMConnectorManager.ACTION_SENT_SUCCESS)) {
			SentBody sentBody = (SentBody) intent
					.getSerializableExtra("sentBody");
			onSentSucceed(sentBody);
		}
		// 发送消息失败
		if (intent.getAction().equals(CIMConnectorManager.ACTION_SENT_FAILED)) {
			Exception exception = (Exception) intent
					.getSerializableExtra("exception");
			SentBody sentBody = (SentBody) intent
					.getSerializableExtra("sentBody");
			onSentFailed(exception, sentBody);
		}
		// 网络状态变化
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_NETWORK_CHANGED)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService("connectivity");
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			onDevicesNetworkChanged(info);
		}
		// 判断连接状态
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_CONNECTION_STATUS)) {
			boolean isConnected = intent.getBooleanExtra(
					CIMPushManager.KEY_CIM_CONNECTION_STATUS, false);
			onConnectionStatus(isConnected);
		}
		// 接收到消息
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_MESSAGE_RECEIVED)) {
			Message message = (Message) intent.getSerializableExtra("message");
			filterType999Message(message);
		}
		// 接收到回复消息
		if (intent.getAction()
				.equals(CIMConnectorManager.ACTION_REPLY_RECEIVED)) {
			ReplyBody replyBody = (ReplyBody) intent
					.getSerializableExtra("replyBody");
			// 绑定用户成功后
			String keyName = replyBody.getKey();
			String code = replyBody.getCode();
			//绑定消息回复
			if(keyName.equals("client_bind") && code.equals(CIMConstant.ReturnCode.CODE_200)){
				// 获取未读消息列表
				SentBody sentBody = new SentBody();
				sentBody.setKey("client_get_unread_message");
				CIMPushManager.sendRequest(mContext, sentBody);
				// 获取好友在线列表
				sentBody.setKey("client_get_online_friends");
				CIMPushManager.sendRequest(mContext, sentBody);
			}
			//未读消息列表回复
			if(keyName.equals("client_get_unread_message") && code.equals(CIMConstant.ReturnCode.CODE_200)){
				String unReadList = replyBody.getMessage();
				if (unReadList != null && !unReadList.equals("") && !unReadList.equals("null")) {
					List<String> messageSetIdList = MessageListManage.getInstance(mContext).saveOrUpdateMessage(unReadList, null);//保存未读消息列表到数据库
					if(messageSetIdList != null && messageSetIdList.size() > 0){
						//更新服务器中消息推送状态
						SentBody sentBody = new SentBody();
						sentBody.setKey("client_update_offline_message");
						JSONArray jsonarray = new JSONArray(messageSetIdList);
						sentBody.put("list", jsonarray.toString());
						CIMPushManager.sendRequest(context, sentBody);
					}
				}
				if(isInBackground(context)){
					//在通知栏中显示离线消息
					
				}
			}
			//好友列表回复
			if(keyName.equals("client_get_online_friends") && code.equals(CIMConstant.ReturnCode.CODE_200)){
				String friendList = replyBody.getMessage();
				if (friendList != null && !friendList.equals("") && !friendList.equals("null")) {
					FriendListManage.getInstance(mContext).saveOrUpdate(friendList);
				}
			}
			//获取某个聊天室消息列表
			if(keyName.equals("client_get_group_message") && code.equals(CIMConstant.ReturnCode.CODE_200)){
				String message = replyBody.getMessage();
				if (message != null && !message.equals("") && !message.equals("null")) {
					MessageManage.getInstance(mContext).saveOrUpdateGroupMessage(
							message, null);
				}
			}
			onReplyReceived(replyBody);
		}
		// 连接出现异常
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_UNCAUGHT_EXCEPTION)) {
			Exception exception = (Exception) intent
					.getSerializableExtra("exception");
			onUncaughtException(exception);
		}
		// 连接关闭
		if (intent.getAction().equals(
				CIMConnectorManager.ACTION_CONNECTION_CLOSED)) {
			dispatchConnectionClosed();
		}
		// 更新UI
		if (intent.getAction().equals(Constant.NOTIFY_UI_CHANGED)) {
			String flag = intent.getStringExtra(Constant.NOTIFY_UI_CHANGED_FLAG);
			notifyUIChanged(flag);
		}
	}

	/**
	 * 连接成功
	 */
	private void dispatchConnectionSucceed() {
		CIMPushManager.setAccount(mContext);
		onConnectionSucceed();
	}

	/**
	 * 连接失败
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
	 * 消息发送失败
	 * 
	 * @param exception
	 * @param sentBody
	 */
	private void onSentFailed(Exception exception, SentBody sentBody) {

		if (exception instanceof CIMSessionDisableException) {
			// 与服务器断开连接，重新连接
			CIMPushManager.init(mContext);
		} else {
			// 发送失败，重新发送
			CIMPushManager.sendRequest(mContext, sentBody);
		}
	}

	/**
	 * 手机网络改变
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
	 * 收到消息，判断是否是账户在其他设备登录，并调用收到接收到消息回调方法
	 * 
	 * @param message
	 */
	private void filterType999Message(Message message) {
		// 判断是否是帐号在其他设备登录的消息
		if (CIMConstant.MessageType.TYPE_999.equals(message.getType())) {
			CIMDataConfig.putBoolean(mContext, CIMDataConfig.KEY_MANUAL_STOP,
					true);
		}
		//将消息写到数据库
		long result = saveMessageToDB(message);
		if(result > 0){
			String messageSetId = message.getMessageSetId();
			//更新服务器中消息推送状态
			SentBody sentBody = new SentBody();
			sentBody.setKey("client_update_offline_message");
			JSONArray jsonarray = new JSONArray();
			jsonarray.put(messageSetId);
			sentBody.put("list", jsonarray.toString());
			CIMPushManager.sendRequest(mContext, sentBody);
		}
		//传递收到消息信号
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
	 * 连接出现异常
	 * 
	 * @param exception
	 */
	private void onUncaughtException(Exception exception) {

	}

	/**
	 * 连接断开
	 */
	private void dispatchConnectionClosed() {
		if (CIMConnectorManager.netWorkAvailable(mContext)) {
			CIMPushManager.init(mContext);
		}
		onConnectionClosed();
	}

	/**
	 * 判断应用是否在后台运行
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
	 * 从message中解析出消息对象
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
						title = name + "的私聊消息";
					} else if (groupType.equals("1")) {
						title = groupName + "的群组消息";
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
				// 设置通知栏标题
				.setContentText(notifyContent)
				// 设置通知栏显示内容
				.setContentIntent(
						getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) // 设置通知栏点击意图
				// .setNumber(number) //设置通知集合的数量
				.setTicker(shortNotify) // 通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
				.setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
				// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.icon);// 设置通知小ICON
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
	 * 收到消息
	 */
	@Override
	public abstract void onMessageReceived(Message message);

	/**
	 * 收到回复消息
	 */
	@Override
	public abstract void onReplyReceived(ReplyBody replyBody);

	/**
	 * 网络状态改变
	 */
	@Override
	public abstract void onNetworkChanged(NetworkInfo networkInfo);

	/**
	 * 消息发送成功
	 * 
	 * @param body
	 */
	public abstract void onSentSucceed(SentBody body);

}
