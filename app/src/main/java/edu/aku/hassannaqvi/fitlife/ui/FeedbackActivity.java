package edu.aku.hassannaqvi.fitlife.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityFeedbackBinding;

import android.view.View;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;

import com.validatorcrawler.aliazaz.Validator;

public class FeedbackActivity extends AppCompatActivity {

    private ActivityFeedbackBinding bi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up data binding
        bi = DataBindingUtil.setContentView(this, R.layout.activity_feedback);

        // Setting up the toolbar
        setSupportActionBar(bi.toolbar);

        // You can also access your views through the binding object
        bi.btnContinue.setOnClickListener(v -> submitFeedback());
        bi.btnEnd.setOnClickListener(v -> endFeedback());

        // Handling the visibility of progress bar or other elements dynamically
        bi.pBar.setVisibility(View.VISIBLE); // example to show progress bar
    }

    // Method for handling submit button click
    private void submitFeedback() {
        if (!formValidationEnd()) return;
        Toast.makeText(this, "Feedback submited successfully!", Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean formValidationEnd() {
        return Validator.emptyCheckingContainer(this, bi.secGrpA);
    }

    // Method for handling continue button click
    private void continueFeedback() {
        // You can navigate or perform actions based on the continue button click
        Toast.makeText(this, "Continue button clicked", Toast.LENGTH_SHORT).show();
    }

    // Method for handling back button click
    private void endFeedback() {
        // You can handle going back to the previous screen
        finish(); // Finish the current activity
    }
}
