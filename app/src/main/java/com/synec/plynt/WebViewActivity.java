package com.synec.plynt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.synec.plynt.functions.DateUtils;
import com.synec.plynt.functions.EdenAIWorkflowRunner;
import com.synec.plynt.functions.GPTCallClass;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "WebViewActivity";
    private String url, newsTitle, newsPublisher, newsDate, newsAuthor, newsCategory, newsDescription, relativeNewsDate, newsKeywords;
    private FloatingActionButton stopButton;
//    EdenAIWorkflowRunner ttsProcessor = new EdenAIWorkflowRunner(getApplicationContext());
//    String language = "en";
//    GPTCallClass gptCall = new GPTCallClass(this);

    // Declare the objects without assigning them
    EdenAIWorkflowRunner ttsProcessor;
    String language;
    GPTCallClass gptCall;


    // Method to start WebViewActivity with the URL and other details
    public static void start(Context context, String url, String newsTitle, String newsPublisher, String newsDate,
                             String newsAuthor, String newsCategory, String newsDescription, String newsKeywords) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("EXTRA_URL", url);
        intent.putExtra("EXTRA_TITLE", newsTitle);
        intent.putExtra("EXTRA_PUBLISHER", newsPublisher);
        intent.putExtra("EXTRA_DATE", newsDate);
        intent.putExtra("EXTRA_AUTHOR", newsAuthor);
        intent.putExtra("EXTRA_CATEGORY", newsCategory);
        intent.putExtra("EXTRA_DESCRIPTION", newsDescription);
        intent.putExtra("EXTRA_KEYWORDS", newsKeywords);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView closeButton = findViewById(R.id.close_button);
        WebView webView = findViewById(R.id.webView);
        ImageView iconReadNews = findViewById(R.id.icon_read_news);
        ImageView iconReadNewsAnalysis = findViewById(R.id.icon_read_news_analysis);
        stopButton = findViewById(R.id.stopButton);

        // Assign values to the declared objects
        ttsProcessor = new EdenAIWorkflowRunner(getApplicationContext());
        language = "en";
        gptCall = new GPTCallClass(this);


        // Get and log extras from intent
        url = getIntent().getStringExtra("EXTRA_URL");
        newsTitle = getIntent().getStringExtra("EXTRA_TITLE");
        newsPublisher = getIntent().getStringExtra("EXTRA_PUBLISHER");
        newsDate = getIntent().getStringExtra("EXTRA_DATE");
        newsAuthor = getIntent().getStringExtra("EXTRA_AUTHOR");
        newsCategory = getIntent().getStringExtra("EXTRA_CATEGORY");
        newsDescription = getIntent().getStringExtra("EXTRA_DESCRIPTION");
        newsKeywords = getIntent().getStringExtra("EXTRA_KEYWORDS");
         relativeNewsDate = DateUtils.getRelativeTime(newsDate);

        Log.d(TAG, "NEWS CONTENTS: ");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "Title: " + newsTitle);
        Log.d(TAG, "Publisher: " + newsPublisher);
        Log.d(TAG, "Date: " + newsDate);
        Log.d(TAG, "RelativeNewsDate: " + relativeNewsDate);
        Log.d(TAG, "Author: " + newsAuthor);
        Log.d(TAG, "Category: " + newsCategory);
        Log.d(TAG, "Keywords: " + newsKeywords);
        Log.d(TAG, "Description: " + newsDescription);

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Ensure links and redirects open within WebView
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);


        // Close button to finish the activity
        closeButton.setOnClickListener(v -> finish());
        iconReadNews.setOnClickListener(v -> readNewsAloud());
        iconReadNewsAnalysis.setOnClickListener(v -> readNewsAnalysisPrompt());

    }
    private void readNewsAloud() {
        // Retrieve the extras from the intent
        // Format the text to be more conversational
        String text = String.format("Here's an article titled '%s', published by %s, %s. Written by %s, under the category of %s. Here's a quick summary: %s. Thank you for listening!",
                newsTitle != null ? newsTitle : "Unknown Title",
                newsPublisher != null ? newsPublisher : "Unknown Publisher",
                relativeNewsDate != null ? relativeNewsDate : "some time",
                newsAuthor != null ? newsAuthor : "an unknown author",
                newsCategory != null ? newsCategory : "general",
                newsDescription != null ? newsDescription : "No description available."
        );
        Log.d(TAG, "READNEWSALOUD: "+text);

        // Define language and create a TTS processor instance
        ttsProcessor.runWorkflow(text, language, stopButton);
    }

    private void readNewsAnalysisPrompt() {
        Toast.makeText(WebViewActivity.this, "Analyzing the article...", Toast.LENGTH_SHORT).show();
        String prompt = "Here are the details of the news article:\n" +
                "URL: " + url + "\n" +
                "Title: " + newsTitle + "\n" +
                "Publisher: " + newsPublisher + "\n" +
                "Date: " + newsDate + "\n" +
                "Relative News Date: " + relativeNewsDate + "\n" +
                "Author: " + newsAuthor + "\n" +
                "Category: " + newsCategory + "\n" +
                "Keywords: " + newsKeywords + "\n" +
                "Description: " + newsDescription + "\n\n" +
                "Summarize the news article, keeping all key details intact, and explain its implications in a concise way.";

        // Initialize and call the GPT Class
        gptCall.sendRequest(prompt, new GPTCallClass.GPTResponseCallback() {
            @Override
            public void onResponseReceived(String response) {
                Log.d(TAG, "Webview GPT Response: " + response);
                ttsProcessor.runWorkflow("Here's the analysis of the article. "+response+". Thank you for listening", language, stopButton);
            }

            @Override
            public void onErrorReceived(String error) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(WebViewActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
