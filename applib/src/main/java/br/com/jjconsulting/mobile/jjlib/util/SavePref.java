package br.com.jjconsulting.mobile.jjlib.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SavePref {

	/**
	 * Loading String in SharedPreferences
	 * @param prefKey
	 * @param mContext
	 * @param key
	 * @return
	 */
	public String getPref(String key, String prefKey, Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences(prefKey, Context.MODE_PRIVATE);

		return prefs.getString(key, null);
	}

	public boolean getBoolPref(String key, String prefKey, Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences(prefKey, Context.MODE_PRIVATE);

		return prefs.getBoolean(key, false);
	}

	/**
	 * Delete Prefence
	 * @param prefKey
	 * @param context
	 * @param key
	 */
	public void deleteSharedPreferences(String key, String prefKey, Context context){
		SharedPreferences preferences = context.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
		preferences.edit().remove(key).commit();
	}


	/**
	 * Save String in SharedPreferences
	 * @param key
	 * @param info
	 * @param prefKey
	 * @param mContext
	 * @return
	 */
	public boolean saveSharedPreferences(String key, String prefKey, String info, Context mContext) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, info);

		return editor.commit();
	}

	public boolean saveBoolSharedPreferences(String key, String prefKey, boolean info, Context mContext) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, info);

		return editor.commit();
	}

    public boolean saveDatabase(String key, String prefKey, String name, int version, Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key + "name", name);
        editor.putString(key + "version", String.valueOf(version));

        return editor.commit();
    }



}
