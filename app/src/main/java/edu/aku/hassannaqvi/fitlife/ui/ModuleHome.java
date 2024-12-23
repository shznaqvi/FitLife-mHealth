package edu.aku.hassannaqvi.fitlife.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.MainApp;

public class ModuleHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_home);
    }

    public void sectionPress(View view) {

        Intent i = new Intent(this, VideoPlayerActivity.class);

        if (view.getId() == R.id.ncd_risk_factors) {
           i.putExtra("videoID", "yb5KhcbtQJ0");
           i.putExtra("sessionName", "Session 1: \n" +
                   "Non-Communicable Diseases (NCDs) and its risk factors\n");
            i.putExtra("sessionObj", "•\tWhat is Non-Communicable Diseases (NCDs)\n" +
                    "•\tTypes of NCDs (e.g., heart diseases, stroke, cancer, diabetes, etc.)\n" +
                    "•\tPrevalence of NCDs in Pakistan\n" +
                    "•\tImportance of preventing NCDs\n" +
                    "•\tTypes of risk factors (behavioral, environmental, genetic)\n" +
                    "•\tCommon risk factors of NCDs in Pakistan (e.g., smoking, physical inactivity, unhealthy diet)\n" +
                    "•\tImpact of NCDs on quality of life");
        } else if (view.getId() == R.id.ncd_physical_activity) {
            i.putExtra("videoID", "UqJszWOyt6g");
            i.putExtra("sessionName", "Session 3: Keep moving ");
            i.putExtra("sessionObj", "•\tImportance of physical activity for NCD prevention\n" +
                    "•\tBenefits of physical activity\n" +
                    "•\tTypes of physical activity\n" +
                    "•\tHow to incorporate physical activity into daily routine");

        }  else if (view.getId() == R.id.ncd_food) {
            i.putExtra("videoID", "gj7y5hC6d0M");
            i.putExtra("sessionName", "Session 2: Eat smart ");
            i.putExtra("sessionObj", "•\tImportance of healthy eating for NCD prevention\n" +
                    "•\tFood groups and their benefits\n" +
                    "•\tHealthy snack options\n" +
                    "•\tHow to make healthy food choices");

        }  else if (view.getId() == R.id.ndc_smoking) {
            i.putExtra("videoID", "ljMTCDmib5g");
            i.putExtra("sessionName", "Session 4: Run away from smoking and drugs");
            i.putExtra("sessionObj", "•\tDangers of tobacco and drug use\n" +
                    "•\tHealth risks associated with tobacco and drug abuse\n" +
                    "•\tTips for avoiding tobacco and drug abuse");



        }  else if (view.getId() == R.id.ncd_mental_health) {
            i.putExtra("videoID", "_ZS34pzTT4I");
            i.putExtra("sessionName", "Session 5: Stay well ");
            i.putExtra("sessionObj", "•\tWhat is mental health\n" +
                    "•\tCommon mental health problems\n" +
                    "•\tRelationship between mental health and NCDs\n" +
                    "•\tTechniques for managing stress (e.g., deep breathing, exercise mindfulness).\n" +
                    "•\tImportance of sleep for overall health");


        }  else if (view.getId() == R.id.ncd_wrap_up) {
            i.putExtra("videoID", "I8p4rmsFJwY");
            i.putExtra("sessionName", "Session 6: Adopting Healthy Habits");
            i.putExtra("sessionObj", "•\tRecognizing the importance of healthy habits in preventing NCDs.\n" +
                    "•\tExploring the impact of small lifestyle changes on long-term health outcomes.\n" +
                    "•\tUnderstanding the interconnectedness between healthy habits, physical activity, nutrition, and mental well-being.");


        }
        startActivity(i);

    }
    public void btnContinue(View view) {
        // Navigate to a new activity
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);

        // Optionally finish the current activity if you don't want it to stay in the back stack
        //finish();
    }
}