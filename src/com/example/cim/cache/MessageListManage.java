package com.example.cim.cache;

import java.util.ArrayList;
import java.util.List;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;

import com.example.cim.model.RecentChat;
import com.example.cim.test.TestData;
import com.example.cim.util.CIMDataConfig;

public class MessageListManage {

	public static MessageListManage instance;

	public Context mContext;

	private MessageListManage(Context mContext) {
		this.mContext = mContext;
	}

	public static MessageListManage getInstance(Context mContext) {
		if (instance == null) {
			synchronized (DBManager.class) {
				if (instance == null) {
					instance = new MessageListManage(mContext);
				}
			}
		}
		return instance;
	}

//	public void saveOrUpdate(String message, SQLiteDatabase database) {
//		try {
//			JSONArray jsonArray = new JSONArray(message);
//			List<ContentValues> list = new ArrayList<ContentValues>();
//			System.out.println(jsonArray);
//			JSONObject jsonObject;
//			ContentValues contentValues;
//			for (int i = 0; i < jsonArray.length(); i++) {
//				jsonObject = (JSONObject) jsonArray.get(i);
//				contentValues = new ContentValues();
//				contentValues
//						.put("ML_GroupID", jsonObject.getString("groupId"));
//				contentValues.put("ML_GroupName",
//						jsonObject.getString("groupName"));
//				contentValues.put("ML_UnreadCount",
//						jsonObject.getString("unReadCounts"));
//				contentValues.put("ML_FromUserID",
//						jsonObject.getString("senderLoginId"));
//				contentValues.put("ML_FromUserName",
//						jsonObject.getString("senderNickName"));
//				contentValues.put("ML_ToUserID",
//						jsonObject.getString("receiverLoginId"));
//				contentValues.put("ML_Type", jsonObject.getString("groupType"));
//				contentValues.put("ML_Content",
//						jsonObject.getString("messageContent"));
//				contentValues.put("ML_ResourceID",
//						jsonObject.getString("messageResourceId"));
//				contentValues.put(
//						"ML_CreateTime",
//						String.valueOf(jsonObject.getJSONObject(
//								"messageCreateTime").get("time")));
//				contentValues.put("ML_BelongTo", CIMDataConfig.getString(
//						mContext, CIMDataConfig.KEY_ACCOUNT));
//				list.add(contentValues);
//			}
//			DBManager.getInstance(mContext).replace(
//					MyDatabaseHelper.TABLE_MESSAGE_LIST, list, database);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
	
