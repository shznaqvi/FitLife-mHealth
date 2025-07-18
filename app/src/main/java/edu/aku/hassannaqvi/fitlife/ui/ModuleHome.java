package edu.aku.hassannaqvi.fitlife.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;

import java.io.File;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.core.SecureFileDownloader;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityModuleHomeBinding;
import edu.aku.hassannaqvi.fitlife.models.Tests;
import edu.aku.hassannaqvi.fitlife.ui.sections.PreTestActivity;

public class ModuleHome extends AppCompatActivity {

    private static final String TAG = "ModuleHome";
    private DatabaseHelper db;
   ActivityModuleHomeBinding bi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_module_home);
        db = MainApp.appInfo.dbHelper;

        updateCompletedModules();
    }

    private void updateCompletedModules() {
        try {
            String userName = MainApp.user.getUserName();
            ImageView[] checkmarks = {
                    bi.checkmark1,
                    bi.checkmark2,
                    bi.checkmark3,
                    bi.checkmark4,
                    bi.checkmark5,
                    bi.checkmark6
            };

            int checkedCount = 0;

            for (int i = 0; i < checkmarks.length; i++) {
                String id = String.valueOf(i + 1); // "1" to "6"
                boolean exists = testExists(id, userName);
                checkmarks[i].setVisibility(exists ? View.VISIBLE : View.GONE);
                if (exists) checkedCount++;
            }

            // Enable the download button if all 6 are checked
            if (checkedCount == checkmarks.length) {
                // bi.btnDownloadCertificate.setEnabled(true);
                Toast.makeText(this, "All modules completed! You can download your certificate.", Toast.LENGTH_SHORT).show();
            } else {
                // bi.btnDownloadCertificate.setEnabled(false);
                Toast.makeText(this, "Complete all modules to download your certificate.", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            throw new RuntimeException(e); // Optionally show error to user or log it
        }
    }

    private boolean testExists(String sessionId, String userName) throws JSONException {


        MainApp.tests = new Tests();

        //MainApp.form = db.getFormByhhid();
        MainApp.tests = db.getTestsByUsernModule(sessionId, userName);

        if (MainApp.tests == null)
            return false;

        return !(MainApp.tests.getPost06().equals("") || MainApp.tests.getPre06().equals(""));


    }

    public void sectionPress(View view) {





        if (view.getId() == R.id.ncd_risk_factors) {
            MainApp.sessionid = 1;
            MainApp.videoID = "yb5KhcbtQJ0"; //<-- 9:19
            MainApp.sessionName = "Session 1: \n" +
                    "Non-Communicable Diseases (NCDs) and its risk factors\n";
            MainApp.sessionObj = "• Understand NCDs and their health implications.\n" +
                    "• Define NCDs and identify types (e.g., heart diseases, stroke, cancer, diabetes).\n" +
                    "• Discuss prevalence of NCDs in Pakistan.\n" +
                    "• Emphasize the importance of preventing NCDs.";

        } else if (view.getId() == R.id.ncd_physical_activity) {
            MainApp.sessionid = 2;
            MainApp.videoID = "UqJszWOyt6g"; //<-- 6:29
            MainApp.sessionName = "Session 3: Keep moving ";
            MainApp.sessionObj = "•\tImportance of physical activity for NCD prevention\n" +
                    "•\tBenefits of physical activity\n" +
                    "•\tTypes of physical activity\n" +
                    "•\tHow to incorporate physical activity into daily routine";


        }  else if (view.getId() == R.id.ncd_food) {
            MainApp.sessionid = 3;
            MainApp.videoID = "gj7y5hC6d0M";//<-- 8:30
            MainApp.sessionName = "Session 2: Eat smart ";
            MainApp.sessionObj = "•\tImportance of healthy eating for NCD prevention\n" +
                    "•\tFood groups and their benefits\n" +
                    "•\tHealthy snack options\n" +
                    "•\tHow to make healthy food choices";


        }  else if (view.getId() == R.id.ndc_smoking) {
            MainApp.sessionid = 4;
            MainApp.videoID = "ljMTCDmib5g"; //<-- 6:43
            MainApp.sessionName = "Session 4: Run away from smoking and drugs";
            MainApp.sessionObj = "•\tDangers of tobacco and drug use\n" +
                    "•\tHealth risks associated with tobacco and drug abuse\n" +
                    "•\tTips for avoiding tobacco and drug abuse";


        }  else if (view.getId() == R.id.ncd_mental_health) {
            MainApp.sessionid = 5;
            MainApp.videoID = "_ZS34pzTT4I"; //<-- 6:21
            MainApp.sessionName = "Session 5: Stay well ";
            MainApp.sessionObj = "•\tWhat is mental health\n" +
                    "•\tCommon mental health problems\n" +
                    "•\tRelationship between mental health and NCDs\n" +
                    "•\tTechniques for managing stress (e.g., deep breathing, exercise mindfulness).\n" +
                    "•\tImportance of sleep for overall health";


        }  else if (view.getId() == R.id.ncd_wrap_up) {
            MainApp.sessionid = 6;
            MainApp.videoID = "I8p4rmsFJwY"; //<-- 9:07
            MainApp.sessionName = "Session 6: Adopting Healthy Habits";
            MainApp.sessionObj = "•\tRecognizing the importance of healthy habits in preventing NCDs.\n" +
                    "•\tExploring the impact of small lifestyle changes on long-term health outcomes.\n" +
                    "•\tUnderstanding the interconnectedness between healthy habits, physical activity, nutrition, and mental well-being.";

        }

        Intent i;
        try {
            if(testExists(String.valueOf(MainApp.sessionid), MainApp.user.getUserName())) {
                i= new Intent(this, VideoPlayerActivity.class);
                MainApp.testCase = false;

            } else {
                i = new Intent(this, PreTestActivity.class);
                MainApp.testCase = true;

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        startActivity(i);

    }




    public void downloadCertificateSimple(View view) {
        String username = MainApp.user != null ? MainApp.user.getUserName() : "default_user";
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Username is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://vhds.aku.edu/eshepp/api/generate_certificate.php?username=" + username;

        Log.d("DownloadDebug", "Download URL: " + url);

        SecureFileDownloader.downloadFile(
                this,
                url,
                "latest_version.zip",
                new SecureFileDownloader.DownloadCallback() {
                    @Override
                    public void onProgress(int progress) {
                        Log.d("DL", "Progress: " + progress + "%");
                    }

                    @Override
                    public void onSuccess(File file) {
                        runOnUiThread(() ->

                                Toast.makeText(getApplicationContext(), "Download complete: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show()
                        );
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() ->


                                Toast.makeText(getApplicationContext(), "Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }
        );


    }
    public void downloadCertificate(View view) {

        String username = MainApp.user != null ? MainApp.user.getUserName() : "default_user";
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Username is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://vhds.aku.edu/eshepp/api/generate_certificate.php?username=" + username;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            Toast.makeText(this, "DownloadManager is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Certificate for " + username);
            request.setDescription("Downloading your certificate PDF");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Certificate_" + username + ".pdf");
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setMimeType("application/pdf");


            long downloadId = downloadManager.enqueue(request); // Get the downloadId
            Log.d("DownloadID", "Download started with ID: " + downloadId);

            Toast.makeText(this, "Downloading certificate...", Toast.LENGTH_SHORT).show();

            // Call checkDownloadStatus immediately or later
            //checkDownloadStatus(downloadId);
            monitorDownloadStatus(downloadId);

        } catch (Exception e) {
            Log.e("DownloadError", "Download failed: " + e.getMessage());
            Toast.makeText(this, "Failed to start download: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void checkDownloadStatus(long downloadId) {
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            Toast.makeText(this, "DownloadManager is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        Cursor cursor = downloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));

            switch (status) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    Toast.makeText(this, "Download successful!", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(this, "Download failed: Reason code " + reason, Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PAUSED:
                    Toast.makeText(this, "Download paused: Reason code " + reason, Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    Log.d("DownloadPending", "Reason code: " + reason);
                    Toast.makeText(this, "Download pending: Reason code " + reason, Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(this, "Download in progress...", Toast.LENGTH_SHORT).show();
                    break;
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No download found with ID: " + downloadId, Toast.LENGTH_SHORT).show();
        }
    }

    private void monitorDownloadStatus(final long downloadId) {
        final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            Toast.makeText(this, "DownloadManager not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Handler handler = new Handler();
        final Runnable monitorRunnable = new Runnable() {
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);

                Cursor cursor = downloadManager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                    int reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON));

                    switch (status) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            Toast.makeText(ModuleHome.this, "Certificate downloaded successfully!", Toast.LENGTH_SHORT).show();
                            cursor.close();
                            handler.removeCallbacks(this);
                            return;

                        case DownloadManager.STATUS_FAILED:
                            Toast.makeText(ModuleHome.this, "Download failed. Reason code: " + reason, Toast.LENGTH_LONG).show();
                            cursor.close();
                            handler.removeCallbacks(this);
                            return;

                        case DownloadManager.STATUS_PAUSED:
                            Log.d("DownloadStatus", "Paused. Reason: " + reason);
                            break;

                        case DownloadManager.STATUS_PENDING:
                            Log.d("DownloadStatus", "Pending...");
                            break;

                        case DownloadManager.STATUS_RUNNING:
                            Log.d("DownloadStatus", "Downloading...");
                            break;
                    }

                    cursor.close();
                }

                // Re-run this check after a delay
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(monitorRunnable);
    }

    public void btnContinue(View view) {
        Toast.makeText(this, "The certificate download has started. Please check your phone's notifications for the download status.", Toast.LENGTH_SHORT).show();
        // Navigate to a new activity
        //Intent intent = new Intent(this, FeedbackActivity.class);
        //startActivity(intent);

        // Optionally finish the current activity if you don't want it to stay in the back stack
        //finish();
    }
}