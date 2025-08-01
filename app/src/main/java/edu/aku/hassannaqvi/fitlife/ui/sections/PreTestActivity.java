package edu.aku.hassannaqvi.fitlife.ui.sections;


import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.tests;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.validatorcrawler.aliazaz.Validator;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityPreTestBinding;
import edu.aku.hassannaqvi.fitlife.models.EntryLog;
import edu.aku.hassannaqvi.fitlife.models.Tests;
import edu.aku.hassannaqvi.fitlife.ui.VideoPlayerActivity;


public class PreTestActivity extends AppCompatActivity {
    private static final String TAG = "SectionA1Activity";
    private final boolean respAgeCheck = false;
    ActivityPreTestBinding bi;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApp) getApplication()).resetInactivityTimer();

        MainApp.setLocale(this);

        //setTheme(MainApp.langRTL ? MainApp.selectedLanguage == 1 ? R.style.AppThemeUrdu : R.style.AppThemeSindhi : R.style.AppThemeEnglish1);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_pre_test);

        tests = new Tests();
        bi.setCallback(this);
        bi.setTests(tests);


        switch (MainApp.sessionid) {
            case 1:
                loadSectionContent("a", R.string.section1, false);
                break;
            case 2:
                loadSectionContent("b", R.string.section2, true);
                break;
            case 3:
                loadSectionContent("c", R.string.section3, true);
                break;
            case 4:
                loadSectionContent("d", R.string.section4, false);
                break;
            case 5:
                loadSectionContent("e", R.string.section5, false);
                break;
            case 6:
                loadSectionContent("f", R.string.section6, true);
                break;
            default:
                loadSectionContent("a", R.string.section1, false);
                break;
        }

        //   if (MainApp.superuser)
        //         bi.btnContinue.setText("Review Next");
        db = MainApp.appInfo.dbHelper;
        //     form.setA101C(String.valueOf(new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date().getTime())));
        recordEntry("Pre-Test Started: "+MainApp.sessionName);

    }

    private void loadSectionContent(String sectionPrefix, int sectionResId, boolean q07) {
        bi.sectionHeader.setText(getString(sectionResId));
        int qnum = 6; // Default number of questions for sections without q07
        if( q07 ) {
            bi.fldGrpCVpre07.setVisibility(View.VISIBLE);
            qnum =7;

        } else {
            bi.fldGrpCVpre07.setVisibility(View.GONE);
        }

        for (int i = 1; i <= qnum; i++) {
            // Question Numbers
            int qNumResId = getResources().getIdentifier("Q_" + sectionPrefix + String.format("%02d", i), "string", getPackageName());
            int qNumId = getResources().getIdentifier("qnumPre" + String.format("%02d", i), "id", getPackageName());
            Log.d(TAG, "loadSectionContent: " + sectionPrefix + "qnumPre" + String.format("%02d", i) + " - " + qNumResId + " - " + qNumId);
            TextView qNumView = findViewById(qNumId);
            qNumView.setText(getString(qNumResId));

            // Question Texts
            int qTxtResId = getResources().getIdentifier(sectionPrefix + String.format("%02d", i), "string", getPackageName());
            int qTxtId = getResources().getIdentifier("qtxtPre" + String.format("%02d", i), "id", getPackageName());
            Log.d(TAG, "loadSectionContent: " + sectionPrefix + "qtxtPre" + String.format("%02d", i) + " - " + qTxtResId + " - " + qTxtId);

            TextView qTxtView = findViewById(qTxtId);
            qTxtView.setText(getString(qTxtResId));

            // Answer Options (A-D)
            for (char option = 'A'; option <= 'D'; option++) {
                int optResId = getResources().getIdentifier(sectionPrefix + String.format("%02d", i) + option, "string", getPackageName());
                int optViewId = getResources().getIdentifier("pre" + String.format("%02d", i) + option, "id", getPackageName());
                Log.d(TAG, "loadSectionContent: " + sectionPrefix + "pre" + String.format("%02d", i) + option + " - " + optResId + " - " + optViewId);
                TextView optView = findViewById(optViewId);
                optView.setText(getString(optResId));
            }
        }
    }

    //
//
//    public void ageSkip(CharSequence s, int i, int i1, int i2) {
//        if (TextUtils.isEmpty(bi.a113.getText())) return;
//        if (Integer.parseInt(bi.a113.getText().toString()) < 18) {
//            bi.a114t.clearCheck();
//            bi.a115t.clearCheck();
//            bi.fldGrpCVa114t.setVisibility(View.GONE);
//            bi.fldGrpCVa115t.setVisibility(View.GONE);
//            bi.btnContinue.setVisibility(View.GONE);
//            respAgeCheck = false;
//        } else {
//            bi.fldGrpCVa114t.setVisibility(View.VISIBLE);
//            bi.fldGrpCVa115t.setVisibility(View.VISIBLE);
//            bi.btnContinue.setVisibility(View.VISIBLE);
//            respAgeCheck = true;
//        }
//    }
//
//
/*    private boolean insertNewRecord() {
        if (!tests.getUid().equals("") || MainApp.superuser) return true;
        tests.populateMeta();
        long rowId = 0;
        try {
         //   rowId = db.addTests(tests);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.db_excp_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        tests.setId(String.valueOf(rowId));
        if (rowId > 0) {
            tests.setUid(tests.getDeviceId() + tests.getId());
       //     db.updatesTestsColumn(TableContracts.TestsTable.COLUMN_UID, tests.getUid());
            return true;
        } else {
            Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }*/