	public List<String> saveOrUpdateMessage(String message, SQLiteDatabase database){
		String[] columns = { "M_ID", "M_MessageID", "M_MessageSetID" };
		String selection = "M_MessageID = ? and M_MessageSetID = ? and M_UserID = ?";
		String userAccount = CIMDataConfig.getString(mContext,
				CIMDataConfig.KEY_ACCOUNT);
		JSONArray jsonArray;
		List<String> messageSetIdList = null;
		try {
			jsonArray = new JSONArray(message);
			messageSetIdList = new ArrayList<String>();
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String messageId = jsonObject.getString("messageId");
				String messageSetId = jsonObject.getString("messageSetId");
				String[] selectionArgs = { messageId, messageSetId,
						userAccount };
				Cursor cursor = DBManager.getInstance(mContext).select(
						MyDatabaseHelper.TABLE_MESSAGE, columns, selection,
						selectionArgs, null, null, null, database);
				if(cursor.moveToFirst() == false){
					ContentValues value = new ContentValues();
					value.put("M_MessageID", messageId);
					value.put("M_MessageSetID", messageSetId);
					value.put("M_FromUserID",
							jsonObject.getString("senderLoginId"));
					value.put("M_FromUserName",
							jsonObject.getString("senderNickName"));
					value.put("M_ToUserID",
							jsonObject.getString("receiverLoginId"));
					value.put("M_Type",
							jsonObject.getString("messageType"));
					value.put("M_Content", jsonObject.getString("messageContent"));
					value.put("M_ResourceID",
							jsonObject.getString("messageResourceId"));
					value.put(
							"M_CreateTime",
							String.valueOf(jsonObject.getJSONObject(
									"messageCreateTime").get("time")));
					value.put("M_GroupID", jsonObject.getString("groupId"));
					value.put("M_GroupName",
							jsonObject.getString("groupName"));
					value.put("M_Statu", jsonObject.getInt("statu"));
					value.put("M_UserID", userAccount);
					value.put("M_JSon", jsonObject.getString("JSonId"));
					DBManager.getInstance(mContext)
							.insert(MyDatabaseHelper.TABLE_MESSAGE, value,
									database);
				}
				cursor.close();
				messageSetIdList.add(messageSetId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return messageSetIdList;
	}

//	public List<RecentChat> getMessageList(SQLiteDatabase database,
//			String userAccount) {
//		String selection = "ML_BelongTo = ?";
//		String[] selectionArgs = new String[] { userAccount };
//		String orderBy = "ML_CreateTime desc";
//		Cursor cursor = DBManager.getInstance(mContext).select(
//				MyDatabaseHelper.TABLE_MESSAGE_LIST, null, selection,
//				selectionArgs, null, null, orderBy, database);
//		List<RecentChat> list = new ArrayList<RecentChat>();
//		if (cursor != null) {
//			int i = 0;
//			while (cursor.moveToNext()) {
//				RecentChat chat = new RecentChat();
//				String userId = cursor.getString(cursor
//						.getColumnIndex("ML_FromUserID"));
//				int type = cursor.getInt(cursor.getColumnIndex("ML_Type"));
//				String groupName = cursor.getString(cursor
//						.getColumnIndex("ML_GroupName"));
//				String userName = null;
//				String content = null;
//				if (type == 0) {
//					String[] group = groupName.split(":");
//					if(group[0].equals(CIMDataConfig.getString(mContext,
//							CIMDataConfig.KEY_USERNAME))){
//						userName = group[1];
//					}else{
//						userName = group[0];
//					}
//					content = cursor.getString(cursor
//							.getColumnIndex("ML_Content"));
//				} else if (type == 1) {
//					userName = cursor.getString(cursor
//							.getColumnIndex("ML_GroupName"));
//					content = cursor.getString(cursor
//							.getColumnIndex("ML_FromUserName"))
//							+ " : "
//							+ cursor.getString(cursor
//									.getColumnIndex("ML_Content"));
//				}
//				String time = cursor.getString(cursor
//						.getColumnIndex("ML_CreateTime"));
//				String groupId = cursor.getString(cursor
//						.getColumnIndex("ML_GroupID"));
//				int t = i % 8;
//				chat.setUserName(userName);
//				chat.setUserFeel(content);
//				chat.setUserTime(time);
//				chat.setImgPath(TestData.dir + TestData.names[t]);
//				chat.setGroupId(groupId);
//				chat.setUserId(userId);
//				chat.setGroupType(type);
//				chat.setGroupName(groupName);
//				list.add(chat);
//				i++;
//			}
//			cursor.close();
//		}
//		return list;
//	}
	
	public List<RecentChat> getChatRoomList(SQLiteDatabase database,
			String userAccount){
		String sql = "select count(*), M_GroupID from message where M_UserID=? and M_Statu=? group by M_GroupID order by M_CreateTime desc";
		String[] selectionArgs = new String[] { userAccount, "0" };
		Cursor cursor = DBManager.getInstance(mContext).rawQuery(sql, selectionArgs, database);
		List<RecentChat> list = new ArrayList<RecentChat>();
		if (cursor != null) {
			int i = 0;
			while (cursor.moveToNext()) {
				RecentChat chat = new RecentChat();
				int count = cursor.getInt(0);
				chat.setCount(count);//未读数量
				String groupId = cursor.getString(1);
				chat.setGroupId(groupId);//互动室Id
				int t = i % 8;
				chat.setImgPath(TestData.dir + TestData.names[t]);//互动室头像
				//----------------------------------------------------
				String selection2 = "M_UserID = ? and M_GroupID=?";
				String[] selectionArgs2 = new String[] { userAccount, groupId };
				String orderBy2 = "M_CreateTime desc";
				String limit = "1";
				Cursor cursor2 = DBManager.getInstance(mContext).select(
						MyDatabaseHelper.TABLE_MESSAGE, null, selection2,
						selectionArgs2, null, null, orderBy2, limit, database);
				if(cursor2 != null){
					while(cursor2.moveToNext()){
						String userId = cursor2.getString(cursor2
								.getColumnIndex("M_FromUserID"));
						chat.setUserId(userId);//消息发送者Id
						int type = cursor2.getInt(cursor2.getColumnIndex("M_Type"));
						chat.setGroupType(type);//消息类型（1为私聊室、2为群聊室）
						String groupName = cursor2.getString(cursor2
								.getColumnIndex("M_GroupName"));
						chat.setGroupName(groupName);//互动室名称
						String JSonId = cursor2.getString(cursor2
								.getColumnIndex("M_JSon"));
						String userName = null;
						String content = null;
						String picture = null;
						if (type == 1) {
							String[] group = groupName.split(":");
							String[] json = JSonId.split(":");
							if(group[0].equals(CIMDataConfig.getString(mContext,
									CIMDataConfig.KEY_USERNAME))){
								userName = group[1];
								picture = json[1];
							}else{
								userName = group[0];
								picture = json[0];
							}
							content = cursor2.getString(cursor2
									.getColumnIndex("M_Content"));
						} else if (type == 2) {
							userName = cursor2.getString(cursor2
									.getColumnIndex("M_GroupName"));
							content = cursor2.getString(cursor2
									.getColumnIndex("M_FromUserName"))
									+ " : "
									+ cursor2.getString(cursor2
											.getColumnIndex("M_Content"));
							picture = groupId;//群聊时，用群ID来获取群头像
						}
//						chat.setImgPath(picture);//互动室头像
						chat.setUserName(userName);//互动室名称
						chat.setUserFeel(content);//发送内容
						String time = cursor2.getString(cursor2
								.getColumnIndex("M_CreateTime"));
						chat.setUserTime(time);//发送时间
						String statu = cursor2.getString(cursor2.getColumnIndex("M_Statu"));
						chat.setStatu(statu);
					}
					cursor2.close();
					list.add(chat);
					i++;
				}
			}
			cursor.close();
		}
		return list;
	}
	
//	public long saveOrUpdate(ContentValues value, SQLiteDatabase database){
//		return DBManager.getInstance(mContext).replace(
//				MyDatabaseHelper.TABLE_MESSAGE_LIST, value, database);
//	}
}
