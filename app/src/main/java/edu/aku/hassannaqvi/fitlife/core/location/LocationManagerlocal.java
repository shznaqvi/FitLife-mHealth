package edu.aku.hassannaqvi.fitlife.core.location;

import android.content.Context;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;

import edu.aku.hassannaqvi.fitlife.core.MainApp;

public class LocationManagerlocal {

    private static final long TWO_MINUTES = 15 * 60 * 1000;
    private static final String TAG = "LocationManagerlocal";

    private static LocationManagerlocal instance;
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final List<LocationObserver> observers = new ArrayList<>();
    private LocationCallback locationCallback;
    private Location currentLocation;
    private GnssStatus.Callback gnssStatusCallback;


    private LocationManagerlocal(Context context) {
        this.context = context;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        setupLocationCallback();
    }

    public static synchronized LocationManagerlocal getInstance(Context context) {
        if (instance == null) {
            instance = new LocationManagerlocal(context);
        }
        return instance;
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocationIfBetter(location);
                }
            }
        };
    }

    private void updateLocationIfBetter(Location newLocation) {
        if (isBetterLocation(newLocation, getCurrentLocation())) {
            saveLocation(newLocation);
            notifyObservers(newLocation);
        }
    }

    private boolean isBetterLocation(Location newLocation, Location currentLocation) {
        if (currentLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), currentLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void saveLocation(Location location) {
        currentLocation = location;
        MainApp.editor.putString("latitude", String.valueOf(location.getLatitude()));
        MainApp.editor.putString("longitude", String.valueOf(location.getLongitude()));
        MainApp.editor.putFloat("accuracy", location.getAccuracy());
        MainApp.editor.putLong("datetime", location.getTime());
        MainApp.editor.putString("provider", location.getProvider());
        MainApp.editor.apply();
    }

    public Location getCurrentLocation() {
        if (currentLocation == null) {
            double latitude = Double.parseDouble(MainApp.sharedPref.getString("latitude", "0"));
            double longitude = Double.parseDouble(MainApp.sharedPref.getString("longitude", "0"));
            float accuracy = MainApp.sharedPref.getFloat("accuracy", 0);
            long datetime = MainApp.sharedPref.getLong("datetime", 0);
            String provider = MainApp.sharedPref.getString("provider", "");
            currentLocation = new Location(provider);
            currentLocation.setLatitude(latitude);
            currentLocation.setLongitude(longitude);
            currentLocation.setAccuracy(accuracy);
            currentLocation.setTime(datetime);
        }
        return currentLocation;
    }

    public void requestLocationUpdates() {
        if (!isLocationEnabled()) {
            Log.d(TAG, "Location services disabled. Satellite count set to 0.");
            saveSatelliteCount(0); // Save 0 satellites if location services are disabled
            return;
        }//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 1000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            registerGnssStatusCallback();

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        unregisterGnssStatusCallback();

    }


    private void notifyObservers(Location location) {
        for (LocationObserver observer : observers) {
            observer.onLocationChanged(location);
        }
    }

    private void registerGnssStatusCallback() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            gnssStatusCallback = new GnssStatus.Callback() {
                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    int satelliteCount = status.getSatelliteCount();
                    //   Log.d(TAG, "Satellites in view: " + satelliteCount);
                    // Handle satellite information as needed
                    saveSatelliteCount(satelliteCount);

                }
            };

            try {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null) {
                    locationManager.registerGnssStatusCallback(gnssStatusCallback);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "GNSS not supported on this device");
        }
    }

    private void unregisterGnssStatusCallback() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null && gnssStatusCallback != null) {
                    locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void saveSatelliteCount(int satelliteCount) {
        MainApp.editor.putInt("satelliteCount", satelliteCount);
        MainApp.editor.apply();
    }
}

