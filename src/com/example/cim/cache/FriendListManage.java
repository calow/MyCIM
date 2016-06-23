package com.example.cim.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.cim.cache.DBManager.TransactionListener;
import com.example.cim.model.RecentChat;
import com.example.cim.test.TestData;
import com.example.cim.util.CIMDataConfig;

import android.content.ContentValues;
import android.content.Context;

public class FriendListManage {

	public static FriendListManage instance;

	public Context mContext;

	private FriendListManage(Context mContext) {
		this.mContext = mContext;
	}

	public static FriendListManage getInstance(Context mContext) {
		if (instance == null) {
			synchronized (DBManager.class) {
				if (instance == null) {
					instance = new FriendListManage(mContext);
				}
			}
		}
		return instance;
	}

	public void saveOrUpdate(final String message) {
		DBManager.getInstance(mContext).beginTransaction(
				new TransactionListener() {

					@Override
					public void beginWithTransaction(SQLiteDatabase db) {
						try {
							JSONArray jsonArray1 = new JSONArray(message);
							String flagCode = String.valueOf(System
									.currentTimeMillis());
							long result = FlagCodeManage.getInstance(mContext)
									.saveOrUpdate(flagCode, CIMDataConfig.getString(mContext,
											CIMDataConfig.KEY_ACCOUNT), db);
							if (result != -1) {
								for (int i = 0; i < jsonArray1.length(); i++) {
									JSONObject jsonObject1 = jsonArray1
											.getJSONObject(i);
									ContentValues values1 = new ContentValues();
									values1.put("FG_ID",
											jsonObject1.getInt("groupId"));
									values1.put("FG_Name",
											jsonObject1.getString("groupName"));
									values1.put("FG_UserID",
											jsonObject1.getString("owner"));
									values1.put("FG_FlagCode", flagCode);
									DBManager
											.getInstance(mContext)
											.replace(
													MyDatabaseHelper.TABLE_FRIEND_GROUPS,
													values1, db);

									JSONArray jsonArray2 = jsonObject1
											.getJSONArray("friendList");
									List<ContentValues> list = new ArrayList<ContentValues>();
									for (int j = 0; j < jsonArray2.length(); j++) {
										JSONObject jsonObject2 = jsonArray2
												.getJSONObject(j);
										ContentValues values2 = new ContentValues();
										values2.put("F_ID",
												jsonObject2.getInt("friedID"));
										values2.put("F_FriendID", jsonObject2
												.getString("loginId"));
										values2.put("F_UserID",
												jsonObject1.getString("owner"));
										values2.put("F_NickName", jsonObject2
												.getString("nickName"));
										values2.put("F_RemarkName", jsonObject2
												.getString("remarkName"));
										values2.put("F_OnlineCode", jsonObject2
												.getString("onlineCode"));
										values2.put(
												"F_OnlineValue",
												jsonObject2
														.getString("onlienValue"));
										values2.put("F_FriendGroupID",
												jsonObject1.getInt("groupId"));
										values2.put("F_GroupID", jsonObject2
												.getInt("chatRoomId"));
										values2.put("F_GroupName", jsonObject2
												.getString("chatRoomName"));
										values2.put("F_Signture",
												jsonObject2.getString("signture"));
										values2.put("F_FlagCode", flagCode);
										list.add(values2);
									}
									DBManager.getInstance(mContext).replace(
											MyDatabaseHelper.TABLE_FRIEND,
											list, db);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	public HashMap<String, List<RecentChat>> getFriendList(SQLiteDatabase database, String userAccount) {
		HashMap<String, List<RecentChat>> lisMap = new HashMap<String, List<RecentChat>>();
		String flagCode = FlagCodeManage.getInstance(mContext)
				.getFlagCode(userAccount, database);
		String[] columns = new String[] { "FG_ID", "FG_Name" };
		String selection = "FG_UserID = ? and FG_FlagCode = ?";
		String[] selectionArgs = new String[] { userAccount, flagCode };
		Cursor cursor = DBManager.getInstance(mContext).select(
				MyDatabaseHelper.TABLE_FRIEND_GROUPS, columns, selection,
				selectionArgs, null, null, null, database);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<RecentChat> listChat = new ArrayList<RecentChat>();
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			String gId = cursor.getString(cursor.getColumnIndex("FG_ID"));
			String gName = cursor.getString(cursor.getColumnIndex("FG_Name"));
			map.put("id", gId);
			map.put("name", gName);
			list.add(map);
		}
		cursor.close();
		for (Map<String, String> maps : list) {
			String id = maps.get("id");
			Cursor cursor2 = DBManager.getInstance(mContext).select(
					MyDatabaseHelper.TABLE_FRIEND,
					new String[] { "F_NickName", "F_Signture", "F_OnlineCode",
							"F_GroupID", "F_GroupName", "F_FriendID" },
					"F_UserID = ? and F_FriendGroupID = ? and F_FlagCode = ?",
					new String[] { userAccount, id, flagCode }, null, null,
					"F_OnlineCode desc", database);
			int i = 0;
			while(cursor2.moveToNext()){
				RecentChat rc = new RecentChat();
				rc.setUserName(cursor2.getString(cursor2.getColumnIndex("F_NickName")));
				rc.setUserFeel(cursor2.getString(cursor2.getColumnIndex("F_Signture")));
				rc.setImgPath(TestData.dir + TestData.names[i % 8]);
				rc.setStatu(cursor2.getString(cursor2.getColumnIndex("F_OnlineCode")));
				rc.setGroupId(cursor2.getString(cursor2.getColumnIndex("F_GroupID")));
				rc.setGroupName(cursor2.getString(cursor2.getColumnIndex("F_GroupName")));
				rc.setUserId(cursor2.getString(cursor2.getColumnIndex("F_FriendID")));
				listChat.add(rc);
			}
			cursor2.close();
			lisMap.put(maps.get("name"), listChat);
		}
		return lisMap;
	}
}
