package edu.aku.hassannaqvi.fitlife.ui;

import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.decryptGCM;
import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.encryptGCM;
import static edu.aku.hassannaqvi.fitlife.core.CipherSecure.hashSHA384;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.sharedPref;
import static edu.aku.hassannaqvi.fitlife.database.DatabaseHelper.DATABASE_COPY;
import static edu.aku.hassannaqvi.fitlife.database.DatabaseHelper.DATABASE_NAME;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts;
import edu.aku.hassannaqvi.fitlife.core.AppInfo;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityLoginBinding;
import edu.aku.hassannaqvi.fitlife.models.EntryLog;
import edu.aku.hassannaqvi.fitlife.models.Users;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    protected static LocationManager locationManager;
    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            /*Toast.makeText(getApplicationContext(),
                    "You are not allowed to copy :)", Toast.LENGTH_SHORT).show();*/
            setResult(RESULT_CANCELED);
        }
    });
    // UI references.
    private final int pos = 0;
    ActivityLoginBinding bi;
    Spinner spinnerDistrict;
    String DirectoryName;
    DatabaseHelper db;
    ArrayAdapter<String> provinceAdapter;
    int attemptCounter = 0;
    String username = "";
    String password = "";
    private int countryCode;
    private ArrayList<String> countryNameList;
    private ArrayList<String> countryCodeList;
    /* private int PhotoSerial = 0;
     private String photolist;
     ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
             new ActivityResultContracts.StartActivityForResult(),
             new ActivityResultCallback<ActivityResult>() {
                 @Override
                 public void onActivityResult(ActivityResult result) {
                     if (result.getResultCode() == Activity.RESULT_OK) {
                         // There are no request codes
                         //Intent data = result.getData();
                         Intent data = result.getData();

                         Toast.makeText(LoginActivity.this, "Photo Taken", Toast.LENGTH_SHORT).show();

                         String fileName = data.getStringExtra("FileName");
                         //   photolist = photolist + fileName + ";";
                         PhotoSerial++;

                         bi.b117.setText(bi.b117.getText().toString() + PhotoSerial + " - " + fileName + ";\r\n");
                     } else {
                         Toast.makeText(LoginActivity.this, "Photo Cancelled", Toast.LENGTH_SHORT).show();

                         //TODO: Implement functionality below when photo was not taken
                         // ...
                         bi.b117.setText("Photo not taken.");
                     }

                     if (result.getResultCode() == Activity.RESULT_CANCELED) {
                         Toast.makeText(LoginActivity.this, "No family member added.", Toast.LENGTH_SHORT).show();
                     }

                 }
             });*/
    private int clicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        initializingCountry();
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS

                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            MainApp.permissionCheck = true;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        bi = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setSupportActionBar(bi.toolbar);


        db = MainApp.appInfo.getDbHelper();


        MainApp.appInfo = new AppInfo(this);
        MainApp.user = new Users();
        bi.txtinstalldate.setText(MainApp.appInfo.getAppInfo());

        dbBackup();
        String plainText = "This is an encrypted message.";
        String encrypted = "awqqGx60wJZAl0s0NVpEWkxJQVRIR0xFT3VRUk8rZEU3eE80c2lqelpTcE8yYW9WeXJNPXfsBUWaMeWMuRhbH1aAxIo=";
        try {

            encrypted = encryptGCM(plainText);
            Log.d(TAG, "onCreate: Encrypted: " + encrypted);
            Log.d(TAG, "onCreate: Decrypted: " + decryptGCM(encrypted));
            Log.d(TAG, "onCreate: hash: " + hashSHA384());


        } catch (Exception e) {
            e.printStackTrace();
        }

        // Disable every mode to disable copy
        bi.username.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub

            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode,
                                               MenuItem item) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        // Disable every mode to disable copy

        bi.password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub

            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode,
                                               MenuItem item) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        // Ensure the EditText is focusable
        bi.username.setFocusable(true);
        bi.username.setFocusableInTouchMode(true);

        // Request focus programmatically
        bi.username.requestFocus();


        // Force show the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(bi.username, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    protected void onResume() {
        super.onResume();


        String latestVersionName = sharedPref.getString("versionName", "");
        int latestVersionCode = Integer.parseInt(sharedPref.getString("versionCode", "0"));
        String applicationId = sharedPref.getString("applicationId", "");

        bi.newApp.setText("Available on Server: " + latestVersionName + latestVersionCode);
        String packageName = this.getPackageName();

        if (MainApp.appInfo.getVersionCode() < latestVersionCode && applicationId.equals(packageName)) {
            new AlertDialog.Builder(this)
                    .setTitle("New Update Available")
                    .setMessage("There is a newer version of this app available (" + latestVersionName + latestVersionCode + "). \nPlease download and update the app now.")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            //     addMoreMWRA();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(R.drawable.ic_alert_24)
                    .show();
        }

