package uk.ac.brookes.danielf.exerciseapp.internal;

import java.util.ArrayList;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class Run {

	/*
	 * Constants used for the XML tags in use
	 */
	public static final String BROOKES = "brookesml";
	public static final String USERID = "user-id";
	public static final String DATE = "date";
	public static final String TRACK = "trk";
	public static final String NAME = "name";
	public static final String TRACKSEGMENT = "trkseg";
	public static final String TRACKPOINT = "trkpt";
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "lon";
	public static final String ELEVATION = "ele";
	public static final String TIME = "time";

	Settings settings;

	public double maxSpeed; // in meters a second
	public String runLength; // "MM:ss"
	public double avgSpeed; // in meters a second
	public String name;
	public String date; // in ISO 8601 format
	public String userid; // the user id specified in settings

	public ArrayList<RunSegment> runSegments;

	// constructor when we have a name
	public Run(Context context, String name) {
		this.name = name;
		settings = new Settings(context);
		userid = settings.getUserid();
		runSegments = new ArrayList<RunSegment>();
	}

	/**
	 * Constructor for when we are getting a Run from the server
	 */
	public Run() {
	}

	public double getMaxSpeed() {

		return maxSpeed;
	}

	/**
	 * Sets maxSpeed, however it will only update if the new value is greater
	 */
	public void setMaxSpeed(float maxSpeed) {
		/*
		 * 0.0f is the default value for a declared float i.e. if this the first
		 * time we are setting a value for maxSpeed
		 */
		if (this.maxSpeed == 0.0f) {
			this.maxSpeed = maxSpeed;
		} else if (this.maxSpeed < maxSpeed) {
			this.maxSpeed = maxSpeed;
		}
	}

	public String getRunLength() {
		return runLength;
	}

	public void setRunLength(String runLength) {
		this.runLength = runLength;
	}

	public double getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(float avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void addSegment(String time, LatLng latLng, double elevation) {
		RunSegment segment = new RunSegment(time, latLng, elevation);
		runSegments.add(segment);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * Uses the Haversine formula to calculate the great-circle distance between
	 * the beginning and end point LatLng of the run.
	 */
	public Integer getDistanceTravelled(LatLng startPoint, LatLng endPoint) {
		int R = 6371; // earths radius in KM
		Double dLat = Math.toRadians((endPoint.latitude - startPoint.latitude));
		Double dLon = Math
				.toRadians((endPoint.longitude - startPoint.longitude));
		Double lat1 = Math.toRadians(startPoint.latitude);
		Double lat2 = Math.toRadians(endPoint.latitude);

		Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		Double distance = R * c;
		distance = distance * 1000; // * 1000 to convert to Meters
		Integer dist = distance.intValue();
		return dist;
	}
}
