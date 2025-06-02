package edu.aku.hassannaqvi.fitlife.ui;

import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.buildSslSocketFactory;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;

import javax.net.ssl.HttpsURLConnection;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.CipherSecure;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityRegisterBinding;
import edu.aku.hassannaqvi.fitlife.models.NewUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding bi;
    private NewUser newUser;
    private HttpsURLConnection urlConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_register);

        newUser = new NewUser();
        bi.setUser(newUser);

        // Initialize the toolbar
        setSupportActionBar(bi.toolbar);

        // Enable the Up button (back button)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true); // Show the home icon

        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Your logic here
                finish(); // or popBackStack() if using fragments
            }
        });

        bi.passwordInputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        bi.confirmPasswordInputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        bi.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    try {
                        JSONArray userJson = new JSONArray();
                        userJson.put(newUser.toJSONObject());
                        sendUserDataToServer(userJson);
                    } catch (JSONException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button click
            onBackPressed(); // This triggers the OnBackPressedDispatcher
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateForm() {
        Log.d(TAG, "validateForm: validating form");

        // Full Name Validation
        if (newUser.getFullName().isEmpty()) {
            bi.fullNameInputLayout.setError("Full name is required");
            bi.fullNameInputLayout.requestFocus();
            showSnackbar("Full name is required", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the full name input
                    bi.fullName.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Full name is required");
            return false;
        } else {
            bi.fullNameInputLayout.setError(null);
        }

        // Age Validation
        if (newUser.getAge().isEmpty()) {
            bi.ageInputLayout.setError("Age is required");
            bi.age.requestFocus();
            showSnackbar("Age is required", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the age input
                    bi.age.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Age is required");
            return false;
        } else {
            bi.ageInputLayout.setError(null);
        }

        // Gender Validation
        if (newUser.getGender().isEmpty()) {
            showSnackbar("Please select your gender", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Take the user to the gender selection
                    // Assuming there's a RadioGroup for gender
                    // You may focus it or scroll it into view
                    bi.genderGroup.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Gender is required");
            return false;
        }

        // Mobile Number Validation
        if (newUser.getMobileNumber().isEmpty() || !Patterns.PHONE.matcher(newUser.getMobileNumber()).matches()) {
            bi.mobileNumberInputLayout.setError("Enter a valid mobile number (with country code if necessary)");
            bi.mobileNumber.requestFocus();
            showSnackbar("Valid mobile number is required", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the mobile number input
                    bi.mobileNumber.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Valid mobile number is required");
            return false;
        } else {
            bi.mobileNumberInputLayout.setError(null);
        }

        // Email Validation
        if (newUser.getEmail().isEmpty()) {
            bi.emailInputLayout.setError("Email is required (e.g., name@example.com)");
            bi.email.requestFocus();
            showSnackbar("Email is required", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the email input
                    bi.email.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(newUser.getEmail()).matches()) {
            bi.emailInputLayout.setError("Please enter a valid email address (Check format: name@example.com)");
            bi.email.requestFocus();
            showSnackbar("Valid email address is required", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the email input
                    bi.email.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Valid email is required");
            return false;
        } else {
            bi.emailInputLayout.setError(null);
        }

        // Username Validation
        if (newUser.getUsername().isEmpty() || newUser.getUsername().length() < 8) {
            bi.usernameInputLayout.setError("Username must be at least 8 characters (Choose a unique one)");
            bi.username.requestFocus();
            showSnackbar("Username must be at least 8 characters", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the username input
                    bi.username.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Username too short");
            return false;
        } else {
            bi.usernameInputLayout.setError(null);
        }

        // Password Validation
        String password = newUser.getPassword();

        if (password.isEmpty() || password.length() < 8) {
            bi.passwordInputLayout.setError("Password must be at least 8 characters long (Use a strong password)");
            bi.password.requestFocus();
            showSnackbar("Password must be at least 8 characters long", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the password input
                    bi.password.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Password too short");
            return false;
        }

        // Password should contain at least one letter
        if (!password.matches(".*[a-zA-Z].*")) {
            bi.passwordInputLayout.setError("Password must include at least one letter");
            bi.password.requestFocus();
            showSnackbar("Password must include at least one letter", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the password input
                    bi.password.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Password lacks letters");
            return false;
        }

        // Password should contain at least one number
        if (!password.matches(".*[0-9].*")) {
            bi.passwordInputLayout.setError("Password must include at least one number");
            bi.password.requestFocus();
            showSnackbar("Password must include at least one number", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the password input
                    bi.password.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Password lacks numbers");
            return false;
        }

        // Password cannot be the same as the username
        if (password.equals(newUser.getUsername())) {
            bi.passwordInputLayout.setError("Username and password cannot be the same");
            bi.password.requestFocus();
            showSnackbar("Username and password cannot be the same", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the password input
                    bi.password.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Password same as username");
            return false;
        }

        bi.passwordInputLayout.setError(null);

        // Confirm Password Validation
        if (!password.equals(bi.confirmPassword.getText().toString())) {
            bi.confirmPasswordInputLayout.setError("Passwords do not match (Re-enter your password)");
            bi.confirmPassword.requestFocus();
            showSnackbar("Passwords do not match", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the confirm password input
                    bi.confirmPassword.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Passwords mismatch");
            return false;
        } else {
            bi.confirmPasswordInputLayout.setError(null);
        }

        // Consent Checkbox Validation
        if (!bi.consentCheckbox.isChecked()) {
            showSnackbar("You must agree to the terms and conditions", "Fix", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Focus back to the consent checkbox
                    bi.consentCheckbox.requestFocus();
                }
            });
            Log.d(TAG, "validateForm(Error): Terms not accepted");
            return false;
        }

        return true;
    }

    // Helper method for showing actionable Snackbars
    private void showSnackbar(String message, String actionText, View.OnClickListener action) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction(actionText, action)
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .show();
    }



    private void sendUserDataToServer(JSONArray userJson) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(MainApp._HOST_URL + MainApp._REGISTER_USER_URL);
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    setupHttpsConnection(urlConnection);

                    Log.d(TAG, "sendUserDataToServer(plaintext): " + userJson);
                    String encryptedData = CipherSecure.encryptGCM(userJson.toString());
                    Log.d(TAG, "sendUserDataToServer(encrypted): " + encryptedData);
                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    wr.writeBytes(encryptedData);
                    wr.flush();
                    wr.close();

                    handleServerResponse();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void setupHttpsConnection(HttpsURLConnection connection) throws CertificateException, IOException {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca;
        try (InputStream caInput = getResources().openRawResource(R.raw.star_aku_edu_2025)) {
            ca = cf.generateCertificate(caInput);
        }

        connection.setSSLSocketFactory(buildSslSocketFactory(this));
    }

    private void handleServerResponse() throws IOException {
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            try {
                Log.d(TAG, "handleServerResponse(Encrypted): " + result);
                String decryptedResponse = CipherSecure.decryptGCM(result.toString());
                Log.d(TAG, "handleServerResponse(Decrypted): " + decryptedResponse);
                JSONArray responseArray = new JSONArray(decryptedResponse);
                JSONObject responseObject = responseArray.getJSONObject(0);

                int status = responseObject.optInt("status", 0);
                int error = responseObject.optInt("error", 0);
                String message = responseObject.optString("message", "No message");

                runOnUiThread(() -> {
                    if (error == 1 || status == 0) {
                        showErrorDialog("Registration Error", message);
                    } else {
                        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> showErrorDialog("Response Error", "Error decrypting or parsing server response."));
            }
        } else {
            runOnUiThread(() -> showErrorDialog("Network Error", "Registration failed with status: " + responseCode));
        }
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }


}