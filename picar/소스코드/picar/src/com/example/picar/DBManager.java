package com.example.picar;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
	
	SQLiteDatabase db;
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	
	public DBManager(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE POINT ("
				   + ID + " INTEGER, "
				   + NAME + " TEXT, "
				   + LATITUDE + " REAL, "
				   + LONGITUDE + " REAL);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void insert(long id, String name, double latitude, double longitude) {
		db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(NAME, name);
		values.put(LATITUDE, latitude);
		values.put(LONGITUDE, longitude);
		db.insert("POINT", null, values);
	}
	
	public void update(long id, String name) {
		db = getWritableDatabase();
		String sql = "UPDATE POINT SET "
				   + NAME + " = '" + name + "' "
				   + "WHERE " + ID + " = " + id;
		Log.d("update", sql);
		db.execSQL(sql);
	}
	
	public ArrayList<ArrayList<Object>> select() {
		db = getReadableDatabase();
		ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();

		Cursor c = db.query("POINT", null, null, null, null, null, null);
		while (c.moveToNext()) {
			ArrayList<Object> values = new ArrayList<Object>();
			values.add(c.getLong(c.getColumnIndex(ID)));
			values.add(c.getString(c.getColumnIndex(NAME)));
			values.add(c.getDouble(c.getColumnIndex(LATITUDE)));
			values.add(c.getDouble(c.getColumnIndex(LONGITUDE)));
			list.add(values);
		}
		
		return list;
	}
	
	public void delete(double id) {
		db = getWritableDatabase();
		String sql = "DELETE FROM POINT "
				   + "WHERE " + ID + " = " + id;
		db.execSQL(sql);
	}
}
