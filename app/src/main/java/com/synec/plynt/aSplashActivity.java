package com.synec.plynt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class aSplashActivity extends AppCompatActivity {

    private static String TAG = "aSplashActivity";
    private ImageView imageSplash;
    private TextView titleSplash;
    private TextView textSplash;
    private ImageView nextButtonSplash;
    ImageView circle_icon_1;
    ImageView circle_icon_2;
    ImageView circle_icon_3;

    // Array of drawable resources for splash images
    private int[] imageResources = {R.drawable.image_splash_1, R.drawable.image_splash_2, R.drawable.image_splash_3};

    // Array of string resources for splash titles and messages
    private int[] titleResources = {R.string.splash_title_1, R.string.splash_title_2, R.string.splash_title_3};
    private int[] messageResources = {R.string.splash_message_1, R.string.splash_message_2, R.string.splash_message_3};

    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Disable Night Mode
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asplash);


        imageSplash = findViewById(R.id.imageSplash);
        titleSplash = findViewById(R.id.titleSplash);
        textSplash = findViewById(R.id.textSplash);
        nextButtonSplash = findViewById(R.id.nextButtonSplash);
        circle_icon_1 = findViewById(R.id.image1);
        circle_icon_2 = findViewById(R.id.image2);
        circle_icon_3 = findViewById(R.id.image3);

        nextButtonSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "next button index: " + currentIndex);
                //for the circle icon navigation in the bottom
                switch (currentIndex) {
                    case 0:
                        // If currentIndex is 0, set circle_icon_1 to turquoise and others to white
//                        circle_icon_1.setImageResource(R.drawable.icon_circle_turquoise);
//                        circle_icon_2.setImageResource(R.drawable.icon_circle_grey);
//                        circle_icon_3.setImageResource(R.drawable.icon_circle_grey);
//                        break;
//
//                    case 0:
                        // If currentIndex is 1, set circle_icon_2 to turquoise and others to white
                        circle_icon_1.setImageResource(R.drawable.icon_circle_turquoise);
                        circle_icon_2.setImageResource(R.drawable.icon_circle_turquoise);
                        circle_icon_3.setImageResource(R.drawable.icon_circle_grey);
                        break;

                    case 1:
                        // If currentIndex is 2, set circle_icon_3 to turquoise and others to white
                        circle_icon_1.setImageResource(R.drawable.icon_circle_turquoise);
                        circle_icon_2.setImageResource(R.drawable.icon_circle_turquoise);
                        circle_icon_3.setImageResource(R.drawable.icon_circle_turquoise);
                        break;

                    default:
                        circle_icon_1.setImageResource(R.drawable.icon_circle_grey);
                        circle_icon_2.setImageResource(R.drawable.icon_circle_grey);
                        circle_icon_3.setImageResource(R.drawable.icon_circle_grey);
                        break;
                }

                // If currentIndex reaches the last set of resources, navigate to the next activity
                if (currentIndex == imageResources.length - 1) {
                    Intent intent = new Intent(aSplashActivity.this, aSplash2Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Perform animation and update content
                    animateAndChangeContent();
                }
            }
        });
    }


    private void animateAndChangeContent() {
        // Load animations
        Animation fadeOutLeft = AnimationUtils.loadAnimation(this, R.anim.fade_out_left);
        Animation fadeInRight = AnimationUtils.loadAnimation(this, R.anim.fade_in_right);

        // Set fade out animation on the splash elements
        imageSplash.startAnimation(fadeOutLeft);
        titleSplash.startAnimation(fadeOutLeft);
        textSplash.startAnimation(fadeOutLeft);

        // Set an animation listener to update content after fade out
        fadeOutLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Update content after fade out
                currentIndex++;
                imageSplash.setImageResource(imageResources[currentIndex]);
                titleSplash.setText(titleResources[currentIndex]);
                textSplash.setText(messageResources[currentIndex]);

                // Set fade in animation on the splash elements
                imageSplash.startAnimation(fadeInRight);
                titleSplash.startAnimation(fadeInRight);
                textSplash.startAnimation(fadeInRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }



}
