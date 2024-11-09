package com.synec.plynt;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static String userDocumentID;
    private static String userFullName;
    private static String topicName;
    private static OnSuccessListener<Void> callback;
    ClipboardManager clipboard;

    static {
        // Initialize Firestore with offline persistence enabled
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)  // Enable offline caching
                .build();
        db.setFirestoreSettings(settings);
    }




    public static void addDataToCollection(String collectionName, Map<String, Object> data, final OnSuccessListener<DocumentReference> callback, final OnFailureListener failureCallback) {
        // Add the data to Firestore with an auto-generated document ID
        db.collection(collectionName)
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Document added with ID: " + documentReference.getId() +" "+ collectionName);
                    if (callback != null) {
                        callback.onSuccess(documentReference);  // Notify success
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding document", e);
                    if (failureCallback != null) {
                        failureCallback.onFailure(e);  // Notify failure
                    }
                });
    }
    public static void deleteDocumentById(String collectionName, String documentId, Context context, ImageView imageIcon,
                                          int imageIconUpdateResourceID) {
        db.collection(collectionName).document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreDelete", "Document deleted successfully from collection: " + collectionName);
                    Toast.makeText(context, "Document deleted successfully!", Toast.LENGTH_SHORT).show();
                    imageIcon.setImageResource(imageIconUpdateResourceID);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreDelete", "Error deleting document from collection: " + collectionName, e);
                    Toast.makeText(context, "Failed to delete document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static void downloadAndSaveCollectionToPreferences(String collectionPath) {
        CollectionReference collectionRef = db.collection(collectionPath);

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    JSONObject jsonObject = new JSONObject();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            jsonObject.put(document.getId(), new JSONObject(document.getData()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(collectionPath, jsonObject.toString());
                    editor.apply();
                } else {
                    // Handle the error
                    if (task.getException() != null) {
                        task.getException().printStackTrace();
                    }
                }
                String savedData = sharedPreferences.getString(collectionPath, null);
                System.out.println("Saved Data News Collection: " + savedData);

            }

        });
    }





    public interface OnDocumentExistsCallback {
        void onResult(String documentId);
    }

    public static void isDocumentExistsAccordingToTwoParameters(
            String collectionName,
            String field1,
            String field2,
            Object field1Value,
            Object field2Value,
            ImageView imageIcon,
            int imageIconUpdateResourceID,
            OnDocumentExistsCallback callback) {

        // Query Firestore for documents matching the specified conditions
        db.collection(collectionName)
                .whereEqualTo(field1, field1Value)
                .whereEqualTo(field2, field2Value)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first matching document's ID
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Log.d(TAG, "Document exists in collection: " + collectionName + ", Document ID: " + documentId);

                        // Uncomment this line if you want to update the ImageView icon when a document is found
                        // imageIcon.setImageResource(imageIconUpdateResourceID);

                        // Trigger the callback with the document ID
                        callback.onResult(documentId);
                    } else {
                        // No matching document found
                        Log.d(TAG, "No document found in collection: " + collectionName);
                        // Trigger the callback with `null`
                        callback.onResult(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error querying collection: " + collectionName, e);
                    // Trigger the callback with `null` in case of an error
                    callback.onResult(null);
                });
    }






    public static String getCurrentFormattedDateTime() {
        // Get the current date and time
        Date currentDate = new Date();

        // Format it using the SimpleDateFormat defined
        return sdf.format(currentDate);
    }

    public static void isTopicExisting(String topicName, final OnSuccessListener<Boolean> callback, final OnFailureListener failureCallback) {
        db.collection("Topics")
                .whereEqualTo("name", topicName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Check if any document with the specified topic name exists
                    boolean exists = !queryDocumentSnapshots.isEmpty();
                    callback.onSuccess(exists);  // Pass result to the callback
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking if topic exists", e);
                    if (failureCallback != null) {
                        failureCallback.onFailure(e);  // Notify failure if callback is provided
                    }
                });
    }


    // Helper function to add a new topic
    private static void addNewTopic(String userDocumentID, String userFullName, String newTopic, final OnSuccessListener<Void> callback) {
        // Prepare the data map with required fields
        Map<String, Object> data = new HashMap<>();
        data.put("name", newTopic); // Assuming 'newTopic' is the topic name
        data.put("date_creation", getCurrentFormattedDateTime()); // Your existing date formatter method
        data.put("popularity_score", null); // Initialize as null if necessary

        // Prepare user information list as a list of maps
        List<Map<String, String>> users = new ArrayList<>();
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("userDocumentID", userDocumentID);
        userInfo.put("userFullName", userFullName);
        users.add(userInfo);

        data.put("users", users); // Add the users list to data map

        // Add the new topic to Firestore
        addDataToCollection("Topics", data, documentReference -> {
            Log.d("Firestore", "New topic added successfully with ID: " + documentReference.getId());
            // Update the order_of_topics field in UserData collection
            addTopicToUserOrder(userDocumentID, newTopic, callback);
        }, e -> {
            Log.e("Firestore", "Error adding new topic to Firestore", e);
        });
    }

    // Helper function to update order_of_topics field in UserData
    private static void addTopicToUserOrder(String userDocumentID, String topicName, final OnSuccessListener<Void> callback) {
        db.collection("UserData")
                .document(userDocumentID)
                .update("order_of_topics", FieldValue.arrayUnion(topicName)) // Add topicName to the order_of_topics array
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Topic added to user's order_of_topics successfully.");
                    if (callback != null) {
                        callback.onSuccess(null); // Notify success
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to add topic to user's order_of_topics", e);
                    if (callback != null) {
                        callback.onSuccess(null); // Notify failure
                    }
                });
    }

    public static void updateTopicData(String topicId, int popularityScoreAction, String usersAction, List<Map<String, String>> userInfo,
                                       final OnSuccessListener<Void> callback, final OnFailureListener failureCallback) {
        DocumentReference topicRef = db.collection("Topics").document(topicId);
        topicRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentScore = documentSnapshot.getLong("popularity_score");
                        if (currentScore == null) currentScore = 0L;

                        long newScore = currentScore + popularityScoreAction;

                        // Prepare the update map
                        Map<String, Object> updateData = new HashMap<>();

                        updateData.put("popularity_score", newScore); //TODO: Only add this if the userDocID is not found in the updateData map

                        // Step 2: Modify users map
                        Map<String, Object> userUpdateMap = new HashMap<>();
                        Log.d("Firestore", "Non update data: " + userInfo.toString());
                        Log.d("Firestore", "Update data: " + updateData.toString());
                        Log.d("Firestore", "User Data data: " + userUpdateMap.toString());

                        for (Map<String, String> user : userInfo) {
                            String userDocumentID = user.get("userDocumentID"); // Access each user's document ID

                            if (usersAction.equals("add")) {
                                userUpdateMap.put("users." + userDocumentID, user);
                            } else if (usersAction.equals("neutral")) {
                                Log.d(TAG, "updateTopicData: NUTRAL, user already recorded to be having this topic");
//                                userUpdateMap.put("users." + userDocumentID, FieldValue.delete());
                            }
                        }

                        // Merge updateData with userUpdateMap
                        updateData.putAll(userUpdateMap);

                        // Step 3: Update Firestore document with both updates
                        topicRef.update(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Topic data updated successfully.");
                                    if (callback != null) {
                                        callback.onSuccess(null);  // Notify success
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error updating topic data", e);
                                    if (failureCallback != null) {
                                        failureCallback.onFailure(e);  // Notify failure
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to retrieve topic document", e);
                    if (failureCallback != null) {
                        failureCallback.onFailure(e);  // Notify failure
                    }
                });
    }


    public static void getOrderOfTopics(String userDocumentID, Context context, final OnSuccessListener<List<String>> callback) {
        Log.d(TAG, "getOrderOfTopics: " + userDocumentID);
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

    public static void addTopicToTheOrderOfTopicList(String userDocumentID, String userFullName, String newTopic, List<String> currentTopicList, Context context, final OnSuccessListener<Void> callback) {
        Log.d(TAG, "addTopicToTheOrderOfTopicList: " + userDocumentID);

        // Check if the topic already exists in the Topics collection
        isTopicExisting(newTopic, exists -> {
            if (exists) {
                // If the topic exists, update it
                Log.d(TAG, "addTopicToTheOrderOfTopicList: " + newTopic + " Topic is existing in the dataset Topics Collection");

                if (!currentTopicList.contains(newTopic)) {
                    Log.d(TAG, "addTopicToTheOrderOfTopicList: Topic is not in the users order of topics " + newTopic);
                    updateUserOrderOfTopics(userDocumentID, newTopic, callback);
                } else {
                    Log.d(TAG, "addTopicToTheOrderOfTopicList: Topic is in the users order of topics " + newTopic);
                    updateExistingTopic(userDocumentID, userFullName, newTopic, callback);
                    Toast.makeText(context, "Topic already exists", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If the topic doesn't exist, add it to the collection
                Log.d(TAG, "addTopicToTheOrderOfTopicList: " + newTopic + " Topic is unique");
                addNewTopic(userDocumentID, userFullName, newTopic, callback);
            }
        }, e -> {
            Log.e(TAG, "Error checking if topic exists", e);
            callback.onSuccess(null); // Notify failure
        });
    }

    // Helper function to update an existing topic
    private static void updateExistingTopic(String userDocumentID, String userFullName, String topicName, final OnSuccessListener<Void> callback) {
        db.collection("Topics")
                .whereEqualTo("name", topicName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assuming there's only one document with this topic name
                        String topicId = querySnapshot.getDocuments().get(0).getId();

                        // Prepare user information as a list of maps
                        List<Map<String, String>> userInformation = new ArrayList<>();
                        Map<String, String> userInfo = new HashMap<>();
                        userInfo.put("userDocumentID", userDocumentID);
                        userInfo.put("userFullName", userFullName);
                        userInformation.add(userInfo);

                        // Update the topic document with the new user information and popularity score
                        updateTopicData(topicId, 1, "add", userInformation, aVoid -> {
                            Log.d(TAG, "Existing topic updated successfully in the Topics Collection.");
//                            updateUserOrderOfTopics(userDocumentID, topicName, callback);
                        }, e -> {
                            Log.e(TAG, "Failed to update existing topic", e);
                            callback.onSuccess(null); // Notify failure
                        });
                    } else {
                        Log.e(TAG, "Topic not found: " + topicName);
                        callback.onSuccess(null); // Notify failure if topic doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve topic document", e);
                    callback.onSuccess(null); // Notify failure
                });
    }


    // Helper function to update user's order_of_topics list
    private static void updateUserOrderOfTopics(String userDocumentID, String newTopic, final OnSuccessListener<Void> callback) {
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
                    Log.d(TAG, "updateUserOrderOfTopics: newTopicTOTheUser " + newTopic);
                    Log.d(TAG, "updateUserOrderOfTopics: updateDataTOTheUser " + updateData);

                    // Update the document
                    db.collection("UserData")
                            .document(userDocumentID)
                            .update(updateData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "User's order of topics updated successfully.");
                                callback.onSuccess(null);  // Notify success
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update user's order of topics", e);
                                callback.onSuccess(null);  // Notify failure
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve current order_of_topics", e);
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

    public static void clearSharedPreferences() {
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
//                            AccountFragment.newInstance().setupViewsAccountFragment();
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
                            if (document.getString("agreed_to_privacy_policy_date") == null) {
                                intent = new Intent(context, bPrivacyPolicyActivity.class);
                            } else {
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
    public static boolean isSessionExistingAlready(Context context) {
        // Access SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);

        // Fields to check
        String[] requiredFields = {
                "session_user_id",
                "session_user_document_id",
                "session_full_name",
                "session_first_name",
                "session_last_name",
                "session_email",
                "session_agreed_to_privacy_policy_date"
        };

        for (String field : requiredFields) {
            // Retrieve the value from SharedPreferences
            String value = preferences.getString(field, null);

            // Check if the field is null or empty
            if (value == null || value.isEmpty()) {
                // Log which field is missing or empty
                Log.d("SessionValidation", field + " is missing or empty.");
                return false;
            } else {
                // Log the valid field content
                Log.d("SessionValidation", field + ": " + value);
            }
        }

        // All required fields have content
        return true;
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
            Log.d("SharedPreferences_" + TAG, "No session data found in SharedPreferences.");
            Toast.makeText(context, "No session data found.", Toast.LENGTH_SHORT).show();
        } else {
            // Loop through the entries and log each one
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("SharedPreferences_" + TAG, entry.getKey() + ": " + entry.getValue().toString());
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

