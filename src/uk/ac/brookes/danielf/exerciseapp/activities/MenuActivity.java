package uk.ac.brookes.danielf.exerciseapp.activities;

import uk.ac.brookes.danielf.exerciseapp.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MenuActivity extends Activity {

	ListView menuOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		menuOptions = (ListView) findViewById(R.id.listView1);
		menuOptions.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * Overridden method to handle a click on a listView item. Each
			 * option will open a new activity i.e this listView is the main
			 * menu.
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				switch (position) {
				// if the user selects the first item in the listView
				case 0:
					Intent exerciseIntent = new Intent(MenuActivity.this,
							ExerciseActivity.class);
					startAnActivity(exerciseIntent);
					break;
				// if the user selects the second item in the listView
				case 1:
					Intent viewIntent = new Intent(MenuActivity.this,
							SocialRuns.class);
					startAnActivity(viewIntent);
					break;
				// if the user selects the third item in the listView
				case 2:
					Intent settingsIntent = new Intent(MenuActivity.this,
							SettingsActivity.class);
					startAnActivity(settingsIntent);
					break;
				default:
					// do nothing
				}
			}

		});
	}

	private void startAnActivity(Intent myIntent) {
		MenuActivity.this.startActivity(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
