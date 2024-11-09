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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class bSignUpActivity extends AppCompatActivity {

    private static final String TAG = "bSignUpActivity";
    private static final int RC_SIGN_IN = 9001;

    // Declare Google SignInClient
    private GoogleSignInClient mGoogleSignInClient;

    // Declare views
    private TextView welcomeText;
    private TextView signupText;
    private ConstraintLayout signUpWithGoogle;
    private ConstraintLayout getSignUpWithFacebook;
    private TextInputLayout emailInputLayout;
    private TextInputEditText emailInput;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText passwordInput;
    private Button registerButton;
    private TextView alreadyHaveAccountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsign_up);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Replace with your web client ID from google-services.json
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Access the views
        welcomeText = findViewById(R.id.welcomeText);
        signupText = findViewById(R.id.signupText);
        signUpWithGoogle = findViewById(R.id.signUpWithCompaniesContainer);
        getSignUpWithFacebook = findViewById(R.id.signUpWithFacebookContainer);
        signupText = findViewById(R.id.signupText);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailInput = findViewById(R.id.emailInput);
        passwordInputLayout = findViewById(R.id.textInputLayout);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        alreadyHaveAccountTextView = findViewById(R.id.alreadyHaveAccountTextView);

        // Set the click listener for Google sign-up
        signUpWithGoogle.setOnClickListener(view -> signInWithGoogle());
        getSignUpWithFacebook.setOnClickListener(view -> _Master.sayNotWorkingYet(this, "FB Registration"));
        // Set the click listener for email/password registration
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (password.length() < 7) {
                Toast.makeText(bSignUpActivity.this, "Password must be at least 7 characters long", Toast.LENGTH_SHORT).show();
            } else if (email == null) {
                Toast.makeText(bSignUpActivity.this, "Email should not be empty", Toast.LENGTH_SHORT).show();
            } else if (!email.contains("@")) {
                Toast.makeText(bSignUpActivity.this, "Email is formatted incorrectly", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with the registration if the password is valid
                signUpWithEmailAndPassword(email, password);
            }
        });

        alreadyHaveAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bSignUpActivity.this, bLogInActivity.class);
                startActivity(intent);
            }
        });

    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign-In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    //    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        _Master.auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // Sign in success, get Firebase user and save to Firestore
//                        FirebaseUser user = _Master.auth.getCurrentUser();
//                        saveUserToFirestore(user); //only execute this if the user is unique like theres no record of him in the auth. Toast
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "signInWithCredential:failure", task.getException());
//                    }
//                });
//    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        _Master.auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, get Firebase user
                        FirebaseUser user = _Master.auth.getCurrentUser();
                        if (user != null) {
                            // Check if the user is already in Firestore
                            _Master.db.collection("UserData").document(user.getUid())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            if (task1.getResult() != null && task1.getResult().exists()) {
                                                // User already exists, so don't save again
                                                Toast.makeText(this, "User already exists. Proceed to login.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // New user, save to Firestore
                                                saveUserToFirestore(user);
                                            }
                                        } else {
                                            // Error while checking for existing user
                                            Toast.makeText(this, "Error checking user: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveUserToFirestore(FirebaseUser user) {
        if (user != null) {
            String formattedDate = _Master.sdf.format(new Date(Objects.requireNonNull(user.getMetadata()).getCreationTimestamp()));

            // Create a user object with name, email, and photo URL
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("user_id", user.getUid());
            userMap.put("full_name", user.getDisplayName());
            userMap.put("first_name", user.getDisplayName() != null ? user.getDisplayName().split(" ")[0] : null);
            userMap.put("last_name", user.getDisplayName() != null && user.getDisplayName().split(" ").length > 1 ? user.getDisplayName().split(" ")[1] : null);
            userMap.put("email", user.getEmail());
            userMap.put("password", null);  // Google Sign-In won't provide password, so it will be null
            userMap.put("image_url", (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null);
            userMap.put("registered_voter", false);
            userMap.put("articles_read_count", 0);
            userMap.put("max_reading_streak", 0);
            userMap.put("informed_rating", 0);
            userMap.put("account_creation", formattedDate);
            userMap.put("birthday", null);
            userMap.put("age", null);
            userMap.put("agreed_to_privacy_policy_date", null);


            _Master.db.collection("UserData").document(user.getUid())
                    .set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        // Log a message when the user is successfully added
                        Log.d(TAG, "User added to Firestore");
                        Toast.makeText(bSignUpActivity.this, "User successfully registered!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, bLogInActivity.class);
                        new Handler().postDelayed(() -> {
                            startActivity(i);
                        }, 2000);  // 2000 milliseconds = 2 seconds

                    })
                    .addOnFailureListener(e -> {
                        // Log a message if there's an error
                        Log.w(TAG, "Error adding user to Firestore", e);
                        Toast.makeText(bSignUpActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void signUpWithEmailAndPassword(String email, String password) {
        // Check if email is already registered
        _Master.auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            // Register the new user
                            registerNewUser(email, password);
                        } else {
                            // Email is already registered, show error
                            Log.d(TAG, "Email already registered");
                            Toast.makeText(bSignUpActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Fetch sign-in methods failed
                        Log.w(TAG, "Error fetching sign-in methods", task.getException());
                        Toast.makeText(bSignUpActivity.this, "Failed to check email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerNewUser(String email, String password) {
        // Create the user in Firebase Authentication
        _Master.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful, send verification email
                        FirebaseUser user = _Master.auth.getCurrentUser();
                        if (user != null) {
                            sendVerificationEmail(user);
                        }
                    } else {
                        // Registration failed
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(bSignUpActivity.this, "Email already used.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Verification email sent to " + user.getEmail());
                        Toast.makeText(bSignUpActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();

                        // Navigate to bSignUpConfirmationActivity
                        Intent intent = new Intent(bSignUpActivity.this, bSignUpConfirmationActivity.class);
                        intent.putExtra("email", user.getEmail());  // Pass email to the next activity
                        intent.putExtra("password", passwordInput.getText());  // Pass password to the next activity
                        startActivity(intent);
                    } else {
                        Log.e(TAG, "sendEmailVerification failed", task.getException());
                        Toast.makeText(bSignUpActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
