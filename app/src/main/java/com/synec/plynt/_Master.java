package com.synec.plynt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class _Master {
    // Define the SimpleDateFormat with milliseconds included
    public static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSSS", Locale.getDefault());
    private static String TAG = "_Master";
    // Global instances of Firestore, Authentication, and Storage
    public static final FirebaseFirestore db;
    public static final FirebaseAuth auth = FirebaseAuth.getInstance();
    public static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final String PREF_NAME = "currentSession"; // SharedPreferences name
    public static final int PREF_MODE = Context.MODE_PRIVATE; // Mode for SharedPreferences
    public static final String PREF_PLAYBACK_STATE = "isPlaying"; // Key for playback state
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static List<String> orderOfTopics;

    static {
        // Initialize Firestore with offline persistence enabled
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)  // Enable offline caching
                .build();
        db.setFirestoreSettings(settings);
    }


    public static void getOrderOfTopics(String userDocumentID, Context context, final OnSuccessListener<List<String>> callback) {
        Log.d(TAG, "getOrderOfTopics: "+userDocumentID);
        db.collection("UserData")
                .document(userDocumentID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the ordered list directly from Firestore
                        List<String> orderOfTopics = (List<String>) documentSnapshot.get("order_of_topics");
                        if (orderOfTopics != null) {
                            Log.d(TAG, "Order of Topics: " + orderOfTopics.toString());
                            callback.onSuccess(new ArrayList<>(orderOfTopics));  // Return a copy of the list to maintain order
                        } else {
                            callback.onSuccess(new ArrayList<>());  // Return an empty list if none found
                        }
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve topics", e);
                    callback.onSuccess(new ArrayList<>());  // Return an empty list on failure
                });
    }
    public static void updateOrderOfTopics(String userDocumentID, Context context, List<String> newOrderOfTopics, final OnSuccessListener<Void> callback) {
        Log.d(TAG, "updateOrderOfTopics: " + userDocumentID);

        // Prepare the data to update in Firestore
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("order_of_topics", newOrderOfTopics);

        // Update the document
        db.collection("UserData")
                .document(userDocumentID)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order of Topics updated successfully: " + newOrderOfTopics);
                    callback.onSuccess(null);  // Notify success
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update Order of Topics", e);
                    callback.onSuccess(null);  // Notify failure
                });
    }
    public static void addTopicToTheOrderOfTopicList(String userDocumentID, String newTopic, Context context, final OnSuccessListener<Void> callback) {
        Log.d(TAG, "addTopicToTheOrderOfTopicList: " + userDocumentID);

        // Reference to the user's document in Firestore
        db.collection("UserData")
                .document(userDocumentID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Retrieve the current order_of_topics array
                    List<String> currentOrderOfTopics = (List<String>) documentSnapshot.get("order_of_topics");

                    if (currentOrderOfTopics == null) {
                        currentOrderOfTopics = new ArrayList<>(); // Initialize if null
                    }

                    // Add the new topic to the list
                    currentOrderOfTopics.add(newTopic);

                    // Prepare the data to update in Firestore
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("order_of_topics", currentOrderOfTopics);

                    // Update the document
                    db.collection("UserData")
                            .document(userDocumentID)
                            .update(updateData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Topic added successfully: " + newTopic);
                                callback.onSuccess(null);  // Notify success
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to add topic to Order of Topics", e);
                                callback.onSuccess(null);  // Notify failure
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve current Order of Topics", e);
                    callback.onSuccess(null);  // Notify failure
                });
    }


    // Method to update a Firestore document using documentID
    public static void updateFirestoreWithDocumentID(Context context, String collectionName, String documentID, String fieldName, Object value) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(fieldName, value);

        db.collection(collectionName).document(documentID)
                .set(updateData, SetOptions.merge())  // This will merge fields instead of overwriting the document
                .addOnSuccessListener(aVoid -> {
                    // Success log
                    System.out.println("Document updated successfully with documentID: " + documentID);
                    //delete all the contents of shreadpreferences here
                    clearSharedPreferences();
                    getUserDataAndStoreSession(context, documentID);

                })
                .addOnFailureListener(e -> {
                    // Failure log
                    System.err.println("Error updating document with documentID: " + documentID + " - " + e.getMessage());
                });
    }
    public static void clearSharedPreferences(){
        editor.clear();
        editor.apply();
        Log.d("Clear SharedSreferences", "Done");
    }

    public static void getUserDataAndStoreSession(Context context, String documentID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserData").document(documentID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Generate a unique 30-character session ID
                            String sessionID = _Master.generateSessionID(30);

                            // Create a map to store session data
                            Map<String, Object> sessionData = new HashMap<>();
                            sessionData.put("session_id", sessionID);
                            sessionData.put("session_user_id", document.getString("user_id"));
                            sessionData.put("session_user_document_id", document.getId());
                            sessionData.put("session_full_name", document.getString("full_name"));
                            sessionData.put("session_first_name", document.getString("first_name"));
                            sessionData.put("session_last_name", document.getString("last_name"));
                            sessionData.put("session_email", document.getString("email"));
                            sessionData.put("session_password", document.getString("password"));
                            sessionData.put("session_image_url", document.getString("image_url"));
                            sessionData.put("session_registered_voter", document.getBoolean("registered_voter"));
                            sessionData.put("session_articles_read_count", document.getLong("articles_read_count"));
                            sessionData.put("session_max_reading_streak", document.getLong("max_reading_streak"));
                            sessionData.put("session_informed_rating", document.getLong("informed_rating"));
                            sessionData.put("session_account_creation", document.getString("account_creation"));
                            sessionData.put("session_birthday", document.getString("birthday"));
                            sessionData.put("session_age", document.getLong("age"));
                            sessionData.put("session_agreed_to_privacy_policy_date", document.getString("agreed_to_privacy_policy_date"));
                            // Assuming order_of_topics is an array in your Firestore document
                            orderOfTopics = (List<String>) document.get("order_of_topics");

                            // Save session data to SharedPreferences
                            saveSessionDataToSharedPreferences(context, sessionData);
                            showAllSharedPreferences(context, "Session Refresh ");
                        } else {
                            Toast.makeText(context, "Something went wrong. Please log in again.", Toast.LENGTH_SHORT).show();
                            clearSharedPreferences();
                            Intent i = new Intent(context, bLogInActivity.class);
                            context.startActivity(i);
                        }
                    } else {
                        Log.e("Firestore Error", "Error getting document: ", task.getException());
                        Toast.makeText(context, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public static void getUserDataAndStoreSession(Context context, String email, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserData")
                .whereEqualTo("email", email)
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            // Get the document
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                            // Generate a unique 30-character session ID
                            String sessionID = _Master.generateSessionID(30);

                            // Create a map to store session data
                            Map<String, Object> sessionData = new HashMap<>();
                            sessionData.put("session_id", sessionID);
                            sessionData.put("session_user_id", document.getString("user_id"));
                            sessionData.put("session_user_document_id", document.getId());
                            sessionData.put("session_full_name", document.getString("full_name"));
                            sessionData.put("session_first_name", document.getString("first_name"));
                            sessionData.put("session_last_name", document.getString("last_name"));
                            sessionData.put("session_email", document.getString("email"));
                            sessionData.put("session_password", document.getString("password"));
                            sessionData.put("session_image_url", document.getString("image_url"));
                            sessionData.put("session_registered_voter", document.getBoolean("registered_voter"));
                            sessionData.put("session_articles_read_count", document.getLong("articles_read_count"));
                            sessionData.put("session_max_reading_streak", document.getLong("max_reading_streak"));
                            sessionData.put("session_informed_rating", document.getLong("informed_rating"));
                            sessionData.put("session_account_creation", document.getString("account_creation"));
                            sessionData.put("session_birthday", document.getString("birthday"));
                            sessionData.put("session_age", document.getLong("age"));
                            sessionData.put("session_agreed_to_privacy_policy_date", document.getString("agreed_to_privacy_policy_date"));
                            // Assuming order_of_topics is an array in your Firestore document
                            orderOfTopics = (List<String>) document.get("order_of_topics");

//                            sessionData.put("session_order_of_topics", orderOfTopicsSet);

                            // Save session data to SharedPreferences
                            saveSessionDataToSharedPreferences(context, sessionData);

                            // Log session data
                            for (Map.Entry<String, Object> entry : sessionData.entrySet()) {
                                Log.d("SessionData", entry.getKey() + ": " + entry.getValue());
                            }

                            // Redirect to bPrivacyPolicyActivity
                            Log.d("LogIn", "Successfully logged in: " + document.getString("full_name"));
                            Intent intent;
                            if (document.getString("agreed_to_privacy_policy_date")==null){
                                intent = new Intent(context, bPrivacyPolicyActivity.class);
                            }else {
                                intent = new Intent(context, MainActivity.class);
                            }
                            context.startActivity(intent);

                        } else {
                            Toast.makeText(context, "No user data found. Log in failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore Error", "Error getting documents: ", task.getException());
                        Toast.makeText(context, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private static void saveSessionDataToSharedPreferences(Context context, Map<String, Object> sessionData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Iterate through sessionData and store each key-value pair in SharedPreferences
            for (Map.Entry<String, Object> entry : sessionData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof String) {
                    editor.putString(key, (String) value);
                } else if (value instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) value);
                } else if (value instanceof Long) {
                    editor.putLong(key, (Long) value);
                } else if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                }

//                Set<String> orderOfTopicsSet = new HashSet<>(orderOfTopics);
//                Set<String> orderOfTopicsSet = new LinkedHashSet<>(orderOfTopics);
//                editor.putStringSet("session_order_of_topics", orderOfTopicsSet);

                String jsonOrderOfTopics = new Gson().toJson(orderOfTopics);
                editor.putString("session_order_of_topics", jsonOrderOfTopics);
        }
        // Apply changes to SharedPreferences
        editor.apply();
    }
    // Function to generate a unique 30-character session ID
    public static String generateSessionID(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sessionId = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sessionId.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sessionId.toString();
    }
    public static void showAllSharedPreferences(Context context, String TAG) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);
        editor = sharedPreferences.edit();

        // Get all the entries from SharedPreferences
        Map<String, ?> allEntries = sharedPreferences.getAll();

        if (allEntries.isEmpty()) {
            Log.d("SharedPreferences_"+TAG, "No session data found in SharedPreferences.");
            Toast.makeText(context, "No session data found.", Toast.LENGTH_SHORT).show();
        } else {
            // Loop through the entries and log each one
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("SharedPreferences_"+TAG, entry.getKey() + ": " + entry.getValue().toString());
            }

            // Optionally, show a Toast to confirm the session details
//            Toast.makeText(context, "Session data loaded. Check logs for details.", Toast.LENGTH_SHORT).show();
        }
    }




    // Method to update a Firestore document based on two field values
    public static void updateFirestoreWithTwoFields(String collectionName, String field1Name, Object field1Value,
                                                    String field2Name, Object field2Value, String fieldNameToUpdate, Object valueToUpdate) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(fieldNameToUpdate, valueToUpdate);

        db.collection(collectionName)
                .whereEqualTo(field1Name, field1Value)
                .whereEqualTo(field2Name, field2Value)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().forEach(document -> {
                            db.collection(collectionName).document(document.getId())
                                    .set(updateData, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        // Success log
                                        System.out.println("Document updated successfully with field1: " + field1Name + " and field2: " + field2Name);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failure log
                                        System.err.println("Error updating document with field1: " + field1Name + " and field2: " + field2Name + " - " + e.getMessage());
                                    });
                        });
                    } else {
                        System.err.println("No document found with the given fields: " + field1Name + " and " + field2Name);
                    }
                })
                .addOnFailureListener(e -> {
                    // Failure log
                    System.err.println("Error retrieving documents with field1: " + field1Name + " and field2: " + field2Name + " - " + e.getMessage());
                });
    }


}
