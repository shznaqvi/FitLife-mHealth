package edu.aku.hassannaqvi.fitlife.ui;

import static edu.aku.hassannaqvi.fitlife.core.MainApp.editor;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.sharedPref;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ActivityMainBinding bi;
    SharedPreferences sp;
    private long downloadId;
    private Handler handler;
    private Runnable updateTitleRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());
        // Retrieve downloadId from SharedPreferences

        // bi = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.app_icon);
        //bi.adminView.setVisibility(MainApp.admin ? View.VISIBLE : View.GONE);
        bi.toolbar.setSubtitle("Welcome, " + MainApp.user.getFullname() + (MainApp.admin ? " (Admin)" : "") + "!");
        invalidateOptionsMenu();


        try {
            String pwExpiry = String.valueOf(new JSONObject(MainApp.user.getPwdExpiry()).get("date")).substring(0, 10);
            //     Toast.makeText(this, pwExpiry, Toast.LENGTH_LONG).show();
            Log.d(TAG, "onCreate: pwExpiry: " + pwExpiry);

            Calendar cal = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            cal.setTime(sdf.parse(pwExpiry));// all done

            int daysLeft = (int) MILLISECONDS.toDays(cal.getTimeInMillis() - System.currentTimeMillis());
            //  Toast.makeText(this, daysLeft+" Days left", Toast.LENGTH_LONG).show();
            if (daysLeft < 1) {
                Toast.makeText(this, "Your password has expired. Please contact your supervisor.", Toast.LENGTH_LONG).show();
                finish();
            }
            if (daysLeft < 10) {
                bi.newApp.setText("Your current password is expiring in " + daysLeft + " day(s) on " + pwExpiry + ". Please change your password to avoid account lockout. (Internet Required.)");
                // bi.message.setText("Your password will expire on " + pwExpiry + ". There are only " + daysLeft + " Days left.");
                bi.newApp.setVisibility(View.VISIBLE);

            } else {
                bi.newApp.setVisibility(View.INVISIBLE);
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }


        String latestVersionName = sharedPref.getString("versionName", "");
        int latestVersionCode = Integer.parseInt(sharedPref.getString("versionCode", "0"));

        if (MainApp.appInfo.getVersionCode() < latestVersionCode) {
            downloadId = sharedPref.getLong("downloadId", -1);
            Log.d(TAG, "onCreate(downloadId): "+downloadId);
         /*   if(downloadId == -1){
            OneTimeWorkRequest downloadWorkRequest = new OneTimeWorkRequest.Builder(AppDownloadWorker.class)
                    .build();

            WorkManager.getInstance(this).enqueue(downloadWorkRequest);

            }*/
            bi.newApp.setVisibility(View.VISIBLE);
            bi.progressBar.setVisibility(View.VISIBLE);

            // Start updating progress
            updateProgress();
            bi.newApp.setText("NOTICE: There is a newer version of this app available on server (" + latestVersionName + latestVersionCode + "). \nPlease download update the app now.");
        } else {
            bi.newApp.setVisibility(View.INVISIBLE);
            bi.progressBar.setVisibility(View.INVISIBLE);

        }

        handler = new Handler(Looper.getMainLooper());
        updateTitleRunnable = new Runnable() {
            @Override
            public void run() {
                updateMenuTitle();
                handler.postDelayed(this, 10000); // 10 seconds interval
            }
        };
        updateMenuTitle(); // Initial update
        startUpdatingTitle();

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((MainApp) getApplication()).resetInactivityTimer();
    }

    private void updateProgress() {
        if (downloadId == -1) return;
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        Cursor cursor = downloadManager.query(query);

        if (cursor != null && cursor.moveToFirst()) {

                int statusColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (statusColumnIndex != -1) {
                    int status = cursor.getInt(statusColumnIndex);
                    if (status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING) { } else{
                        editor.remove("downloadId");
                        editor.apply();
                    }

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // Download is successful
                        Toast.makeText(this, "Download finished successfully", Toast.LENGTH_SHORT).show();
                        // Handle other success-related tasks
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        // Download has failed
                        Toast.makeText(this, "Download finished with failure", Toast.LENGTH_SHORT).show();
                        // Handle other failure-related tasks
                    } else if (status == DownloadManager.STATUS_RUNNING) {
                        // Download is still running
                        Toast.makeText(this, "Download is still in progress", Toast.LENGTH_SHORT).show();
                        // You can show a message or take any relevant action
                    } else if (status == DownloadManager.STATUS_PENDING) {
                        // Download is pending
                        Toast.makeText(this, "Download is pending", Toast.LENGTH_SHORT).show();
                        // You can show a message or take any relevant action
                    }
                }


            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int downloadedBytes = cursor.getInt(columnIndex);

            columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int totalBytes = cursor.getInt(columnIndex);

            cursor.close();

            if (totalBytes > 0) {
                int progress = (downloadedBytes * 100) / totalBytes;
                bi.setProgress(progress); // Update progress using DataBinding
            }
        }
        // Schedule next update
        bi.getRoot().postDelayed(downloadId!=-1?this::updateProgress: null, 1000); // Update every 1 second
    }


    public void sectionPress(View view) {


        if (view.getId() == R.id.startInterview) {
            MainApp.entryType = 1;
        } else {
            MainApp.entryType = 0;
        }

        if (view.getId() == R.id.startInterview) {
          //  MainApp.listings = new Tests();
        //startActivity(new Intent(this, VideoPlayerActivity.class));
        startActivity(new Intent(this, ModuleHome.class));
        } else if (view.getId() == R.id.seca1) {
            // MainApp.listing = new Tests();
            // startActivity(new Intent(this, SectionHHActivity.class));
        } /*else if (view.getId() == R.id.secri) {
            MainApp.listings = new Tests();
            startActivity(new Intent(this, SectionA1Activity.class));
*//*        } else if (view.getId() == R.id.secis) {
            MainApp.listings = new Tests();
            startActivity(new Intent(this, EndingActivity.class));*//*
        } */else if (view.getId() == R.id.dbManager) {
           // startActivity(new Intent(this, AndroidDatabaseManager.class));
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        int itemId = item.getItemId();

        if (itemId == R.id.action_database) {
          //  intent = new Intent(MainActivity.this, AndroidDatabaseManager.class);
            startActivity(intent);
        }  else if (itemId == R.id.action_checkWorker) {
      //  intent = new Intent(MainActivity.this, CheckWorkerActivity.class);
        startActivity(intent);
    }
    else if (itemId == R.id.onSync) {
            intent = new Intent(MainActivity.this, SyncActivity.class);
            startActivity(intent);
    /*    } else if (itemId == R.id.checkPendingForms) {
            intent = new Intent(MainActivity.this, FormsReportPending.class);
            startActivity(intent);*/
        } else if (itemId == R.id.formsReportDate) {
         //   intent = new Intent(MainActivity.this, FormsReportDate.class);
            startActivity(intent);
        } else if (itemId == R.id.changePassword) {
            intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
    /*    } else if (itemId == R.id.formsReportCluster) {
            intent = new Intent(MainActivity.this, FormsReportCluster.class);
            startActivity(intent);*/
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        MenuItem action_database = menu.findItem(R.id.action_database);
        MenuItem action_checkworker = menu.findItem(R.id.action_checkWorker);

        action_database.setVisible(MainApp.admin);
        action_checkworker.setVisible(MainApp.admin);
        return true;

    }

    public void takePhoto(View view) {

     //   Intent intent = new Intent(this, TakePhoto.class);

      //  intent.putExtra("picID", "000000" + "_" + "A-0000-000" + "_" + "01" + "_");
      //  intent.putExtra("childName", "Test ChilD");
/*
        intent.putExtra("picID", "901001" + "_" + "A-0001-001" + "_" + "1" + "_");
        intent.putExtra("childName", "Hassan");
*/
        if (view.getId() == R.id.frontPhoto) {
     //       intent.putExtra("picView", "front".toUpperCase());
      //      startActivityForResult(intent, 1); // Activity is started with requestCode 1 = Front
        } else {
      //      intent.putExtra("picView", "back".toUpperCase());
      //      startActivityForResult(intent, 2); // Activity is started with requestCode 2 = Back
        }
    }

    // Call Back method  to get the Message listing other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, requestCode + "_" + resultCode, Toast.LENGTH_SHORT).show();

            String fileName = data.getStringExtra("FileName");

            // Check if the requestCode 1 = Front : 2 = Back -- resultCode 1 = Success : 2 = Failure
            // Results received with requestCode 1 = Front

            if (requestCode == 1 && resultCode == RESULT_OK) {
                Toast.makeText(this, "Photo Taken", Toast.LENGTH_SHORT).show();

             /*   bi.frontFileName.setText(fileName);
                bi.frontPhoto.setEnabled(false);
*/

            } else if (requestCode == 1 && resultCode != RESULT_CANCELED) {
                Toast.makeText(this, "Photo Cancelled", Toast.LENGTH_SHORT).show();

                //TODO: Implement functionality below when photo was not taken
                // ...
                //     bi.frontFileName.setText("Photo not taken.");

            }

            // Results received with requestCode 2 = Back
            if (requestCode == 2 && resultCode == RESULT_OK) {
                Toast.makeText(this, "Photo Taken", Toast.LENGTH_SHORT).show();
/*
                bi.backFileName.setText(fileName);
                bi.backPhoto.setEnabled(false);*/
            } else if (requestCode == 2 && resultCode != RESULT_CANCELED) {

                Toast.makeText(this, "Photo Cancelled", Toast.LENGTH_SHORT).show();

                //TODO: Implement functionality below when photo was not taken
                // ...
                //      bi.backFileName.setText("Photo not taken.");

            }
        }
    }

    private void updateMenuTitle() {
        String title = String.valueOf(sharedPref.getInt("satelliteCount", 0));
       // bi.icSatelliteLive.setText(title);

    }

    private void startUpdatingTitle() {
        handler.post(updateTitleRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTitleRunnable); // Stop updates when activity is destroyed
    }
}