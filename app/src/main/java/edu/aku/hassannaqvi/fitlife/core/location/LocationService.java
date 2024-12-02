package edu.aku.hassannaqvi.fitlife.core.location;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import edu.aku.hassannaqvi.fitlife.R;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private static final long TWO_MINUTES = 2 * 60 * 1000;

    private LocationManagerlocal locationManagerlocal;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManagerlocal = LocationManagerlocal.getInstance(this);
        startForegroundService();
        locationManagerlocal.requestLocationUpdates();
    }

    private void startForegroundService() {
        // Create a notification for the foreground service
        Notification notification = createNotification();
        startForeground(12345678, notification); // Use a unique notification ID here
    }

    private Notification createNotification() {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("location_service_channel",
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "location_service_channel")
                .setContentTitle("Location Service")
                .setContentText("Running in background")
                .setSmallIcon(R.drawable.app_icon); // Replace with your app's icon

        return builder.build();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManagerlocal.removeLocationUpdates();
    }
}
