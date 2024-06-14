package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;


public class JJLocationView {

    private Activity mActivity;

    private String latlong;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private OnChangeLocation onChangeLocation;

    public JJLocationView(Activity activity){
        this.mActivity = activity;
    }

    public void getLocation(OnChangeLocation onChangeLocation){
        locationManager = (LocationManager)
                mActivity.getSystemService(Context.LOCATION_SERVICE);
        this.onChangeLocation = onChangeLocation;
        if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            locationListener = new JJLocationListener();

            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if(location != null){
                latlong = location.getLatitude() + " " + location.getLongitude();
                onChangeLocation.onChange();
            }

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        }
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String location) {
        this.latlong = location;
    }

    private class JJLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            try{
                latlong = loc.getLatitude() + " " + loc.getLongitude();
                locationManager.removeUpdates(locationListener);
                onChangeLocation.onChange();
            }catch (Exception ex){
                LogUser.log(Config.TAG, ex.toString());
            }

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public interface OnChangeLocation{
        void onChange();
    }
}