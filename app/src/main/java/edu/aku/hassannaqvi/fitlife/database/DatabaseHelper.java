package edu.aku.hassannaqvi.fitlife.database;


import static edu.aku.hassannaqvi.fitlife.core.MainApp.IBAHC;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;
import static edu.aku.hassannaqvi.fitlife.core.UserAuth.checkPassword;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import edu.aku.hassannaqvi.fitlife.contracts.TableContracts;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.ChildTable;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.EntryLogTable;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.TestsTable;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.StreetsTable;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts.UsersTable;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.models.EntryLog;
import edu.aku.hassannaqvi.fitlife.models.Tests;
import edu.aku.hassannaqvi.fitlife.models.Users;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = PROJECT_NAME + ".db";
    public static final String DATABASE_COPY = PROJECT_NAME + "_copy.db";
    public static final String DATABASE_PASSWORD = IBAHC;
    private static final int DATABASE_VERSION = 1;
    private final String TAG = "DatabaseHelper";
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CreateTable.SQL_CREATE_USERS);
        db.execSQL(CreateTable.SQL_CREATE_CLUSTERS);


        //db.execSQL(CreateTable.SQL_CREATE_LISTING);
        db.execSQL(CreateTable.SQL_CREATE_STREETS);
        db.execSQL(CreateTable.SQL_CREATE_ENTRYLOGS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:

                // DO NOT BREAK AFTER EACH VERSION
                //break;
            case 2:
                db.execSQL(CreateTable.SQL_CREATE_DISTRICTS);
            case 3:

            default:

        }
    }


    //ADDITION in DB
    public long addListing(Tests tests) throws JSONException, SQLiteException {
        SQLiteDatabase db = null;
        long newRowId = -1;


        db = this.getWritableDatabase();

        if (db == null) {
            return newRowId; // Return -1 or handle the error as needed
        }

        ContentValues values = new ContentValues();
        values.put(TestsTable.COLUMN_PROJECT_NAME, tests.getProjectName());
        values.put(TableContracts.TestsTable.COLUMN_UID, tests.getUid());
        values.put(TestsTable.COLUMN_USERNAME, tests.getUserName());
        values.put(TestsTable.COLUMN_SYSDATE, tests.getSysDate());
/*        values.put(TestsTable.COLUMN_GPSLAT, tests.getGpsLat());
        values.put(TestsTable.COLUMN_GPSLNG, tests.getGpsLng());
        values.put(TestsTable.COLUMN_GPSPRO, tests.getGpsProvider());
        values.put(TestsTable.COLUMN_GPSDATE, tests.getGpsDT());
        values.put(TestsTable.COLUMN_GPSACC, tests.getGpsAcc());*/
        values.put(TestsTable.COLUMN_ISTATUS, tests.getiStatus());
        values.put(TestsTable.COLUMN_DEVICEID, tests.getDeviceId());
        values.put(TestsTable.COLUMN_APPVERSION, tests.getAppver());
        values.put(TestsTable.COLUMN_SYNCED, tests.getSynced());
        values.put(TestsTable.COLUMN_SYNC_DATE, tests.getSyncDate());
        values.put(TestsTable.COLUMN_STESTS, tests.sTestToString());


        // Insert into database
        newRowId = db.insertOrThrow(TestsTable.TABLE_NAME, null, values);


        if (db != null) {
            db.close(); // Close the database connection
        }


        return newRowId;
    }



    public Long addEntryLog(EntryLog entryLog) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EntryLogTable.COLUMN_PROJECT_NAME, entryLog.getProjectName());
        values.put(EntryLogTable.COLUMN_UUID, entryLog.getUuid());
        values.put(EntryLogTable.COLUMN_EB_CODE, entryLog.getEbCode());
        values.put(EntryLogTable.COLUMN_HHID, entryLog.getHhid());
        values.put(EntryLogTable.COLUMN_USERNAME, entryLog.getUserName());
        values.put(EntryLogTable.COLUMN_SYSDATE, entryLog.getSysDate());
        values.put(EntryLogTable.COLUMN_IStatus, entryLog.getIStatus());
        values.put(EntryLogTable.COLUMN_IStatus96x, entryLog.getIStatus96x());
        values.put(EntryLogTable.COLUMN_ENTRY_TYPE, entryLog.getEntryType());
        values.put(EntryLogTable.COLUMN_ENTRY_DATE, entryLog.getEntryDate());
        values.put(EntryLogTable.COLUMN_DEVICEID, entryLog.getDeviceId());
        values.put(EntryLogTable.COLUMN_SYNCED, entryLog.getSynced());
        values.put(EntryLogTable.COLUMN_SYNC_DATE, entryLog.getSyncDate());
        values.put(EntryLogTable.COLUMN_APPVERSION, entryLog.getAppver());

        long newRowId;
        newRowId = db.insertOrThrow(
                EntryLogTable.TABLE_NAME,
                EntryLogTable.COLUMN_NAME_NULLABLE,
                values);
        return newRowId;
    }



    //UPDATE in DB
    public int updatesFormColumn(String column, String value) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(column, value);

        String selection = TableContracts.TestsTable._ID + " =? ";
        String[] selectionArgs = {String.valueOf(MainApp.tests.getId())};

        return db.update(TestsTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public int updateListingColumn(String column, String value) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(column, value);

        String selection = TestsTable._ID + " =? ";
        String[] selectionArgs = {String.valueOf(MainApp.tests.getId())};

        return db.update(TestsTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }



    public int updatesEntryLogColumn(String column, String value, String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(column, value);

        String selection = EntryLogTable._ID + " =? ";
        String[] selectionArgs = {id};

        return db.update(EntryLogTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    //Functions that dealing with table data
    public boolean doLogin(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalArgumentException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = null;
        String whereClause = UsersTable.COLUMN_USERNAME + "=? ";
        String[] whereArgs = {username};
        String groupBy = null;
        String having = null;
        String orderBy = UsersTable.COLUMN_ID + " ASC";

        Users loggedInUser = new Users();
        c = db.query(
                UsersTable.TABLE_NAME,  // The table to query
                columns,                   // The columns to return
                whereClause,               // The columns for the WHERE clause
                whereArgs,                 // The values for the WHERE clause
                groupBy,                   // don't group the rows
                having,                    // don't filter by row groups
                orderBy                    // The sort order
        );
        while (c.moveToNext()) {
            loggedInUser = new Users().hydrate(c);

        }
        boolean countCheck = c.getCount() > 0;
        if (c != null && !c.isClosed()) {
            c.close();
        }

        if (checkPassword(password, loggedInUser.getPassword())) {
            MainApp.user = loggedInUser;
            MainApp.selectedDistrict = loggedInUser.getDist_id();
            MainApp.selectedLHW = String.valueOf(loggedInUser.getUserID());
            return countCheck;
        } else {
            return false;
        }
    }





    public int syncAppUser(JSONArray userList) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UsersTable.TABLE_NAME, null, null);
        int insertCount = 0;
        for (int i = 0; i < userList.length(); i++) {

            JSONObject jsonObjectUser = userList.getJSONObject(i);

            Users user = new Users();
            user.sync(jsonObjectUser);
            ContentValues values = new ContentValues();

            values.put(UsersTable.COLUMN_USERNAME, user.getUserName());
            values.put(UsersTable.COLUMN_PASSWORD, user.getPassword());
            values.put(UsersTable.COLUMN_FULLNAME, user.getFullname());
            values.put(UsersTable.COLUMN_ENABLED, user.getEnabled());
            values.put(UsersTable.COLUMN_ISNEW_USER, user.getNewUser());
            values.put(UsersTable.COLUMN_PWD_EXPIRY, user.getPwdExpiry());
            values.put(UsersTable.COLUMN_DESIGNATION, user.getDesignation());
            values.put(UsersTable.COLUMN_DISTRICT_ID, user.getDist_id());
            long rowID = db.insertOrThrow(UsersTable.TABLE_NAME, null, values);
            if (rowID != -1) insertCount++;
        }


        db.close();

        db.close();

        return insertCount;
    }


    public JSONArray getUnsyncedListing() throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = null;

        String whereClause;
        //whereClause = null;
        whereClause = TableContracts.TestsTable.COLUMN_SYNCED + " = '' ";

        String[] whereArgs = null;

        String groupBy = null;
        String having = null;

        String orderBy = TestsTable._ID + " ASC";

        JSONArray allForms = new JSONArray();
        c = db.query(
                TestsTable.TABLE_NAME,  // The table to query
                columns,                   // The columns to return
                whereClause,               // The columns for the WHERE clause
                whereArgs,                 // The values for the WHERE clause
                groupBy,                   // don't group the rows
                having,                    // don't filter by row groups
                orderBy                    // The sort order
        );
        while (c.moveToNext()) {
            /** WorkManager Upload
             /*Tests fc = new Tests();
             allFC.add(fc.Hydrate(c));*/
            Log.d(TAG, "getUnsyncedFormHH: " + c.getCount());
            Tests tests = new Tests();
            allForms.put(tests.hydrate(c).toJSONObject());


        }

        if (c != null && !c.isClosed()) {
            c.close();
        }

        Log.d(TAG, "getUnsyncedFormHH: " + allForms.toString().length());
        Log.d(TAG, "getUnsyncedFormHH: " + allForms);
        return allForms;
    }






    public JSONArray getUnsyncedEntryLog() throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = null;
        String whereClause;
        whereClause = EntryLogTable.COLUMN_SYNCED + " = '' ";

        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = EntryLogTable.COLUMN_ID + " ASC";

        JSONArray all = new JSONArray();
        c = db.query(
                EntryLogTable.TABLE_NAME,  // The table to query
                columns,                   // The columns to return
                whereClause,               // The columns for the WHERE clause
                whereArgs,                 // The values for the WHERE clause
                groupBy,                   // don't group the rows
                having,                    // don't filter by row groups
                orderBy                    // The sort order
        );
        while (c.moveToNext()) {
            Log.d(TAG, "getUnsyncedEntryLog: " + c.getCount());
            EntryLog entryLog = new EntryLog();
            all.put(entryLog.Hydrate(c).toJSONObject());
        }
        Log.d(TAG, "getUnsyncedEntryLog: " + all.toString().length());
        Log.d(TAG, "getUnsyncedEntryLog: " + all);
        return all;
    }


    //update SyncedTables
    public void updateSyncedListing(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "updateSyncedListing: " + id);
