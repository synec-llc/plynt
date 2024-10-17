package com.synec.plynt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class bSignUpConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "bSignUpConfirmActivity";

    private TextView securityCodeDescriptionText;
    private TextInputEditText securityCodeInput;
    private Button confirmButton;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsign_up_confirmation);



        securityCodeInput = findViewById(R.id.securityCodeInput);
        confirmButton = findViewById(R.id.confirmButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the confirmation button click listener
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailVerification();
            }
        });

        updateConfirmationInstructionMessage();
    }

    private void updateConfirmationInstructionMessage(){
        try {
            securityCodeDescriptionText = findViewById(R.id.securityCodeDescriptionText);
            String email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
            Log.d(TAG, "updateConfirmationInstructionMessage: passsword1 "+ password);
            if (email != null) {
                String confirmationText = "A confirmation email with a link was sent to your email address \"" + email + "\". Click the link from the email then click Validate Confirmation Button below.";
                securityCodeDescriptionText.setText(confirmationText);
            } else {
                securityCodeDescriptionText.setText("A confirmation email was sent. Click the link in the email and then click Validate Confirmation Button below.");
            }
        } catch (Exception e) {
            Log.e("bSignUpConfirmationActivity", "Error getting email from intent", e);
            securityCodeDescriptionText.setText("A confirmation email with a link was sent to your email address. Click the link from the email then click Validate Confirmation Button below.");
        }
    }

    private void checkEmailVerification() {
        FirebaseUser user = _Master.auth.getCurrentUser();

        if (user != null) {
            // Reload user data to check if email has been verified
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful() && user.isEmailVerified()) {
                    Log.d(TAG, "Email verified: " + user.getEmail());
                    saveUserToFirestore(user);
                } else {
                    Log.d(TAG, "Email not verified yet.");
                    Toast.makeText(bSignUpConfirmationActivity.this, "Please verify your email to continue.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveUserToFirestore(FirebaseUser user) {
        // Create the user data map
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS", Locale.getDefault());
        String formattedDate = sdf.format(new Date(user.getMetadata().getCreationTimestamp()));

        Log.d(TAG, "saveUserToFirestore: PASSWORDDDD" + password);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("user_id", user.getUid());
        userMap.put("full_name", user.getDisplayName());
        userMap.put("first_name", user.getDisplayName() != null ? user.getDisplayName().split(" ")[0] : null);
        userMap.put("last_name", user.getDisplayName() != null && user.getDisplayName().split(" ").length > 1 ? user.getDisplayName().split(" ")[1] : null);
        userMap.put("email", user.getEmail());
        userMap.put("password", password);  // Google Sign-In won't provide password, so it will be null
        userMap.put("image_url", (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null);
        userMap.put("registered_voter", false);  // Placeholder, update based on user input
        userMap.put("articles_read_count", 0);  // Placeholder
        userMap.put("max_reading_streak", 0);  // Placeholder
        userMap.put("informed_rating", 0);  // Placeholder
        userMap.put("account_creation", formattedDate);  // Timestamp formatted
        userMap.put("birthday", null);  // Placeholder
        userMap.put("age", null);  // Placeholder
        userMap.put("agreed_to_privacy_policy_date", null);  // Placeholder

        // Save to Firestore
        _Master.db.collection("UserData").document(user.getUid())
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // Log a message when the user is successfully added
                    Log.d(TAG, "User added to Firestore");
                    Toast.makeText(bSignUpConfirmationActivity.this, "User successfully registered!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, bLogInActivity.class);
                    new Handler().postDelayed(() -> {
                        startActivity(i);
                    }, 2000);  // 2000 milliseconds = 2 seconds

                })
                .addOnFailureListener(e -> {
                    // Log a message if there's an error
                    Log.w(TAG, "Error adding user to Firestore", e);
                    Toast.makeText(bSignUpConfirmationActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                });
    }
}
