package com.synec.plynt.ui.plyntify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.synec.plynt.R;
import com.synec.plynt._Master;
import com.synec.plynt.adapters.OrderOfTopicsAdapter;
import com.synec.plynt.functions.DisableBlinkingCursorWhenClickedOutsideClass;
import com.synec.plynt.functions.ManageOrderOfTopicsTouchHelperClass;
import com.synec.plynt.functions.ProcessingPlyntifyClass;
import com.synec.plynt.helpers.MediaPlayerSingleton;

import java.util.ArrayList;
import java.util.List;

public class PlyntifyFragment extends Fragment implements ManageOrderOfTopicsTouchHelperClass.OnOrderChangedListener {

    private static final int MAX_SUGGESTIONS = 4;
    private static final String TAG = "PlyntifyFragment";
    private RecyclerView recyclerView;
    private OrderOfTopicsAdapter adapter;
    private List<String> topicList;
    private Context context;
    private Button plyntifyButton;
    private ConstraintLayout parentLayout;
    private ConstraintLayout dailyPlyntContainer, widgetContainer;
    private ImageView hintContainer;
    SharedPreferences preferences;
    AutoCompleteTextView searchInput;

    public static PlyntifyFragment newInstance() {
        return new PlyntifyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_plyntify, container, false);

        // Initialize button and RecyclerView
        plyntifyButton = root.findViewById(R.id.plantPlyntifyButton);
        parentLayout = root.findViewById(R.id.PlyntifyFragmentParent);
        recyclerView = root.findViewById(R.id.item_topic_recyclerview);
        context = getContext();
        preferences = requireActivity().getSharedPreferences(_Master.PREF_NAME, Context.MODE_PRIVATE);
        searchInput = root.findViewById(R.id.searchInput);
        dailyPlyntContainer = root.findViewById(R.id.goodMorningContainer);
        widgetContainer = root.findViewById(R.id.sendFeedbackContainer);
        hintContainer = root.findViewById(R.id.plyntifyHint);


        topicList = new ArrayList<>();
        new DisableBlinkingCursorWhenClickedOutsideClass(searchInput, parentLayout);
        fillOrderOfTopicList("");

        // Update button text based on playback state
        if (MediaPlayerSingleton.isPlaying()) {
            plyntifyButton.setText("Pause");
        } else {
            plyntifyButton.setText("Play Plyntify");
        }

        // Toggle playback on button click
//        plyntifyButton.setOnClickListener(v -> togglePlayPause()); //old
        plyntifyButton.setOnClickListener(v -> processTopics());

