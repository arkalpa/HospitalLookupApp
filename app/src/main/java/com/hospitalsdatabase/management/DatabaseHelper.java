package com.hospitalsdatabase.management;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	// Declare Variables
	private static final String DB_NAME = "HospitalsDatabase";
	public static final String TABLE_NAME = "HospitalsTable";
	public static final String HOSPITALTITLE = "hospitaltitle";
	public static final String DISTRICT = "district";
	private static final String TYPE = "type";
	private static final String LANDLINE = "landline";
	private static final String MOBILE = "mobile";
	private static final String COORDINATES = "coordinates";
    private final Context myContext;
    public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		
		super(context, DB_NAME, factory, version);

        this.myContext = context;
	}

	@Override	
	public void onCreate(SQLiteDatabase db) {
		
		 	Log.w("LOG>>>>","Creating database");
		System.out.println("");
		// Create a database table
		String createQuery = "CREATE TABLE " + TABLE_NAME
				+ " (_id integer primary key autoincrement," + HOSPITALTITLE + ", "
				+ DISTRICT +", "+ TYPE +", "+ LANDLINE +", "+ MOBILE +", "+ COORDINATES +
				");";
		db.execSQL(createQuery);

        // Pre-load data from csv file

        Log.w("preloading Database","Staring process");
        AssetManager assetManager = this.myContext.getAssets();
        try {
            InputStream inputStream = assetManager.open("Institution_Med.csv");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String line;
            String[] values;
            int i =0;
            while ((line = bufferedReader.readLine()) != null) {

                values = line.split(",");
                Log.w("line>>> ",Integer.toString(i++)+" line length>> "+values.length);
                String hospitaltitle = values[4];
                String district = values[1];
                String type = values[5];
                String landline = values[6];
                String mobile = values[7];
                String coordinates;
                if(values.length == 8)
                coordinates= "";
                else coordinates=values[8];
                ContentValues newCon = new ContentValues();
                newCon.put(HOSPITALTITLE, hospitaltitle);
                newCon.put(DISTRICT, district);
                newCon.put(TYPE, type);
                newCon.put(LANDLINE, landline);
                newCon.put(MOBILE, mobile);
                newCon.put(COORDINATES, coordinates);
                db.insert(TABLE_NAME, null, newCon);
            }

        } catch (IOException e) {
            Log.e("ERROR", "Failed to open data input file");
            e.printStackTrace();
        }
        Log.w("preloading Database","ending process");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Database will be wipe on version change
		Log.w("LOG>>>>","Dropping database");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}


}