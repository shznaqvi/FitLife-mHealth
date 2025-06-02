package edu.aku.hassannaqvi.fitlife.workers;


import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.buildSslSocketFactory;
import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.certIsValid;
import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.decryptGCM;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts;
import edu.aku.hassannaqvi.fitlife.core.CipherSecure;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class DataUpPeriodicWorkerALL extends Worker {

    public static final String NOTIFICATION_CHANNEL = "DataUpPeriodicWorkerALL";
    private static final String TAG = "DataUpPeriodicWorkerALL";
    /**
     * The update hosts notification identifier.
     */
    private static final int NOTIFICATION_ID = 10;


    // to be initialised by workParams
    private final Context mContext;
    private final String uploadTable;
    private final URL serverURL = null;
    private final int position;
    private final String uploadWhere;
    private final DatabaseHelper db;
    HttpsURLConnection urlConnection;
    private JSONArray uploadData;
    private String nTitle = MainApp.PROJECT_NAME + ": Data Upload";
    private String nMessage;
    private ProgressDialog pd;
    private int length;
    private Data data;
    private long startTime;
    private int responseLength = 0, requestLength = 0;

    public DataUpPeriodicWorkerALL(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = MainApp.appInfo.getDbHelper();

        mContext = context;
        uploadTable = workerParams.getInputData().getString("table");
        position = workerParams.getInputData().getInt("position", -2);
        //uploadData = MainApp.uploadData.get(position);
        //uploadTables.clear();
        //MainApp.uploadDataPeriodic.clear();

        switch (uploadTable) {
            // Forms
            case TableContracts.TestsTable.TABLE_NAME:
                try {
                    uploadData = db.getUnsyncedTests();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ProcessStart: JSONException(Tests): " + e.getMessage());
                }
                break;


            // Entry Log
            case TableContracts.EntryLogTable.TABLE_NAME:
                try {
                    uploadData = db.getUnsyncedEntryLog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ProcessStart: JSONException(EntryLog): " + e.getMessage());
                }
        }

        Log.d(TAG, "Upload Begins uploadData: " + uploadData);

        Log.d(TAG, "DataDownWorkerALL: position " + position);
        //uploadColumns = workerParams.getInputData().getString("columns");
        uploadWhere = workerParams.getInputData().getString("where");

    }

    /*
     * This method is responsible for doing the work
     * so whatever work that is needed to be performed
     * we will put it here
     *
     * For example, here I am calling the method displayNotification()
     * It will display a notification
     * So that we will understand the work is executed
     * */
  /*  private static SSLSocketFactory buildSslSocketFactory(Context context) {
        try {


            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            AssetManager assetManager = context.getAssets();
            InputStream caInput = assetManager.open("vcoe1_aku_edu.cer");
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
*//*

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
            *//*
            // Create an SSLContext that uses our TrustManager
            SSLContext context1 = SSLContext.getInstance("TLSv1.2");
            context1.init(null, tmf.getTrustManagers(), null);
            return context1.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | CertificateException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static void longInfo(String str) {
        if (str.length() > 4000) {
            Log.i(TAG, str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.i(TAG, str);
    }

    /*
     * The method is doing nothing but only generating
     * a simple notification
     * If you are confused about it
     * you should check the Android Notification Tutorial
     * */
/*    private void displayNotification(String title, String task) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("scrlog", "BLF", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "scrlog")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.mipmap.ic_launcher);

        final int maxProgress = 100;
        int curProgress = 0;
        notification.setProgress(length, curProgress, false);

        //notificationManager.notify(1, notification.build());
    }*/

   /* private boolean certIsValid(Certificate[] certs, Certificate ca) {
        for (Certificate cert : certs) {
            System.out.println("Certificate is: " + cert);
            if (cert instanceof X509Certificate) {

                try {
                    ((X509Certificate) cert).checkValidity();

                    System.out.println("Certificate is active for current date");
                    if (cert.equals(ca)) {

                        return true;
                    }
                    //  Toast.makeText(mContext, "Certificate is active for current date", Toast.LENGTH_SHORT).show();
                } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                    e.printStackTrace();
                    return false;
                }
            }

        }
        return false;
    }*/

    @NonNull
    @Override
    public Result doWork() {
        startTime = System.currentTimeMillis();

        if (uploadData.length() == 0) {
            data = new Data.Builder()
                    .putString("error", "No new records to upload")
                    .putString("time", getTime())
                    .putString("size", getSize(requestLength) + "/" + getSize(responseLength))
                    .putInt("position", this.position)
                    .build();

            return Result.failure(data);
        }
        Log.d(TAG, "doWork: Starting");
        displayNotification(nTitle, "Starting upload");

        StringBuilder result = new StringBuilder();

        URL url = null;

        InputStream caInput = null;
        Certificate ca = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            AssetManager assetManager = mContext.getAssets();
            caInput = assetManager.open("star_aku_edu_2025.crt");


            ca = cf.generateCertificate(caInput);
            //     System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                caInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (serverURL == null) {
                url = new URL(MainApp._HOST_URL + MainApp._SERVER_URL);
            } else {
                url = serverURL;
            }
            Log.d(TAG, "doWork: Connecting...");

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    //Logcat.d(hostname + " / " + apiHostname);
                    Log.d(TAG, "verify: hostname " + hostname);
                    return true;
                }
            };
            //HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(buildSslSocketFactory(mContext));
            urlConnection.setReadTimeout(100000 /* milliseconds */);
            urlConnection.setConnectTimeout(150000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setUseCaches(false);
            startTime = System.currentTimeMillis();
            urlConnection.connect();

            Certificate[] certs = urlConnection.getServerCertificates();

            if (certIsValid(certs, ca)) {


                Log.d(TAG, "downloadURL: " + url);

                JSONArray jsonSync = new JSONArray();

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                JSONObject jsonTable = new JSONObject();
                JSONArray jsonParam = new JSONArray();

                jsonTable.put("table", uploadTable);
                //Log.d(TAG, "doWork: " + uploadData);
                //System.out.print("doWork: " + uploadData);
                //jsonSync.put(uploadData);
                jsonParam
                        .put(jsonTable)
                        .put(uploadData);

                Log.d(TAG, "Upload Begins Length: " + jsonParam.length());
                Log.d(TAG, "Upload Begins: " + jsonParam);
                longInfo(String.valueOf(jsonParam));

                String cipheredRequest = CipherSecure.encryptGCM(jsonParam.toString());
                requestLength = cipheredRequest.length();
                wr.writeBytes(cipheredRequest);

                String writeEnc = CipherSecure.encryptGCM(jsonParam.toString());

                longInfo("Encrypted: " + writeEnc);

                //     wr.writeBytes(jsonParam.toString());

                wr.flush();
                wr.close();

                Log.d(TAG, "doInBackground: " + urlConnection.getResponseCode());

                if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                    Log.d(TAG, "Connection Response: " + urlConnection.getResponseCode());
                    displayNotification(nTitle, "Connection Established");

                    responseLength = urlConnection.getContentLength();
                    Log.d(TAG, "Content Length: " + length);

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }
                    displayNotification(nTitle, "Received Data");
                    longInfo("result-server: " + decryptGCM(String.valueOf(result)));

                } else {

                    Log.d(TAG, "Connection Response (Server Failure): " + urlConnection.getResponseCode());

                    data = new Data.Builder()
                            .putString("error", String.valueOf(urlConnection.getResponseCode()))
                            .putString("time", getTime())
                            .putString("size", getSize(requestLength) + "/" + getSize(responseLength))
                            .putInt("position", this.position)
                            .build();
                    nTitle = "Data Upload Failed";
                    nMessage = "Data Uploaded Successfully Completed\nError: " + urlConnection.getResponseCode();
                    displayNotification(nTitle, nMessage);
                    return Result.failure(data);
                }
            } else {
                data = new Data.Builder()
                        .putString("error", "Invalid Certificate")
                        .putString("time", getTime())
                        .putString("size", getSize(requestLength) + "/" + getSize(responseLength))
                        .putInt("position", this.position)
                        .build();
                nTitle = "Data Upload Failed";
                nMessage = "Data Uploaded Successfully Completed\nError: Invalid Certificate";
                displayNotification(nTitle, nMessage);
                return Result.failure(data);
            }
        } catch (java.net.SocketTimeoutException e) {
            Log.d(TAG, "doWork (Timeout): " + e.getMessage());
            displayNotification(nTitle, "Timeout Error: " + e.getMessage());
            data = new Data.Builder()
                    .putString("error", e.getMessage())
                    .putString("time", getTime())
                    .putString("size", getSize(requestLength) + "/" + getSize(responseLength))
                    .putInt("position", this.position)
                    .build();
            nTitle = "Data Upload Failed";
            nMessage = "Data Uploaded Successfully Completed\nError: " + e.getMessage();
            displayNotification(nTitle, nMessage);
            return Result.failure(data);

        } catch (IOException | JSONException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            Log.d(TAG, "doWork (IO Error): " + e.getMessage());
            displayNotification(nTitle, "IO Error: " + e.getMessage());
            data = new Data.Builder()
                    .putString("error", e.getMessage())
                    .putString("time", getTime())
                    .putString("size", getSize(requestLength) + "/" + getSize(responseLength))
                    .putInt("position", this.position)
                    .build();
            nTitle = "Data Upload Failed";
            nMessage = "Data Uploaded Successfully Completed\nError: " + e.getMessage();
            displayNotification(nTitle, nMessage);
            return Result.failure(data);

        }
        try {
            result = new StringBuilder(CipherSecure.decryptGCM(result.toString()));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException |
                 IllegalBlockSizeException | InvalidAlgorithmParameterException |
                 InvalidKeyException | UnsupportedEncodingException e) {
            Log.d(TAG, "doWork (Encryption Error): " + e.getMessage());
            displayNotification(nTitle, "Encryption Error: " + e.getMessage());
            data = new Data.Builder()
                    .putString("error", e.getMessage())
                    .putString("time", getTime())
                    .putString("size", getSize(requestLength) + "/" + getSize(responseLength))
                    .putInt("position", this.position)
                    .build();
            nTitle = "Data Upload Failed";
            nMessage = "Data Uploaded Successfully Completed\nError: " + e.getMessage();
            displayNotification(nTitle, nMessage);
            return Result.failure(data);

        }

        if (result != null) {
            if (result.length() > 0) {
                try {
                    Log.d(TAG, "onPostExecute: " + result);
                    JSONArray json = new JSONArray(result.toString());

                    Method method = null;
                    for (Method method1 : db.getClass().getDeclaredMethods()) {

                        // Log.d(TAG, "onChanged Methods: " + method1.getName());
                        /**
                         * MAKE SURE TABLE_NAME = <table> IS SAME AS updateSynced<table> :
                         *
                         *      -   public static final String TABLE_NAME = "<table>";  // in Contract
                         *      -   public JSONArray updateSynced<table>() {              // in DatabaseHelper
                         *
                         *      e.g: Forms and updateSyncedForms
                         *
                         */
                        if (method1.getName().equals("updateSynced" + uploadTable)) {
                            method = method1;
                            break;
                        }
                    }
                    if (method != null) {
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonObject = new JSONObject(json.getString(i));
                            Log.d(TAG, "onChanged: " + json.getString(i));
                            if (jsonObject.getString("status").equals("1") && jsonObject.getString("error").equals("0")) {
                                method.invoke(db, jsonObject.getString("id"));
                            } else if (jsonObject.getString("status").equals("2") && jsonObject.getString("error").equals("0")) {
                                method.invoke(db, jsonObject.getString("id"));
                            } else {
                                Log.d(TAG, "Error: " + jsonObject.getString("message"));
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();


                }
            }
        }

        //Do something with the JSON string
        if (result != null) {
            displayNotification(nTitle, "Starting Data Processing");

            //String json = result.toString();
            /*if (json.length() > 0) {*/
            displayNotification(nTitle, "Data Size: " + result.length());


            // JSONArray jsonArray = new JSONArray(json);


            //JSONObject jsonObjectCC = jsonArray.getJSONObject(0);
            ///BE CAREFULL DATA.BUILDER CAN HAVE ONLY 1024O BYTES. EACH CHAR HAS 8 BIT
          /*  if (result.toString().length() > 10240) {
                data = new Data.Builder()
                        .putString("message", "Data Limit Reached ("+result.toString().length()+"/10240):"+String.valueOf(result).substring(0, (10240 - 1) / 8))
                        .putInt("position", this.position)
                        .build();
            } else {*/
            MainApp.downloadData[position] = result.toString();


            data = new Data.Builder()
                    //  .putString("message", String.valueOf(result))
                    .putString("time", getTime())
                    .putString("size", getSize(requestLength) + "/" + getSize(responseLength))
                    .putInt("position", this.position)
                    .build();
            /*   }*/

            displayNotification(nTitle, "Uploaded successfully");
            nTitle = "Data Upload Completion";
            nMessage = "Data Uploaded Successfully Completed\nRecords Synced: " + uploadData.length();
            displayNotification(nTitle, nMessage);
            return Result.success(data);

        } else {
            data = new Data.Builder()
                    .putString("error", String.valueOf(result))
                    .putString("time", getTime())
                    .putString("size", getSize(requestLength) + "/" + getSize(responseLength))
                    .putInt("position", this.position)
                    .build();
            displayNotification(nTitle, "Error Received");
            nTitle = "Data Upload Failed";
            nMessage = "Data Uploaded Successfully Completed\nError: " + result;
            displayNotification(nTitle, nMessage);
            return Result.failure(data);
        }


    }

    private String getSize(int length) {
        if (length < 0) return "0B";
        double lengthM = length / 1024 / 1024;
        return lengthM > 1 ? lengthM + "MB" : (length / 1024) > 1 ? (length / 1024) + "KB" : length + "B";
    }

    private String getTime() {

        long timeElapsed = System.currentTimeMillis() - startTime;
        long toMinutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed);
        long toSeconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsed - (toMinutes * 60 * 1000));

        return toMinutes > 0 ? toMinutes + "m " + toSeconds + "s" : toSeconds > 0 ? TimeUnit.MILLISECONDS.toSeconds(timeElapsed) + "s" : timeElapsed + "ms";
    }

/*    public static void enqueuePeriodicWork(Context context) {
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(
                        DataUpWorkerALL.class,
                        15, TimeUnit.MINUTES
                ).build();

        WorkManager.getInstance(context).enqueue(periodicWorkRequest);
    }*/

    private void showNotification(String title, String content) {
        Toast.makeText(mContext, "Notifying...: " + title + " : " + content, Toast.LENGTH_LONG).show();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setColorized(true)
                .setContentTitle(title);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
            Toast.makeText(mContext, "Suceed: "+ title+" : "+content, Toast.LENGTH_LONG).show();

    }

    private void displayNotification(String title, String task) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("scrlog", "BLF", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "scrlog")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.mipmap.ic_launcher);

        final int maxProgress = 100;
        int curProgress = 0;
        notification.setProgress(length, curProgress, false);

        //notificationManager.notify(1, notification.build());
    }
}