// New value for one column
        ContentValues values = new ContentValues();
        values.put(TestsTable.COLUMN_SYNCED, true);
        values.put(TableContracts.TestsTable.COLUMN_SYNC_DATE, new Date().toString());

// Which row to update, based on the title
        String where = TableContracts.TestsTable.COLUMN_ID + " = ?";
        String[] whereArgs = {id};

        int count = db.update(
                TableContracts.TestsTable.TABLE_NAME,
                values,
                where,
                whereArgs);
    }

    //update SyncedTables
    public void updateSyncedStreets(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "updateSyncedStreets: " + id);
// New value for one column
        ContentValues values = new ContentValues();
        values.put(StreetsTable.COLUMN_SYNCED, true);
        values.put(StreetsTable.COLUMN_SYNC_DATE, new Date().toString());

// Which row to update, based on the title
        String where = StreetsTable._ID + " = ?";
        String[] whereArgs = {id};

        int count = db.update(
                StreetsTable.TABLE_NAME,
                values,
                where,
                whereArgs);
    }

    public void updateSyncedFamilyMember(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "updateSyncedForms: " + id);

// New value for one column
        ContentValues values = new ContentValues();
        values.put(TestsTable.COLUMN_SYNCED, true);
        values.put(TestsTable.COLUMN_SYNCED_DATE, new Date().toString());