        // Setup autocomplete for searchInput
        setupAutoComplete();

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            // Check if the enter key is pressed
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String inputText = searchInput.getText().toString().trim().toLowerCase();
                if (!inputText.isEmpty()) {
                    _Master.addTopicToTheOrderOfTopicList(preferences.getString("session_user_id", ""),
                            preferences.getString("session_full_name", ""),
                            inputText,
                            topicList,
                            context,
                            aVoid -> addNewTopicInTheFragmentRecyclerView(inputText));
                    searchInput.setText("");

                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                    }
                }
                return true;
            }
            return false;
        });

        // Define click listeners in onCreateView() or a similar initialization method
        dailyPlyntContainer.setOnClickListener(v -> showCustomDialog("Daily Plynt",
                "Daily Plynt is the automatic playing of Plyntify at a specific time set by the user." +
                        "So it can play when the user wakes up, eating, going to sleep, or when commuting to work. " +
                        "This enables the app to fit seamlessly to the lifestyle of the user 😄 This is still not functional." +
                        "\n\nBest,\nNephi"));

        widgetContainer.setOnClickListener(v -> showCustomDialog("Widget Overview",
                "This widget feature enables quick access to playing Plyntify without opening the app, making it easy " +
                        "to keep up with the latest stories at a click. Not working." +
                        "\n\nBest,\nNephi"));

        hintContainer.setOnClickListener(v -> showCustomDialog("How Plyntify Works",
                "Plyntify is the heart of Plynt.\n" +
                        "This feature curates the top news based on topics you select, analyzing content from reliable sources to deliver concise, engaging summaries.\n\n" +
                        "It uses AI to break down complex information and presents it in a conversational, friendly tone, like an informed friend keeping you updated.\n\n" +
                        "Each summary is narrated in a radio-style format, allowing you to listen on the go, stay informed, and enjoy news that’s easy to understand.\n\n" +
                        "With Plyntify, catching up on current events feels natural and engaging, helping you stay well-informed without needing to read through multiple articles."));

        return root;
    }

    //plyntify processing
    private void processTopics() {
        Toast.makeText(context, "Preparing Plyntify... please wait", Toast.LENGTH_SHORT).show();
        String[] topicsArray = topicList.toArray(new String[0]);
        int totalLength = 0;
        for (String topic : topicsArray) {
            totalLength += topic.length();
        }
        Log.d(TAG, "to PlyntifyprocessTopics: " + topicsArray.toString());
        new ProcessingPlyntifyClass(context, plyntifyButton, topicsArray, totalLength);
    }


    private void setupAutoComplete() {
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryText = s.toString().toLowerCase();
                fetchTopicsForAutocomplete(queryText);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // No action needed here
            }
        });
    }
    private void fetchTopicsForAutocomplete(String queryText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Topics")
                .whereGreaterThanOrEqualTo("name", queryText)
                .whereLessThanOrEqualTo("name", queryText + "\uf8ff") // "Starts with" filtering
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> suggestions = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            if (name != null) {
                                suggestions.add(name);
                            }
                            // Stop adding to suggestions once we've reached the max limit
                            if (suggestions.size() >= MAX_SUGGESTIONS) {
                                break;
                            }
                        }
                        updateAutoCompleteSuggestions(suggestions);
                    } else {
                        Log.e(TAG, "Error fetching topics for autocomplete", task.getException());
                    }
                });
    }

    private void updateAutoCompleteSuggestions(List<String> suggestions) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, suggestions);
        searchInput.setAdapter(adapter);
        searchInput.showDropDown();
    }

    public void fillOrderOfTopicList(String searchText) {
        Log.d(TAG, "session doc id: " + preferences.getString("session_user_id", ""));
        _Master.getOrderOfTopics(preferences.getString("session_user_id", ""), context, orderOfTopics -> {
            topicList.clear();
            for (String topic : orderOfTopics) {
                if (topic.toLowerCase().startsWith(searchText.toLowerCase())) {
                    topicList.add(topic);
                }
            }
            setTopicsToTheRecyclerView(topicList);
        });
    }

    public void addNewTopicInTheFragmentRecyclerView(String newTopic) {
        if (!topicList.contains(newTopic)) {
            Log.d(TAG, "Topic added successfully in Firestore");
            topicList.add(newTopic);
            setTopicsToTheRecyclerView(topicList);
        } else {
            Log.d(TAG, "Topic already exists in the list. Not adding.");
        }
    }

    private void setTopicsToTheRecyclerView(List<String> finalTopicList) {
        // Convert each topic to title case
        List<String> formattedTopicList = new ArrayList<>();
        for (String topic : finalTopicList) {
            formattedTopicList.add(convertToTitleCase(topic));
        }
        // Use the formatted list for the adapter
        adapter = new OrderOfTopicsAdapter(formattedTopicList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ManageOrderOfTopicsTouchHelperClass(adapter, formattedTopicList, context, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Helper function to convert a string to title case
    private String convertToTitleCase(String input) {
        String[] words = input.split(" ");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return titleCase.toString().trim(); // Remove the trailing space
    }


    @Override
    public void onOrderChanged(List<String> newOrder) {
        Log.d(TAG, "Updated topic order: " + newOrder);

        _Master.updateOrderOfTopics(
                preferences.getString("session_user_id", ""),
                context,
                newOrder,
                aVoid -> Log.d(TAG, "Order of topics successfully updated in Firestore")
        );
    }

    public void togglePlayPause() {
        if (MediaPlayerSingleton.isPlaying()) {
            MediaPlayerSingleton.pause(context);
            plyntifyButton.setText("Play Plyntify");
        } else {
            MediaPlayerSingleton.play(context);
            plyntifyButton.setText("Pause");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaPlayerSingleton.release();


    }

    public void showCustomDialog(String titleText, String descriptionText) {
        // Load the custom layout for the alert dialog
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_custom_alert, null);

        // Initialize the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(customView);
        AlertDialog dialog = builder.create();

        // Set title and description
        TextView title = customView.findViewById(R.id.alertTitle);
        title.setText(titleText);

        TextView description = customView.findViewById(R.id.alertMessage);
        description.setText(descriptionText);

        // Set up the OK button
        Button okButton = customView.findViewById(R.id.alertOkButton);
        okButton.setOnClickListener(v -> dialog.dismiss());

        // Display the dialog
        dialog.show();
    }



}
