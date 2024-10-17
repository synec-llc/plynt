package com.synec.plynt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class bLogInActivity extends AppCompatActivity {

    private static final String TAG = "bLogInActivity";
    private static final int RC_SIGN_IN = 9001;

    // Declare all views
    private TextView welcomeTextView;
    private TextView signUpTextView;
    private TextInputLayout emailInputLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText passwordEditText;
    private Button logInButton;
    private TextView dontHaveAccountTextView;
    private ImageView googleLoginImageView;
    private ImageView facebookLoginImageView;

    // Firebase Auth
    private FirebaseAuth mAuth;

    // Declare Google SignInClient
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In options to always show the list of accounts
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // You need to replace this with your web client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize all views
        welcomeTextView = findViewById(R.id.welcomeTextView);
        signUpTextView = findViewById(R.id.signUpTextView);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        passwordEditText = findViewById(R.id.passwordEditText);
        logInButton = findViewById(R.id.logInButton);
        dontHaveAccountTextView = findViewById(R.id.dontHaveAccountTextView);
        googleLoginImageView = findViewById(R.id.googleLoginImageView);
        facebookLoginImageView = findViewById(R.id.facebookLoginImageView);

        // Handle login button click
        logInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate email and password
            if (email.isEmpty()) {
                Toast.makeText(bLogInActivity.this, "Email should not be empty.", Toast.LENGTH_SHORT).show();
            } else if (!email.contains("@")) {
                Toast.makeText(bLogInActivity.this, "Make sure that your email is valid.", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to check Firebase Authentication
                signInWithEmailAndPassword(email, password);
            }
        });

        // Handle Google Sign-In
        googleLoginImageView.setOnClickListener(v -> {
            signInWithGoogle();
        });

        // Set onClickListener to redirect to bSignUpActivity when clicked
        dontHaveAccountTextView.setOnClickListener(v -> {
            Intent intent = new Intent(bLogInActivity.this, bSignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, get Firebase user
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Set session data
                        String sessionID = user.getUid();
                        String sessionEmail = user.getEmail();
                        String sessionFullName = user.getDisplayName() != null ? user.getDisplayName() : "N/A";

                        // Log session data
                        Log.d("Session Info", "Session ID: " + sessionID);
                        Log.d("Session Info", "Session Email: " + sessionEmail);
                        Log.d("Session Info", "Session Full Name: " + sessionFullName);

                        _Master.getUserDataAndStoreSession(bLogInActivity.this, user.getEmail(), user.getUid());
                    } else {
                        // If sign in fails, display a message to the user
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            // User does not exist
                            Toast.makeText(bLogInActivity.this, "User does not exist. Please sign up.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(bLogInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
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
                Toast.makeText(bLogInActivity.this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, get Firebase user
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Check if the user is unique and store the session if they are
                        if (user != null) {
                            _Master.db.collection("UserData").document(user.getUid())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null && task1.getResult().exists()) {
                                            _Master.getUserDataAndStoreSession(bLogInActivity.this, user.getEmail(), user.getUid());
//                                            Toast.makeText(bLogInActivity.this, "User already exists in the database.", Toast.LENGTH_SHORT).show();
                                        } else {
//                                            _Master.getUserDataAndStoreSession(bLogInActivity.this, user.getEmail(), user.getUid());
                                            Toast.makeText(bLogInActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(bLogInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
