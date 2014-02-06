package uk.ac.brookes.danielf.exerciseapp.activities;

import uk.ac.brookes.danielf.exerciseapp.R;
import uk.ac.brookes.danielf.exerciseapp.internal.Settings;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {

	ToggleButton toggleButton;
	EditText editUsername;
	Button saveButton;

	boolean autoupload = false;
	String username;
	boolean userSet = false;
	boolean uploadSet = false;

	private Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		settings = new Settings(this);

		/*
		 * Get the two widgets and set listeners for them
		 */
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
		toggleButton.setTextOn("On");
		toggleButton.setTextOff("Off");
		toggleButton.setChecked(settings.isAutoUpload());
		toggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String state = toggleButton.getText().toString();
				if (state.equalsIgnoreCase("on")) {
					autoupload = true;
				} else {
					autoupload = false;
				}
				uploadSet = true;
			}

		});
		editUsername = (EditText) findViewById(R.id.editText1);
		if (!settings.getUserid().isEmpty()) {
			editUsername.setText(settings.getUserid());
		}
		editUsername.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable editableText) {
				/*
				 * get the username as a char array then convert to a string
				 * then add it to the preferences
				 */
				username = editableText.toString();
				userSet = true;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// do nothing
			}

			@Override
			public void onTextChanged(CharSequence username, int start,
					int before, int count) {
				// do nothing
			}

		});

		saveButton = (Button) findViewById(R.id.button1);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveSettings();
			}
		});
	}

	private void saveSettings() {
		if (userSet && uploadSet) {
			settings = new Settings(this, username, autoupload);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
