package edu.aku.hassannaqvi.fitlife.ui;

import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.sdDir;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.uploadDataPeriodic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.EntryLogTable;
import edu.aku.hassannaqvi.fitlife.core.CustomLifecycleOwner;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.models.SyncModel;
import edu.aku.hassannaqvi.fitlife.workers.DataUpPeriodicWorkerALL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PeriodicWorkerHelper {
    private static final String TAG = "PeriodicWorkerHelper";
    private static final int NOTIFICATION_ID = 1; // You can use any integer value here

    private final Context context;
    private final CustomLifecycleOwner lifecycleOwner;
    private final WorkManager workManager;
    private final Constraints constraints;
    DatabaseHelper db;
    List<SyncModel> uploadTables;
    long repeatInterval = 15; // Set your desired repeat interval
    TimeUnit timeUnit = TimeUnit.MINUTES; // Set the time unit (HOURS, MINUTES, etc.)

    public PeriodicWorkerHelper(Context applicationContext) {
        context = applicationContext;
        lifecycleOwner = new CustomLifecycleOwner();

        this.workManager = WorkManager.getInstance(context);
        Log.d(TAG, "PeriodicWorkerHelper: initialising...");
        // Set up constraints (optional, adjust as needed)
        constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        db = MainApp.appInfo.getDbHelper();
        uploadTables = new ArrayList<>();
        uploadDataPeriodic = new ArrayList<>();
        /*sdDir = new File(this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), PROJECT_NAME);*/

        sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        sdDir = new File(sdDir, PROJECT_NAME);


        db = MainApp.appInfo.dbHelper;

    }

    public void ProcessStart() {

        Log.d(TAG, "ProcessStart: Staring...");
        uploadTables.clear();
        uploadDataPeriodic.clear();

       /* // Forms
        uploadTables.add(new SyncModel(TestsTable.TABLE_NAME));
        try {
            MainApp.uploadDataPeriodic.add(db.getUnsyncedFormHH());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "ProcessStart: JSONException(Forms): " + e.getMessage());
        }

        // Tests
        uploadTables.add(new SyncModel(TableContracts.TestsTable.TABLE_NAME));
        try {
            MainApp.uploadDataPeriodic.add(db.getUnsyncedTests());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "ProcessStart: JSONException(Tests): " + e.getMessage());
        }*/

        // Entry Log
        uploadTables.add(new SyncModel(EntryLogTable.TABLE_NAME));
        try {
            uploadDataPeriodic.add(db.getUnsyncedEntryLog());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "ProcessStart: JSONException(Forms): " + e.getMessage());
        }

        MainApp.downloadData = new String[uploadDataPeriodic.size()];
        BeginUpload();

    }


    private void BeginUpload() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        List<PeriodicWorkRequest> workRequests = new ArrayList<>();
        List<Data> inputDataList = new ArrayList<>();


        for (int i = 0; i < uploadTables.size(); i++) {
            Data data = new Data.Builder()
                    .putString("table", uploadTables.get(i).getTableName())
                    .putInt("position", i)
                    .build();
           PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(DataUpPeriodicWorkerALL.class, repeatInterval, timeUnit)
                    .addTag(uploadTables.get(i).getTableName())
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build();
           workRequests.add(workRequest);
            // Enqueue the work request with the KEEP policy
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    uploadTables.get(i).getTableName(),
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
            );

        }
        showNotification(TAG, "Starting Periodic Upload...");
       // WorkManager.getInstance(context).enqueue(workRequests);


        for (PeriodicWorkRequest workRequest : workRequests) {
            WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.getId())
                    .observe(lifecycleOwner, workInfo -> {

                        int position = workInfo.getOutputData().getInt("position", 0);

                        Log.d(TAG, "workInfo(getState): " + workInfo.getState());
                        Log.d(TAG, "workInfo(data): " + MainApp.downloadData[position]);
                        Log.d(TAG, "workInfo(error): " + workInfo.getOutputData().getString("error"));
                        Log.d(TAG, "workInfo(position): " + workInfo.getOutputData().getInt("position", 0));

                        String tableName = uploadTables.get(position).getTableName();
                        String result = MainApp.downloadData[position];


                        if (workInfo.getState() != null &&
                                workInfo.getState() == WorkInfo.State.SUCCEEDED) {

                            StringBuilder sSyncedError = new StringBuilder();
                            JSONArray json;

                            if (result != null) {
                                if (result.length() > 0) {
                                    try {
                                        Log.d(TAG, "onPostExecute: " + result);
                                        json = new JSONArray(result);

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
                                            if (method1.getName().equals("updateSynced" + tableName)) {
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
                                                    sSyncedError.append("\nError: ").append(jsonObject.getString("message"));
                                                }
                                            }

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();


                                    }
                                }
                            }
                        }

                    });

        }


    }

    private void showNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification_channel_id")
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}