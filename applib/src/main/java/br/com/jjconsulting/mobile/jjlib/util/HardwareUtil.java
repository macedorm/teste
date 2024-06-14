package br.com.jjconsulting.mobile.jjlib.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

public class HardwareUtil {

    public static String getDeviceIMEI(Context context) {
        String deviceUniqueIdentifier = "";

        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT < 29 && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                deviceUniqueIdentifier = telephonyManager.getDeviceId();
            } else {
                deviceUniqueIdentifier =  Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }



        return deviceUniqueIdentifier;
    }

    public static boolean isStoreVersion(Context context) {
        try {
            String installer = context.getPackageManager().getInstallerPackageName(
                    context.getPackageName());
            
            if (installer != null) {
                LogUser.log( "isStoreVersion: " + installer);
            }

            return installer != null && installer.contains("com.android.vending");
        } catch (Throwable e) {
            LogUser.log(Config.TAG, e.toString());
        }

        return false;
    }
}
