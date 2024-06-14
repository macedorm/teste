package br.com.jjconsulting.mobile.jjlib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

public class NetworkUtils {

    private NetworkUtils() {

    }

    /**
     * Checks if the network is available (it's necessary to request ACCESS_NETWORK_STATE permission).
     * <br>
     * Note that having an active network interface doesn't guarantee that a particular networked
     * service is available or that the internet is actually connected.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Checks if the device is actually connected to the internet.
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean isOnline() throws IOException, InterruptedException {
        // We ping the Google DNS servers to check for the expected exit
        // value and see if it' connected to internet.
        Runtime runtime = Runtime.getRuntime();
        Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
        int exitValue = ipProcess.waitFor();
        return exitValue == 0;
    }

    /**
     * Checks the connection type.
     * @return 0 - no connection; 1 - WiFi; 2 - mobile broadband.
     */
    public static int getNetworkType(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null) {
            return 0;
        }

        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return 1;
        }

        if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            return 2;
        }

        return -1;
    }
}
