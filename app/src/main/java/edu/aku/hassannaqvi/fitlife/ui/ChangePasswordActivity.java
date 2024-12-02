package edu.aku.hassannaqvi.fitlife.ui;

import static edu.aku.hassannaqvi.fitlife.core.UserAuth.checkPassword;
import static edu.aku.hassannaqvi.fitlife.core.UserAuth.generatePassword;

import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.CipherSecure;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.core.UserAuth;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityChangePasswordBinding;
import edu.aku.hassannaqvi.fitlife.workers.UserWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";
    ActivityChangePasswordBinding bi;
    private DatabaseHelper db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApp) getApplication()).resetInactivityTimer();
        bi = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        db = MainApp.appInfo.dbHelper;

    }

    public void onShowPasswordClick(View view) {
        //TODO implement
        EditText p = bi.passwordOld;
        if (view.getId() == R.id.showPasswordOld) {
            p = bi.passwordOld;
        } else if (view.getId() == R.id.showPassword1) {
            p = bi.password1;
        } else if (view.getId() == R.id.showPassword2) {
            p = bi.password2;
        }

/*        if (view.getId() == bi.showPassword1.getId()) {
            p = bi.password1;
        } else {
            p = bi.password2;
        }*/

        if (p.getTransformationMethod() == null) {
            p.setTransformationMethod(new PasswordTransformationMethod());
            p.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_close, 0, 0, 0);
        } else {
            p.setTransformationMethod(null);
            p.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_open, 0, 0, 0);
        }
    }


    public void attemptReset(View view) {

        if (!formValidation()) return;


        bi.pBarUser.setVisibility(View.VISIBLE);
        bi.btnResetPassword.setVisibility(View.GONE);

        WorkManager workManager = WorkManager.getInstance(this);

        try {
            String hashedPassword = generatePassword(bi.password2.getText().toString(), null);

            Data data = new Data.Builder()
                    .putString("newPassword", hashedPassword)
                    //    .putString("data", uploadData.get(i).toString())

                    //.putString("columns", "_id, sysdate")
                    // .putString("where", where)
                    .build();

            final OneTimeWorkRequest workRequest1 = new OneTimeWorkRequest.Builder(UserWorker.class).setInputData(data).build();
            workManager.enqueue(workRequest1);

            workManager.getWorkInfoByIdLiveData(workRequest1.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(@Nullable WorkInfo workInfo) {


                            //String progress = workInfo.getProgress().getString("progress");
                            //bi.wmError.setText("Progress: " + progress);
                            Log.d(TAG, "onChanged: " + workInfo.getState());

                            if (workInfo.getState() != null &&
                                    workInfo.getState() == WorkInfo.State.SUCCEEDED) {

                                Log.d(TAG, "onChanged: SUCCEEDED");
                                db.updatePassword(hashedPassword);

                                //Displaying the status into TextView
                                //mTextView1.append("\n" + workInfo.getState().name());
                                bi.pBarUser.setVisibility(View.GONE);

                                String message = workInfo.getOutputData().getString("message");
                                try {
                                    JSONArray json = new JSONArray(message);

                                    message = new JSONObject(json.getString(0)).getString("message");
                                    String error = new JSONObject(json.getString(0)).getString("error");
                                    bi.txtMessage.setText(message);
                                    bi.txtMessage.setVisibility(View.VISIBLE);
                                    bi.txtMessage.setTextColor(!error.equals("1") ? getResources().getColor(R.color.green) : getResources().getColor(R.color.redDark));

                                    if (!error.equals("1")) {
                                        new Handler().postDelayed(() -> finish(), 3500);
                                    }
                          /*  StringBuilder sSyncedError = new StringBuilder();
                            JSONObject jsonObject;
                            try {

                                JSONArray json = new JSONArray(message);
                                allWords.clear();
                                for (int i = 0; i < json.length(); i++) {
                                    Words words = new Words();
                                    if (!db.WordExists(new JSONObject(json.getString(i)).getString("id"))) {

                                        db.syncWords(new JSONObject(json.getString(i)));
                                        displayNotification("VocApp", "New Word: " + new JSONObject(json.getString(i)).getString("word"), new JSONObject(json.getString(i)).getInt("id"));

                                    }
                                    allWords.add(words.Hydrate(new JSONObject(json.getString(i))));
                                    i = json.length() + 1;

                                }

                            } catch (JSONException e) {
                                bi.wmError.setText("JSON Error: " + message);
                                bi.wmError.setVisibility(View.VISIBLE);
                                Log.d("JSON Error", "onChanged: " + message);
                                e.printStackTrace();


                            }
                            //fupsAdapter = new FollowupsAdapter((List<FollowUps>) allWords, FollowUpsList.this);
                            //recyclerView.setAdapter(fupsAdapter);

                            //bi.s3.setText(allWords.get(0).getSentcol3());
                            try {
                                allWords = db.getAllWords(bmFlag);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(TAG, "getWords: " + e.getMessage());
                            }*/

                                    /*   updateCard();
                                     */
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ChangePasswordActivity.this, "JSONException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            //mTextView1.append("\n" + workInfo.getState().name());
                            if (workInfo.getState() != null &&
                                    workInfo.getState() == WorkInfo.State.FAILED) {
                                Log.d(TAG, "onChanged: FAILED");
                                bi.pBarUser.setVisibility(View.GONE);

                                String message = workInfo.getOutputData().getString("error");

                                bi.txtMessage.setText(message);
                                bi.txtMessage.setVisibility(View.VISIBLE);
                                bi.txtMessage.setTextColor(getResources().getColor(R.color.redDark));
                                bi.btnResetPassword.setVisibility(View.VISIBLE);
     /*                       bi.wmError.setText(message);
                            bi.wmError.setVisibility(View.VISIBLE);*/

                            }
                        }
                    });


            Log.d(TAG, "attemptReset: " + CipherSecure.encryptGCM(bi.password2.getText().toString()));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(this, "NoSuchAlgorithmException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            Toast.makeText(this, "InvalidKeySpecException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Toast.makeText(this, "InvalidKeyException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            Toast.makeText(this, "InvalidAlgorithmParameterException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            Toast.makeText(this, "NoSuchPaddingException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (BadPaddingException e) {
            e.printStackTrace();
            Toast.makeText(this, "BadPaddingException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            Toast.makeText(this, "IllegalBlockSizeException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private boolean formValidation() {
        // return Validator.emptyCheckingContainer(this, bi.GrpName);


/*        String hashedPasswordOld = "";
        try {
            hashedPasswordOld = generatePassword(bi.passwordOld.getText().toString(), null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "formValidation(oldHash): "+MainApp.user.getPassword());
        Log.d(TAG, "formValidation(newHash): "+hashedPasswordOld);
        if (!MainApp.user.getPassword().equals(hashedPasswordOld)) {
            bi.passwordOld.setError("Old password do not match.");
            Toast.makeText(this, "Old password do not match.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            bi.passwordOld.setError(null);

        }*/

        try {
            if (!checkPassword(bi.passwordOld.getText().toString(), MainApp.user.getPassword())) {
                bi.passwordOld.setError("Old password do not match.");
                Toast.makeText(this, "Old password do not match.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                bi.passwordOld.setError(null);

            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(this, "NoSuchAlgorithmException(UserAuth.checkPassword): " + e.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            Toast.makeText(this, "InvalidKeySpecException(UserAuth.checkPassword): " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        if (bi.password1.getText().toString().length() < 8) {
            bi.password1.setError("Password should be at least 8 characters long.");
            Toast.makeText(this, "Passwords is empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            bi.password1.setError(null);

        }


        if (!bi.password2.getText().toString().equals(bi.password1.getText().toString())) {
            bi.password2.setError("Passwords do not match.");
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            bi.password2.setError(null);

        }

        if (!isValidPassword(bi.password1.getText().toString())) {
            //   bi.password1.setError("Passwords do not match.");
            Toast.makeText(this, "Password do not match criteria", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            bi.password1.setError(null);

        }

        return true;
    }

    public boolean isValidPassword(String password) {
        boolean isValid = true;

        // Check not same as previous
        try {
            if (UserAuth.checkPassword(password, MainApp.user.getPassword())) {
                System.out.println("Password is same as previous.");
                bi.password1.setError("Password must not be same as previous.");
                isValid = false;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        // Check password length
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters in length.");
            bi.password1.setError("Password must be at least 8 characters in length.");
            isValid = false;
        }

        // Check special characters
        String upperCaseChars = "(.*[a-zA-Z].*)";
        if (!password.matches(upperCaseChars)) {
            System.out.println("Password must have atleast one alphabet");
            bi.password1.setError("Password must have atleast one alphabet");
            isValid = false;
        }
 /*       String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars ))
        {
            System.out.println("Password must have atleast one lowercase character");
            isValid = false;
        }*/

        // Check number
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            System.out.println("Password must have atleast one number");
            bi.password1.setError("Password must have atleast one number");
            isValid = false;
        }

        // Check same username and password
        if (password.matches(MainApp.user.getUserName())) {
            System.out.println("Username and Password cannot be same");
            bi.password1.setError("Username and Password cannot be same");
            isValid = false;
        }

        /*String specialChars = "(.*[@,#,$,%].*$)";
        if (!password.matches(specialChars ))
        {
            System.out.println("Password must have atleast one special character among @#$%");
            isValid = false;
        }*/
        return isValid;
    }
}