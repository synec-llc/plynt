package com.synec.plynt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class aSplash2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asplash2);

        // Find the VideoView
        VideoView videoView = findViewById(R.id.videoViewSplash);

        // Set the video path (assuming the video is in the res/raw directory)
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video_sample;
        videoView.setVideoPath(videoPath);

        // Add media controller to handle play/pause controls
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Start playing the video
        videoView.start();

        // Redirect to bSignUpActivity when the video finishes
        videoView.setOnCompletionListener(mp -> {
            Intent intent = new Intent(aSplash2Activity.this, bSignUpActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });

        // Handle the Skip TextView click
        TextView skipTextView = findViewById(R.id.skipTextView);
        skipTextView.setOnClickListener(v -> {
            // Show a confirmation dialog when the skip button is clicked
            new AlertDialog.Builder(aSplash2Activity.this)
                    .setTitle("Skip Video")
                    .setMessage("Are you sure you want to skip the video?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Redirect to bSignUpActivity if confirmed
                        Intent intent = new Intent(aSplash2Activity.this, bSignUpActivity.class);
                        startActivity(intent);
                        finish(); // Close the current activity
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        new Handler().postDelayed(() -> {
            skipTextView.setVisibility(View.VISIBLE); // Make the skip button visible after 5 seconds
        }, 5000); // 5 seconds delay

    }
}
