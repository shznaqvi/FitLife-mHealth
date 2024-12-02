package edu.aku.hassannaqvi.fitlife.core;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.core.app.ActivityCompat;import edu.aku.hassannaqvi.fitlife.core.MainApp;

public class GPSLocationListener implements LocationListener {

    public static long TWO_MINUTES = 1000 * 60 * 2;
    private final MainApp mainApp;


    public GPSLocationListener(MainApp mainApp) {
        this.mainApp = mainApp;


    }

    public void onLocationChanged(Location location) {

        SharedPreferences sharedPref = mainApp.getSharedPreferences("GPSCoordinates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String dt = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(sharedPref.getString("Time", "0"))).toString();

        Location bestLocation = new Location("storedProvider");
        bestLocation.setAccuracy(Float.parseFloat(sharedPref.getString("Accuracy", "0")));
        bestLocation.setTime(Long.parseLong(sharedPref.getString(dt, "0")));
        bestLocation.setLatitude(Float.parseFloat(sharedPref.getString("Latitude", "0")));
        bestLocation.setLongitude(Float.parseFloat(sharedPref.getString("Longitude", "0")));

        if (isBetterLocation(location, bestLocation)) {
            editor.putString("Longitude", String.valueOf(location.getLongitude()));
            editor.putString("Latitude", String.valueOf(location.getLatitude()));
            editor.putString("Accuracy", String.valueOf(location.getAccuracy()));
            editor.putString("Time", String.valueOf(location.getTime()));
            String date = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(String.valueOf(location.getTime()))).toString();
           /* Toast.makeText(mainApp,
                    "GPS Commit! LAT: " + location.getLongitude() +
                            " LNG: " + location.getLatitude() +
                            " Accuracy: " + location.getAccuracy() +
                            " Time: " + date +
                            "Provider: " + location.getProvider(),
                    Toast.LENGTH_SHORT).show();*/

            editor.apply();
        }
    }


    protected void showCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(mainApp, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainApp, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = MainApp.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            //Toast.makeText(getApplicationContext(), message,
            //Toast.LENGTH_SHORT).show();
        }

    }

    public void onStatusChanged(String s, int i, Bundle b) {
        showCurrentLocation();
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;

            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


}
