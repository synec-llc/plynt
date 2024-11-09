package com.synec.plynt;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.synec.plynt.databinding.ActivityMainBinding;
import com.synec.plynt.helpers.PermissionsHelper;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private ActivityMainBinding binding;
//    public SharedPreferences preferences = MainActivity.this.getSharedPreferences(_Master.PREF_NAME, Context.MODE_PRIVATE);
private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar as the ActionBar and then hide it (This is required but I don't want it)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Call the PermissionsHelper to check and request permissions
        PermissionsHelper.checkAndRequestPermissions(this);
        _Master.showAllSharedPreferences(MainActivity.this, TAG);

//        _Master.downloadAndSaveCollectionToPreferences("NewsData");

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_plyntify, R.id.navigation_search, R.id.navigation_account)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_search) {
                // Clear the back stack of SettingsFragment and navigate to SearchFragment
                navController.popBackStack(R.id.settingsFragment, false); // Clear settings fragment if it exists
                navController.navigate(R.id.navigation_search); // Navigate to search fragment
                return true;
            } else if (item.getItemId() == R.id.navigation_account) {
                // Navigate to AccountFragment
                navController.popBackStack(R.id.settingsFragment, false); // Clear settings fragment if it exists
                navController.navigate(R.id.navigation_account);
                return true;
            } else if (item.getItemId() == R.id.navigation_plyntify) {
                // Navigate to PlyntifyFragment
                navController.popBackStack(R.id.settingsFragment, false); // Clear settings fragment if it exists
                navController.navigate(R.id.navigation_plyntify);
                return true;
            } else if (item.getItemId() == R.id.navigation_feed) {
                // Navigate to FeedFragment
                navController.popBackStack(R.id.settingsFragment, false); // Clear settings fragment if it exists
                navController.navigate(R.id.navigation_feed);
                return true;
            } else {
                return false;
            }
        });


        textView = findViewById(R.id.textView);
        String inputText = "Hello, Llama!";

        // Calling the GPT Class
//        GPTCallClass gptCall = new GPTCallClass(this);
//        String prompt = "Explain the concept of machine learning.";
//        gptCall.sendRequest(prompt, new GPTCallClass.GPTResponseCallback() {
//            @Override
//            public void onResponseReceived(String response) {
//                Log.d(TAG, "GPT Response: " + response);
//                Toast.makeText(MainActivity.this, "Response: " + response, Toast.LENGTH_LONG).show();
//            }
//            @Override
//            public void onErrorReceived(String error) {
//                Log.e(TAG, "Error: " + error);
//                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
//            }
//        });

//        EdenAIWorkflowRunner ttsProcessor = new EdenAIWorkflowRunner(getApplicationContext());
//        String text = "You will never learn your lesson";
//        String language = "en";
//        ttsProcessor.runWorkflow(text, language);

    }

    

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, new AppBarConfiguration.Builder(navController.getGraph()).build())
                || super.onSupportNavigateUp();
    }


    //    Checking for permissions needed
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsHelper.PERMISSION_REQUEST_CODE) {
            if (PermissionsHelper.arePermissionsGranted(grantResults)) {
                Log.d(TAG, "onRequestPermissionsResult: All permissions are granted" );
//                Toast.makeText(this, "All permissions granted.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Not all permissions are granted" );
//                Toast.makeText(this, "To use all the features of Plynt, please grant the permissions.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
