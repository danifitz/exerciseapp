package uk.ac.brookes.danielf.exerciseapp.activities;

import java.util.concurrent.ExecutionException;

import uk.ac.brookes.danielf.exerciseapp.R;
import uk.ac.brookes.danielf.exerciseapp.internal.ParseXML;
import uk.ac.brookes.danielf.exerciseapp.internal.Run;
import uk.ac.brookes.danielf.exerciseapp.internal.RunSegment;
import uk.ac.brookes.danielf.exerciseapp.internal.Server;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class SocialRuns extends Activity {

	private GoogleMap map;

	Button fetchButton;
	EditText usernameField;

	String username = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_runs);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		/*
		 * Username text field
		 */
		usernameField = (EditText) findViewById(R.id.username);
		usernameField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable editableText) {
				username = editableText.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

		});

		/*
		 * Fetch button
		 */
		fetchButton = (Button) findViewById(R.id.fetchbutton);
		fetchButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
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
					if (username == null) {
						Toast.makeText(getApplicationContext(),
								"Please enter a username", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(getApplicationContext(),
								"Searching for: " + username,
								Toast.LENGTH_SHORT).show();

						Pair<String, String> searchParams = Pair.create(
								Server.SEARCH, username);
						String idstring = null;
						try {
							idstring = new Server().execute(searchParams).get();
						} catch (InterruptedException e2) {
							e2.printStackTrace();
						} catch (ExecutionException e2) {
							e2.printStackTrace();
						}
						int[] routeids = null;
						if (idstring != null) {
							int id = 0;
							String[] ids = null;
							boolean isMultipleRuns = false;
							if (idstring.contains(",")) {
								ids = idstring.trim().split(",");
								routeids = new int[ids.length];
								for (int i = 0; i < ids.length; i++)
									routeids[i] = Integer.parseInt(ids[i]);
								Log.d("the number of routeids",
										String.valueOf(ids.length));
								isMultipleRuns = true;
							} else {
								id = Integer.valueOf(idstring.trim());
								routeids = new int[] { id };
							}

							Run[] runs = new Run[routeids.length];
							for (int i = 0; i < routeids.length; i++) {
								Log.d("fetching route ids: ",
										String.valueOf(routeids[i]));
								ParseXML parser = new ParseXML();
								Pair<String, String> retrieveParams = null;
								if (isMultipleRuns) {
									retrieveParams = Pair.create(
											Server.RETRIEVE,
											String.valueOf(routeids[i]).trim());
								} else {
									retrieveParams = Pair.create(
											Server.RETRIEVE, String.valueOf(id));
								}

								String xmlDoc = null;
								try {
									xmlDoc = new Server().execute(
											retrieveParams).get()
											+ ">";
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								} catch (ExecutionException e1) {
									e1.printStackTrace();
								}

								try {
									runs[i] = parser.execute(xmlDoc).get();
									Log.d("run name is: ", runs[i].name);
								} catch (InterruptedException e) {
									e.printStackTrace();
								} catch (ExecutionException e) {
									e.printStackTrace();
								}
							}
							plotRuns(runs);
						} else {
							Toast.makeText(
									getApplicationContext(),
									"Cannot find user, are you sure they exist?",
									Toast.LENGTH_SHORT).show();
							Log.d("SocialRuns: fetching routes",
									"username doesn't exist!");
						}
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"No internet connection!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.social_runs, menu);
		return true;
	}

	private void plotRuns(Run[] runs) {
		// move the camera to the location of the first segment
		LatLng currentLatLng = runs[0].runSegments.get(0).getLatLng();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
		for (int i = 0; i < runs.length; i++) {
			PolylineOptions plo = new PolylineOptions();
			// draw the polyLine points on the map
			for (RunSegment segment : runs[i].runSegments) {
				plo.add(segment.getLatLng());
				map.addPolyline(plo);
			}
		}
	}
}
