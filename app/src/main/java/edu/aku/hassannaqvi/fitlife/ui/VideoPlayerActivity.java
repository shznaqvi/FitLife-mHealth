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

import androidx.appcompat.app.AppCompatActivity;

import edu.aku.hassannaqvi.fitlife.databinding.ActivityVideoPlayerBinding;

public class VideoPlayerActivity extends AppCompatActivity {

    ActivityVideoPlayerBinding bi;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private FrameLayout fullscreenContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());

        // Set toolbar and retrieve intent extras
        setSupportActionBar(bi.toolbar);
        bi.sectionName.setText(getIntent().getStringExtra("sessionName"));
        bi.sectionObj.setText(getIntent().getStringExtra("sessionObj"));

        // Configure WebView settings
        WebSettings webSettings = bi.youtubeWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Load the YouTube video using an iframe URL
        String videoId = getIntent().getStringExtra("videoID");
        String iframeUrl = "https://www.youtube.com/embed/" + videoId +     "?autoplay=1&showinfo=0&vq=hd720&controls=0&rel=0&modestbranding=1";;
        bi.youtubeWebview.loadUrl(iframeUrl);

        // Set WebViewClient to handle URL loading within WebView
        bi.youtubeWebview.setWebViewClient(new WebViewClient());

        // Set WebChromeClient to handle fullscreen
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
