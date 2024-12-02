package edu.aku.hassannaqvi.fitlife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;


import androidx.appcompat.app.AppCompatActivity;


import edu.aku.hassannaqvi.fitlife.core.MainApp;
import edu.aku.hassannaqvi.fitlife.databinding.ActivityVideoPlayerBinding;


public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";

    ActivityVideoPlayerBinding bi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());
        // Retrieve downloadId from SharedPreferences
        Intent i = getIntent();
        // bi = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(bi.toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.app_icon);
        //bi.adminView.setVisibility(MainApp.admin ? View.VISIBLE : View.GONE);
//        bi.toolbar.setSubtitle("Welcome, " + MainApp.user.getFullname() + (MainApp.admin ? " (Admin)" : "") + "!");
        bi.sectionName.setText(i.getStringExtra("sessionName"));

        WebSettings webSettings = bi.youtubeWebview.getSettings();

        webSettings.setDomStorageEnabled(true);

        webSettings.setJavaScriptEnabled(true); // Enable JavaScript

        bi.youtubeWebview.setWebViewClient(new WebViewClient()); // Open links within the youtubeWebview

        // YouTube IFrame Player URL with a video ID
        String videoId = i.getStringExtra("videoID"); // Replace with your YouTube video ID
        String iframeUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=1&vq=hd720&controls=0&rel=0";

        // Load the YouTube video in the youtubeWebview
        bi.youtubeWebview.loadUrl(iframeUrl);

    }

}