package com.hospitalsdatabase.management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hospitalsdatabase.management.R;

public class ViewNote extends Activity {
	
	//declared variables
	private long rowID;
	private TextView HospitalTitleTv;
	private TextView DistrictTv;
	private TextView TypeTv;
	private TextView LandlineTv;
	private TextView MobileTv;
	private TextView CoordinatesTv;
	private static final String HOSPITALTITLE = "hospitaltitle";
	private static final String DISTRICT = "district";
	private static final String TYPE = "type";
	private static final String LANDLINE = "landline";
	private static final String MOBILE = "mobile";
	private static final String COORDINATES = "coordinates";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_note);

		// Locate the TextView in view_note.xml
		HospitalTitleTv = (TextView) findViewById(R.id.HospitalTitleText);
		DistrictTv = (TextView) findViewById(R.id.DistrictText);
		TypeTv = (TextView) findViewById(R.id.TypeText);
		LandlineTv = (TextView) findViewById(R.id.LandlineText);
		MobileTv = (TextView) findViewById(R.id.MobileText);
		CoordinatesTv = (TextView) findViewById(R.id.CoordinatesText);
		// Retrieve the ROW ID from MainActivity.java
		Bundle extras = getIntent().getExtras();
		rowID = extras.getLong(MainActivity.ROW_ID);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Execute LoadNotes() AsyncTask
		new LoadNotes().execute(rowID);
	}

	// LoadNotes() AsyncTask
	private class LoadNotes extends AsyncTask<Long, Object, Cursor> {
		// Calls DatabaseConnector.java class
		DatabaseConnector dbConnector = new DatabaseConnector(ViewNote.this);

        private GoogleMap googleMap;
		@Override
		protected Cursor doInBackground(Long... params) {
			// Pass the Row ID into GetOneNote function in
			// DatabaseConnector.java class
			dbConnector.open();
			return dbConnector.GetOneNote(params[0]);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);

			result.moveToFirst();
			// Retrieve the column index for each data item
			int HospitalTitleIndex = result.getColumnIndex(HOSPITALTITLE);
			int DistrictIndex = result.getColumnIndex(DISTRICT);
			int TypeIndex = result.getColumnIndex(TYPE);
			int LandlineIndex = result.getColumnIndex(LANDLINE);
			int MobileIndex = result.getColumnIndex(MOBILE);
			int CoordinatesIndex = result.getColumnIndex(COORDINATES);

			// Set the Text in TextView
			HospitalTitleTv.setText(result.getString(HospitalTitleIndex));
			DistrictTv.setText(result.getString(DistrictIndex));
			TypeTv.setText(result.getString(TypeIndex));
			LandlineTv.setText(result.getString(LandlineIndex));
			MobileTv.setText(result.getString(MobileIndex));
			CoordinatesTv.setText(result.getString(CoordinatesIndex));

            try {
                if (googleMap == null) {
                    googleMap = ((MapFragment) getFragmentManager().
                            findFragmentById(R.id.map)).getMap();
                }
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                String LatLong []= result.getString(CoordinatesIndex).split(";");
                LatLng hospitalLocation = new LatLng(0,0);// default
                if(LatLong.length == 2) {
                    Log.w("Latitude Longitude",LatLong[0] + " " + LatLong[1]);
                    hospitalLocation = new LatLng(Double.parseDouble(LatLong[0]), Double.parseDouble(LatLong[1]));
                }
                Log.w(" hospitalLocation",hospitalLocation.toString());
                Marker TP = googleMap.addMarker(new MarkerOptions().
                        position(hospitalLocation).title("Hospital location"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

			result.close();
			dbConnector.close();
		}
	}

	// Create an options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Edit Note")
				.setOnMenuItemClickListener(this.EditButtonClickListener)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add("Delete Notes")
				.setOnMenuItemClickListener(this.DeleteButtonClickListener)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateOptionsMenu(menu);
	}

	// Capture edit menu item click
	OnMenuItemClickListener EditButtonClickListener = new OnMenuItemClickListener() {
		public boolean onMenuItemClick(MenuItem item) {

			// Pass Row ID and data to AddEditNotes.java
			Intent addeditnotes = new Intent(ViewNote.this, AddEditNotes.class);

			addeditnotes.putExtra(MainActivity.ROW_ID, rowID);
			addeditnotes.putExtra(HOSPITALTITLE, HospitalTitleTv.getText());
			addeditnotes.putExtra(DISTRICT, DistrictTv.getText());
			addeditnotes.putExtra(TYPE, TypeTv.getText());
			addeditnotes.putExtra(LANDLINE, LandlineTv.getText());
			addeditnotes.putExtra(MOBILE, MobileTv.getText());
			addeditnotes.putExtra(COORDINATES, CoordinatesTv.getText());
			
			startActivity(addeditnotes);

			return false;

		}
	};

	// Capture delete menu item click
	OnMenuItemClickListener DeleteButtonClickListener = new OnMenuItemClickListener() {
		public boolean onMenuItemClick(MenuItem item) {

			// Calls DeleteNote() Function
			DeleteNote();

			return false;

		}
	};

	private void DeleteNote() {

		// Display a simple alert dialog to reconfirm the deletion
		AlertDialog.Builder alert = new AlertDialog.Builder(ViewNote.this);
		alert.setTitle("Delete Item");
		alert.setMessage("Do you really want to delete this note?");

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				final DatabaseConnector dbConnector = new DatabaseConnector(
						ViewNote.this);

				AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
					@Override
					protected Object doInBackground(Long... params) {
						// Passes the Row ID to DeleteNote function in
						// DatabaseConnector.java
						dbConnector.DeleteNote(params[0]);
						return null;
					}

					@Override
					protected void onPostExecute(Object result) {
						// Close this activity
						finish();
					}
				};
				// Execute the deleteTask AsyncTask above
				deleteTask.execute(new Long[] { rowID });
			}
		});

		// Do nothing on No button click
		alert.setNegativeButton("No", null).show();
	}
}
