package edu.aku.hassannaqvi.fitlife.core;

import static edu.aku.hassannaqvi.fitlife.database.DatabaseHelper.DATABASE_NAME;
import static edu.aku.hassannaqvi.fitlife.database.DatabaseHelper.DATABASE_PASSWORD;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.scottyab.rootbeer.RootBeer;

import net.zetetic.database.sqlcipher.SQLiteDatabase;

import org.json.JSONArray;

import java.io.File;
import java.util.List;
import java.util.Locale;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.core.location.LocationService;
import edu.aku.hassannaqvi.fitlife.models.Tests;
import edu.aku.hassannaqvi.fitlife.models.Users;
import edu.aku.hassannaqvi.fitlife.ui.LoginActivity;
import edu.aku.hassannaqvi.fitlife.ui.PeriodicWorkerHelper;


public class MainApp extends Application implements LifecycleObserver {

    public static final String PROJECT_NAME = "room_database";
    public static final String DIST_ID = null;
    public static final String SYNC_LOGIN = "sync_login";
    public static final String _IP = "https://vhds.aku.edu";// .LIVE server
    // public static final String _IP = "https://cls-pae-fp51764";// .TEST server
    // public static final String _IP = "http://f49461:8080/prosystem";// .TEST server
    //public static final String _IP = "http://43.245.131.159:8080";// .TEST server
    public static final String _HOST_URL = MainApp._IP + "/eshepp/api/";// .TEST server;
    public static final String _PHOTO_UPLOAD_URL = _HOST_URL + "uploads.php";
    public static final String _SERVER_URL = "syncgcm.php";
    public static final String _REGISTER_USER_URL = "regUser.php";
    public static final String _USER_URL = "resetpassword.php";
    public static final String _SERVER_GET_URL = "getDatagcm.php";
    public static final String _UPDATE_URL = MainApp._IP + "/room_database/app/";
    public static final String _APP_FOLDER = "../app";
    public static final String _EMPTY_ = "";
    public static final long INACTIVITY_TIMEOUT =  20 * 60 * 1000; // 2 minutes
    private static final String TAG = "MainApp";
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    public static int TRATS = 8;
    public static String IBAHC = "";
    //COUNTRIES
    public static int URDU = 1;
    public static File sdDir;
    public static String selectedTab;
    public static String selectedArea;
    public static String[] clusterInfo;
    public static int maxStructure;
    public static int hhid;
    public static int streetCount;
    public static ArrayAdapter<String> streetAdapter;
    //public static Samples samples;
    public static String[] downloadData;
    public static Tests tests;
    public static int preScore = 0;
    public static int postScore = 0;
    public static String DeviceURL = "devices.php";
    public static AppInfo appInfo;
    public static Users user;
    public static Boolean admin = false;
    public static List<JSONArray> uploadData;
    public static List<JSONArray> uploadDataPeriodic;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences sharedPref;
    public static String deviceid;
    public static boolean permissionCheck = false;
    public static boolean lhwComplete = false;
    public static String selectedLHW;
    public static String selectedHousehold;
    public static int memberCount = 0;
    public static int selectedMember;
    public static int entryType = 0;
    public static int childCount = 0;
    public static String selectedMWRA;
    public static int selectedChild;
    public static String selectedChildName = "";
    public static int memberCountComplete;
    public static boolean memberComplete;
    public static boolean hhheadSelected = false;
    public static boolean superuser;
    public static String selectedDistrict = "";
    public static String selectedTehsil = "";
    public static String selectedUC = "";
    public static int selectedLanguage = 0;
    public static boolean langRTL = false;
    public static int ageOfIndexChild;
    public static List<Integer> childCompleted;
    public static boolean householdChecked = false;
    public static long preAgeInMonths;
    public static NoMenuEditText noMenuEditText;
    public static int incompCount;
    public static int memberCountInomplete;
    public static String structureType = "";
    public static String selectedDistrictnName;
    public static String civilID;
    public static String civilID2;
    public static int maxstreet;
    public static int totalFloors = 0;
    public static int currentFloor = 1;
    public static int currentApartment = 1;
    public static int sessionid;
    public static String sessionName;
    public static String sessionObj;
    public static String videoID;

    protected static LocationManager locationManager;
    private Handler inactivityHandler;
    private Runnable inactivityCallback;
    private CountDownTimer countDownTimer;
    private ToneGenerator toneGen1;


    public static void hideSystemUI(View decorView) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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

