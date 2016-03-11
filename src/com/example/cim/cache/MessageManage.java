package com.example.cim.cache;

import java.util.ArrayList;
import java.util.Date;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;

import com.example.cim.model.ChatMsgEntity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.util.DateUtil;

public class MessageManage {
	public static MessageManage instance;

	public Context mContext;

	private MessageManage(Context mContext) {
		this.mContext = mContext;
	}

	public static MessageManage getInstance(Context mContext) {
		if (instance == null) {
			synchronized (DBManager.class) {
				if (instance == null) {
					instance = new MessageManage(mContext);
				}
			}
		}
		return instance;
	}

	public void saveOrUpdateGroupMessage(String message, SQLiteDatabase database) {
		String[] columns = { "M_ID", "M_MessageID", "M_MessageSetID" };
		String selection = "M_MessageID = ? and M_MessageSetID = ? and M_UserID = ?";
		String userAccount = CIMDataConfig.getString(mContext,
				CIMDataConfig.KEY_ACCOUNT);
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(message);
			if (jsonObject.getInt("total") > 0) {
				JSONArray jsonArray = jsonObject.getJSONArray("rows");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					String messageId = jsonObject2.getString("messageId");
					String messageSetId = jsonObject2.getString("messageSetId");
					String[] selectionArgs = { messageId, messageSetId,
							userAccount };
					Cursor cursor = DBManager.getInstance(mContext).select(
							MyDatabaseHelper.TABLE_MESSAGE, columns, selection,
							selectionArgs, null, null, null, database);
					if (cursor.moveToFirst() == false) {
						ContentValues value = new ContentValues();
						value.put("M_MessageID", messageId);
						value.put("M_MessageSetID", messageSetId);
						value.put("M_FromUserID",
								jsonObject2.getString("senderId"));
						value.put("M_FromUserName",
								jsonObject2.getString("senderName"));
						value.put("M_ToUserID",
								jsonObject2.getString("receiverId"));
						value.put("M_Type",
								jsonObject2.getString("messageType"));
						value.put("M_Content", jsonObject2.getString("content"));
						value.put("M_ResourceID",
								jsonObject2.getString("resourceId"));
						value.put(
								"M_CreateTime",
								String.valueOf(jsonObject2.getJSONObject(
										"sendTime").get("time")));
						value.put("M_GroupID", jsonObject2.getString("groupId"));
						value.put("M_GroupName",
								jsonObject2.getString("groupName"));
						value.put("M_Statu", jsonObject2.getInt("statu"));
						value.put("M_UserID", userAccount);
						value.put("M_JSon", jsonObject2.getInt("json"));
						DBManager.getInstance(mContext)
								.insert(MyDatabaseHelper.TABLE_MESSAGE, value,
										database);
					}
					cursor.close();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ChatMsgEntity> getMessageListByGroupId(String groupId,
			String userAccount, SQLiteDatabase database) {
		String[] columns = { "M_FromUserName", "M_FromUserID", "M_Content",
				"M_ResourceID", "M_CreateTime", "M_Statu", "M_ID" };
		String selection = "M_GroupID = ? and M_UserID = ?";
		String[] selectionArgs = { groupId, userAccount };
		String orderBy = "M_CreateTime asc";
		Cursor cursor = DBManager.getInstance(mContext).select(
				MyDatabaseHelper.TABLE_MESSAGE, columns, selection,
				selectionArgs, null, null, orderBy, database);
		ArrayList<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				ChatMsgEntity entity = new ChatMsgEntity();
				String date = cursor.getString(cursor
						.getColumnIndex("M_CreateTime"));
				date = DateUtil.getStringOfDate(new Date(Long.valueOf(date)));
				entity.setDate(date);
				String name = cursor.getString(cursor
						.getColumnIndex("M_FromUserName"));
				entity.setName(name);
				String text = cursor.getString(cursor
						.getColumnIndex("M_Content"));
				entity.setText(text);
				String userId = cursor.getString(cursor
						.getColumnIndex("M_FromUserID"));
				if (userId.equals(CIMDataConfig.getString(mContext,
						CIMDataConfig.KEY_ACCOUNT))) {
					entity.setComMeg(false);
					entity.setResId(mContext.getResources().getIdentifier(
							"userhead2", "drawable", mContext.getPackageName()));
				} else {
					entity.setComMeg(true);
					entity.setResId(mContext.getResources().getIdentifier(
							"userhead1", "drawable", mContext.getPackageName()));
				}
				int statu = cursor.getInt(cursor.getColumnIndex("M_Statu"));
				entity.setSendStatu(statu);// 0正在发送、1发送成功、-1发送失败
				int messageId = cursor.getInt(cursor.getColumnIndex("M_ID"));
				entity.setMessageId(messageId);
				list.add(entity);
			}
			cursor.close();
		}
		return list;
	}

	public long saveSendingMessage(ContentValues value, SQLiteDatabase database) {
		return DBManager.getInstance(mContext).insert(
				MyDatabaseHelper.TABLE_MESSAGE, value, database);
	}

	public boolean updateSendingMessage(int messageId, int messageSetId,
			int client_messageId, String account, int statu,
			SQLiteDatabase database) {
		String sql = "UPDATE message SET M_MessageID = ?, M_MessageSetID = ?, M_Statu = ? WHERE M_ID = ? AND M_UserID = ?";
		Object[] bindArgs = { messageId, messageSetId, statu, client_messageId,
				account };
		return DBManager.getInstance(mContext).execSQL(sql, bindArgs, database);
	}

	public long saveReceiveMessage(ContentValues value, SQLiteDatabase database) {
		return DBManager.getInstance(mContext).insert(
				MyDatabaseHelper.TABLE_MESSAGE, value, database);
	}
}
