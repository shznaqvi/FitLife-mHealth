package edu.aku.hassannaqvi.fitlife.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityMainBinding;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityUserGuideBinding;

public class UserGuideActivity extends AppCompatActivity {

    private ActivityUserGuideBinding bi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityUserGuideBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());
        // Retrieve downloadId from SharedPreferences

        // bi = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        bi.navView.setSelectedItemId(R.id.nav_instructions);
        // Set BottomNavigationView Listener
        bi.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                // Handle Home action
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_feedback) {
                // Handle Profile action
                Intent i = new Intent(UserGuideActivity.this, FeedbackActivity.class);
                startActivity(i);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_instructions) {
                // Handle Settings action
                return true;
            }
            return false;
        });
    }
}