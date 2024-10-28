package com.synec.plynt.ui.plyntify;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.synec.plynt.R;
import com.synec.plynt._Master;
import com.synec.plynt.adapters.OrderOfTopicsAdapter;
import com.synec.plynt.functions.ManageOrderOfTopicsTouchHelperClass;

import java.util.ArrayList;
import java.util.List;

public class PlyntifyFragment extends Fragment implements ManageOrderOfTopicsTouchHelperClass.OnOrderChangedListener {

    private String TAG = "PlyntifyFragment";
    private PlyntifyViewModel mViewModel;
    private RecyclerView recyclerView;
    private OrderOfTopicsAdapter adapter;
    private List<String> topicList;
    private Context context;
    private MediaPlayer mediaPlayer;
    private Button plyntifyButton;
    SharedPreferences preferences;
    TextInputEditText searchInput;

    public static PlyntifyFragment newInstance() {
        return new PlyntifyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_plyntify, container, false);

        // Initialize button and RecyclerView
        plyntifyButton = root.findViewById(R.id.plantPlyntifyButton);
        recyclerView = root.findViewById(R.id.item_topic_recyclerview);
        context = getContext();
        preferences = requireActivity().getSharedPreferences(_Master.PREF_NAME, Context.MODE_PRIVATE);
        searchInput = root.findViewById(R.id.searchInput);
        topicList = new ArrayList<>();
        fillOrderOfTopicList();


        // MediaPlayer and button setup (same as before)
        boolean isPlaying = preferences.getBoolean(_Master.PREF_PLAYBACK_STATE, false);
        int savedPosition = preferences.getInt("currentAudioPosition", 0);
        if (isPlaying) {
            initializeMediaPlayer();
            mediaPlayer.seekTo(savedPosition);
            mediaPlayer.start();
            plyntifyButton.setText("Pause");
        } else {
            plyntifyButton.setText("Play Plyntify");
        }
        plyntifyButton.setOnClickListener(v -> togglePlayPause());


        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            // Check if the enter key is pressed
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String inputText = searchInput.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    _Master.addTopicToTheOrderOfTopicList(preferences.getString("session_user_id", ""), inputText, context,
                            aVoid -> addNewTopic(inputText));
                    searchInput.setText("");

                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                    }
                }
                return true;  // Indicate that the event was handled
            }
            return false;
        });

        return root;
    }


    public void fillOrderOfTopicList() {
        Log.d(TAG, "session doc id: " + preferences.getString("session_user_id", ""));
        _Master.getOrderOfTopics(preferences.getString("session_user_id", ""), context, orderOfTopics -> {
            for (String topic : orderOfTopics) {
                Log.d("PlyntifyFragment", "Topic: " + topic);
                topicList.add(topic);
            }

            setTopicsToTheRecyclerView(topicList);
        });
    }
    private void addNewTopic(String newTopic){
        Log.d(TAG, "Topic added successfully in Firestore");
        topicList.add(newTopic);
        setTopicsToTheRecyclerView(topicList);
    }
    private void setTopicsToTheRecyclerView(List<String> finalTopicList){
        adapter = new OrderOfTopicsAdapter(finalTopicList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        // Set up swipe and drag feature with custom ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ManageOrderOfTopicsTouchHelperClass(adapter, topicList, context, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onOrderChanged(List<String> newOrder) {
        Log.d(TAG, "Updated topic order: " + newOrder);

        // Call updateOrderOfTopics and handle success or failure in the callback
        _Master.updateOrderOfTopics(
                preferences.getString("session_user_id", ""),
                context,
                newOrder,
                aVoid -> Log.d(TAG, "Order of topics successfully updated in Firestore")
        );
    }


    private void initializeMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.sample_audio_1);
            mediaPlayer.setOnCompletionListener(mp -> {
                plyntifyButton.setText("Play Plyntify");
                savePlaybackState(false, 0);
            });
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            initializeMediaPlayer();
        }

        if (mediaPlayer.isPlaying()) {
            int position = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            plyntifyButton.setText("Continue Playing");
            savePlaybackState(false, position);
        } else {
            mediaPlayer.start();
            plyntifyButton.setText("Pause");
            savePlaybackState(true, mediaPlayer.getCurrentPosition());
        }
    }

    private void savePlaybackState(boolean isPlaying, int position) {
        SharedPreferences preferences = requireActivity().getSharedPreferences(_Master.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(_Master.PREF_PLAYBACK_STATE, isPlaying);
        editor.putInt("currentAudioPosition", position);
        editor.apply();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            savePlaybackState(false, 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}
