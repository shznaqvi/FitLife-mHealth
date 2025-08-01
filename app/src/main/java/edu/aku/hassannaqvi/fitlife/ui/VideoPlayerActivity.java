package edu.aku.hassannaqvi.fitlife.ui;

import static edu.aku.hassannaqvi.fitlife.core.MainApp.PROJECT_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.aku.hassannaqvi.fitlife.R;
import edu.aku.hassannaqvi.fitlife.contracts.TableContracts;
import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.database.DatabaseHelper;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityVideoPlayerBinding;
import edu.aku.hassannaqvi.fitlife.models.EntryLog;
import edu.aku.hassannaqvi.fitlife.ui.sections.PostTestActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";
    ActivityVideoPlayerBinding bi;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private FrameLayout fullscreenContainer;
    private static boolean isVideoFinished = false;
    private DatabaseHelper db;
    double videoDuration;
    private ViewGroup.LayoutParams params;
    private int widthYoutube;
    private Handler handler = new Handler();
    private VideoJavaScriptInterface vi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());

        db = MainApp.appInfo.dbHelper;

        // Set toolbar and retrieve intent extras
        setSupportActionBar(bi.toolbar);
        bi.sectionName.setText(MainApp.sessionName);
        bi.sectionObj.setText(MainApp.sessionObj);
        recordEntry("Session started: "+MainApp.sessionName);

        bi.youtubeWebview.post(() -> {
            widthYoutube = bi.youtubeWebview.getWidth();
            int heightSrc = (int) ((widthYoutube / 16.0) * 9);
            Log.d(TAG, "onTouch: " + heightSrc);

            ViewGroup.LayoutParams webviewParams = bi.youtubeWebview.getLayoutParams();
            webviewParams.height = heightSrc;
            bi.youtubeWebview.setLayoutParams(webviewParams);
        });

        params = bi.webviewHolder.getLayoutParams();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bi.fullscreenContainer.setVisibility(View.VISIBLE);
            bi.toolbar.setVisibility(View.VISIBLE);
            bi.youtubeWebview.setVisibility(View.GONE);

           // holderParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            bi.youtubeWebview.setLayoutParams(params);
        } else {
            bi.fullscreenContainer.setVisibility(View.GONE);
            bi.toolbar.setVisibility(View.GONE);
            bi.youtubeWebview.setVisibility(View.VISIBLE);
            int heightSrc = (int) ((widthYoutube / 16.0) * 9);
            Log.d(TAG, "onTouch: " + heightSrc);
            params.height = Integer.valueOf(heightSrc); // Set height to maintain 16:9 aspect ratio
            bi.youtubeWebview.setLayoutParams(params);

        }
        //showBlankOverlay(); // Show while loading



       /* // Configure WebView settings
        WebSettings webSettings = bi.youtubeWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Add JavaScript interface
        bi.youtubeWebview.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void onVideoEnded() {
                isVideoFinished = true;
                runOnUiThread(() -> enableContinueButton());
            }
        }, "Android");

        // Load the YouTube video using an iframe URL
        String iframeUrl = "https://www.youtube.com/embed/" + MainApp.videoID + "?autoplay=1&showinfo=0&vq=hd720&controls=0&rel=0&modestbranding=1";
        bi.youtubeWebview.loadUrl(iframeUrl);

        // Set WebViewClient to handle URL loading within WebView
        bi.youtubeWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:(function() { " +
                        "var logo = document.querySelector('.ytp-logo');" +
                        "if (logo) { logo.style.display = 'none'; }" +
                        "var shareButton = document.querySelector('.ytp-share-button');" +
                        "if (shareButton) { shareButton.style.display = 'none'; }" +
                        "var relatedVideos = document.querySelector('.ytp-related');" +
                        "if (relatedVideos) { relatedVideos.style.display = 'none'; }" +
                        "var player = document.querySelector('.html5-video-player');" +
                        "player.addEventListener('ended', function() { Android.onVideoEnded(); });" +
                        "})()");
            }
        });

        bi.youtubeWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }

                // Store the view and callback
                customView = view;
                customViewCallback = callback;

                // Set to fullscreen container
                fullscreenContainer = new FrameLayout(VideoPlayerActivity.this);
                fullscreenContainer.addView(customView);
                setContentView(fullscreenContainer);

                // Force device to landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                fullscreenContainer.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) return;

                fullscreenContainer.removeView(customView);
                customView = null;

                // Restore default view
                setContentView(bi.getRoot());

                // Restore orientation to sensor-based
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                customViewCallback.onCustomViewHidden();
            }

        });*/

        // Configure WebView settings
        WebSettings webSettings = bi.youtubeWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setMediaPlaybackRequiresUserGesture(false); // <-- Important

        vi = new VideoJavaScriptInterface();


