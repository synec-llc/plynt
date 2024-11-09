package com.synec.plynt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.plynt_product_validation_video2;
        videoView.setVideoPath(videoPath);

//        getPitchDeck();
        // Add media controller to handle play/pause controls
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Start playing the video
        videoView.start();

        // Redirect to bSignUpActivity when the video finishes
        videoView.setOnCompletionListener(mp -> {
            Toast.makeText(this, "I recommend downloading the pitch deck", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(aSplash2Activity.this, bSignUpActivity.class);
//            startActivity(intent);
//            finish(); // Close the current activity
        });

        // Handle the Skip TextView click
        TextView skipTextView = findViewById(R.id.skipTextView);
        skipTextView.setOnClickListener(v -> {
            // Show a confirmation dialog when the skip button is clicked
            new AlertDialog.Builder(aSplash2Activity.this)
                    .setTitle("Skip Video")
                    .setMessage("Please skim the pitch deck first before skipping. Thanks 😄")
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


        Button getPitchDeckBtn = findViewById(R.id.getPitchDeckBtn);
        getPitchDeckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             getPitchDeck();
            }
        });

    }

    private void getPitchDeck(){
        Toast.makeText(aSplash2Activity.this, "Downloading Pitch Decks", Toast.LENGTH_SHORT).show();
        // Link to the Google Drive file
        String url = "https://drive.google.com/uc?export=download&id=1GP_62zytd5eu20saxKytm-21nM5GfM84";

        // Create an Intent with ACTION_VIEW to open the link
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        // Start the intent to open the link in a browser for download
        startActivity(intent);
    }
}
