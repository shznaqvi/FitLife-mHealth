package edu.aku.hassannaqvi.fitlife.ui;

import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityModuleHomeBinding;
import edu.aku.hassannaqvi.fitlife.models.EntryLog;
import edu.aku.hassannaqvi.fitlife.models.Tests;
import edu.aku.hassannaqvi.fitlife.ui.sections.PreTestActivity;

public class ModuleHome extends AppCompatActivity {

    private static final String TAG = "ModuleHome";
    private DatabaseHelper db;
    ActivityModuleHomeBinding bi;
    private long currentDownloadId = -1;
    private BroadcastReceiver downloadReceiver;
    private long downloadId;
    private Handler handler = new Handler(Looper.getMainLooper());
    private DownloadManager downloadManager;

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

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor cursor = downloadManager.query(query);

            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    Toast.makeText(context, "Certificate downloaded successfully!", Toast.LENGTH_SHORT).show();
                    setDownloadButtonText();
                    recordEntry("Certificate Download Successful.");
                    bi.progressBarDownload.setProgress(100);


                    showSystemUI();
                } else if (status == DownloadManager.STATUS_RUNNING) {
                    int percent = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)) * 100 /
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    Toast.makeText(context, "Download in progress: " + percent + "%", Toast.LENGTH_SHORT).show();
//                    bi.progressBarDownload.setVisibility(View.VISIBLE);
//                    bi.progressBarDownload.setProgress(percent);
                } else {
                    int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                    Toast.makeText(context, "Download failed! Reason: " + reason, Toast.LENGTH_LONG).show();
                    recordEntry("Certificate Download Failed: " + reason);

                }
            }
            cursor.close();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_module_home);
        db = MainApp.appInfo.dbHelper;

        updateCompletedModules();
        setDownloadButtonText();
    }

    public void downloadCertificate(View view) {
        String username = MainApp.user.getUserName();
        String userFullName = MainApp.user.getFullname();
        //username = "s";
        String fileName = userFullName + "_certificate.pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        if (file.exists()) {
            //Toast.makeText(this, "File already downloaded. Opening...", Toast.LENGTH_SHORT).show();

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(uri, "application/pdf");
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                recordEntry("Certificate File Opened.");
                startActivity(openIntent);

            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No app found to open PDF.", Toast.LENGTH_LONG).show();
            }

            return;  // ✅ Don't download again
        }

        // Proceed with download if file doesn't exist
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return;
        }

        try {
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://vhds.aku.edu/eshepp/api/generate_certificate.php?username=" + username);
            Log.d(TAG, "downloadCertificate URL: " + uri.toString());
            DownloadManager.Request request = new DownloadManager.Request(uri);

            request.setTitle("Certificate Download");
            request.setDescription("Downloading PDF...");
            request.setMimeType("application/pdf");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // 👇 Save to shared Downloads so it gets "Open" option in DM notification
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            downloadId = downloadManager.enqueue(request);
            monitorDownloadProgress();
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            Toast.makeText(this, "Downloading PDF...", Toast.LENGTH_SHORT).show();
            recordEntry("Certificate Download Started.");

        } catch (Exception e) {
            recordEntry("Certificate Download Failed.");

            Log.e("DownloadError", "Download failed", e);
            Toast.makeText(this, "Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void setDownloadButtonText() {
        String username = MainApp.user.getFullname();
        //username = "s";
        String fileName = username + "_certificate.pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            bi.btnDownloadCertificate.setText("Open Certificate");
        } else {
            bi.btnDownloadCertificate.setText("Download Certificate");
        }
    }

    private void monitorDownloadProgress() {
        Log.d(TAG, "monitorDownloadProgress: Monitoring download progress for ID: " + downloadId);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);

                if (cursor != null && cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    Log.d(TAG, "Download status: " + status);
                    if (status == DownloadManager.STATUS_RUNNING) {
                        Log.d(TAG, "run: Download is running");
                        int downloadedBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int totalBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        int progress = (int) ((downloadedBytes * 100L) / totalBytes);
                        Log.d(TAG, "run: Download progress: " + progress + "%");
                        bi.progressBarDownload.setVisibility(View.VISIBLE);
                        bi.progressBarDownload.setProgress(progress);
                    }

                    if (status != DownloadManager.STATUS_SUCCESSFUL && status != DownloadManager.STATUS_FAILED) {
                        // Continue polling
                        handler.postDelayed(this, 1000); // check every second
                        Log.d(TAG, "run: Continuing to monitor download progress...");
                    }

                }
                if (cursor != null) cursor.close();
            }
        }, 1000);
    }


    private void showSystemUI() {
        // Show StatusBar and NavigationBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void btnContinue(View view) {
        Toast.makeText(this, "The certificate download has started. Please check your phone's notifications for the download status.", Toast.LENGTH_SHORT).show();
        // Navigate to a new activity
        //Intent intent = new Intent(this, FeedbackActivity.class);
        //startActivity(intent);

        // Optionally finish the current activity if you don't want it to stay in the back stack
        //finish();
    }

    @Override
    protected void onDestroy() {
        try {
            if (downloadReceiver != null) {
                unregisterReceiver(downloadReceiver);
            }
        } catch (IllegalArgumentException e) {
            Log.w("Receiver", "Receiver already unregistered.");
        }
        super.onDestroy();
    }

    private void recordEntry(String entryType) {
        EntryLog entryLog = new EntryLog();
        entryLog.setProjectName(PROJECT_NAME);
        entryLog.setUserName(MainApp.user.getUserName());
        entryLog.setEntryDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date().getTime()));
        entryLog.setAppver(MainApp.appInfo.getAppVersion());
        entryLog.setEntryType(entryType);
        entryLog.setDeviceId(MainApp.deviceid);
        Long rowId = null;
        try {
            rowId = db.addEntryLog(entryLog);
            if (rowId != -1) {
                entryLog.setId(String.valueOf(rowId));
                entryLog.setUid(entryLog.getDeviceId() + entryLog.getId());
                db.updatesEntryLogColumn(TableContracts.EntryLogTable.COLUMN_UID, entryLog.getUid(), entryLog.getId());
            } else {
                Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();

            }
        } catch (SQLiteException e) {
            Toast.makeText(this, "SQLiteException(EntryLog)" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "recordEntry: " + e.getMessage());
        }
    }
}