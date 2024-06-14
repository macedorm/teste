package br.com.jjconsulting.mobile.jjlib.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Geocoding{

    private static final String TAG = "GeocodingLocation";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "latitude";


    public  static String getDistance(Location locationPointA, Location locationPointB){
        float distance = locationPointA.distanceTo(locationPointB)/1000;

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        String output = formatter.format(distance);

        return output;
    }

    public static void getAddressFromLocation(String locationAddress,
                                              Context context, Handler handler) {

        Thread thread = new Thread() {

            Address addressLocation = null;

            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    List address = geocoder.getFromLocationName(locationAddress, 1);

                    if (address != null && address.size() > 0) {
                        addressLocation = (Address) address.get(0);

                    }
                } catch (IOException e) {
                    LogUser.log(Config.TAG, e.getMessage());
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (addressLocation != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putDouble(TAG_LATITUDE, addressLocation.getLatitude());
                        bundle.putDouble(TAG_LONGITUDE, addressLocation.getLongitude());

                        message.setData(bundle);
                    } else {
                        message.what = 2;
                        Bundle bundle = new Bundle();
                        message.setData(bundle);
                    }

                    message.sendToTarget();
                }
            }
        };

        thread.start();
    }
}
