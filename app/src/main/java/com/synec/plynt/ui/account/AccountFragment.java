package com.synec.plynt.ui.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.synec.plynt.R;
import com.synec.plynt._Master;

public class AccountFragment extends Fragment {

    private AccountViewModel mViewModel;
    private static String TAG = "AccountFragment";
    // Declare global variables for the views
    private ImageView iconSettings;
    private ImageView iconFeedback;
    private TextView feedbackText;
    private ImageView profileImage;
    private TextView userName;
    private TextView userEmail;
    private TextView totalArticles;
    private TextView articlesLabel;
    private TextView maxReadingStreak;
    private TextView maxReadingStreakLabel;
    private TextView informedRate;
    private TextView informedRateLabel;
    private ImageView bookmarkIcon;
    private TextView bookmarksText;
    View root;
    // Add other views as needed...

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_account, container, false);
        setupViews(root);



        return root; // Return the inflated view
    }

    private void setupViews(View rootView) {
        // Initialize views
        iconSettings = rootView.findViewById(R.id.icon_search); // Correct reference to the view
        iconFeedback = rootView.findViewById(R.id.icon_feedback);
        feedbackText = rootView.findViewById(R.id.feedbackText);
        profileImage = rootView.findViewById(R.id.profileImage); //assign here the imageURl
        userName = rootView.findViewById(R.id.userName); //asssign the fullNmae
        userEmail = rootView.findViewById(R.id.userEmail); //assign the email
        totalArticles = rootView.findViewById(R.id.totalArticles); //assign the artciles count
        articlesLabel = rootView.findViewById(R.id.articlesLabel);
        maxReadingStreak = rootView.findViewById(R.id.maxReadingStreak); //assign reading streak
        maxReadingStreakLabel = rootView.findViewById(R.id.maxReadingStreakLabel);
        informedRate = rootView.findViewById(R.id.informedRate); //asign the informed rate
        informedRateLabel = rootView.findViewById(R.id.informedRateLabel);
        bookmarkIcon = rootView.findViewById(R.id.bookmarkIcon);
        bookmarksText = rootView.findViewById(R.id.bookmarksText);

        //put them in the views
        String imageUrl = _Master.sharedPreferences.getString("session_image_url", "");
        String fullName = _Master.sharedPreferences.getString("session_full_name", "");
        String email = _Master.sharedPreferences.getString("session_email", "");
        // Directly retrieve integer values
        Long articlesCount = _Master.sharedPreferences.getLong("session_articles_read_count", 0);
        Long readingStreak = _Master.sharedPreferences.getLong("session_max_reading_streak", 0);
        Long informedRating = _Master.sharedPreferences.getLong("session_informed_rating", 0);
        Log.d(TAG, "setupViews: "+ _Master.sharedPreferences.getString("session_image_url",""));
        // Load image using Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile_silhoutte) // Placeholder image
                .into(profileImage);

        // Assign values to the views
        userName.setText(fullName);
        userEmail.setText(email);
        totalArticles.setText(articlesCount.toString());
        maxReadingStreak.setText(readingStreak.toString());
        informedRate.setText(informedRating.toString() + "%");

        onClicks();

    }

    private void onClicks(){
        profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String currentUserID = _Master.sharedPreferences.getString("session_user_id","");
                _Master.clearSharedPreferences();
                _Master.getUserDataAndStoreSession(getContext(), currentUserID);
                Log.d(TAG, "onLongClick: Inulet "+ _Master.sharedPreferences.getString("session_user_id",""));
                Toast.makeText(getContext(), "Preferences reset done", Toast.LENGTH_SHORT).show();

                setupViews(root);
                return false;
            }
        });
        iconSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                // Navigate to SettingsFragment
                navController.navigate(R.id.action_accountFragment_to_settingsFragment);
            }
        });

        // Set other click listeners here if needed
        iconFeedback.setOnClickListener(v -> {
            // Handle feedback click
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        // TODO: Use the ViewModel if needed
    }



}
