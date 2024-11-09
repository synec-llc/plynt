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

import com.synec.plynt.R;
import com.synec.plynt.bLogInActivity;

public class SettingsFragment extends Fragment {

    String TAG = "SettingsFragment";
    public static final String PREF_NAME = "currentSession"; // SharedPreferences name
    public static final int PREF_MODE = Context.MODE_PRIVATE; // Mode for SharedPreferences

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find the ImageView for log out
        ImageView logOutIcon = view.findViewById(R.id.icon_log_out);

        // Set an OnClickListener to show the confirmation dialog
        logOutIcon.setOnClickListener(v -> showLogOutConfirmation());

        return view;
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

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void redirectToLogin() {
        // Redirect to login activity
        Intent intent = new Intent(getActivity(), bLogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
        startActivity(intent);
    }
}
