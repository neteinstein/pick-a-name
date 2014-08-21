package org.neteinstein.pickaname.main;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.UiThread;
import org.neteinstein.pickaname.R;
import org.neteinstein.pickaname.app.PANApp;
import org.neteinstein.pickaname.bus.models.DatabaseResult;
import org.neteinstein.pickaname.database.DatabaseAdapter;
import org.neteinstein.pickaname.database.DatabaseHelper;
import org.neteinstein.pickaname.details.NameDetailsActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class NamesAllowedActivity extends Activity {

	private PANApp app = null;
	private ListView listView;
	private DatabaseAdapter database = null;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		// Register the BUS
		PANApp.getInstance().getEventBus().register(this);

		setContentView(R.layout.screen_names_allowed);

		showLoading();
		this.listView = ((ListView) findViewById(R.id.names_allowed_listview));
		this.listView.setOnItemClickListener(onItemClickListener);
		this.app = ((PANApp) getApplication());
		this.database = this.app.getAdapter();

		EditText localEditText = (EditText) findViewById(R.id.names_filter);
		localEditText.addTextChangedListener(this.filterListener);

		checkDatabase();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Unregister the BUS
		PANApp.getInstance().getEventBus().unregister(this);
	}

	@Background
	private void checkDatabase() {
		if (database.isDatabaseReady) {
			populateScreen();
		}
	}

	// This runs on background
	public void onEventAsync(DatabaseResult result) {
		if (result.isSuccess()) {
			populateScreen();
		} else {
			// Show error message
		}
	}

	private void populateScreen() {
		Cursor cursor = getDatabaseCursor();
		fillListView(cursor);
	}

	private Cursor getDatabaseCursor() {
		DatabaseAdapter localDatabaseAdapter = NamesAllowedActivity.this.database;

		String[] arrayOfString = new String[] { DatabaseHelper.NAMES_ID,
				DatabaseHelper.NAMES_NAME, DatabaseHelper.NAMES_GENDER,
				DatabaseHelper.NAMES_ALLOWED, DatabaseHelper.NAMES_GENDER };

		Cursor cursor = localDatabaseAdapter.fetchTable(
				DatabaseHelper.TABLE_NAMES, arrayOfString,
				DatabaseHelper.NAMES_ALLOWED + " = 1", DatabaseHelper.NAMES_ID
						+ " ASC");

		return cursor;
	}

	@UiThread
	private void fillListView(Cursor cursor) {
		if (cursor != null) {
			NamesAllowedAdapter localNamesAllowedAdapter = new NamesAllowedAdapter(
					NamesAllowedActivity.this.getApplicationContext(), cursor,
					true);
			listView.setAdapter(localNamesAllowedAdapter);
			hideLoading();
		}
	}

	private TextWatcher filterListener = new TextWatcher() {
		public void afterTextChanged(Editable editableText) {
			DatabaseAdapter localDatabaseAdapter = NamesAllowedActivity.this.database;
			String[] arrayOfString = new String[] { DatabaseHelper.NAMES_ID,
					DatabaseHelper.NAMES_NAME, DatabaseHelper.NAMES_GENDER,
					DatabaseHelper.NAMES_ALLOWED, DatabaseHelper.NAMES_GENDER };
			Cursor localCursor = localDatabaseAdapter.fetchTable(
					DatabaseHelper.TABLE_NAMES,
					arrayOfString,
					DatabaseHelper.NAMES_NAME + " like '%"
							+ editableText.toString() + "%'",
					DatabaseHelper.NAMES_ID + " ASC");

			if (localCursor != null) {
				NamesAllowedAdapter localNamesAllowedAdapter = new NamesAllowedAdapter(
						getApplicationContext(), localCursor, true);

				listView.setAdapter(localNamesAllowedAdapter);
			}
		}

		public void beforeTextChanged(CharSequence paramCharSequence,
				int paramInt1, int paramInt2, int paramInt3) {
		}

		public void onTextChanged(CharSequence paramCharSequence,
				int paramInt1, int paramInt2, int paramInt3) {
		}
	};

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> paramAdapterView,
				View paramView, int paramInt, long paramLong) {
			Intent localIntent = new Intent(NamesAllowedActivity.this,
					NameDetailsActivity.class);
			localIntent.putExtra(NameDetailsActivity.NAME_ID, paramLong);
			NamesAllowedActivity.this.startActivity(localIntent);
		}
	};

	private void hideLoading() {
		findViewById(R.id.loading).setVisibility(View.GONE);
	}

	private void showLoading() {
		findViewById(R.id.loading).setVisibility(View.VISIBLE);
	}

}