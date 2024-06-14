package br.com.jjconsulting.mobile.jjlib.util;

import android.util.Log;

import br.com.jjconsulting.mobile.jjlib.BuildConfig;

public class LogUser {

    /**
     * Show log in logcat
     * @param TAG
     * @param message
     */
    public static void log(String TAG, String message) {
        createLog(TAG, message);
    }

    public static void log(String message) {
        createLog(Config.TAG, message);
    }

    private static void createLog(String TAG, String message){
       if (BuildConfig.DEBUG) {
            if(message != null){
                Log.i(TAG, message);
            }
       }
    }
}
