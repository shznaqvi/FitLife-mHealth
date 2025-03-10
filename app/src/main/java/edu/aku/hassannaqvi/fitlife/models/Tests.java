package edu.aku.hassannaqvi.fitlife.models;


import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;
import static edu.aku.hassannaqvi.fitlife.core.MainApp._EMPTY_;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.tests;

import android.database.Cursor;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;

import edu.aku.hassannaqvi.fitlife.BR;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.TestsTable;
import edu.aku.hassannaqvi.fitlife.core.MainApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tests extends BaseObservable implements Observable {

    final String TAG = "Tests";


    // FIELD VARIABLES for Pre-Test and Post-Test Questions (pre01 - pre06 and post01 - post06)
    public String pre01 = _EMPTY_;
    public String pre02 = _EMPTY_;
    public String pre03 = _EMPTY_;
    public String pre04 = _EMPTY_;
    public String pre05 = _EMPTY_;
    public String pre06 = _EMPTY_;

    public String post01 = _EMPTY_;
    public String post02 = _EMPTY_;
    public String post03 = _EMPTY_;
    public String post04 = _EMPTY_;
    public String post05 = _EMPTY_;
    public String post06 = _EMPTY_;


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
    String[] correctAnswers = {"3", "2", "1", "2", "4", "1"}; // Correct answers indexed as 0, 1, 2


    public Tests() {
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



    // Getters and Setters for Pre-Test Questions

    // pre01
    @Bindable
    public String getPre01() {
        return pre01;
    }

    public void setPre01(String pre01) {
        this.pre01 = pre01;

        if (pre01.equals(correctAnswers[1-1])) {
            MainApp.preScore++;
        }

        notifyPropertyChanged(BR.pre01);
    }

    // pre02
    @Bindable
    public String getPre02() {
        return pre02;
    }

    public void setPre02(String pre02) {
        this.pre02 = pre02;
        if (pre02.equals(correctAnswers[2-1])) {
            MainApp.preScore++;
        }
        notifyPropertyChanged(BR.pre02);
    }

    // pre03
    @Bindable
    public String getPre03() {
        return pre03;
    }

    public void setPre03(String pre03) {
        this.pre03 = pre03;
        if (pre03.equals(correctAnswers[3-1])) {
            MainApp.preScore++;
        }
        notifyPropertyChanged(BR.pre03);
    }

    // pre04
    @Bindable
    public String getPre04() {
        return pre04;
    }

    public void setPre04(String pre04) {
        this.pre04 = pre04;
        if (pre04.equals(correctAnswers[4-1])) {
            MainApp.preScore++;
        }
        notifyPropertyChanged(BR.pre04);
    }

    // pre05
    @Bindable
    public String getPre05() {
        return pre05;
    }

    public void setPre05(String pre05) {
        this.pre05 = pre05;
        if (pre05.equals(correctAnswers[5-1])) {
            MainApp.preScore++;
        }
        notifyPropertyChanged(BR.pre05);
    }

    // pre06
    @Bindable
    public String getPre06() {
        return pre06;
    }

    public void setPre06(String pre06) {
        this.pre06 = pre06;
        if (pre06.equals(correctAnswers[6-1])) {
            MainApp.preScore++;
        }
        notifyPropertyChanged(BR.pre06);
    }

// Getters and Setters for Post-Test Questions

    // post01
    @Bindable
    public String getPost01() {
        return post01;
    }

    public void setPost01(String post01) {
        this.post01 = post01;
        if (post01.equals(correctAnswers[1-1])) {
            MainApp.postScore++;
        }
        notifyPropertyChanged(BR.post01);
    }

    // post02
    @Bindable
    public String getPost02() {
        return post02;
    }

    public void setPost02(String post02) {
        this.post02 = post02;
        if (post02.equals(correctAnswers[2-1])) {
            MainApp.postScore++;
        }
        notifyPropertyChanged(BR.post02);
    }

    // post03
    @Bindable
    public String getPost03() {
        return post03;
    }

    public void setPost03(String post03) {
        this.post03 = post03;
        if (post03.equals(correctAnswers[3-1])) {
            MainApp.postScore++;
        }
        notifyPropertyChanged(BR.post03);
    }

    // post04
    @Bindable
    public String getPost04() {
        return post04;
    }

    public void setPost04(String post04) {
        this.post04 = post04;
        if (post04.equals(correctAnswers[4-1])) {
            MainApp.postScore++;
        }
        notifyPropertyChanged(BR.post04);
    }

    // post05
    @Bindable
    public String getPost05() {
        return post05;
    }

    public void setPost05(String post05) {
        this.post05 = post05;
        if (post05.equals(correctAnswers[5-1])) {
            MainApp.postScore++;
        }
        notifyPropertyChanged(BR.post05);
    }

    // post06
    @Bindable
    public String getPost06() {
        return post06;
    }

    public void setPost06(String post06) {
        this.post06 = post06;
        if (post06.equals(correctAnswers[6-1])) {
            MainApp.postScore++;
        }
        notifyPropertyChanged(BR.post06);
    }

    public Tests hydrate(Cursor cursor) throws JSONException {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor must not be null.");
        }

        this.id = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_ID));
        this.sessionID = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_SESSION_ID));

        this.uid = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_UID));
        this.projectName = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_PROJECT_NAME));


        this.userName = cursor.getString(cursor.getColumnIndexOrThrow(TestsTable.COLUMN_USERNAME));
        this.sysDate = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_SYSDATE));
        this.deviceId = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_DEVICEID));
        this.appver = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_APPVERSION));
        this.iStatus = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_ISTATUS));
        this.synced = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_SYNCED));
        this.syncDate = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_SYNC_DATE));


        // Hydrate sHH fields
        String sTestJsonString = cursor.getString(cursor.getColumnIndexOrThrow(TableContracts.TestsTable.COLUMN_STESTS));
        if (sTestJsonString != null) {
            sTestHydrate(sTestJsonString);
        }


        return this;
    }


    public void sTestHydrate(String jsonString) throws JSONException {
        Log.d(TAG, "hydrateTest: " + jsonString);
        if (jsonString != null) {
            JSONObject json = new JSONObject(jsonString);

            this.pre01 = json.optString("pre01", _EMPTY_);
            this.pre02 = json.optString("pre02", _EMPTY_);
            this.pre03 = json.optString("pre03", _EMPTY_);
            this.pre04 = json.optString("pre04", _EMPTY_);
            this.pre05 = json.optString("pre05", _EMPTY_);
            this.pre06 = json.optString("pre06", _EMPTY_);

            this.post01 = json.optString("post01", _EMPTY_);
            this.post02 = json.optString("post02", _EMPTY_);
            this.post03 = json.optString("post03", _EMPTY_);
            this.post04 = json.optString("post04", _EMPTY_);
            this.post05 = json.optString("post05", _EMPTY_);
            this.post06 = json.optString("post06", _EMPTY_);
        }
    }


    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();

        // Common fields
        json.put(TableContracts.TestsTable.COLUMN_ID, this.id);
        json.put(TableContracts.TestsTable.COLUMN_SESSION_ID, this.sessionID);
        json.put(TableContracts.TestsTable.COLUMN_UID, this.uid);
        json.put(TestsTable.COLUMN_PROJECT_NAME, this.projectName);
        json.put(TableContracts.TestsTable.COLUMN_USERNAME, this.userName);
        json.put(TableContracts.TestsTable.COLUMN_SYSDATE, this.sysDate);
        json.put(TableContracts.TestsTable.COLUMN_DEVICEID, this.deviceId);
        json.put(TableContracts.TestsTable.COLUMN_ISTATUS, this.iStatus);
//        json.put(TestsTable.COLUMN_SYNCED, this.synced);
//        json.put(TestsTable.COLUMN_SYNC_DATE, this.syncDate);
        json.put(TableContracts.TestsTable.COLUMN_APPVERSION, this.appver);


        // Convert sHH and sBG groups to JSONObjects
        json.put(TableContracts.TestsTable.COLUMN_STESTS, new JSONObject(sTestToString()));

        return json;
    }



    public String sTestToString() throws JSONException {
        Log.d(TAG, "hydrateTestToString: ");

        JSONObject json = new JSONObject();

        json.put("pre01", pre01);
        json.put("pre02", pre02);
        json.put("pre03", pre03);
        json.put("pre04", pre04);
        json.put("pre05", pre05);
        json.put("pre06", pre06);

        json.put("post01", post01);
        json.put("post02", post02);
        json.put("post03", post03);
        json.put("post04", post04);
        json.put("post05", post05);
        json.put("post06", post06);

        return json.toString();
    }




}