// Which row to update, based on the title
        String where = TestsTable.COLUMN_ID + " = ?";
        String[] whereArgs = {id};

        int count = db.update(
                TestsTable.TABLE_NAME,
                values,
                where,
                whereArgs);
    }


    public void updateSyncedChildren(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChildTable.COLUMN_SYNCED, true);
        values.put(ChildTable.COLUMN_SYNC_DATE, new Date().toString());
        String where = ChildTable.COLUMN_ID + " = ?";
        String[] whereArgs = {id};
        int count = db.update(
                ChildTable.TABLE_NAME,
                values,
                where,
                whereArgs);
    }

    public void updateSyncedEntryLog(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(EntryLogTable.COLUMN_SYNCED, true);
        values.put(EntryLogTable.COLUMN_SYNC_DATE, new Date().toString());
        String where = EntryLogTable.COLUMN_ID + " = ?";
        String[] whereArgs = {id};
        int count = db.update(
                EntryLogTable.TABLE_NAME,
                values,
                where,
                whereArgs);
    }



    public int updatePassword(String hashedPassword) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(UsersTable.COLUMN_PASSWORD, hashedPassword);
        values.put(UsersTable.COLUMN_ISNEW_USER, "");

        String selection = UsersTable.COLUMN_USERNAME + " =? ";
        String[] selectionArgs = {MainApp.user.getUserName()};

        return db.update(UsersTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }






}
