package uk.ac.brookes.danielf.exerciseapp.internal;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	private SharedPreferences settings;
	private SharedPreferences.Editor settingsEditor;

	public Settings(Context context, String username, boolean autoUpload) {
		/*
		 * Get a preferences object to store the settings chosen by the user in
		 * this activity
		 */
		settings = context.getSharedPreferences("settings",
				Context.MODE_PRIVATE);
		settingsEditor = settings.edit();
		setUserid(username);
		setAutoUpload(autoUpload);
		settingsEditor.commit();
	}

	public Settings(Context context) {
		/*
		 * Get a preferences object to store the settings chosen by the user in
		 * this activity
		 */
		settings = context.getSharedPreferences("settings",
				Context.MODE_PRIVATE);
		settingsEditor = settings.edit();
	}

	public String getUserid() {
		String user = settings.getString("username", "");
		return user;
	}

	public void setUserid(String userid) {
		settingsEditor.putString("username", userid);
		settingsEditor.commit();
	}

	public boolean isAutoUpload() {
		boolean auto = settings.getBoolean("autoupload", false);
		return auto;
	}

	public void setAutoUpload(boolean autoUpload) {
		settingsEditor.putBoolean("autoupload", autoUpload);
		settingsEditor.commit();
	}

}
