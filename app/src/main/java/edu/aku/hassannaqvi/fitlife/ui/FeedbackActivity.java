package edu.aku.hassannaqvi.fitlife.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityFeedbackBinding;
import edu.aku.hassannaqvi.fitlife.models.Feedback;

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
        MainApp.feedback = new Feedback();
        bi.setFeedback(MainApp.feedback);

        // You can also access your views through the binding object
        bi.btnContinue.setOnClickListener(v -> submitFeedback());
        bi.btnEnd.setOnClickListener(v -> endFeedback());

        // Handling the visibility of progress bar or other elements dynamically
        bi.pBar.setVisibility(View.VISIBLE); // example to show progress bar
    }

    // Method for handling submit button click
    private void submitFeedback() {
        if (!formValidationEnd()) return;
        sendFeedbackByEmail();
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

    public void sendFeedbackByEmail() {
        Feedback feedback = MainApp.feedback;
        String subject = "eShepp NCD Feedback Form - " + feedback.getUserName() + " (" + feedback.getSysDate() + ")";
        String message = buildFeedbackMessage();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:ehealth.ncd@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            this.startActivity(Intent.createChooser(emailIntent, "Send Feedback..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }

    private String buildFeedbackMessage() {
        Feedback f = MainApp.feedback;
        return "Feedback Submitted By: " + MainApp.user.getUserName() + "\n" +
                "Project Name: " + f.getProjectName() + "\n" +
                "User: " + f.getUserName() + "\n" +
                "Date: " + f.getSysDate() + "\n\n" +

                "--- Section A ---\n" +
                this.getString(R.string.fba01) + ": " + f.getFba01() + "\n" +
                this.getString(R.string.fba02) + ": " + f.getFba02() + "\n" +
                this.getString(R.string.fba03) + ": " + f.getFba03() + "\n\n" +

                "--- Section B ---\n" +
                this.getString(R.string.fbb01) + ": " + f.getFbb01() + "\n" +
                this.getString(R.string.fbb02) + ": " + f.getFbb02() + "\n" +
                this.getString(R.string.fbb03) + ": " + f.getFbb03() + "\n\n" +

                "--- Section C ---\n" +
                this.getString(R.string.fbc01) + ": " + f.getFbc01() + "\n" +
                this.getString(R.string.fbc02) + ": " + f.getFbc02() + "\n" +
                this.getString(R.string.fbc03) + ": " + f.getFbc03() + "\n\n" +

                "--- Metadata ---\n" +
                "App Version: " + f.getAppver();
    }

}
