package edu.aku.hassannaqvi.fitlife.models;


import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;
import static edu.aku.hassannaqvi.fitlife.core.MainApp._EMPTY_;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.tests;

import android.database.Cursor;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.aku.hassannaqvi.fitlife.BR;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.FeedbackTable;
import edu.aku.hassannaqvi.fitlife.core.MainApp;

public class Feedback extends BaseObservable implements Observable {

    final String TAG = "Tests";


    // FIELD VARIABLES for Pre-Test and Post-Test Questions (pre01 - pre06 and post01 - post06)
    // --- SECTION FBA: Feedback Section A ---
    public String fba01 = _EMPTY_;  // Very Likely


    public String fba02 = _EMPTY_;  // Strongly Agree


    public String fba03 = _EMPTY_;  // Excellent

    // --- SECTION FBB: Feedback Section B ---
    public String fbb01 = _EMPTY_;  // Yes, frequently


    public String fbb02 = _EMPTY_;  // Very Easy


    public String fbb03 = _EMPTY_;  // Completely


    // --- SECTION FBC: Feedback Section C ---
    public String fbc01 = _EMPTY_;  // Very Responsive

    public String fbc02 = _EMPTY_;  // Yes, very easy

    public String fbc03 = _EMPTY_;  // Very Satisfied


    // APP VARIABLES
    String projectName = PROJECT_NAME;
    String id = _EMPTY_;
    String sessionID = _EMPTY_;
    String uid = _EMPTY_;
    String userName = _EMPTY_;
    String sysDate = _EMPTY_;
    String deviceId = _EMPTY_;
    String appver = _EMPTY_;
    String endTime = _EMPTY_;
    String synced = _EMPTY_;
    String syncDate = _EMPTY_;
    String entryType = _EMPTY_;
    String gpsLat = _EMPTY_;
    String gpsLng = _EMPTY_;
    String gpsDT = _EMPTY_;
    String gpsAcc = _EMPTY_;
    String gpsProvider = _EMPTY_;
    String iStatus = _EMPTY_;
    String iStatus96x = _EMPTY_;

