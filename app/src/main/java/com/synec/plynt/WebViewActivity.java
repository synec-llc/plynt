package com.synec.plynt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "com.synec.plynt.EXTRA_URL";

    // Method to start WebViewActivity with the URL
    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView closeButton = findViewById(R.id.close_button);
        WebView webView = findViewById(R.id.webView);

        // Close button to finish the activity
        closeButton.setOnClickListener(v -> finish());

        // Get URL from intent
        String url = getIntent().getStringExtra(EXTRA_URL);

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Ensure links and redirects open within WebView
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
