package edu.aku.hassannaqvi.fitlife.core;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SecureFileDownloader {

    private static final String TAG = "SecureDownloader";

    public static void downloadFile(Context context, String fileUrl, String fileName, DownloadCallback callback) {

        NotificationUtils.createChannel(context);

        new Thread(() -> {
            InputStream input = null;
            FileOutputStream output = null;
            HttpsURLConnection connection = null;


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            int notificationId = new Random().nextInt(10000);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
                    .setContentTitle("Downloading file")
                    .setContentText("Download in progress")
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true);


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(notificationId, builder.build());
            } else {
                Log.w("SecureFileDownloader", "Notification permission not granted. Skipping notification.");
            }

            try {
                // Load pinned certificate
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream caInput = context.getAssets().open("star_aku_edu_2025.crt");
                Certificate ca = cf.generateCertificate(caInput);
                caInput.close();

                // Create a KeyStore containing our trusted cert
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the cert in our KeyStore
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);

                // Create an SSLContext with our TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

                URL url = new URL(fileUrl);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(20000);
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("Server returned HTTP " + responseCode);
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();

                File outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File outputFile = new File(outputDir, fileName);

                output = new FileOutputStream(outputFile);

                byte[] buffer = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        builder.setProgress(100, progress, false)
                                .setContentText("Downloaded " + progress + "%");
                        notificationManager.notify(notificationId, builder.build());
                        callback.onProgress(progress);
                    }


                    output.write(buffer, 0, count);


                }

                output.flush();

                // Update notification to show download complete
                builder.setContentTitle("Download complete")
                        .setContentText(fileName)
                        .setProgress(0, 0, false)
                        .setOngoing(false)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .addAction(android.R.drawable.ic_menu_view, "Open File", PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW).setDataAndType(
                                        FileProvider.getUriForFile(context, context.getPackageName() + ".provider", outputFile),
                                        URLConnection.guessContentTypeFromName(fileName)
                                ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        ));
                notificationManager.notify(notificationId, builder.build());


                callback.onSuccess(outputFile);
                Log.i(TAG, "Download successful: " + outputFile.getAbsolutePath());

            } catch (Exception e) {
                Log.e(TAG, "Download failed: ", e);
                callback.onFailure(e);
            } finally {
                try {
                    if (input != null) input.close();
                    if (output != null) output.close();
                    if (connection != null) connection.disconnect();
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    public interface DownloadCallback {
        void onProgress(int progress);

        void onSuccess(File downloadedFile);

        void onFailure(Exception e);
    }

    public class NotificationUtils {
        public static final String CHANNEL_ID = "secure_download_channel";

        public static void createChannel(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Download Channel";
                String description = "Notifications for secure file downloads";
                int importance = NotificationManager.IMPORTANCE_LOW;

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);

                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
