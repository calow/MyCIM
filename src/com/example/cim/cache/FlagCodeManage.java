package com.example.cim.cache;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;

public class FlagCodeManage {
	public static FlagCodeManage instance;

	public Context mContext;

	private FlagCodeManage(Context mContext) {
		this.mContext = mContext;
	}

	public static FlagCodeManage getInstance(Context mContext) {
		if (instance == null) {
			synchronized (DBManager.class) {
				if (instance == null) {
					instance = new FlagCodeManage(mContext);
				}
			}
		}
		return instance;
	}

	public long saveOrUpdate(String code, String userAccount,
			SQLiteDatabase database) {
		ContentValues values = new ContentValues();
		values.put("FC_UserID", userAccount);
		values.put("FC_Value", code);
		return DBManager.getInstance(mContext).replace(
				MyDatabaseHelper.TABLE_FLAG_CODE, values, database);
	}

	public String getFlagCode(String userAccount, SQLiteDatabase database) {
		String flagCode;
		Cursor cursor = DBManager.getInstance(mContext).select(
				MyDatabaseHelper.TABLE_FLAG_CODE, new String[] { "FC_Value" },
				"FC_UserID = ?", new String[] { userAccount }, null, null,
				null, database);
		if (cursor.moveToNext()) {
			flagCode = cursor.getString(0);
		} else {
			flagCode = String.valueOf(System.currentTimeMillis());
			saveOrUpdate(flagCode, userAccount, database);
		}
		cursor.close();
		return flagCode;
	}
}
