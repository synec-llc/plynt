package com.synec.plynt.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.synec.plynt.R;
import com.synec.plynt._Master;
import com.synec.plynt.bLogInActivity;

public class SettingsFragment extends Fragment {

    String TAG = "SettingsFragment";
    public static final String PREF_NAME = "currentSession"; // SharedPreferences name
    public static final int PREF_MODE = Context.MODE_PRIVATE; // Mode for SharedPreferences
    private ImageView profileImageEdit, uploadIcon, editPasswordIcon, logOUtButton;
    private TextInputEditText firstNameInput, lastNameInput, birthdayInput;
    SharedPreferences preferences;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        preferences = requireActivity().getSharedPreferences(_Master.PREF_NAME, Context.MODE_PRIVATE);
        // Initialize ImageViews
        uploadIcon = root.findViewById(R.id.upload_icon);
        editPasswordIcon = root.findViewById(R.id.edit_password_icon);
        profileImageEdit = root.findViewById(R.id.profileImageEdit);
        logOUtButton = root.findViewById(R.id.icon_log_out);

        // Initialize TextInputEditTexts
        firstNameInput = root.findViewById(R.id.firstNameInput);
        lastNameInput = root.findViewById(R.id.lastNameInput);
        birthdayInput = root.findViewById(R.id.birthdayInput);

        // You can now use these views, for example:
        profileImageEdit.setOnClickListener(v -> {
            // Handle profile image edit click
        });

        editPasswordIcon.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sorry, not working yet.", Toast.LENGTH_SHORT).show();
        });
        uploadIcon.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sorry, not working yet.", Toast.LENGTH_SHORT).show();
        });
        logOUtButton.setOnClickListener(v -> {
            showLogOutConfirmation();
             });

        setThings();
        return root;
    }

    private void setThings(){
        // Retrieve stored session data
        String firstName = preferences.getString("session_first_name", "");
        String lastName = preferences.getString("session_last_name", "");
        String birthday = preferences.getString("session_birthday", "");
        String imageUrl = preferences.getString("session_image_url", "");
        firstNameInput.setHint(firstName);
        lastNameInput.setHint(lastName);
        birthdayInput.setHint(birthday);
        // Load image into profileImageEdit using Glide (or Picasso if you prefer)
        if (!imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_silhoutte)  // Fallback image
                    .error(R.drawable.profile_silhoutte)        // Error image
                    .into(profileImageEdit);
        }
    }


    private void showLogOutConfirmation() {
        // Show a confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User clicked "Yes" - clear session data and redirect
                    SharedPreferences preferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();  // This will clear all data in PREF_NAME preferences
                    editor.apply();  // Commit the changes asynchronously
                    clearSessionData();
                    redirectToLogin();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User clicked "No" - just dismiss the dialog
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void clearSessionData() {
        // Clear SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREF_NAME, PREF_MODE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Log.d(TAG, "clearSessionData Successful ");

    }

    private void redirectToLogin() {
        // Redirect to login activity
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), bLogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
        startActivity(intent);
    }
}