/*    private boolean updateDB() {
        if (MainApp.superuser) return true;

        int updcount = 0;
        try {
            updcount = db.updatesTestsColumn(TableContracts.TestsTable.COLUMN_SA1, tests.sA1toString());
        } catch (JSONException e) {
            Toast.makeText(this, R.string.upd_db + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (updcount == 1) {
            return true;
        } else {
            Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }*/
private boolean insertNewRecord() {
    if (!tests.getUid().equals("") || MainApp.superuser) return true;

    tests.populateMeta();

    long rowId = 0;
    try {
        rowId = db.addTest(MainApp.tests);
    } catch (JSONException e) {
        e.printStackTrace();
        Log.d(TAG, "insertNewRecord: " + e.getMessage());
        Toast.makeText(this, R.string.db_excp_error + " FORM-add", Toast.LENGTH_SHORT).show();
        return false;
    }
    MainApp.tests.setId(String.valueOf(rowId));
    if (rowId > 0) {
        MainApp.tests.setUid(MainApp.tests.getDeviceId() + MainApp.tests.getId());
        db.updatesFormColumn(TableContracts.TestsTable.COLUMN_UID, MainApp.tests.getUid());
        return true;
    } else {
        Toast.makeText(this, R.string.upd_db_error + " FORM-update", Toast.LENGTH_SHORT).show();
        return false;
    }
}

    private boolean updateDB() {
        if (MainApp.superuser) return true;

        db = MainApp.appInfo.getDbHelper();
        long updcount = 0;
        try {
            updcount = db.updatesFormColumn(TableContracts.TestsTable.COLUMN_SPRETESTS, tests.sPreTestToString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, R.string.upd_db + e.getMessage());
            Toast.makeText(this, R.string.upd_db + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (updcount > 0) return true;
        else {
            Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void btnContinue() {
        if (!formValidation()) return;
        if (!insertNewRecord()) return;
        // saveDraft();
        if (updateDB()) {
            recordEntry("Pre-Test Finished: "+MainApp.sessionName);

            Intent i = new Intent(this, VideoPlayerActivity.class);
            startActivity(i);
            finish();

            // THIS IS NOT A CONSENT ACTIVITY
          /*  if (form.getA110().equals("1")) {
                startActivity(new Intent(this, FamilyMembersListActivity.class));
            } else {
                Intent endingActivityIntent = new Intent(this, EndingActivity.class);
                endingActivityIntent.putExtra("complete", false);
                endingActivityIntent.putExtra("checkToEnable", 4);
                startActivity(endingActivityIntent);
            }*/
        } else
            Toast.makeText(this, R.string.fail_db_upd, Toast.LENGTH_SHORT).show();
    }




  /*  public void btnContinue() {
        if (!formValidation()) return;

        Intent i = new Intent(this, VideoPlayerActivity.class);
        startActivity(i);
        finish();




         if (!insertNewRecord()) return;
        // saveDraft();
        if (updateDB()) {
            setResult(RESULT_OK);
            Intent i;
            i = new Intent(this, SectionB1Activity.class).putExtra("complete", true).setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(i);
            finish();

            if (tests.getA110().equals("1")) {
                Intent i;
                i = new Intent(this, SectionB1Activity.class).putExtra("complete", true).setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(i);
                finish();
            } else {
                Intent returnIntent = new Intent();
                //  returnIntent.putExtra("requestCode", requestCode);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        } else {
            Toast.makeText(this, R.string.fail_db_upd, Toast.LENGTH_SHORT).show();
        }
    }
*/


    public void btnEnd() {
        recordEntry("Pre-Test Canceled: "+MainApp.sessionName);

        finish();
     //   if (!formValidationEnd()) return;
     //   if (!insertNewRecord()) return;
   /*     if (updateDB()) {
            finish();
      //      startActivity(new Intent(this, EndingActivity.class).putExtra("complete", false));
        } else {
            Toast.makeText(this, "Failed to Update Database!", Toast.LENGTH_SHORT).show();
        }*/
        /*finish();
        startActivity(new Intent(this, EndingActivity.class).putExtra("complete", false));*/
    }


    private boolean formValidation() {
        return Validator.emptyCheckingContainer(this, bi.secGrpA);
    }


    private boolean formValidationEnd() {
        return Validator.emptyCheckingContainer(this, bi.secGrpA);
    }


    @Override
    public void onBackPressed() {

        // Allow BackPress
        super.onBackPressed();
        setResult(RESULT_CANCELED);

        // Dont Allow BackPress
        // Toast.makeText(this, "Back Press Not Allowed", Toast.LENGTH_SHORT).show();

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