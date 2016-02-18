package com.example.cim.cache;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

public class DBManager {

	public static DBManager instance;
	public Context mContext;
	public SQLiteDatabase db;

	private DBManager(Context mContext) {
		this.mContext = mContext;

		SQLiteDatabase.loadLibs(mContext);
		MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext, "cim.db",
				null, 1);
		db = dbHelper.getWritableDatabase("key");
	}

	public static DBManager getInstance(Context mContext) {
		if (instance == null) {
			synchronized (DBManager.class) {
				if (instance == null) {
					instance = new DBManager(mContext);
				}
			}
		}
		return instance;
	}

	public SQLiteDatabase getDatabase() {
		if (db == null) {
			SQLiteDatabase.loadLibs(mContext);
			MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext,
					"cim.db", null, 1);
			db = dbHelper.getWritableDatabase("key");
		}
		return db;
	}

	public long insert(String table, ContentValues values,
			SQLiteDatabase database) {
		long result = -1;
		SQLiteDatabase db = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			result = db.insert(table, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				// db.close();
			}
		}
		return result;
	}

	public long insert(String table, List<ContentValues> values,
			SQLiteDatabase database) {
		long result = -1;
		SQLiteDatabase db = null;
		if (database == null) {
			db = this.getDatabase();
		} else {
			db = database;
		}
		if (db != null) {
			db.beginTransaction();
			try {
				for (ContentValues v : values) {
					result = db.insert(table, null, v);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				result = -1;
			} finally {
				db.endTransaction();
				// db.close();
			}
		}
		return result;
	}

	public int delete(String table, String whereClause, String[] whereArgs,
			SQLiteDatabase database) {
		int result = -1;
		SQLiteDatabase db = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			result = db.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				// db.close();
			}
		}
		return result;
	}

	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs, SQLiteDatabase database) {
		int result = -1;
		SQLiteDatabase db = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			result = db.update(table, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				// db.close();
			}
		}
		return result;
	}

	public long replace(String table, ContentValues values,
			SQLiteDatabase database) {
		long result = -1;
		SQLiteDatabase db = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			result = db.replace(table, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				// db.close();
			}
		}
		return result;
	}

	public long replace(String table, List<ContentValues> values,
			SQLiteDatabase database) {
		long result = -1;
		SQLiteDatabase db = null;
		if (database == null) {
			db = this.getDatabase();
		} else {
			db = database;
		}
		if (db != null) {
			db.beginTransaction();
			try {
				for (ContentValues v : values) {
					result = db.replace(table, null, v);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				result = -1;
			} finally {
				db.endTransaction();
				// db.close();
			}
		}
		return result;
	}

	public Cursor select(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, SQLiteDatabase database) {
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			cursor = db.query(table, columns, selection, selectionArgs,
					groupBy, having, orderBy);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				// db.close();
			}
		}
		return cursor;
	}

	public Cursor select(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit, SQLiteDatabase database) {
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			cursor = db.query(table, columns, selection, selectionArgs,
					groupBy, having, orderBy, limit);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				// db.close();
			}
		}
		return cursor;
	}

	public boolean execSQL(String sql, SQLiteDatabase database) {
		SQLiteDatabase db = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			db.execSQL(sql);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				// db.close();
			}
		}
	}

	public boolean execSQL(String sql, Object[] bindArgs,
			SQLiteDatabase database) {
		SQLiteDatabase db = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			db.execSQL(sql, bindArgs);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				// db.close();
			}
		}
	}

	public Cursor rawQuery(String sql, String[] selectionArgs,
			SQLiteDatabase database) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			if (database == null) {
				db = this.getDatabase();
			} else {
				db = database;
			}
			cursor = db.rawQuery(sql, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				// db.close();
			}
		}
		return cursor;
	}

	public void beginTransaction(TransactionListener listener) {
		SQLiteDatabase db = this.getDatabase();
		if (db != null && listener != null) {
			db.beginTransaction();
			try {
				listener.beginWithTransaction(db);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
				// db.close();
			}
		}
	}

	public interface TransactionListener {
		public void beginWithTransaction(SQLiteDatabase db);
	}
}
