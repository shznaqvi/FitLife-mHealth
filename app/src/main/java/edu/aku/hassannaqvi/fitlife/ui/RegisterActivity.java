package edu.aku.hassannaqvi.fitlife.ui;

import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.buildSslSocketFactory;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

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
    private ActivityRegisterBinding binding;
    private NewUser newUser;
    private HttpsURLConnection urlConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        newUser = new NewUser();
        binding.setUser(newUser);

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
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

    private boolean validateForm() {
        boolean valid = true;

        if (newUser.getFullName().isEmpty()) {
            binding.fullName.setError("Full name is required");
            valid = false;
        } else {
            binding.fullName.setError(null);
        }

        if (newUser.getAge().isEmpty()) {
            binding.age.setError("Age is required");
            valid = false;
        } else {
            binding.age.setError(null);
        }

        if (newUser.getGender().isEmpty()) {
            Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (newUser.getMobileNumber().isEmpty() || !Patterns.PHONE.matcher(newUser.getMobileNumber()).matches()) {
            binding.mobileNumber.setError("Valid mobile number is required");
            valid = false;
        } else {
            binding.mobileNumber.setError(null);
        }

        if (!newUser.getEmail().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(newUser.getEmail()).matches()) {
            binding.email.setError("Valid email is required");
            valid = false;
        } else {
            binding.email.setError(null);
        }

        if (newUser.getUsername().isEmpty() || newUser.getUsername().length() < 8) {
            binding.username.setError("Username must be at least 8 characters in length");
            valid = false;
        } else {
            binding.username.setError(null);
        }

        if (newUser.getPassword().isEmpty() || newUser.getPassword().length() < 8) {
            binding.password.setError("Password must be at least 8 characters in length");
            valid = false;
        } else {
            binding.password.setError(null);
        }

        if (!newUser.getPassword().matches(".*[a-zA-Z].*")) {
            binding.password.setError("Password must have at least one alphabet");
            valid = false;
        } else {
            binding.password.setError(null);
        }

        if (!newUser.getPassword().matches(".*[0-9].*")) {
            binding.password.setError("Password must have at least one number");
            valid = false;
        } else {
            binding.password.setError(null);
        }

        if (newUser.getPassword().equals(newUser.getUsername())) {
            binding.password.setError("Username and Password cannot be the same");
            valid = false;
        } else {
            binding.password.setError(null);
        }

        if (!newUser.getPassword().equals(binding.confirmPassword.getText().toString())) {
            binding.confirmPassword.setError("Passwords do not match");
            valid = false;
        } else {
            binding.confirmPassword.setError(null);
        }

        if (!binding.consentCheckbox.isChecked()) {
            Toast.makeText(this, "You must agree to the terms of use and data policy", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
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

                int status = responseObject.getInt("status");
                String message = responseObject.getString("message");

                runOnUiThread(() -> {
                    if (status == 0) {
                        Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error decrypting response", Toast.LENGTH_SHORT).show());
            }
        } else {
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration failed with status: " + responseCode, Toast.LENGTH_SHORT).show());
        }
    }
}