package org.neteinstein.pickaname.main;

import org.neteinstein.pickaname.R;
import org.neteinstein.pickaname.database.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class NamesAllowedAdapter extends CursorAdapter implements
		SectionIndexer {

	private AlphabetIndexer mAlphabetIndexer;
	private Context mContext = null;
	private LayoutInflater mLayoutInflater = null;

	public NamesAllowedAdapter(Context context, Cursor cursor,
			boolean autoRequery) {
		super(context, cursor, autoRequery);

		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(this.mContext);

		this.mAlphabetIndexer = new AlphabetIndexer(cursor,
				cursor.getColumnIndex(DatabaseHelper.NAMES_NAME),
				" ABCDEFGHIJKLMNOPQRTSUVWXYZ");
		this.mAlphabetIndexer.setCursor(cursor);
	}

	public void bindView(View paramView, Context paramContext,
			Cursor paramCursor) {

		TextView nameTV = (TextView) paramView
				.findViewById(R.id.names_allowed_name);
		if (nameTV != null) {
			String name = paramCursor.getString(paramCursor
					.getColumnIndexOrThrow(DatabaseHelper.NAMES_NAME));

			nameTV.setText(name);
		}

		TextView genderTV = (TextView) paramView
				.findViewById(R.id.names_allowed_gender);
		if (genderTV != null) {
			String gender = paramCursor.getString(paramCursor
					.getColumnIndexOrThrow(DatabaseHelper.NAMES_GENDER));

			if ("M".equalsIgnoreCase(gender)) {
				genderTV.setText(mContext
						.getString(R.string.names_allowed_male));
			} else if ("F".equalsIgnoreCase(gender)) {
				genderTV.setText(mContext
						.getString(R.string.names_allowed_female));
			} else {
				genderTV.setText(gender);
			}

		}
	}

	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = this.mLayoutInflater.inflate(
				R.layout.list_line_names_allowed, parent, false);
		return view;
	}

	@Override
	public Object[] getSections() {
		return mAlphabetIndexer.getSections();
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		return mAlphabetIndexer.getPositionForSection(sectionIndex);
	}

	@Override
	public int getSectionForPosition(int position) {
		return mAlphabetIndexer.getSectionForPosition(position);
	}
}