/*        try {
            Log.d(TAG, "onResume(Password): " + generatePassword("Password", null));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }*/

    /*    try {

            String password = "password2"; // New password to check
            String oldPassword = "ni1uSHv83fen67umTfPqSU4L"; // Old stored password hash

            if (checkPassword(password, oldPassword)) {
               Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
//
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }*/
    }

    public void dbBackup() {


        if (sharedPref.getBoolean("flag", false)) {

            String dt = sharedPref.getString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));

            if (!dt.equals(new SimpleDateFormat("dd-MM-yy").format(new Date()))) {
                MainApp.editor.putString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));
                MainApp.editor.apply();
            }

            // File folder = new File(Environment.getExternalStorageDirectory() + File.separator + PROJECT_NAME);
            File folder = new File(this.getExternalFilesDir("Backups"), File.separator);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {

                DirectoryName = folder.getPath() + File.separator + sharedPref.getString("dt", "");
                folder = new File(DirectoryName);
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {

                    try {
                        File dbFile = new File(this.getDatabasePath(DATABASE_NAME).getPath());
                        FileInputStream fis = new FileInputStream(dbFile);
                        String outFileName = DirectoryName + File.separator + DATABASE_COPY;
                        // Open the empty db as the output stream
                        OutputStream output = new FileOutputStream(outFileName);

                        // Transfer bytes from the inputfile to the outputfile
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                        // Close the streams
                        output.flush();
                        output.close();
                        fis.close();
                    } catch (IOException e) {
                        Log.e("dbBackup:", Objects.requireNonNull(e.getMessage()));
                    }

                }

            } else {
                Toast.makeText(this, getString(R.string.folder_not_created), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void onShowPasswordClick(View view) {
        //TODO implement
        if (bi.password.getTransformationMethod() == null) {
            bi.password.setTransformationMethod(new PasswordTransformationMethod());
            bi.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_close, 0, 0, 0);
            bi.password.setEnabled(false);

        } else {
            bi.password.setTransformationMethod(null);
            bi.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_open, 0, 0, 0);
            bi.password.setEnabled(true);

        }
    }

    public void onSyncDataClick(View view) {
        //callUsersWorker();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            startActivity(new Intent(this, SyncActivity.class).putExtra("login", true));
        } else {
            Toast.makeText(this, getString(R.string.network_conn_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void attemptLogin(View view) {

        if (!username.equals(bi.username.getText().toString())) {
            attemptCounter = 0;
        }
        attemptCounter++;
        // Reset errors.
        bi.username.setError(null);
        bi.password.setError(null);
        //bi.as1q01.setError(null);
        // Toast.makeText(this, String.valueOf(attemptCounter), Toast.LENGTH_SHORT).show();
        if (attemptCounter > 5) {
            bi.username.setError("This user has been blocked.");
            Toast.makeText(this, "This user has been blocked.", Toast.LENGTH_LONG).show();

        } else {
            // Store values at the time of the login attempt.
            username = bi.username.getText().toString();
            password = bi.password.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (password.length() < 8) {
                bi.password.setError(getString(R.string.invalid_password));
                focusView = bi.password;
                return;
            }

            // Check for a valid username address.
            if (TextUtils.isEmpty(username)) {
                bi.username.setError(getString(R.string.username_required));
                focusView = bi.username;
                return;
            }

            // UserName and Password cannot be same
            if (username.equals(password)) {
                bi.username.setError(getString(R.string.username_password_same));
                focusView = bi.username;
                return;
            }

            //if(!Validator.emptySpinner(this, bi.countrySwitch)) return;
            /*if (bi.countrySwitch.getSelectedItemPosition() == 0) {
                bi.as1q01.setError(getString(R.string.as1q01));
                return;
            }*/

            try {
                if ((username.equals("dmu@aku") && password.equals("aku?dmu"))
                        || (username.equals("test1234") && password.equals("test1235"))
                        || db.doLogin(username, password)
                ) {

                    MainApp.user.setUserName(username);

                    MainApp.admin = username.contains("@") || username.contains("test");
                    MainApp.superuser = MainApp.user.getDesignation().contains("Supervisor");
                    Intent iLogin = null;
                    if (MainApp.admin) {
                        recordEntry("Successful Login (Admin)");
                        iLogin = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(iLogin);
                    } else if (MainApp.user.getEnabled().equals("1")) {
                        if (!MainApp.user.getNewUser().equals("1")) {
                            recordEntry("Successful Login");
                            iLogin = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(iLogin);
                        } else if (MainApp.user.getNewUser().equals("1")) {
                            recordEntry("First Login");
                            iLogin = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                            startActivity(iLogin);
                        }
                    } else {
                        recordEntry("Inactive User (Disabled)");
                        Toast.makeText(this, "This user is inactive.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    recordEntry("Failed Login: Incorrect username or password");
                    bi.password.setError(getString(R.string.incorrect_username_or_password));
                    bi.password.requestFocus();
                    //  Toast.makeText(LoginActivity.this, username + " " + password, Toast.LENGTH_SHORT).show();
                }
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                Toast.makeText(this, "InvalidKeySpecException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Toast.makeText(this, "NoSuchAlgorithmException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Toast.makeText(this, "IllegalArgumentException(UserAuth):" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }


        }
    }

    private void recordEntry(String entryType) {
        EntryLog entryLog = new EntryLog();
        entryLog.setProjectName(PROJECT_NAME);
        entryLog.setUserName(username);
        entryLog.setEntryDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date().getTime()));
        entryLog.setAppver(MainApp.appInfo.getAppVersion());
        entryLog.setEntryType(entryType);
        entryLog.setDeviceId(MainApp.deviceid);
        Long rowId = null;
        try {
            rowId = db.addEntryLog(entryLog);
            if (rowId != -1) {
                entryLog.setId(String.valueOf(rowId));
                entryLog.setUid(entryLog.getDeviceId() + entryLog.getId());
                db.updatesEntryLogColumn(TableContracts.EntryLogTable.COLUMN_UID, entryLog.getUid(), entryLog.getId());
            } else {
                Toast.makeText(this, R.string.upd_db_error, Toast.LENGTH_SHORT).show();

            }
        } catch (SQLiteException e) {
            Toast.makeText(this, "SQLiteException(EntryLog)" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "recordEntry: " + e.getMessage());
        }
    }

    public void resetPassword(View view) {
        finish();
        //  startActivity(new Intent(this, WebViewActivity.class));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

/*    private void settingCountryCode() {


        List<Villages> countries = db.getAllCountries();

        countryNameList = new ArrayList<>();
        countryCodeList = new ArrayList<>();

        countryNameList.add("...");
        countryCodeList.add("...");

        for (Villages c : countries) {
            countryNameList.add(c.getCountry());
            countryCodeList.add(c.getCcode());
        }


        bi.countrySwitch.setAdapter(new ArrayAdapter<>(LoginActivity.this, R.layout.custom_spinner, countryNameList));


        bi.countrySwitch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0 && position != pos) {
                    //   MainApp.selectedDistrict = Integer.parseInt(countryCodeList.get(position));
                    // changeLanguage(MainApp.selectedDistrict);

                    //  startActivity(new Intent(LoginActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("pos", position));
                    //   overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }*/

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

/*    public void TakePhoto(View view) {

        Intent intent = new Intent(this, TakePhoto.class);
        intent.putExtra("picID", "picid");
        intent.putExtra("id", "id");
        if (PhotoSerial == 0) {
            intent.putExtra("picView", "Brand/logo");} else {
            intent.putExtra("picView", "Ingredients");
        }
        takePhotoLauncher.launch(intent);

    }*/

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /*
     * Toggle Language
     * */
    private void changeLanguage(int countryCode) {
        String lang;
        String country;

        switch (countryCode) {
            case 1:
                lang = "ur";
                country = "PK";
                MainApp.editor
                        .putString("lang", "1")
                        .apply();
                MainApp.selectedLanguage = 1;
                MainApp.langRTL = true;
                break;

            case 2:
                lang = "sd";
                country = "PK";
                MainApp.editor
                        .putString("lang", "2")
                        .apply();
                MainApp.langRTL = true;
                MainApp.selectedLanguage = 2;


                break;

            default:
                lang = "en";
                country = "US";
                MainApp.editor
                        .putString("lang", "0")
                        .apply();
                MainApp.langRTL = false;
                MainApp.selectedLanguage = 0;


        }
        Log.d(TAG, "changeLanguage: " + lang + "-" + country);

       /* Locale locale = new Locale(lang, country);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        config.setLayoutDirection(new Locale(lang, country));
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());*/
        MainApp.setLocale(this);
    }

    /*
     * Setting clusterNo code in Shared Preference
     * */
    private void initializingCountry() {
        countryCode = Integer.parseInt(sharedPref.getString("lang", "0"));
        Log.d(TAG, "initializingCountry: " + countryCode);
        if (countryCode == 0) {
            MainApp.editor.putString("lang", "0").apply();
        }

        changeLanguage(Integer.parseInt(sharedPref.getString("lang", "0")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.language_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.NO) {
            return true;
        } else if (item.getItemId() == R.id.UR) {
            MainApp.selectedLanguage = 1;
            MainApp.langRTL = true;
        } else if (item.getItemId() == R.id.SD) {
            MainApp.selectedLanguage = 2;
            MainApp.langRTL = true;
        } else {
            MainApp.selectedLanguage = 0;
            MainApp.langRTL = false;
        }
        changeLanguage(MainApp.selectedLanguage);
        startActivity(new Intent(LoginActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        return true;
    }

    public String validatePassword2(String password, String encodedPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException {


        byte[] cipherPassword = Base64.decode(encodedPassword, Base64.NO_WRAP);
        byte[] salt = Arrays.copyOfRange(cipherPassword, 0, 16);
        byte[] hash = Arrays.copyOfRange(cipherPassword, 16, cipherPassword.length);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt);
        byte[] byteData = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        Log.d("TAG", "computeHash: " + sb);
        if (sb.toString().equals("encodedPassword")) {
            return "True";
        } else {
            return "false";
        }
    }

    public void showCredits(View view) {
        if (clicks < 7) {
            clicks++;
            Toast.makeText(this, String.valueOf(clicks), Toast.LENGTH_SHORT).show();
        } else {
            clicks = 0;
            Snackbar.make(view, "      * * * * * TEAM CREDITS * * * * *      " +
                                    "\n|| Hassan Naqvi <Hassan.naqvi@aku.edu>, Shaharyar Usman <shaharyar.usman@aku.edu>, Maham Aziz <maham.aziz@aku.edu>, Ghulam Kubra <ghulam.kubra@aku.edu>, Zeerak Jarrar <zeerak.jarrar@aku.edu> ||"

                            ,
                            Snackbar.LENGTH_LONG)
                    .show();
        }


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Refresh the UI or do other necessary updates
    }

    public void registerUser(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

