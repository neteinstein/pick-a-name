package org.neteinstein.pickaname.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.neteinstein.pickaname.app.PANApp;
import org.neteinstein.pickaname.bus.models.DatabaseResult;
import org.neteinstein.pickaname.config.AppConfiguration;
import org.neteinstein.pickaname.models.NameModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import de.greenrobot.event.EventBus;

public class DatabaseAdapter {

	public boolean isDatabaseReady = false;
	private final String DATABASE_NAME = AppConfiguration.SQLITE_DATABASE;
	private final int DATABASE_VERSION = 1;
	private Context context = null;
	private SQLiteDatabase database;
	private DatabaseHelper databaseHelper;

	public DatabaseAdapter(Context context) {
		this.context = context;

		if (this.databaseHelper == null) {
			this.databaseHelper = new DatabaseHelper(context, DATABASE_NAME,
					null, DATABASE_VERSION);
		}

		if (this.database == null) {
			this.database = open();
		}
	}

	public void validateDatabase() {
		if (isDatabaseEmpty()) {
			fillDatabase();
		} else {
			isDatabaseReady = true;
		}
	}

	private SQLiteDatabase open() throws SQLiteException {
		SQLiteDatabase localSQLiteDatabase = this.databaseHelper
				.getWritableDatabase();
		this.database = localSQLiteDatabase;
		return localSQLiteDatabase;
	}

	public void checkIfOpened() {
		if (!this.database.isOpen())
			open();
	}

	public void close() {
		this.database.close();
	}

	public boolean deleteRow(long paramLong, String paramString1,
			String paramString2) {

		return this.database.delete(paramString1, paramString2 + "="
				+ paramLong, null) > 0;
	}

	public NameModel fetchName(long paramLong) {
		return this.databaseHelper.fetchName(this.database, paramLong);
	}

	public Cursor fetchTable(String paramString1, String[] paramArrayOfString,
			String paramString2, String paramString3) {
		if ((this.database.isDbLockedByCurrentThread())
				|| (!this.database.isOpen()))
			Log.d(DatabaseAdapter.class.getSimpleName(),
					"Error on fetchTable: "
							+ this.database.isDbLockedByCurrentThread() + " "
							+ this.database.isOpen());
		return this.database.query(paramString1, paramArrayOfString,
				paramString2, null, null, null, paramString3);
	}

	public void fillDatabase() {

		try {
			InputStreamReader inputStream = new InputStreamReader(context
					.getAssets().open(AppConfiguration.ASSETS_DATABASE), "UTF-8");
			BufferedReader localBufferedReader = new BufferedReader(inputStream);

			String str = null;

			do {
				str = localBufferedReader.readLine();
				Log.d(DatabaseAdapter.class.getSimpleName().toString(),
						"SQL --->" + str);
				try {
					this.database.execSQL(str);
					str = localBufferedReader.readLine();
				} catch (Exception e) {
					Log.d(DatabaseAdapter.class.getSimpleName().toString(),
							"SQL --->" + e);
				}

			} while (str != null);
		} catch (IOException e) {
			Log.d(DatabaseAdapter.class.getSimpleName().toString(),
					AppConfiguration.ASSETS_DATABASE, e);
		}

		isDatabaseReady = true;

		PANApp app = PANApp.getInstance();
		EventBus bus = app.getEventBus();
		bus.post(new DatabaseResult(true));
	}

	public boolean isDatabaseEmpty() {
		return this.databaseHelper.isDatabaseEmpty(this.database);
	}

	public Cursor rawQuery(String paramString, String[] paramArrayOfString) {
		return this.database.rawQuery(paramString, paramArrayOfString);
	}
}