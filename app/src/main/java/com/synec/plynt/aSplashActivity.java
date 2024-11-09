package com.synec.plynt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class aSplashActivity extends AppCompatActivity {

    private static final String TAG = "aSplashActivity";
    private static final String PREF_FIRST_TIME = "pref_first_time";

    private ImageView imageSplash;
    private TextView titleSplash;
    private TextView textSplash;
    private ImageView nextButtonSplash;
    private ImageView circle_icon_1, circle_icon_2, circle_icon_3;
    private int[] imageResources = {R.drawable.image_splash_1, R.drawable.image_splash_2, R.drawable.image_splash_3};
    private int[] titleResources = {R.string.splash_title_1, R.string.splash_title_2, R.string.splash_title_3};
    private int[] messageResources = {R.string.splash_message_1, R.string.splash_message_2, R.string.splash_message_3};
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_asplash);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(PREF_FIRST_TIME, true)) {
            Log.d(TAG, "PREF_FIRST_TIME: NOT FIRST TIME");
            startLoginActivity();
            return;
        }

        setupViews();
        setupNextButton();
    }

    private void setupViews() {
        imageSplash = findViewById(R.id.imageSplash);
        titleSplash = findViewById(R.id.titleSplash);
        textSplash = findViewById(R.id.textSplash);
        nextButtonSplash = findViewById(R.id.nextButtonSplash);
        circle_icon_1 = findViewById(R.id.image1);
        circle_icon_2 = findViewById(R.id.image2);
        circle_icon_3 = findViewById(R.id.image3);
    }

    private void setupNextButton() {
        nextButtonSplash.setOnClickListener(v -> {
            Log.d(TAG, "next button index: " + currentIndex);
            updateIndicators();
            if (currentIndex == imageResources.length - 1) {
                finishSplashSequence();
            } else {
                animateAndChangeContent();
            }
        });
    }

    private void updateIndicators() {
        // Updates circle indicators as per current index
        switch (currentIndex) {
            case 0:
                circle_icon_1.setImageResource(R.drawable.icon_circle_turquoise);
                circle_icon_2.setImageResource(R.drawable.icon_circle_grey);
                circle_icon_3.setImageResource(R.drawable.icon_circle_grey);
                break;
            case 1:
                circle_icon_1.setImageResource(R.drawable.icon_circle_turquoise);
                circle_icon_2.setImageResource(R.drawable.icon_circle_turquoise);
                circle_icon_3.setImageResource(R.drawable.icon_circle_grey);
                break;
            case 2:
                circle_icon_1.setImageResource(R.drawable.icon_circle_turquoise);
                circle_icon_2.setImageResource(R.drawable.icon_circle_turquoise);
                circle_icon_3.setImageResource(R.drawable.icon_circle_turquoise);
                break;
            default:
                break;
        }
    }

    private void finishSplashSequence() {
        Log.d(TAG, "PREF_FIRST_TIME: IS FIRST TIME");
        Intent intent = new Intent(this, aSplash2Activity.class);
        startActivity(intent);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(PREF_FIRST_TIME, false);
        editor.apply();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, bLogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void animateAndChangeContent() {
        Animation fadeOutLeft = AnimationUtils.loadAnimation(this, R.anim.fade_out_left);
        Animation fadeInRight = AnimationUtils.loadAnimation(this, R.anim.fade_in_right);

        fadeOutLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                currentIndex++;
                if (currentIndex < imageResources.length) {
                    imageSplash.setImageResource(imageResources[currentIndex]);
                    titleSplash.setText(titleResources[currentIndex]);
                    textSplash.setText(messageResources[currentIndex]);

                    imageSplash.startAnimation(fadeInRight);
                    titleSplash.startAnimation(fadeInRight);
                    textSplash.startAnimation(fadeInRight);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        imageSplash.startAnimation(fadeOutLeft);
        titleSplash.startAnimation(fadeOutLeft);
        textSplash.startAnimation(fadeOutLeft);
    }
}