    public static String getDeviceId(Context context) {
        String deviceId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
           /* final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }*/
        }
        return "deviceId";
    }

    public static String kishGrid(int Hh_Sno, int totalMwra) {

        // GENERATE RANDOM
        // int household  = 1 + (int)(Math.random() * ((10 - 1) + 1));
        // int eligibles = 1 + (int)(Math.random() * ((8 - 1) + 1));

        int household = Math.min(Hh_Sno, 9);
        int eligibles = Math.min(totalMwra, 8);

        int[][] grid = {
                // Eligible People ===>>
                //1- 2- 3- 4- 5- 6- 7- 8
                {1, 2, 1, 2, 5, 4, 3, 2},  // 0 - Household (10th)
                {1, 1, 1, 1, 1, 1, 1, 1},  // 1 - Household (1st)
                {1, 2, 2, 2, 2, 2, 2, 2},  //         ||
                {1, 1, 3, 3, 3, 3, 3, 3},  //         ||
                {1, 2, 1, 4, 4, 4, 4, 4},  //         ||
                {1, 1, 2, 1, 5, 5, 5, 5},  //        \\//
                {1, 2, 3, 2, 1, 6, 6, 6},  //         \/
                {1, 1, 1, 3, 2, 1, 7, 7},  //
                {1, 2, 2, 4, 3, 2, 1, 8},  // 8 - Household (8th)
                {1, 1, 3, 1, 4, 3, 2, 1},  // 9 - Household (9th)
        };


        // System.out.println(household+"-"+eligibles);
        // System.out.println(grid[household-1][eligibles-1]);

        /**
         *  0 household mean 0 index
         *  1 eligibles means eligibles-1 index
         *
         *  return Index of MWRAList (SNO-1)
         */
        return String.valueOf((grid[household][eligibles - 1]) - 1);
    }

    public static Boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    public static void setLocale(Context context) {
        String lang;
        String country;

        switch (MainApp.selectedLanguage) {
            case 1:
                lang = "ur";
                country = "PK";
                MainApp.langRTL = true;
                break;

            case 2:
                lang = "sd";
                country = "PK";
                MainApp.langRTL = true;
                break;

            default:
                lang = "en";
                country = "US";
                MainApp.langRTL = false;

        }
        Log.d(TAG, "changeLanguage: " + lang + "-" + country);

        final Locale locale = new Locale(lang, country);
        Locale.setDefault(locale);
        final Configuration config = new Configuration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        final Resources res = context.getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());
        Toast.makeText(context, lang + "_" + country, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Create a SplashScreenViewProvider with the logo image
     /*   SplashScreenViewProvider splashScreenViewProvider = new SplashScreenViewProvider(this) {
            public View createSplashScreenView(Bundle savedInstanceState) {
                ImageView logoImageView = new ImageView(getApplicationContext());
                logoImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.wellness_health_care)); // Replace with your logo image
                logoImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                return logoImageView;
            }
        };

        // Initialize the splash screen with the SplashScreenViewProvider
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this, splashScreenViewProvider);

        // Set the splash screen animation duration (optional)
        splashScreen.setKeepVisibleCondition(duration -> duration < 3000); // Display for up to 3 seconds
    }*/
        RootBeer rootBeer = new RootBeer(this);
        if (rootBeer.isRooted()) {
            android.os.Process.killProcess(android.os.Process.myPid());
            throw new RuntimeException("This is a crash");

            //System.exit(1);
        }

        //SQLiteDatabase.loadLibs(this);
        System.loadLibrary("sqlcipher");

        //Initiate DateTime
        //Initializ App info
        appInfo = new AppInfo(this);
        sharedPref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPref.edit();
        deviceid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new GPSLocationListener(this) // Implement this class from code

        );

        initSecure();

        // Create an instance of PeriodicWorkerHelper
        PeriodicWorkerHelper periodicWorkerHelper = new PeriodicWorkerHelper(getApplicationContext());

        // Call the ProcessStart() method to start the worker
        Log.d(TAG, "onCreate (periodicWorkerHelper): Starting...");
        periodicWorkerHelper.ProcessStart();
        inactivityHandler = new Handler();
        inactivityCallback = new Runnable() {
            @Override
            public void run() {
                logoutUser();
            }
        };

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 50);


        resetInactivityTimer();

    }

    private void initSecure() {
        // Initialize SQLCipher library
        File databaseFile = getDatabasePath(DATABASE_NAME);
       /* databaseFile.mkdirs();
        databaseFile.delete();*/
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), DATABASE_PASSWORD, null, null);
        // Prepare encryption KEY
        ApplicationInfo ai = null;
        // Start the location service
        startService(new Intent(this, LocationService.class));
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            //TRATS = bundle.getInt("YEK_TRATS");
            //IBAHC = bundle.getString("YEK_REVRES").substring(TRATS, TRATS + 16);
            IBAHC = bundle.getString("YEK_REVRES");
            Log.d(TAG, "onCreate: YEK_REVRES = " + IBAHC);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        resetInactivityTimer();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        inactivityHandler.removeCallbacks(inactivityCallback);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void resetInactivityTimer() {
        if (inactivityCallback != null) {
            inactivityHandler.removeCallbacks(inactivityCallback);
            inactivityHandler.postDelayed(inactivityCallback, INACTIVITY_TIMEOUT);
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(INACTIVITY_TIMEOUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Example functionality for `onTick`
                if ((millisUntilFinished / 1000) < 14) {
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                }
                // Update UI if necessary, for example:
                // bi.timeLeft.setText((millisUntilFinished / 1000) + " secs left");
            }

            @Override
            public void onFinish() {
                // Optionally handle timer finish here
            }
        };

        countDownTimer.start();
    }

    private void logoutUser() {
        // Implement your logout logic here
        // For example, clearing user data and redirecting to the login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //  startActivity(intent);
        Log.d(TAG, "onFinish: Timer depleted");

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // Stop the location service
        stopService(new Intent(this, LocationService.class));
    }
}
