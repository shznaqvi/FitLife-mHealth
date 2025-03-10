package edu.aku.hassannaqvi.fitlife.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityVideoPlayerBinding;
import edu.aku.hassannaqvi.fitlife.ui.sections.PostTestActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    ActivityVideoPlayerBinding bi;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private FrameLayout fullscreenContainer;
    private boolean isVideoFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());

        // Set toolbar and retrieve intent extras
        setSupportActionBar(bi.toolbar);
        bi.sectionName.setText(MainApp.sessionName);
        bi.sectionObj.setText(MainApp.sessionObj);

        // Configure WebView settings
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

        });
    }

    private void enableContinueButton() {
        Toast.makeText(this, "Continue button enabled!", Toast.LENGTH_SHORT).show();
        bi.btnContinue.setEnabled(true);
        bi.btnContinue.setClickable(true);
    }

    public void btnEnd(View view) {
        finish();
    }

    public void btnContinue(View v) {
        Intent intent = new Intent(this, PostTestActivity.class);
        startActivity(intent);
        finish();
        if (isVideoFinished) {
            startActivity(intent);
            finish();
        } else {
            // Show a message to the user that the video is not finished yet
            Toast.makeText(this, "Please finish watching the video before continuing.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            // Exit fullscreen if a custom view is showing
            customViewCallback.onCustomViewHidden();
            return;
        }
        super.onBackPressed();
    }
}