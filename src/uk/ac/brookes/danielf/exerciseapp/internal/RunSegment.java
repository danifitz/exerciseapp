package uk.ac.brookes.danielf.exerciseapp.internal;

import com.google.android.gms.maps.model.LatLng;

public class RunSegment {

	public RunSegment(String time, LatLng latLng, double elevation) {
		this.time = time;
		this.latLng = latLng;
		this.elevation = elevation;
	}

	/**
	 * Constructor for when time, latlng and elevation aren't immediately known
	 */
	public RunSegment() {
	};

	public String time;
	public LatLng latLng;
	public double elevation;

	public String getTime() {
		return time;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public double getElevation() {
		return elevation;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
}
