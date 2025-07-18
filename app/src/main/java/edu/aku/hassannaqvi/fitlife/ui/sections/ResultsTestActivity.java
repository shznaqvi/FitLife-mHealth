package edu.aku.hassannaqvi.fitlife.ui.sections;


import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.tests;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.validatorcrawler.aliazaz.Validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityResultTestBinding;
import edu.aku.hassannaqvi.fitlife.models.EntryLog;


public class ResultsTestActivity extends AppCompatActivity {
    private static final String TAG = "SectionA1Activity";
    private final boolean respAgeCheck = false;
    ActivityResultTestBinding bi;
    private DatabaseHelper db;
    private int qnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApp) getApplication()).resetInactivityTimer();

        MainApp.setLocale(this);

        setTheme(MainApp.langRTL ? MainApp.selectedLanguage == 1 ? R.style.AppThemeUrdu : R.style.AppThemeSindhi : R.style.AppThemeEnglish1);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_result_test);

        // MainApp.form = new Form();
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
        updateDB();

        int preScorePercentage = (MainApp.preScore * 100) / qnum; // Calculate percentage
        int postScorePercentage = (MainApp.preScore * 100) / qnum; // Calculate percentage

// Determine the preColor
        int preColor;
        int postColor;
        switch (preScorePercentage / 10) {
            case 10:
            case 9:
            case 8:
                preColor = ContextCompat.getColor(this, R.color.muted_green);
                break;
            case 7:
            case 6:
            case 5:
                preColor = ContextCompat.getColor(this, R.color.warm_mustard);
                break;
            default:
                preColor = ContextCompat.getColor(this, R.color.soft_coral);
                break;
        }

        switch (postScorePercentage / 10) {
            case 10:
            case 9:
            case 8:
                postColor = ContextCompat.getColor(this, R.color.muted_green);
                break;
            case 7:
            case 6:
            case 5:
                postColor = ContextCompat.getColor(this, R.color.warm_mustard);
                break;
            default:
                postColor = ContextCompat.getColor(this, R.color.soft_coral);
                break;
        }
// Apply preColor to TextView
        bi.tvPreScore.setTextColor(preColor);
        bi.tvPostScore.setTextColor(postColor);
    }


    private void loadSectionContent(String sectionPrefix, int sectionResId, boolean q07) {
        bi.sectionHeader.setText(getString(sectionResId));
        qnum = 6; // Default number of questions for sections without q07
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
            TextView qNumView = findViewById(qNumId);
            qNumView.setText(getString(qNumResId));

            // Question Texts
            int qTxtResId = getResources().getIdentifier(sectionPrefix + String.format("%02d", i), "string", getPackageName());
            int qTxtId = getResources().getIdentifier("qtxtPre" + String.format("%02d", i), "id", getPackageName());
            TextView qTxtView = findViewById(qTxtId);
            qTxtView.setText(getString(qTxtResId));

            // Answer Options (A-D)
            for (char option = 'A'; option <= 'D'; option++) {
                int optResId = getResources().getIdentifier(sectionPrefix + String.format("%02d", i) + option, "string", getPackageName());
                int optViewId = getResources().getIdentifier("pre" + String.format("%02d", i) + option, "id", getPackageName());
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


    private boolean updateDB() {
        if (MainApp.superuser) return true;

        int updcount = 0;

        updcount = db.updatesFormColumn(TableContracts.TestsTable.COLUMN_ISTATUS, "1");
        recordEntry("Result completed");


        if (updcount == 1) {
            return true;
        } else {
            Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void btnContinue() {
      //  if (!formValidation()) return;
        finish();
        // if (!insertNewRecord()) return;
        // saveDraft();
      /*  if (updateDB()) {
            setResult(RESULT_OK);
            Intent i;
            i = new Intent(this, SectionB1Activity.class).putExtra("complete", true).setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(i);
            finish();

          *//*  if (tests.getA110().equals("1")) {
                Intent i;
                i = new Intent(this, SectionB1Activity.class).putExtra("complete", true).setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(i);
                finish();
            } else {
                Intent returnIntent = new Intent();
                //  returnIntent.putExtra("requestCode", requestCode);
                setResult(RESULT_OK, returnIntent);
                finish();
            }*//*
        } else {
            Toast.makeText(this, R.string.fail_db_upd, Toast.LENGTH_SHORT).show();
        }*/
    }



    public void btnEnd() {
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