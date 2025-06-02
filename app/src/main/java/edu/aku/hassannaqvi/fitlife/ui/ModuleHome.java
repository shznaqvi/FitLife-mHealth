package edu.aku.hassannaqvi.fitlife.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.models.Tests;
import edu.aku.hassannaqvi.fitlife.ui.sections.PreTestActivity;

public class ModuleHome extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_home);
        db = MainApp.appInfo.dbHelper;
    }

    private boolean testExists() throws JSONException {


        MainApp.tests = new Tests();

        //MainApp.form = db.getFormByhhid();
        MainApp.tests = db.getTestsByUsernModule();

        return MainApp.tests != null;


    }

    public void sectionPress(View view) {





        if (view.getId() == R.id.ncd_risk_factors) {
            MainApp.sessionid = 1;
            MainApp.videoID = "yb5KhcbtQJ0";
            MainApp.sessionName = "Session 1: \n" +
                    "Non-Communicable Diseases (NCDs) and its risk factors\n";
            MainApp.sessionObj = "• Understand NCDs and their health implications.\n" +
                    "• Define NCDs and identify types (e.g., heart diseases, stroke, cancer, diabetes).\n" +
                    "• Discuss prevalence of NCDs in Pakistan.\n" +
                    "• Emphasize the importance of preventing NCDs.";

        } else if (view.getId() == R.id.ncd_physical_activity) {
            MainApp.sessionid = 2;
            MainApp.videoID = "UqJszWOyt6g";
            MainApp.sessionName = "Session 3: Keep moving ";
            MainApp.sessionObj = "•\tImportance of physical activity for NCD prevention\n" +
                    "•\tBenefits of physical activity\n" +
                    "•\tTypes of physical activity\n" +
                    "•\tHow to incorporate physical activity into daily routine";


        }  else if (view.getId() == R.id.ncd_food) {
            MainApp.sessionid = 3;
            MainApp.videoID = "gj7y5hC6d0M";
            MainApp.sessionName = "Session 2: Eat smart ";
            MainApp.sessionObj = "•\tImportance of healthy eating for NCD prevention\n" +
                    "•\tFood groups and their benefits\n" +
                    "•\tHealthy snack options\n" +
                    "•\tHow to make healthy food choices";


        }  else if (view.getId() == R.id.ndc_smoking) {
            MainApp.sessionid = 4;
            MainApp.videoID = "ljMTCDmib5g";
            MainApp.sessionName = "Session 4: Run away from smoking and drugs";
            MainApp.sessionObj = "•\tDangers of tobacco and drug use\n" +
                    "•\tHealth risks associated with tobacco and drug abuse\n" +
                    "•\tTips for avoiding tobacco and drug abuse";


        }  else if (view.getId() == R.id.ncd_mental_health) {
            MainApp.sessionid = 5;
            MainApp.videoID = "_ZS34pzTT4I";
            MainApp.sessionName = "Session 5: Stay well ";
            MainApp.sessionObj = "•\tWhat is mental health\n" +
                    "•\tCommon mental health problems\n" +
                    "•\tRelationship between mental health and NCDs\n" +
                    "•\tTechniques for managing stress (e.g., deep breathing, exercise mindfulness).\n" +
                    "•\tImportance of sleep for overall health";


        }  else if (view.getId() == R.id.ncd_wrap_up) {
            MainApp.sessionid = 6;
            MainApp.videoID = "I8p4rmsFJwY";
            MainApp.sessionName = "Session 6: Adopting Healthy Habits";
            MainApp.sessionObj = "•\tRecognizing the importance of healthy habits in preventing NCDs.\n" +
                    "•\tExploring the impact of small lifestyle changes on long-term health outcomes.\n" +
                    "•\tUnderstanding the interconnectedness between healthy habits, physical activity, nutrition, and mental well-being.";

        }

        Intent i;
        try {
            if(testExists()) {
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
    public void btnContinue(View view) {
        Toast.makeText(this, "The certificate download has started. Please check your phone's notifications for the download status.", Toast.LENGTH_SHORT).show();
        // Navigate to a new activity
        //Intent intent = new Intent(this, FeedbackActivity.class);
        //startActivity(intent);

        // Optionally finish the current activity if you don't want it to stay in the back stack
        //finish();
    }
}