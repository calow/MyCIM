package com.example.cim.cache;

import android.content.Context;
import android.util.Log;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "cim.db";
	public static final String TABLE_MESSAGE_LIST = "messagelist";
	public static final String TABLE_MESSAGE = "message";
	public static final String TABLE_FRIEND_GROUPS = "friendgroups";
	public static final String TABLE_FRIEND = "friend";
	public static final String TABLE_FLAG_CODE = "flagcode";
	
	public static final String MESSAGELIST_TABLE = ""
			+ "CREATE TABLE IF NOT EXISTS messagelist ("
			+ "  ML_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "  ML_GroupID int NOT NULL UNIQUE,"
			+ "  ML_GroupName varchar(20) NOT NULL,"
			+ "  ML_UnreadCount int NOT NULL,"
			+ "  ML_FromUserID varchar(20) NOT NULL,"
			+ "  ML_FromUserName varchar(20),"
			+ "  ML_ToUserID varchar(20),"
			+ "  ML_Type int NOT NULL,"
			+ "  ML_Content varchar(100) DEFAULT NULL,"
			+ "  ML_ResourceID int DEFAULT NULL,"
			+ "  ML_CreateTime varchar(20) NOT NULL,"
			+ "  ML_BelongTo varchar(20) NOT NULL"
			+ ") ";

	public static final String MESSAGE_TABLE = ""
			+ "CREATE TABLE IF NOT EXISTS message ("
			+ "  M_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "  M_MessageID int,"
			+ "  M_MessageSetID int,"
			+ "  M_FromUserID varchar(20) NOT NULL,"
			+ "  M_FromUserName varchar(20),"
			+ "  M_ToUserID varchar(20),"
			+ "  M_Type int NOT NULL,"
			+ "  M_Content varchar(100) DEFAULT NULL,"
			+ "  M_ResourceID int DEFAULT NULL,"
			+ "  M_CreateTime varchar(20) NOT NULL,"
			+ "  M_GroupID int NOT NULL,"
			+ "  M_GroupName varchar(20) NOT NULL,"
			+ "  M_Statu int NOT NULL,"
			+ "  M_UserID varchar(20) NOT NULL"
			+ ") ";
	
	public static final String FRIENDGROUPS_TABLE = ""
			+ "CREATE TABLE IF NOT EXISTS friendgroups ("
			+ "  FG_ID int NOT NULL,"
			+ "  FG_Name varchar(20) NOT NULL,"
			+ "  FG_UserID varchar(20) NOT NULL,"
			+ "  FG_FlagCode varchar(50) NOT NULL,"
			+ "  PRIMARY KEY (FG_ID)"
			+ ") ";
	
	public static final String FRIEND_TABLE = ""
			+ "CREATE TABLE IF NOT EXISTS friend ("
			+ "  F_ID int NOT NULL,"
			+ "  F_FriendID varchar(20) NOT NULL,"
			+ "  F_UserID varchar(20) NOT NULL,"
			+ "  F_NickName varchar(20) NOT NULL,"
			+ "  F_RemarkName varchar(30) DEFAULT NULL,"
			+ "  F_OnlineCode int NOT NULL,"
			+ "  F_OnlineValue varchar(10) NOT NULL,"
			+ "  F_FriendGroupID int NOT NULL,"
			+ "  F_GroupID int NOT NULL,"
			+ "  F_Signture varchar(100) DEFAULT NULL,"
			+ "  F_FlagCode varchar(50) NOT NULL,"
			+ "  PRIMARY KEY (F_ID)"
			+ ") ";
	
	public static final String FLAGCODE_TABLE = ""
			+ "CREATE TABLE IF NOT EXISTS flagcode ("
			+ "  FC_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "  FC_UserID varchar(20) NOT NULL UNIQUE,"
			+ "  FC_Value varchar(50) NOT NULL"
			+ ") ";
	
	public MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DB_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(MESSAGELIST_TABLE);
			db.execSQL(MESSAGE_TABLE);
			db.execSQL(FRIENDGROUPS_TABLE);
			db.execSQL(FRIEND_TABLE);
			db.execSQL(FLAGCODE_TABLE);
		} catch (net.sqlcipher.SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL("drop table if exists messagelist");
			db.execSQL("drop table if exists message");
			db.execSQL("drop table if exists flagcode");
			db.execSQL("drop table if exists friendgroups");
			db.execSQL("drop table if exists friend");
		} catch (SQLException e) {
			Log.i("error", "drop table failed");
		}
		onCreate(db);
	}

}
