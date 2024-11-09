package com.synec.plynt.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.synec.plynt.R;
import com.synec.plynt._Master;

public class MediaPlayerSingleton {
    private static final String TAG = "MediaPlayerSingleton";
    private static MediaPlayer mediaPlayer;
    private static boolean isPlaying;

    public static MediaPlayer getInstance(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.sample_audio_1);
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                savePlaybackState(context, false, 0); // Reset playback position
            });
        }
        return mediaPlayer;
    }

    public static void play(Context context) {
        getInstance(context); // Ensure MediaPlayer is initialized
        if (!isPlaying) {
            SharedPreferences preferences = context.getSharedPreferences(_Master.PREF_NAME, Context.MODE_PRIVATE);
            int lastPosition = preferences.getInt("currentAudioPosition", 0);

            // Start from the last saved position if not already playing
            mediaPlayer.seekTo(lastPosition);
            mediaPlayer.start();
            isPlaying = true;
            savePlaybackState(context, true, mediaPlayer.getCurrentPosition());
        }
    }

    public static void pause(Context context) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            savePlaybackState(context, false, currentPosition);
            isPlaying = false;
        }
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    private static void savePlaybackState(Context context, boolean playing, int position) {
        SharedPreferences preferences = context.getSharedPreferences(_Master.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(_Master.PREF_PLAYBACK_STATE, playing);
        editor.putInt("currentAudioPosition", position);
        editor.apply();
    }
}