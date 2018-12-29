/**
 * Author: Suhrid Ranjan Das
 */
package com.suhrid.simplified.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "android_api";

	// Login table name
	private static final String TABLE_USER = "user";



	// Login Table Columns names
	private static final String KEY = "id";
	private static final String  U_ID= "u_id";
	private static final String IS_STAFF = "is_staff";
	private static final String EMP_TYPE = "emp_type";
	/*private static final String SCHOOL_NAME = "sc_name";
	private static final String SCHOOL_LOGO = "sc_logo";
	private static final String  STUDENT_IMG= "st_img";
	private static final String  CLASS_NAME= "class_name";
	private static final String  SECTION= "secion";
	private static final String  GENDER= "gender";
	private static final String  PHN_NO= "phno";
	private static final String  KEY_ID= "key_id";
    private static final String  COLOR= "color";
    private static final String  URL= "url";*/





	public SQLiteHandler(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ KEY + " INTEGER PRIMARY KEY," +  U_ID + " TEXT," + IS_STAFF + " TEXT," + EMP_TYPE + " TEXT" +")";
		db.execSQL(CREATE_LOGIN_TABLE);

		Log.d(TAG, "Database tables created");

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String u_id,String is_staff,String emp_type)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(U_ID, u_id);
		values.put(IS_STAFF,is_staff);
		values.put(EMP_TYPE,emp_type);
		/*values.put(SCHOOL_NAME, sc_name); // Email
		values.put(SCHOOL_LOGO, sc_logo);
		values.put(STUDENT_IMG, st_img);// Created At
		values.put(CLASS_NAME, class_name);
		values.put(SECTION, section);
		values.put(GENDER, gender);
		values.put(PHN_NO, phno);
		values.put(KEY_ID, key_id);
        values.put(COLOR, color);
        values.put(URL, url);*/
		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}


	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails()
	{
		HashMap<String, String> user = new HashMap<>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			//user.put("key", cursor.getString(1));
			user.put("u_id", cursor.getString(1));
			user.put("is_staff", cursor.getString(2));
			user.put("emp_type", cursor.getString(3));
			/*user.put("sc_name", cursor.getString(3));
			user.put("sc_logo", cursor.getString(4));
			user.put("st_img", cursor.getString(5));
			user.put("class_name", cursor.getString(6));
			user.put("section", cursor.getString(7));
			user.put("gender", cursor.getString(8));
			user.put("phno", cursor.getString(9));
			user.put("key_id", cursor.getString(10));
            user.put("color", cursor.getString(11));
            user.put("url", cursor.getString(12));*/
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}


	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteUsers()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}



}
