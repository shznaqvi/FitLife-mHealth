package edu.aku.hassannaqvi.fitlife.workers;

import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.buildSslSocketFactory;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;
import static edu.aku.hassannaqvi.fitlife.core.MainApp._PHOTO_UPLOAD_URL;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.sdDir;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.MainApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class PhotoUploadWorker2 extends Worker {

    private static final String TAG = "PhotoUploadWorker2";
    private final Context mContext;
    private final int photoId;
    private final String notificationTitle = PROJECT_NAME + ": Photo Upload";
    private final File fileToUpload;
    private boolean hasError = false;
    private Data outputData;
    private HttpsURLConnection urlConnection;

    public PhotoUploadWorker2(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        fileToUpload = new File(workerParams.getInputData().getString("filename"));
        photoId = new Random().nextInt();
        displayNotification(fileToUpload.toString(), "Starting...", 100, 0);
    }

    @NonNull
    @Override
    public Result doWork() {
        String response;
        try {
            response = uploadPhoto(String.valueOf(new File(sdDir + File.separator + fileToUpload.getName())));
        } catch (Exception e) {
            Log.d(TAG, "doWork: " + e.getMessage());
            displayNotification(fileToUpload.toString(), "Error: " + e.getMessage(), 100, 0);

            outputData = new Data.Builder().putString("error", "1 " + e.getMessage()).build();
            return Result.failure(outputData);
        } finally {
            if (hasError) {
                return Result.failure(outputData);
            }
        }

        handlePostUpload(response);
        return hasError ? Result.failure(outputData) : Result.success(outputData);
    }

    private void displayNotification(String title, String task, int maxProgress, int currentProgress) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("photo_upload_channel", notificationTitle, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "photo_upload_channel")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.drawable.app_icon);

        if (currentProgress >= maxProgress) {
            notification.setContentTitle("[DONE] " + title);
            notification.setTimeoutAfter(3500);
        }
        notification.setProgress(maxProgress, currentProgress, false);
        notificationManager.notify(photoId, notification.build());
    }

    private void moveFile(String fileName) {
        Log.d(TAG, "moveFile: " + fileName);
        File source = new File(MainApp.sdDir, fileName);
        File destinationDir = new File(MainApp.sdDir + File.separator + "uploaded");
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        File destination = new File(destinationDir, fileName);

        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(destination)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
            source.delete();
        } catch (IOException e) {
            Log.e(TAG, "moveFile: " + e.getMessage());
        }
    }

    private void handlePostUpload(String result) {
        displayNotification(fileToUpload.toString(), "Processing...", 100, 0);

        try {
            JSONObject jsonObject = new JSONObject(result);

            if ("1".equals(jsonObject.getString("status")) && "0".equals(jsonObject.getString("error"))) {
                displayNotification(fileToUpload.toString(), "Successfully Uploaded.", 100, 0);
                outputData = new Data.Builder().putString("message", fileToUpload.getName()).build();
                hasError = false;
                moveFile(fileToUpload.getName());

            } else if ("2".equals(jsonObject.getString("status")) && "0".equals(jsonObject.getString("error"))) {
                displayNotification(fileToUpload.toString(), "Duplicate file.", 100, 0);
                outputData = new Data.Builder().putString("message", "Duplicate: " + fileToUpload.getName()).build();
                hasError = false;
                moveFile(fileToUpload.getName());

            } else {
                displayNotification(fileToUpload.toString(), "Error: " + jsonObject.getString("message"), 100, 0);
                outputData = new Data.Builder().putString("error", jsonObject.getString("message")).build();
                hasError = true;
            }
        } catch (JSONException e) {
            Log.e(TAG, "handlePostUpload: " + e.getMessage());
            displayNotification(fileToUpload.toString(), "Error: " + result, 100, 0);
            outputData = new Data.Builder().putString("error", "2 " + result).build();
            hasError = true;
        }
    }

    private String uploadPhoto(String filepath) {
        displayNotification(fileToUpload.toString(), "Connecting...", 100, 0);

        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String boundary = "*****" + System.currentTimeMillis() + "*****";
        String lineEnd = "\r\n";

        File file = new File(filepath);
        FileInputStream fileInputStream = null;
        String fileName = file.getName();
        String filefield = "image";

        try {
            fileInputStream = new FileInputStream(file);

            URL url = new URL(_PHOTO_UPLOAD_URL);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(buildSslSocketFactory(mContext));
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Create JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("filename", fileName);
            jsonPayload.put("image", Base64.encodeToString(getFileBytes(file), Base64.NO_WRAP));

            outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(jsonPayload.toString());

            inputStream = urlConnection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (outputStream != null) outputStream.flush();
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
                if (urlConnection != null) urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getFileBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }


    private String encodeFileToBase64(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            return Base64.encodeToString(fileBytes, Base64.NO_WRAP);
        } catch (IOException e) {
            Log.e(TAG, "encodeFileToBase64: " + e.getMessage());
            return "";
        }
    }
}
