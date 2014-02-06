package uk.ac.brookes.danielf.exerciseapp.activities;

import uk.ac.brookes.danielf.exerciseapp.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

/**
 * This class should display a summary of a users run including time taken,
 * distance travelled, average speed and maximum speed.
 * 
 * @author danfitzgerald
 * 
 */
public class SummarizeActivity extends Activity {

	TextView runDistanceText;
	TextView runLengthText;
	TextView avgSpeedText;
	TextView maxSpeedText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summarize);

		// get the text views
		runDistanceText = (TextView) findViewById(R.id.distance);
		runLengthText = (TextView) findViewById(R.id.runlength);
		avgSpeedText = (TextView) findViewById(R.id.averagespeed);
		maxSpeedText = (TextView) findViewById(R.id.maximumspeed);

		// get the info from the intent used to start this activity
		Intent myIntent = getIntent();
		String[] runDetails = myIntent.getStringArrayExtra("runDetails");
		String distanceTravelled = runDetails[0];
		String avgSpeed = runDetails[1];
		String maxSpeed = runDetails[2];
		String runLength = runDetails[3];

		// set the values of all the text views
		runDistanceText.setText(distanceTravelled);
		runLengthText.setText(runLength);
		avgSpeedText.setText(avgSpeed);
		maxSpeedText.setText(maxSpeed);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summarize, menu);
		return true;
	}

}
