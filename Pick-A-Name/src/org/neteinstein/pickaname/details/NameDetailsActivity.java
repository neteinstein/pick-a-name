package org.neteinstein.pickaname.details;

import org.neteinstein.pickaname.R;
import org.neteinstein.pickaname.app.PANApp;
import org.neteinstein.pickaname.models.NameModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NameDetailsActivity extends Activity {
	public static String NAME_ID = "NAME_ID";

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.screen_name_details);

		long nameId = -1;

		Intent localIntent = getIntent();
		if (localIntent != null) {
			nameId = localIntent.getLongExtra(NAME_ID, -1);
		}

		fillName(nameId);
	}

	private void fillName(long nameId) {

		NameModel localNameModel = PANApp.getInstance().getAdapter()
				.fetchName(nameId);

		if (localNameModel != null) {

			TextView nameTV = ((TextView) findViewById(R.id.name_details_name));
			nameTV.setText(localNameModel.getName());

			TextView genderTV = (TextView) findViewById(R.id.name_details_gender);
			String str = localNameModel.getGender();
			if ((str != null) && (!"".equalsIgnoreCase(str))) {
				genderTV.setText(str);
			}

			TextView notesTV = ((TextView) findViewById(R.id.name_details_notes));
			notesTV.setText(localNameModel.getNote());
		}
	}

}