// Add JavaScript interface instance
        bi.youtubeWebview.addJavascriptInterface(vi, "Android");

        // Set click listener to toggle play/pause
        bi.playPauseButton.setOnClickListener(v -> {
            bi.youtubeWebview.evaluateJavascript("togglePlayPause();", null);
        });

        bi.youtubeWebview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {






                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    @SuppressLint("ClickableViewAccessibility") int currentOrientation = getRequestedOrientation();
                    Log.d(TAG, "onTouch: touched");
                    // Show button


                    if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                        int heightSrc = (int) ((widthYoutube / 16.0) * 9);
                        Log.d(TAG, "onTouch: " + heightSrc);
                        params.height = Integer.valueOf(heightSrc); // Set height to maintain 16:9 aspect ratio
                        bi.youtubeWebview.setLayoutParams(params);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        bi.youtubeWebview.setVisibility(View.VISIBLE);
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        bi.youtubeWebview.setLayoutParams(params);
                    }
                }
                // Returning true blocks all touch events
                return true;
            }



        });



// Load YouTube player using iframe API inside WebView
        String html = "<html><head>" +
                "<style>" +
                "  body, html { margin:0; padding:0; height:100%; overflow:hidden; background:black; }" +
                "  #player { position:absolute; top:0; left:0; width:100%; height:100%; }" +
                "  #overlay { " +
                "    position: absolute; top:0; left:0; width:100%; height:100%; " +
                "    background-color: black; z-index: 9999;" +
                "  }" +
                "</style>" +
                "</head><body>" +
                "<div id=\"player\"></div>" +
                "<div id=\"overlay\"><div style='color:white; text-align:center; margin-top:50%;'>Loading...</div></div>\n" + // 👈 full-screen blank overlay
                "<script>" +
                "  var tag = document.createElement('script');" +
                "  tag.src = \"https://www.youtube.com/iframe_api\";" +
                "  var firstScriptTag = document.getElementsByTagName('script')[0];" +
                "  firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);" +

                "  var player;" +
                "  var isPlaying = false;" + // state tracker

                "  function hideOverlay() { document.getElementById('overlay').style.display = 'none'; }" +
                "  function showOverlay() { document.getElementById('overlay').style.display = 'block'; }" +


                "  function onYouTubeIframeAPIReady() {" +
                "    player = new YT.Player('player', {" +
                "      videoId: '" + MainApp.videoID + "'," +
                "      playerVars: { 'autoplay': 1, 'controls': 0, 'rel': 0, 'modestbranding': 1, 'vq': 'hd1080' }," +
                "      events: {" +
                "        'onReady': onPlayerReady," +
                "        'onStateChange': onPlayerStateChange" +
                "      }" +
                "    });" +
                "  }" +

                "  function onPlayerReady(event) {" +
                "    hideOverlay();" +  // 👈 Hide when ready

                "    var duration = player.getDuration();" +
                "    Android.onVideoDuration(duration);" +

                "    setInterval(function() {" +
                "      var currentTime = player.getCurrentTime();" +
                "      Android.onVideoProgress(currentTime);" +
                "    }, 1000);" +
                "  }" +

                "  function onPlayerStateChange(event) {" +
                "    if (event.data == YT.PlayerState.ENDED) {" +
                "      Android.onVideoEnded();" +
                "      showOverlay(); " + // 👈 Show after end
                "    } else if (event.data == YT.PlayerState.PLAYING) {" +
                "      isPlaying = true;" +
                "      Android.onVideoPlay();" +              // <-- Add this
                "    } else if (event.data == YT.PlayerState.PAUSED) {" +
                "      isPlaying = false;" +
                "      Android.onVideoPause();" +             // <-- And this
                "    }" +
                "  }" +

                // Toggle play/pause function
                "  function togglePlayPause() {" +
                "    if (player && player.playVideo && player.pauseVideo) {" +
                "      if (isPlaying) {" +
                "        player.pauseVideo();" +
                "      } else {" +
                "        player.playVideo();" +
                "      }" +
                "    }" +
                "  }" +
                "</script>" +
                "</body></html>";



        bi.youtubeWebview.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null);

