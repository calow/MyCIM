package com.example.cim.test;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class TestDB {
	public static void getDBTableList(SQLiteDatabase db) {
		String sql = "select name from sqlite_master where type='table' order by name;";
		Cursor cursor = db.rawQuery(sql, null);
		String name = "";
		while (cursor.moveToNext()) {
			name = cursor.getString(cursor.getColumnIndex("name"));
			System.out.println(name);
		}
		cursor.close();
	}
}
