package com.example.cim.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.cim.R;
import com.example.cim.adapter.ChatMsgAdapter;
import com.example.cim.cache.DBManager;
import com.example.cim.cache.MessageManage;
import com.example.cim.manager.CIMPushManager;
import com.example.cim.model.ChatMsgEntity;
import com.example.cim.network.API;
import com.example.cim.network.HttpRequest;
import com.example.cim.network.HttpRequest.HttpCompliteListener;
import com.example.cim.nio.constant.Constant;
import com.example.cim.nio.mutual.ReplyBody;
import com.example.cim.nio.mutual.SentBody;
import com.example.cim.ui.base.CIMMonitorFragmentActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.view.TitleBarView;

public class ChatActivity extends CIMMonitorFragmentActivity implements
		OnClickListener {

	private ListView mListView;

	private Button btnSend;

	private EditText mEditText;

	private TitleBarView mTitleBarView;

	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();

	private String[] msgArray = new String[] { "[媚眼]测试啦[媚眼]", "测试啦", "测试啦",
			"测试啦", "测试啦", "你妹[苦逼]", "测[惊讶]你妹", "测你妹[胜利]", "测试啦" };

	private String[] dataArray = new String[] { "2012-12-12 12:00",
			"2012-12-12 12:10", "2012-12-12 12:11", "2012-12-12 12:20",
			"2012-12-12 12:30", "2012-12-12 12:35", "2012-12-12 12:40",
			"2012-12-12 12:50", "2012-12-12 12:50" };

	private final static int COUNT = 9;

	private final static int UPDATE_CHAACTIVITY = 1;

	public ChatMsgAdapter mAdapter;

	public String id;

	public String userName;

	public int groupType;

	public String groupName;
	
	public String faceToUserId;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CHAACTIVITY:
				Bundle bundle = msg.getData();
				ArrayList<ChatMsgEntity> list = bundle.getParcelableArrayList("list");
				mDataArrays.clear();
				mDataArrays.addAll(list);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		findView();
		init();
	}

	private void findView() {
		mListView = (ListView) findViewById(R.id.lv_chat);
		btnSend = (Button) findViewById(R.id.btn_send);
		mEditText = (EditText) findViewById(R.id.et_message);
		mTitleBarView = (TitleBarView) findViewById(R.id.title_bar);
	}

	private void init() {
		// 获取intent数据
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getStringExtra("id");
			userName = intent.getStringExtra("userName");
			groupType = intent.getIntExtra("groupType", 0);
			groupName = intent.getStringExtra("groupName");
			faceToUserId = intent.getStringExtra("faceToUserId");
		}
		// 初始化title
		mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE,
				View.VISIBLE);
		mTitleBarView.setTitleText(userName);
		mTitleBarView.setBtnLeft(R.drawable.boss_unipay_icon_back,
				R.string.back);
		mTitleBarView.setBtnRight(R.drawable.noq);
		mTitleBarView.setBtnLeftOnclickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnSend.setOnClickListener(this);
		List<ChatMsgEntity> list = MessageManage.getInstance(ChatActivity.this)
				.getMessageListByGroupId(
						id,
						CIMDataConfig.getString(ChatActivity.this,
								CIMDataConfig.KEY_ACCOUNT), null);
		mDataArrays.addAll(list);
		mAdapter = new ChatMsgAdapter(mDataArrays, this);
		mListView.setAdapter(mAdapter);
		new AsyncGetGroupMessage().execute(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		}
	}

	public void send() {
		final String contString = mEditText.getText().toString().trim();
		if (contString.length() > 0) {
			SQLiteDatabase db = DBManager.getInstance(ChatActivity.this)
					.getDatabase();
			try {
				db.beginTransaction();
				long result1 = saveSendingMessageToMessageTable(contString, db);
				if (result1 > 0) {
					db.setTransactionSuccessful();
				}
				notifyMessageListChange();
				showNewMessageToList(contString, result1);
				sendMessageToServer(contString, id, String.valueOf(result1));
			} catch (Exception e) {
				db.endTransaction();
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
	}

	private String getDate() {
		Calendar c = Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}

	private class AsyncGetGroupMessage extends
			AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = 0;
			SentBody sentBody = new SentBody();
			sentBody.setKey("client_get_group_message");
			sentBody.put("groupId", id);
			CIMPushManager.sendRequest(ChatActivity.this, sentBody);
			result = 1;
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	@Override
	public void onReplyReceived(ReplyBody replyBody) {
		String keyName = replyBody.getKey();
		String message = replyBody.getMessage();
		if (keyName.equals("client_get_group_message")) {
			if (message != null && !message.equals("")
					&& !message.equals("null")) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(message);
					if (jsonObject.getString("groupId").equals(id)) {
						updateChatActivity();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public long saveSendingMessageToMessageTable(String message,
			SQLiteDatabase database) {
		ContentValues value = new ContentValues();
		value.put("M_MessageID", -1);
		value.put("M_MessageSetID", -1);
		value.put("M_FromUserID", CIMDataConfig.getString(ChatActivity.this,
				CIMDataConfig.KEY_ACCOUNT));
		value.put("M_FromUserName", CIMDataConfig.getString(ChatActivity.this,
				CIMDataConfig.KEY_USERNAME));
		value.put("M_ToUserID", CIMDataConfig.getString(ChatActivity.this,
				CIMDataConfig.KEY_ACCOUNT));
		value.put("M_Type", groupType);
		value.put("M_Content", message);
		value.put("M_ResourceID", "");
		value.put("M_CreateTime", System.currentTimeMillis());
		value.put("M_GroupID", id);
		value.put("M_GroupName", groupName);
		value.put("M_Statu", -1);
		value.put("M_UserID", CIMDataConfig.getString(ChatActivity.this,
				CIMDataConfig.KEY_ACCOUNT));
		if(groupType == 1){//私聊室
			value.put("M_JSon", faceToUserId + ":" + CIMDataConfig.getString(ChatActivity.this,
					CIMDataConfig.KEY_ACCOUNT));
		}else if(groupType == 2){//群聊室
			value.put("M_JSon", "1");
		}
		long result = MessageManage.getInstance(ChatActivity.this)
				.saveSendingMessage(value, database);
		return result;
	}

	public void notifyMessageListChange() {
		Intent intent = new Intent();
		intent.setAction(Constant.NOTIFY_UI_CHANGED);
		intent.putExtra(Constant.NOTIFY_UI_CHANGED_FLAG,
				Constant.UIChangeType.MESSAGELIST);
		sendBroadcast(intent);
	}

	public void showNewMessageToList(String contString, long messageId) {
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setDate(getDate());
		entity.setComMeg(false);
		entity.setResId(getResources().getIdentifier("userhead2", "drawable",
				getPackageName()));
		entity.setText(contString);
		entity.setName(CIMDataConfig.getString(ChatActivity.this,
				CIMDataConfig.KEY_USERNAME));
		entity.setMessageId(Integer.parseInt(messageId + ""));
		mDataArrays.add(entity);
		mAdapter.notifyDataSetChanged();
		mEditText.setText("");
		mListView.setSelection(mListView.getCount() - 1);
	}

	public void sendMessageToServer(String contString, String groupId,
			String cacheMessageId) {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("content", contString);
		map.put("groupId", groupId);
		map.put("messageId", cacheMessageId);
		HttpRequest request = new HttpRequest(new HttpCompliteListener() {

			@Override
			public void onResponseSuccess(String json) {
				try {
					JSONObject object = new JSONObject(json);
					int code = object.getInt("code");
					if (code == 200) {
						JSONObject object2 = object.getJSONObject("data");
						String messageSetId = object2.getString("messageSet");
						String messageId = object2.getString("messageId");
						String client_messageId = object2
								.getString("client_messageId");

						updateSendingMessage(Integer
								.valueOf(messageId), Integer
								.valueOf(messageSetId), CIMDataConfig
								.getString(ChatActivity.this,
										CIMDataConfig.KEY_ACCOUNT), Integer
								.valueOf(client_messageId), 1, null);
						updateChatActivity();// 更新互动室显示
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onResponseError(int code) {
				System.out.println("发送失败");
				System.out.println(code);
				updateSendingMessage(-2, -2, CIMDataConfig.getString(
						ChatActivity.this, CIMDataConfig.KEY_ACCOUNT), Integer.valueOf(map
								.get("messageId")), -2, null);
				updateChatActivity();
			}

			@Override
			public void onRequestException(Exception e) {
				System.out.println("发送异常");
				System.out.println(e);
				updateSendingMessage(-2, -2, CIMDataConfig.getString(
						ChatActivity.this, CIMDataConfig.KEY_ACCOUNT), Integer.valueOf(map
								.get("messageId")), -2, null);
				updateChatActivity();
			}
		});
		request.httpPost(API.SendMsg_URL + "message_send.action", map);
	}

	public boolean updateSendingMessage(int messageId, int messageSetId,
			String account, int client_messageId, int statu,
			SQLiteDatabase database) {
		return MessageManage.getInstance(ChatActivity.this)
				.updateSendingMessage(messageId, messageSetId,
						client_messageId, account, statu, database);
	}

	/**
	 * 如果收到的消息是本互动室的，则更新互动室
	 */
	@Override
	public void onMessageReceived(com.example.cim.nio.mutual.Message message) {
		String groupId = message.getGroupId();
		if (groupId.equals(id)) {
			updateChatActivity();
		}
	}

	/**
	 * 更新互动室
	 */
	public void updateChatActivity() {
		ArrayList<ChatMsgEntity> list = MessageManage.getInstance(
				ChatActivity.this).getMessageListByGroupId(
				id,
				CIMDataConfig.getString(ChatActivity.this,
						CIMDataConfig.KEY_ACCOUNT), null);// 获取并更新
		Bundle b = new Bundle();
		b.putParcelableArrayList("list", list);
		Message message = new Message();
		message.setData(b);
		message.what = UPDATE_CHAACTIVITY;
		mHandler.sendMessage(message);
	}
}
