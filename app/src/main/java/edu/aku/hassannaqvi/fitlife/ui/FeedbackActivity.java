package edu.aku.hassannaqvi.fitlife.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityFeedbackBinding;

import android.view.View;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;

public class FeedbackActivity extends AppCompatActivity {

    private ActivityFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);

        // Setting up the toolbar
        setSupportActionBar(binding.toolbar);

        // You can also access your views through the binding object
        binding.btnContinue.setOnClickListener(v -> submitFeedback());
        binding.btnEnd.setOnClickListener(v -> endFeedback());

        // Handling the visibility of progress bar or other elements dynamically
        binding.pBar.setVisibility(View.VISIBLE); // example to show progress bar
    }

    // Method for handling submit button click
    private void submitFeedback() {
        String name = binding.inputName.getText().toString();
        String email = binding.inputEmail.getText().toString();
        String feedback = binding.inputFeedback.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !feedback.isEmpty()) {
            // Handle the submission logic (e.g., send data to the server)
            Toast.makeText(this, "Feedback Submitted", Toast.LENGTH_SHORT).show();
            finish(); // Finish the current activity
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
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