    public Feedback() {
        setSysDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date().getTime()));
    }


    public void populateMeta() {
        setSysDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date().getTime()));
        setUserName(MainApp.user.getUserName());  // Ensure this is properly set in your application
        setDeviceId(MainApp.deviceid);  // Ensure this is properly set in your application
        setAppver(MainApp.appInfo.getAppVersion());      // Ensure this is properly set in your application
        setProjectName(PROJECT_NAME); // Ensure this is properly set in your application
        tests.setUserName(MainApp.user.getUserName());

        //listings.setStructureNo(String.valueOf(MainApp.maxStructure));
        setGPS();
    }

    private void setGPS() {
        String latitude = MainApp.sharedPref.getString("latitude", "0");
        String longitude = MainApp.sharedPref.getString("longitude", "0");
        String accuracy = String.valueOf(MainApp.sharedPref.getFloat("accuracy", 0));
        long datetime = MainApp.sharedPref.getLong("datetime", 0);
        String provider = MainApp.sharedPref.getString("provider", "");

        // Convert timestamp to formatted date string
        String formattedDateTime = getFormattedDateTime(datetime);

        // Display formatted date time in the db
        setGpsLat(latitude);
        setGpsLng(longitude);
        setGpsAcc(accuracy);
        setGpsDT(formattedDateTime);
        setGpsProvider(provider);
    }

    private String getFormattedDateTime(long timestamp) {
        if (timestamp == 0) return "0";
        // Create a SimpleDateFormat object with desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Convert timestamp to Date object
        Date date = new Date(timestamp);

        // Format the Date object to desired format
        return sdf.format(date);
    }

    // Getters and Setters for basic fields

    // ProjectName
    @Bindable
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
        //    notifyPropertyChanged(BR.projectName);
    }

    // ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // SessionID
    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    // UID
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    // UserName
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // SysDate
    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

    // DeviceId
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    // Appver
    public String getAppver() {
        return appver;
    }

    public void setAppver(String appver) {
        this.appver = appver;
    }

    // EndTime
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // Synced
    public String getSynced() {
        return synced;
    }

    public void setSynced(String synced) {
        this.synced = synced;
    }

    // SyncDate
    public String getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(String syncDate) {
        this.syncDate = syncDate;
    }

    // GPSLat
    @Bindable
    public String getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(String gpsLat) {
        this.gpsLat = gpsLat;
        //    notifyPropertyChanged(BR.gpsLat);
    }

    // GPSLng
    @Bindable
    public String getGpsLng() {
        return gpsLng;
    }

    public void setGpsLng(String gpsLng) {
        this.gpsLng = gpsLng;
        //  notifyPropertyChanged(BR.gpsLng);
    }

    // GPSDT
    @Bindable
    public String getGpsDT() {
        return gpsDT;
    }

    public void setGpsDT(String gpsDT) {
        this.gpsDT = gpsDT;
        //   notifyPropertyChanged(BR.gpsDT);
    }

    // GPSAcc
    @Bindable
    public String getGpsAcc() {
        return gpsAcc;
    }

    public void setGpsAcc(String gpsAcc) {
        this.gpsAcc = gpsAcc;
        //   notifyPropertyChanged(BR.gpsAcc);
    }

    // GPSProvider
    @Bindable
    public String getGpsProvider() {
        return gpsProvider;
    }

    public void setGpsProvider(String gpsProvider) {
        this.gpsProvider = gpsProvider;
        //    notifyPropertyChanged(BR.gpsProvider);
    }

    // IStatus
    @Bindable
    public String getiStatus() {
        return iStatus;
    }

    public void setiStatus(String iStatus) {
        this.iStatus = iStatus;
        //  notifyPropertyChanged(BR.iStatus);
    }

    // IStatus96x
    @Bindable
    public String getiStatus96x() {
        return iStatus96x;
    }

    public void setiStatus96x(String iStatus96x) {
        this.iStatus96x = iStatus96x;
        //   notifyPropertyChanged(BR.iStatus96x);
    }


    // Getters and Setters for Section FBA
    @Bindable
    public String getFba01() {
        return fba01;
    }

    public void setFba01(String fba01) {
        this.fba01 = fba01;
        notifyPropertyChanged(BR.fba01);
    }

    @Bindable
    public String getFba02() {
        return fba02;
    }
    public void setFba02(String fba02) {
        this.fba02 = fba02;
        notifyPropertyChanged(BR.fba02);
    }

    @Bindable
    public String getFba03() {
        return fba03;
    }
    public void setFba03(String fba03) {
        this.fba03 = fba03;
        notifyPropertyChanged(BR.fba03);
    }

    // Getters and Setters for Section FBB
    @Bindable
    public String getFbb01() {
        return fbb01;
    }
    public void setFbb01(String fbb01) {
        this.fbb01 = fbb01;
        notifyPropertyChanged(BR.fbb01);
    }

    @Bindable
    public String getFbb02() {
        return fbb02;
    }
    public void setFbb02(String fbb02) {
        this.fbb02 = fbb02;
        notifyPropertyChanged(BR.fbb02);
    }

    @Bindable
    public String getFbb03() {
        return fbb03;
    }

    public void setFbb03(String fbb03) {
        this.fbb03 = fbb03;
        notifyPropertyChanged(BR.fbb03);
    }
    // Getters and Setters for Section FBC
    @Bindable
    public String getFbc01() {
        return fbc01;
    }
    public void setFbc01(String fbc01) {
        this.fbc01 = fbc01;
        notifyPropertyChanged(BR.fbc01);
    }

    @Bindable
    public String getFbc02() {
        return fbc02;
    }

    public void setFbc02(String fbc02) {
        this.fbc02 = fbc02;
        notifyPropertyChanged(BR.fbc02);
    }
    @Bindable
    public String getFbc03() {
        return fbc03;
    }

        public void setFbc03(String fbc03) {
        this.fbc03 = fbc03;
        notifyPropertyChanged(BR.fbc03);
    }


    public Feedback hydrate(Cursor cursor) throws JSONException {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor must not be null.");
        }

        this.id = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_ID));

        this.uid = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_UID));
        this.projectName = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_PROJECT_NAME));


        this.userName = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_USERNAME));
        this.sysDate = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_SYSDATE));
        this.deviceId = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_DEVICEID));
        this.appver = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_APPVERSION));
        this.iStatus = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_ISTATUS));
        this.synced = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_SYNCED));
        this.syncDate = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_SYNC_DATE));


        // Hydrate sHH fields
        String sFbJsonString = cursor.getString(cursor.getColumnIndexOrThrow(FeedbackTable.COLUMN_SFB));
        if (sFbJsonString != null) {
            sFbHydrate(sFbJsonString);
        }


        return this;
    }


    public void sFbHydrate(String jsonString) throws JSONException {
        Log.d(TAG, "hydrateTest: " + jsonString);
        if (jsonString != null) {
            JSONObject json = new JSONObject(jsonString);

            // Fields following the fba0101, fba0102, ... naming convention
            this.fba01 = json.optString("fba0101", _EMPTY_);
            this.fba02 = json.optString("fba0201", _EMPTY_);
            this.fba03 = json.optString("fba0301", _EMPTY_);

            // Fields under fbb prefix
            this.fbb01 = json.optString("fbb0101", _EMPTY_);
            this.fbb02 = json.optString("fbb0201", _EMPTY_);
            this.fbb03 = json.optString("fbb0301", _EMPTY_);


            // Fields under fbc prefix
            this.fbc01 = json.optString("fbc0101", _EMPTY_);
            this.fbc02 = json.optString("fbc0201", _EMPTY_);
            this.fbc03 = json.optString("fbc0301", _EMPTY_);

        }
    }


    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();

        // Common fields
        json.put(FeedbackTable.COLUMN_ID, this.id);
        json.put(FeedbackTable.COLUMN_UID, this.uid);
        json.put(FeedbackTable.COLUMN_PROJECT_NAME, this.projectName);
        json.put(FeedbackTable.COLUMN_USERNAME, this.userName);
        json.put(FeedbackTable.COLUMN_SYSDATE, this.sysDate);
        json.put(FeedbackTable.COLUMN_DEVICEID, this.deviceId);
        json.put(FeedbackTable.COLUMN_ISTATUS, this.iStatus);
//        json.put(FeedbackTable.COLUMN_SYNCED, this.synced);
//        json.put(FeedbackTable.COLUMN_SYNC_DATE, this.syncDate);
        json.put(FeedbackTable.COLUMN_APPVERSION, this.appver);


        // Convert sHH and sBG groups to JSONObjects
        json.put(FeedbackTable.COLUMN_SFB, new JSONObject(sFbToString()));

        return json;
    }


    public String sFbToString() throws JSONException {
        Log.d(TAG, "sFeedbackToString: ");

        JSONObject json = new JSONObject();

        // Fields following the fba0101, fba0102, ... naming convention
        json.put("fba0101", fba01);
        json.put("fba0201", fba02);
        json.put("fba0301", fba03);
        // Fields under fbb prefix
        json.put("fbb0101", fbb01);
        json.put("fbb0201", fbb02);
        json.put("fbb0301", fbb03);
        // Fields under fbc prefix
        json.put("fbc0101", fbc01);
        json.put("fbc0201", fbc02);
        json.put("fbc0301", fbc03);

        return json.toString();
    }


}