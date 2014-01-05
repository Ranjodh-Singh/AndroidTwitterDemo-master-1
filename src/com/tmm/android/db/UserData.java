package com.tmm.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserData {

	static final int VERSION = 1;
	static final String DATABASE = "user.db";
	static final String TABLE = "users";

	public static final String C_USERNAME = "username";
	public static final String C_OAUTH = "oauth";
	public static final String C_LAT = "location_lat";
	public static final String C_LONG = "location_long";
	public static final String C_PHONENUMBER = "phonenumber";
	private static final String[] DB_TEXT_COLUMNS = {C_USERNAME,
			C_OAUTH, C_LAT, C_LONG, C_PHONENUMBER };

	// DbHelper implementations
	class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		
			System.out.println("Creating database: " + DATABASE);
		
			db.execSQL("create table " + TABLE + " (" + C_USERNAME + " text, " + C_OAUTH + " text , " + C_LAT
					+ " text, " + C_LONG + " text, " + C_PHONENUMBER + " text)");
			System.out.println("Created database: " + DATABASE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table " + TABLE);
			this.onCreate(db);
		}
	}

	final DbHelper dbHelper;

	public UserData(Context context) {
		this.dbHelper = new DbHelper(context);
		System.out.println("Initialized data");
	}

	public void close() {
		this.dbHelper.close();
	}

	public void insertOrIgnore(ContentValues values) {
		System.out.println("insertOrIgnore on " + values);

		SQLiteDatabase db = this.dbHelper.getWritableDatabase();

		try {
			long primarykey = db.insertWithOnConflict(TABLE, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
			System.out.println(" insertOrIgnore Primary Key  " + primarykey);
		} finally {
			db.close();
		}
	}

	
	public UserDBObject getUsername(String username) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		UserDBObject userdbObject = new UserDBObject();

		try {
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_USERNAME + "='" + username+"'", null, null, null, null);
			try {
				if (cursor.moveToNext()) {
					userdbObject.setUserName(cursor.getString(cursor.getColumnIndex(C_USERNAME)));
					userdbObject.setOauthProvider(cursor.getString(cursor.getColumnIndex(C_OAUTH)));
					userdbObject.setLocation_lat(cursor.getString(cursor.getColumnIndex(C_LAT)));
					userdbObject.setLocation_long(cursor.getString(cursor.getColumnIndex(C_LONG)));
					userdbObject.setPhoneNumber(cursor.getString(cursor.getColumnIndex(C_PHONENUMBER)));
					return userdbObject;
				} else {
					return null;
				}
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}

	}

	/**
	 * Deletes ALL the data
	 */
	public void delete() {
		// Open Database
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// Delete the data
		db.delete(TABLE, null, null);

		// Close Database
		db.close();
	}

}
