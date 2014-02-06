package uk.ac.brookes.danielf.exerciseapp.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class takes a Run object and produces an XML document from it as a
 * String
 * 
 * @author danfitzgerald
 * 
 */
public class BuildXML extends AsyncTask<Run, Void, String> {

	@Override
	protected String doInBackground(Run... params) {

		StringWriter writer = null;
		// start DOC
		try {
			XmlSerializer xml = Xml.newSerializer();
			writer = new StringWriter();
			xml.setOutput(writer);

			xml.startDocument("UTF-8", null);

			// open tag: <brookesml>
			xml.startTag("", Run.BROOKES);

			// open tag: <user-id>
			xml.startTag("", Run.USERID);
			xml.text(params[0].userid);
			// close tag: </user-id>
			xml.endTag("", Run.USERID);

			// open tag: <date>
			xml.startTag("", Run.DATE);
			xml.text(params[0].date.toString());
			// close tag: </date>
			xml.endTag("", Run.DATE);

			// open tag: <trk>
			xml.startTag("", Run.TRACK);

			// open tag: <name>
			xml.startTag("", Run.NAME);
			xml.text(params[0].name);
			// close tag: </name>
			xml.endTag("", Run.NAME);

			// open tag: <trkseg>
			xml.startTag("", Run.TRACKSEGMENT);

			/*
			 * we'll put in as many <trkpt> elements as there are entries in the
			 * coords structure i.e as many readings as we took from the run
			 */
			ArrayList<RunSegment> runSegments = params[0].runSegments;
			for (RunSegment runSegment : runSegments) {
				// get the LatLng object from the map using the time from the
				// keySet
				LatLng latLng = runSegment.getLatLng();
				xml.startTag("", Run.TRACKPOINT);
				xml.attribute("", Run.LATITUDE, String.valueOf(latLng.latitude));
				xml.attribute("", Run.LONGITUDE,
						String.valueOf(latLng.longitude));

				// start tag: <ele>
				xml.startTag("", Run.ELEVATION);
				xml.text(String.valueOf(runSegment.getElevation()));
				// close tag: </ele>
				xml.endTag("", Run.ELEVATION);

				// start tag: <time>
				xml.startTag("", Run.TIME);
				xml.text(String.valueOf(runSegment.getTime()));
				// close tag: </time>
				xml.endTag("", Run.TIME);

				// close tag: </trkpt>
				xml.endTag("", Run.TRACKPOINT);
			}

			// close tag: </trkseg>
			xml.endTag("", Run.TRACKSEGMENT);
			// close tag: </trk>
			xml.endTag("", Run.TRACK);
			// close tag: </brookesml>
			xml.endTag("", Run.BROOKES);

			// end DOCUMENT
			xml.endDocument();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("XML document is ", writer.toString().replaceAll("\n", " "));
		// this regex should replace newlines no matter what platform we are on.
		return writer.toString().replaceAll("\n", " ");
	}
}
