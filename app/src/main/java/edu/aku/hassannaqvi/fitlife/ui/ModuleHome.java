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
           i.putExtra("sessionName", "Session 1: Risk Factors");
        } else if (view.getId() == R.id.ncd_physical_activity) {
            i.putExtra("videoID", "UqJszWOyt6g");
            i.putExtra("sessionName", "Session 2: Physical Activity");

        }  else if (view.getId() == R.id.ncd_food) {
            i.putExtra("videoID", "gj7y5hC6d0M");
            i.putExtra("sessionName", "Session 2: Food");

        }  else if (view.getId() == R.id.ndc_smoking) {
            i.putExtra("videoID", "ljMTCDmib5g");
            i.putExtra("sessionName", "Session 4: Smoking");

        }  else if (view.getId() == R.id.ncd_mental_health) {
            i.putExtra("videoID", "_ZS34pzTT4I");
            i.putExtra("sessionName", "Session 5: Mental Health");

        }  else if (view.getId() == R.id.ncd_wrap_up) {
            i.putExtra("videoID", "I8p4rmsFJwY");
            i.putExtra("sessionName", "Session 1: Wrap Up");

        }
        startActivity(i);

    }
}