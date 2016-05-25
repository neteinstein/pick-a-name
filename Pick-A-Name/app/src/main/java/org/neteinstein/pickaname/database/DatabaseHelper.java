package org.neteinstein.pickaname.database;

import org.neteinstein.pickaname.models.NameModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAMES = "TABLE_NAMES";
	public static final String NAMES_ID = "_id";
	public static final String NAMES_NAME = "NAMES_NAME";
	public static final String NAMES_GENDER = "NAMES_GENDER";
	public static final String NAMES_ALLOWED = "NAMES_ALLOWED";
	public static final String NAMES_NOTES = "NAMES_NOTES";

	private static final String CREATE_TABLE_NAME = "CREATE TABLE "
			+ TABLE_NAMES + " (" + NAMES_ID + " INTEGER PRIMARY KEY, "
			+ NAMES_NAME + " TEXT, " + NAMES_GENDER + " TEXT, " + NAMES_ALLOWED
			+ " INTEGER, " + NAMES_NOTES + " TEXT );";
	private static final String DROP_TABLE_NAMES = "DROP TABLE IF EXISTS NAME";

	private static final String[] CREATE = new String[] { CREATE_TABLE_NAME };
	// private static final String[] CLEAR;
	private static final String[] DROP = new String[] { DROP_TABLE_NAMES };

	private Context context = null;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		String[] arrayOfString = CREATE;

		for (int i = 0; i < arrayOfString.length; i++) {
			paramSQLiteDatabase.execSQL(arrayOfString[i]);
		}

		Log.i(DatabaseHelper.class.getSimpleName(), "Database Created.");

	}

	public void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
			int paramInt2) {
		Log.i(DatabaseHelper.class.getSimpleName(),
				"WARNING: Database Drop. ALL data will be LOST.");
		resetDatabase(paramSQLiteDatabase);
		onCreate(paramSQLiteDatabase);
	}

	public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
			int paramInt2) {
		Log.i(DatabaseHelper.class.getSimpleName(),
				"WARNING: Database Drop. ALL data will be LOST.");
		resetDatabase(paramSQLiteDatabase);
		onCreate(paramSQLiteDatabase);
	}

	public NameModel fetchName(SQLiteDatabase paramSQLiteDatabase,
			long paramLong) {
		String[] arrayOfString = new String[] { NAMES_ID, NAMES_NAME,
				NAMES_GENDER, NAMES_ALLOWED, NAMES_NOTES };

		Cursor localCursor = paramSQLiteDatabase.query(TABLE_NAMES,
				arrayOfString, NAMES_ID + " == " + paramLong, null, null, null,
				null);

		NameModel localNameModel = null;

		if (localCursor.moveToFirst()) {

			localNameModel = new NameModel();
			localNameModel.setName(localCursor.getString(localCursor
					.getColumnIndex(NAMES_NAME)));
			localNameModel.setGender(localCursor.getString(localCursor
					.getColumnIndex(NAMES_GENDER)));
			localNameModel.setNote(localCursor.getString(localCursor
					.getColumnIndex(NAMES_NOTES)));
		}
		localCursor.close();
		return localNameModel;
	}

	public boolean isDatabaseEmpty(SQLiteDatabase paramSQLiteDatabase) {

		boolean databaseEmpty = true;

		try {
			int numberOfNames = paramSQLiteDatabase.rawQuery(
					"SELECT " + NAMES_ID + " FROM " + TABLE_NAMES, null)
					.getCount();
			if (numberOfNames > 0) {
				databaseEmpty = false;
			}
		} catch (Exception e) {
			Log.e(DatabaseHelper.class.getSimpleName().toString(),
					"isDatabaseEmpty", e);

		}

		return databaseEmpty;
	}

	public void resetDatabase(SQLiteDatabase paramSQLiteDatabase) {
		String[] arrayOfString = DROP;

		for (int i = 0; i < arrayOfString.length; i++) {
			paramSQLiteDatabase.execSQL(arrayOfString[i]);
		}

	}

	// public void clearAllTables(SQLiteDatabase paramSQLiteDatabase) {
	// String[] arrayOfString = CLEAR;
	// int i = arrayOfString.length;
	// for (int j = 0;; j++) {
	// if (j >= i)
	// return;
	// paramSQLiteDatabase.execSQL(arrayOfString[j]);
	// }
	// }
}