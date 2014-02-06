package uk.ac.brookes.danielf.exerciseapp.activities;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import uk.ac.brookes.danielf.exerciseapp.R;
import uk.ac.brookes.danielf.exerciseapp.internal.BuildXML;
import uk.ac.brookes.danielf.exerciseapp.internal.Run;
import uk.ac.brookes.danielf.exerciseapp.internal.RunSegment;
import uk.ac.brookes.danielf.exerciseapp.internal.Server;
import uk.ac.brookes.danielf.exerciseapp.internal.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class ExerciseActivity extends Activity {

	Button startStop;
	Button submit;

	private GoogleMap map;
	PolylineOptions po;

	LocationListener locListener;

	boolean running = false;

	Run run;

	private String runLength;
	private int distanceTravelled;
	private String avgSpeed;
	private String maxSpeed;

	private final TimeZone tz = TimeZone.getTimeZone("GMT");
	// "YYYY-MM-DDThh:mm:ss"
	private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise);

		// set the time zone
		df.setTimeZone(tz);

		startStop = (Button) findViewById(R.id.startbutton);
		startStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*
				 * Let's check if we have an internet connection
				 */
				ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo network = cm.getActiveNetworkInfo();
				boolean isConnected = network != null && network.isConnected();

				if (isConnected) {
					/*
					 * if we aren't running then let's start otherwise let's
					 * stop
					 */
					if (!running) {
						// start running
						running = true;
						beginRunning();
						startStop.setText("Stop Running");
					} else {
						// stop running
						running = false;
						stopRunning();
						startStop.setText("Start Running");
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Not connected to the outside world!",
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		submit = (Button) findViewById(R.id.submitbutton);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!running && run != null) {
					/*
					 * The Run object has all it needs to be transformed into an
					 * XML document ready to be submitted to the server
					 */
					String xmlDoc = "";
					try {
						xmlDoc = new BuildXML().execute(run).get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					Log.d("ExerciseActivity:submit()", xmlDoc);
					submitRoute(xmlDoc);
					showSummary(String.valueOf(distanceTravelled),
							String.valueOf(avgSpeed), String.valueOf(maxSpeed),
							runLength);
				} else if (running) {
					Toast.makeText(getApplicationContext(),
							"Cannot submit while running", Toast.LENGTH_SHORT)
							.show();
				} else if (run == null) {
					Toast.makeText(
							getApplicationContext(),
							"No run data recorded! Press start running before submitting",
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// register location listener
		locListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				// create a new LatLng object from location update
				LatLng currentLatLng = new LatLng(location.getLatitude(),
						location.getLongitude());

				// move camera to reflect new location on Google maps
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
						15));

				/*
				 * We only want to start recording the co-ords if the user has
				 * pressed 'start run'.
				 */
				if (running) {
					// plot to GoogleMap
					mapRoute(currentLatLng);

					// Get the valuable information from the current location
					String time = df.format(new Date());
					LatLng latLng = new LatLng(location.getLatitude(),
							location.getLongitude());
					double elevation = location.getAltitude();

					// record
					run.addSegment(time, latLng, elevation);
					run.setMaxSpeed(location.getSpeed());
				}
			}

			@Override
			public void onProviderDisabled(String arg0) {
				// what if user turns off GPS
				Toast.makeText(getApplicationContext(),
						"Cannot record location with GPS switched off!",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProviderEnabled(String arg0) {
				// pretty sure we can ignore this one
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// what does this one even do?!
			}

		};

		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				6 / 1000, 0, locListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exercise, menu);
		return true;
	}

	/**
	 * Creates a new run object and begins a request for updates from the
	 * location service
	 */
	private void beginRunning() {
		run = new Run(getApplicationContext(), "haha");
		// create a new PolylineOptions object to start tracing the route on the
		// map
		po = new PolylineOptions();
	}

	/**
	 * Adds more info to the run object once the run is finished and we can work
	 * things out like avgspeed.
	 */
	private void stopRunning() {
		Log.d("ExerciseActivity:stopRunning()", "stopped running");
		try {

			RunSegment[] rSegments = new RunSegment[run.runSegments.size()];
			for (int i = 0; i < rSegments.length; i++) {
				rSegments[i] = run.runSegments.get(i);
				Log.d("ExerciseActivity:stopRunning(", "getting run segment: "
						+ i);
			}

			/*
			 * Set the date of the run
			 */
			Log.d("ExerciseActivity - stopRunning()", rSegments[0].getTime());
			int year = Integer.parseInt(rSegments[0].getTime().substring(0, 4));
			int month = Integer
					.parseInt(rSegments[0].getTime().substring(5, 7));
			int day = Integer.parseInt(rSegments[0].getTime().substring(8, 10));
			String date = String.valueOf(year) + "-" + String.valueOf(month)
					+ "-" + String.valueOf(day);
			Log.d("ExerciseActivity:stopRunning()", "date of run is: " + date);
			run.setDate(date);

			/*
			 * Calculate the length of the run
			 */
			Date begin = null;
			Date end = null;
			try {
				// yyyy-MM-dd'T'HH:mm:ss
				begin = new SimpleDateFormat("hh:mm:ss").parse(rSegments[0]
						.getTime().substring(11, 19));
				end = new SimpleDateFormat("hh:mm:ss")
						.parse(rSegments[rSegments.length - 1].getTime()
								.substring(11, 19));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(begin);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(end);

			Date x = calendar1.getTime();
			Date y = calendar2.getTime();
			long diff = y.getTime() - x.getTime();
			double diffMinutes = diff / (60 * 1000);
			runLength = String.valueOf(diffMinutes);
			Log.d("ExerciseActivity:stopRunning()", "runlength is " + runLength);
			run.setRunLength(runLength);

			distanceTravelled = run.getDistanceTravelled(
					rSegments[0].getLatLng(),
					rSegments[rSegments.length - 1].getLatLng());
			DecimalFormat df = new DecimalFormat("00.00");
			avgSpeed = df.format(distanceTravelled / (diffMinutes * 60));
			maxSpeed = df.format(run.getMaxSpeed());

			/*
			 * if the user has checked autoupload in settings then upload the
			 * run once it's finished
			 */
			Settings settings = new Settings(this);
			Log.d("ExerciseActivity:stopRunning()",
					"autoupload is " + String.valueOf(settings.isAutoUpload()));
			if (settings.isAutoUpload()) {
				/*
				 * The Run object has all it needs to be transformed into an XML
				 * document ready to be submitted to the server
				 */
				String xmlDoc = "";
				try {
					xmlDoc = new BuildXML().execute(run).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				Log.d("ExerciseActivity:stopRunning()", xmlDoc);
				submitRoute(xmlDoc);

				showSummary(String.valueOf(distanceTravelled), avgSpeed,
						maxSpeed, runLength);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.d("ExerciseActivity:stopRunning()",
					"user has stopped running without recording any locations");
		}
		map.clear();
	}

	/**
	 * Method to map incoming location updates to a google map view
	 */
	private void mapRoute(LatLng latlng) {
		po.add(latlng);
		map.addPolyline(po);
		Log.d("ExerciseActivity:mapRoute()", "mapped lat: " + latlng.latitude
				+ "mapped lng: " + latlng.longitude);
	}

	@SuppressWarnings("unchecked")
	private void submitRoute(String xmlDoc) {
		Pair<String, String> params = Pair.create(Server.STORE, xmlDoc);
		new Server().execute(params);
	}

	/**
	 * Starts a new activity that summarizes the users run
	 * 
	 * @param distanceTravelled
	 * @param avgSpeed
	 * @param maxSpeed
	 */
	private void showSummary(String distanceTravelled, String avgSpeed,
			String maxSpeed, String runLength) {
		Intent myIntent = new Intent(this, SummarizeActivity.class);
		String[] runDetails = { distanceTravelled, avgSpeed, maxSpeed,
				runLength };
		myIntent.putExtra("runDetails", runDetails);
		this.startActivity(myIntent);
	}
}
