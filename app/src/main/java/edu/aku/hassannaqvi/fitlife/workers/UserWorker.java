package edu.aku.hassannaqvi.fitlife.workers;

import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.certIsValid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.CipherSecure;
import edu.aku.hassannaqvi.fitlife.core.MainApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class UserWorker extends Worker {

    private static final String TAG = "DataWorkerEN()";

    // to be initialised by workParams
    private final Context mContext;
    private final URL serverURL = null;
    private final String newPassword;
    HttpsURLConnection urlConnection;
    private String nTitle = "";
    private ProgressDialog pd;
    private int length;
    private Data data;


    public UserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        newPassword = workerParams.getInputData().getString("newPassword");
        nTitle = mContext.getResources().getString(R.string.app_name) + ": Users";
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
    /*private static SSLSocketFactory buildSslSocketFactory(Context context) {
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
    private void displayNotification(String title, String task) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("scrlog", "BLF", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "scrlog")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.drawable.app_icon);

        final int maxProgress = 100;
        int curProgress = 0;
        notification.setProgress(length, curProgress, false);

       // notificationManager.notify(1, notification.build());
    }

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

        displayNotification(nTitle, "Connenction server...");

        StringBuilder result = new StringBuilder();

        URL url = null;

        InputStream caInput = null;
        Certificate ca = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            AssetManager assetManager = mContext.getAssets();
            caInput = assetManager.open("star_aku_edu_2024.crt");


            ca = cf.generateCertificate(caInput);
            //    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
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
                url = new URL(MainApp._HOST_URL + MainApp._USER_URL);
            } else {
                url = serverURL;
            }
            Log.d(TAG, "doWork: Connecting...");
            Log.d(TAG, "doWork: Connecting... " + url);

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    //Logcat.d(hostname + " / " + apiHostname);
                    Log.d(TAG, "verify: hostname " + hostname);
                    return true;
                }
            };
            //HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(CipherSecure.buildSslSocketFactory(mContext));
            urlConnection.setReadTimeout(100000 /* milliseconds */);
            urlConnection.setConnectTimeout(150000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            Certificate[] certs = urlConnection.getServerCertificates();

            if (certIsValid(certs, ca)) {


                Log.d(TAG, "downloadURL: " + url);

                JSONArray jsonSync = new JSONArray();

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                JSONObject jsonParam = new JSONObject();

                jsonParam.put("userName", MainApp.user.getUserName());
                jsonParam.put("oldPassword", MainApp.user.getPassword());
                jsonParam.put("newPassword", newPassword);
                //Log.d(TAG, "doWork: " + uploadData);
                //System.out.print("doWork: " + uploadData);
                //jsonSync.put(uploadData);


                Log.d(TAG, "Upload Begins Length: " + jsonParam.length());
                Log.d(TAG, "Upload Begins: " + jsonParam);
                longInfo(String.valueOf(jsonParam));


                //wr.writeBytes(URLEncoder.encode(jsonParam.toString(), "utf-8"));
                wr.writeBytes(CipherSecure.encryptGCM(jsonParam.toString()));

                String writeEnc = CipherSecure.encryptGCM(jsonParam.toString());

                longInfo("Encrypted: " + writeEnc);

                //     wr.writeBytes(jsonParam.toString());

                wr.flush();
                wr.close();

                Log.d(TAG, "doInBackground: " + urlConnection.getResponseCode());

                if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    Log.d(TAG, "Connection Response: " + urlConnection.getResponseCode());
                    displayNotification(nTitle, "Connection Established");

                    length = urlConnection.getContentLength();
                    Log.d(TAG, "Content Length: " + length);

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }
                    displayNotification(nTitle, "Received Data");
                    longInfo("result-server: " + writeEnc);
                    longInfo("result-server: " + writeEnc);

                } else {

                    Log.d(TAG, "Connection Response (Server Failure): " + urlConnection.getResponseCode());

                    data = new Data.Builder()
                            .putString("error", "Connection Response (Server Failure): " + urlConnection.getResponseCode())
                            .build();
                    return Result.failure(data);
                }
            } else {
                data = new Data.Builder()
                        .putString("error", "Invalid Certificate")
                        .build();

                return Result.failure(data);
            }
        } catch (java.net.SocketTimeoutException e) {
            Log.d(TAG, "doWork (Timeout): " + e.getMessage());
            displayNotification(nTitle, "Timeout Error: " + e.getMessage());
            data = new Data.Builder()
                    .putString("error", "Timeout Error: " + e.getMessage())
                    .build();
            return Result.failure(data);

        } catch (IOException | JSONException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Log.d(TAG, "doWork (IO Error): " + e.getMessage());
            displayNotification(nTitle, "IO Error: " + e.getMessage());
            data = new Data.Builder()
                    .putString("error", e.getClass().getSimpleName() + ": " + e.getMessage())
                    .build();

            return Result.failure(data);

        }
        try {
            result = new StringBuilder(CipherSecure.decryptGCM(result.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            data = new Data.Builder()
                    .putString("error", e.getClass().getSimpleName() + ": " + e.getMessage())
                    .build();

            return Result.failure(data);
        }
        longInfo("result-server(Decrypted): " + result);

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


            data = new Data.Builder()
                    .putString("message", String.valueOf(result))
                    .build();
            /*   }*/

            displayNotification(nTitle, "Uploaded successfully");
            return Result.success(data);

        } else {
            data = new Data.Builder()
                    .putString("error", "No data Received: " + result)
                    .build();
            displayNotification(nTitle, "Error Received");
            return Result.failure(data);
        }


    }
}