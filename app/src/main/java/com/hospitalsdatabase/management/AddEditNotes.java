package com.hospitalsdatabase.management;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.MenuItem.OnMenuItemClickListener;
import com.hospitalsdatabase.management.R;

public class AddEditNotes extends Activity {

	// Declare Variables
	private long rowID;
	private EditText hospitaltitle_edit;
	private EditText district_edit;
	private EditText type_edit;
	private EditText landline_edit;
	private EditText mobile_edit;
	private EditText coordinates_edit;
	private static final String HOSPITALTITLE = "hospitaltitle";
	private static final String DISTRICT = "district";
	private static final String TYPE = "type";
	private static final String LANDLINE = "landline";
	private static final String MOBILE = "mobile";
	private static final String COORDINATES = "coordinates";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_note);

		// Locate the EditText in add_note.xml
		hospitaltitle_edit = (EditText) findViewById(R.id.hospitalTitleEdit);
		district_edit = (EditText) findViewById(R.id.districtEdit);
		type_edit = (EditText) findViewById(R.id.typeEdit);
		landline_edit = (EditText) findViewById(R.id.landlineEdit);
		mobile_edit = (EditText) findViewById(R.id.mobileEdit);
		coordinates_edit = (EditText) findViewById(R.id.coordinateEdit);

		// Retrieve the Row ID from ViewNote.java
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			rowID = extras.getLong("row_id");
			hospitaltitle_edit.setText(extras.getString(HOSPITALTITLE));
			district_edit.setText(extras.getString(DISTRICT));
			type_edit.setText(extras.getString(TYPE));
			landline_edit.setText(extras.getString(LANDLINE));
			mobile_edit.setText(extras.getString(MOBILE));
			coordinates_edit.setText(extras.getString(COORDINATES));
			
		}
	}

	// Create an ActionBar menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Save Note")
				.setOnMenuItemClickListener(this.SaveButtonClickListener)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateOptionsMenu(menu);
	}

	// Capture save menu item click
	OnMenuItemClickListener SaveButtonClickListener = new OnMenuItemClickListener() {
		public boolean onMenuItemClick(MenuItem item) {
			Log.w("SaveButtonClickListener","--------");
			// Passes the data into saveNote() function
			if (hospitaltitle_edit.getText().length() != 0) {
				Log.w("hospitaltitle_edit",hospitaltitle_edit.getText().toString());
				AsyncTask<Object, Object, Object> saveNoteAsyncTask = new AsyncTask<Object, Object, Object>() {
					@Override
					protected Object doInBackground(Object... params) {
						Log.w("SaveButtonClickListener","---doInBackground----");
						saveNote();
						return null;
					}

					@Override
					protected void onPostExecute(Object result) {
						// Close this activity
						finish();
					}
				};
//				 Execute the saveNoteAsyncTask AsyncTask above
				saveNoteAsyncTask.execute((Object[]) null);
			}

			else {
				// Display a simple alert dialog that forces user to put in a title
				AlertDialog.Builder alert = new AlertDialog.Builder(
						AddEditNotes.this);
				alert.setTitle("Hospital Title is required");
				alert.setMessage("Put in a Hospital title");
				alert.setPositiveButton("Okay", null);
				alert.show();
			}

			return false;

		}
	};
	// saveNote() function
	private void saveNote() {
		Log.w("saveNote>> ","save note");
		DatabaseConnector dbConnector = new DatabaseConnector(this);
		
		if (getIntent().getExtras() == null) {
			Log.w("saveNote>> ","if");
			// Passes the data to InsertNote in DatabaseConnector.java
			dbConnector.InsertNote(hospitaltitle_edit.getText().toString(), district_edit.getText().toString(),
					type_edit.getText().toString(), landline_edit.getText().toString(),
					mobile_edit.getText().toString(), coordinates_edit.getText().toString());
		} else {
			Log.w("saveNote>> ","else");
			// Passes the Row ID and data to UpdateNote in DatabaseConnector.java
			dbConnector.UpdateNote(rowID, hospitaltitle_edit.getText().toString(),
					district_edit.getText().toString(), type_edit.getText().toString(), landline_edit.getText().toString(),
					mobile_edit.getText().toString(), coordinates_edit.getText().toString());
		}
	}
}
