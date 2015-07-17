package com.hospitalsdatabase.management;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseConnector {

	// Declare Variables
	private static final String DB_NAME = "HospitalsDatabase";
	private static final String TABLE_NAME = "HospitalsTable";
	private static final String HOSPITALTITLE = "hospitaltitle";
	private static final String ID = "_id";
	private static final String DISTRICT = "district";
	private static final String TYPE = "type";
	private static final String LANDLINE = "landline";
	private static final String MOBILE = "mobile";
	private static final String COORDINATES = "coordinates";
	
	private static final int DATABASE_VERSION = 6;
	private SQLiteDatabase database;
	private DatabaseHelper dbOpenHelper;

	public DatabaseConnector(Context context) {
		dbOpenHelper = new DatabaseHelper(context, DB_NAME, null,
				DATABASE_VERSION);

	}

	// Open Database function
	public void open() throws SQLException {
		// Allow database to be in writable mode
		database = dbOpenHelper.getWritableDatabase();
	}

	// Close Database function
	public void close() {
		if (database != null)
			database.close();
		
	}

	// Create Database function
	public void InsertNote(String hospitaltitle, String district, String type, String landline, String mobile, String coordinates) {
		Log.w("Insert Hospital >>> ","hospital "+hospitaltitle+" district "+district+" type "+type+ " landline"+landline+" mob "+mobile+" coor "+coordinates);
		ContentValues newCon = new ContentValues();
		newCon.put(HOSPITALTITLE, hospitaltitle);
		newCon.put(DISTRICT, district);
		newCon.put(TYPE, type);
		newCon.put(LANDLINE, landline);
		newCon.put(MOBILE, mobile);
		newCon.put(COORDINATES, coordinates);
		
		Log.w("Insert Hospital >>> ","before hospital is inserted to DB");
		open();
//		try{
		database.insert(TABLE_NAME, null, newCon);
		Cursor res = database.query(TABLE_NAME, new String[] { ID, HOSPITALTITLE }, null,
				null, null, null, HOSPITALTITLE);
		Log.w("Res after insert>>> ",Integer.toString(res.getCount()));
//		}
//		catch(Exception ex){
//			Log.v("exception ",ex.getMessage());
//		}
		Log.w("Insert Hospital ######>>> ","after hospital is inserted to DB");
		close();
		Log.w("Insert Hospital >>> ","after hospital is inserted to DB");
	}

	// Update Database function
	public void UpdateNote(long id, String hospitaltitle, String district, String type, String landline, String mobile, String coordinates) {
		Log.w("UpdateNote >>> ","hospital "+hospitaltitle+" district "+district+" type "+type+ " landline"+landline+" mob "+mobile+" coor "+coordinates);
		
		ContentValues editCon = new ContentValues();
		editCon.put(HOSPITALTITLE, hospitaltitle);
		editCon.put(DISTRICT, district);
		editCon.put(TYPE, type);
		editCon.put(LANDLINE, landline);
		editCon.put(MOBILE, mobile);
		editCon.put(COORDINATES, coordinates);

		open();
		database.update(TABLE_NAME, editCon, ID + "=" + id, null);
		close();
	}

	// Delete Database function
	public void DeleteNote(long id) {
		open();
		database.delete(TABLE_NAME, ID + "=" + id, null);
		close();
	}

	// List all data function
	public Cursor ListAllNotes() {
		Log.w("ListAllNotes Hospital >>> ","ListAllNotes ");
		Cursor res = database.query(TABLE_NAME, new String[] { ID, HOSPITALTITLE }, null,
				null, null, null, HOSPITALTITLE);
		Log.w("ListAllNotes Result>>> ",Integer.toString(res.getCount()));
		return res;
	}

	// Capture single data by ID
	public Cursor GetOneNote(long id) {
		return database.query(TABLE_NAME, null, ID + "=" + id, null, null,
				null, null);
	}

}