// Set WebViewClient if needed (for example, to hide logos/buttons)
// You can keep or remove this depending on your UI requirements
        bi.youtubeWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Optional: hide YouTube UI elements with JavaScript, if needed
                view.loadUrl("javascript:(function() { " +
                        "var logo = document.querySelector('.ytp-logo');" +
                        "if (logo) { logo.style.display = 'none'; }" +
                        "var shareButton = document.querySelector('.ytp-share-button');" +
                        "if (shareButton) { shareButton.style.display = 'none'; }" +
                        "var relatedVideos = document.querySelector('.ytp-related');" +
                        "if (relatedVideos) { relatedVideos.style.display = 'none'; }" +
                        "})()");
            }
        });



// WebChromeClient for fullscreen video support
        bi.youtubeWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }

                customView = view;
                customViewCallback = callback;

                fullscreenContainer = new FrameLayout(VideoPlayerActivity.this);
                fullscreenContainer.addView(customView);
                setContentView(fullscreenContainer);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                Toast.makeText(VideoPlayerActivity.this, "show custom", Toast.LENGTH_SHORT).show();
                fullscreenContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        fullscreenContainer.setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        );
                    }
                });
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) return;

                fullscreenContainer.removeView(customView);
                customView = null;

                setContentView(bi.getRoot());

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                customViewCallback.onCustomViewHidden();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bi.fullscreenContainer.setVisibility(View.VISIBLE);
            bi.toolbar.setVisibility(View.VISIBLE);
            bi.youtubeWebview.setVisibility(View.GONE);

            //holderParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            bi.youtubeWebview.setLayoutParams(params);
        } else {
            bi.fullscreenContainer.setVisibility(View.GONE);
            bi.toolbar.setVisibility(View.GONE);
            bi.youtubeWebview.setVisibility(View.VISIBLE);
            int heightSrc = (int) ((widthYoutube / 16.0) * 9);
            Log.d(TAG, "onTouch: " + heightSrc);
            params.height = Integer.valueOf(heightSrc); // Set height to maintain 16:9 aspect ratio
            bi.youtubeWebview.setLayoutParams(params);
        }
    }

    private void showBlankOverlay() {
//        runOnUiThread(() -> bi.blankOverlay.setVisibility(View.VISIBLE));
        runOnUiThread(() -> bi.youtubeWebview.setVisibility(View.GONE));
    }

    private static void setIsVideoFinished(boolean b) {
        isVideoFinished = b;
    }

    private void enableContinueButton() {
        Toast.makeText(this, "Video has ended. Continue button enabled!", Toast.LENGTH_SHORT).show();

        bi.btnContinue.setEnabled(true);
        bi.btnContinue.setClickable(true);
        bi.btnContinue.setTextColor(getResources().getColor(R.color.warm_mustard));
    }

    public void btnEnd(View view) {
        recordEntry("Session cancelled: " + MainApp.sessionName);

        finish();
    }

    public void btnContinue(View v) {
        if (isVideoFinished) {
            recordEntry("Session ended: " + MainApp.sessionName);
            Intent intent = new Intent(this, PostTestActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Please finish watching the video before continuing.", Toast.LENGTH_SHORT).show();
        }
    }


    private void recordEntry(String entryType) {
        EntryLog entryLog = new EntryLog();
        entryLog.setProjectName(PROJECT_NAME);
        entryLog.setUserName(MainApp.user.getUserName());
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

    private void hideBlankOverlay() {
        //   runOnUiThread(() -> bi.blankOverlay.setVisibility(View.GONE));
        runOnUiThread(() -> bi.youtubeWebview.setVisibility(View.VISIBLE));

    }

    @Override
    public void onBackPressed() {
        if (vi.isPlaying()) {
            Toast.makeText(this, "Please pause the video before exiting.", Toast.LENGTH_SHORT).show();
            return; // Prevent back press
        }
        super.onBackPressed(); // Allow back press if video is not playing
    }

    // JavaScript interface class to receive events from JS
    public class VideoJavaScriptInterface {

        private CountDownTimer countDownTimer;
        private boolean isVideoFinished = false;
        private boolean isPlaying = false;

        @android.webkit.JavascriptInterface
        public void onVideoEnded() {
            VideoPlayerActivity.setIsVideoFinished(true);
            isPlaying = false;
            bi.sectionName.setText("(Video Finished) " + bi.sectionName.getText());
            Toast.makeText(VideoPlayerActivity.this, "Video has ended. Continue button enabled!", Toast.LENGTH_SHORT).show();

            //runOnUiThread(() -> enableContinueButton());
        }

        @android.webkit.JavascriptInterface
        public void onVideoDuration(double duration) {
            runOnUiThread(() -> {
                videoDuration = duration;
                int durationMillis = (int) (duration * 1000); // convert to ms

                // Set the duration in the TextView
                int minutes = (int) duration / 60;
                int seconds = (int) duration % 60;

                String formattedDuration = String.format(Locale.getDefault(), "/ %02d:%02d", minutes, seconds);
                bi.vidDuration.setText(formattedDuration);


                bi.progressBar.setMax(durationMillis);
                bi.progressBar.setProgress(0);
                bi.progressBar.setVisibility(View.VISIBLE);


                // Use duration as needed, e.g., show toast or update UI
                Toast.makeText(VideoPlayerActivity.this, "Video duration: " + duration + " seconds", Toast.LENGTH_SHORT).show();
            });
        }


        @android.webkit.JavascriptInterface
        public void onVideoProgress(double currentTime) {
            if (videoDuration <= 0) return;

            runOnUiThread(() -> {
                int currentMillis = (int) (currentTime * 1000);
                bi.progressBar.setProgress(currentMillis);

                // Set the duration in the TextView
                int minutes = (int) currentTime / 60;
                int seconds = (int) currentTime % 60;

                String formattedDuration = String.format(Locale.getDefault(), "Duration: %02d:%02d", minutes, seconds);
                bi.vidProgress.setText(formattedDuration);
            });
        }
        @android.webkit.JavascriptInterface
        public void onVideoPlay() {
            isPlaying = true;
            runOnUiThread(() -> {
                // Optionally update UI, e.g., toggle play/pause icon
                bi.playPauseButton.setImageResource(R.drawable.ic_pause);

                recordEntry("Video Played: " + MainApp.sessionName);

                isPlaying = true;
                //hideBlankOverlay(); // ❗ Hide when video starts playing


            });
        }

        @android.webkit.JavascriptInterface
        public void onVideoPause() {
            isPlaying = false;
            runOnUiThread(() -> {
                // Optionally update UI, e.g., toggle play/pause icon
                bi.playPauseButton.setImageResource(R.drawable.ic_play);
                recordEntry("Video Paused: "+MainApp.sessionName);
                isPlaying = false;

            });
        }


        public boolean isPlaying() {
            return isPlaying;
        }


    }
}