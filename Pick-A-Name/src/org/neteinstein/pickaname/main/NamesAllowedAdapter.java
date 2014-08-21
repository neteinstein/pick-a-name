package org.neteinstein.pickaname.main;

import org.neteinstein.pickaname.R;
import org.neteinstein.pickaname.database.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NamesAllowedAdapter extends CursorAdapter {
	private LayoutInflater mLayoutInflater = null;

	public NamesAllowedAdapter(Context context, Cursor cursor,
			boolean autoRequery) {
		super(context, cursor, autoRequery);

		mLayoutInflater = LayoutInflater.from(this.mContext);
	}

	public void bindView(View paramView, Context paramContext,
			Cursor paramCursor) {
		String name = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow(DatabaseHelper.NAMES_NAME));
		TextView nameTV = (TextView) paramView
				.findViewById(R.id.names_allowed_name);
		if (nameTV != null) {
			nameTV.setText(name);
		}

		String gender = paramCursor.getString(paramCursor
				.getColumnIndexOrThrow(DatabaseHelper.NAMES_GENDER));
		TextView genderTV = (TextView) paramView
				.findViewById(R.id.names_allowed_gender);
		if (genderTV != null) {
			genderTV.setText(gender);
		}
	}

	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = this.mLayoutInflater.inflate(
				R.layout.list_line_names_allowed, parent, false);
		return view;
	}
}
