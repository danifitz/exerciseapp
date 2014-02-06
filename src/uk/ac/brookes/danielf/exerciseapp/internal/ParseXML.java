package uk.ac.brookes.danielf.exerciseapp.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class takes an XML document and parses it into a Run object
 */
public class ParseXML extends AsyncTask<String, Void, Run> {

	@Override
	protected Run doInBackground(String... xmlDoc) {
		// new Run object to hold the data we parse
		Run run = null;
		// new RunSegment object to add to the arrayList
		RunSegment runSegment = null;
		// new RunSegment arrayList to hold the segments we parse
		ArrayList<RunSegment> runSegments = null;

		try {
			// get a pull parser from the factory
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			// set input source
			InputStream is = new ByteArrayInputStream(xmlDoc[0].getBytes());
			parser.setInput(is, null);
			// get event type
			int eventType = parser.getEventType();
			// process while we don't reach the end of the document
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// at start: START_DOCUMENT
				case XmlPullParser.START_DOCUMENT:
					run = new Run();
					runSegments = new ArrayList<RunSegment>();
					break;

				// at start of a tag: START_TAG
				case XmlPullParser.START_TAG:
					// get name of tag
					String tagName = parser.getName();
					// if <user-id>
					if (tagName.equalsIgnoreCase(Run.USERID)) {
						run.setUserid(parser.nextText());
					}
					// if <date>
					else if (tagName.equalsIgnoreCase(Run.DATE)) {
						run.setDate(parser.nextText());
					}
					// if <name>
					else if (tagName.equalsIgnoreCase(Run.NAME)) {
						run.setName(parser.nextText());
					}
					// if <trkpt>, get attributes lat & lng
					else if (tagName.equalsIgnoreCase(Run.TRACKPOINT)) {
						LatLng latLng = new LatLng(Float.parseFloat(parser
								.getAttributeValue("", Run.LATITUDE)),
								Float.parseFloat(parser.getAttributeValue("",
										Run.LONGITUDE)));
						runSegment = new RunSegment();
						runSegment.setLatLng(latLng);
					}
					// if <ele>
					else if (tagName.equalsIgnoreCase(Run.ELEVATION)) {
						double elevation = Double
								.parseDouble(parser.nextText());
						runSegment.setElevation(elevation);
					}
					// if <time>
					else if (tagName.equalsIgnoreCase(Run.TIME)) {
						String time = parser.nextText();
						runSegment.setTime(time);
						// now all the fields of runSegment have been set
						// add it to the arrayList
						runSegments.add(runSegment);
					}
					break;
				}
				// next event type
				eventType = parser.next();
			}
			// exceptions - check for null on the other end
		} catch (XmlPullParserException e) {
			run = null;
		} catch (IOException e) {
			run = null;
		}
		// set the Run's segments to the ones we just parsed
		run.runSegments = runSegments;
		Log.d("ParseXML", run.name);
		return run;
	}
}
