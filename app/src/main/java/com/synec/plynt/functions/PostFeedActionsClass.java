package com.synec.plynt.functions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.synec.plynt.WebViewActivity;
import com.synec.plynt._Master;
import com.synec.plynt.adapters.NewsAdapter;
import com.synec.plynt.models.NewsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostFeedActionsClass extends NewsAdapter {
    private NewsViewHolder holder; // Added to hold reference to the ViewHolder

    public PostFeedActionsClass(List<NewsModel> newsList, Context context, NewsViewHolder holder) {
        super(newsList, context);
        this.holder = holder;
    }

    private static String TAG = "PostFeedActionsClass";


    // Method to open WebViewActivity when the news headline is clicked
//    public void goToWebView(String url) {
//        if (url != null && !url.isEmpty()) {
//            WebViewActivity.start(context, url); // Using the getter for context
//            Log.d(TAG, "setupClickListener: " + url);
//        }
//    }
    public void goToWebView(String url, String newsTitle, String newsPublisher, String newsDate,
                            String newsAuthor, String newsCategory, String newsDescription, String newsKeywords) {
        if (url != null && !url.isEmpty()) {
            WebViewActivity.start(context, url, newsTitle, newsPublisher, newsDate, newsAuthor, newsCategory, newsDescription, newsKeywords);
            Log.d(TAG, "setupClickListener: " + url);
        }
    }


    public void putURLToClipboard(String url) {
        if (url != null && !url.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE); //TODO: make the clipboard be instantiated in the _Master
            ClipData clip = ClipData.newPlainText("Copied URL", url);
            clipboard.setPrimaryClip(clip);
            Log.d(TAG, "setupClickListener: URL copied " + url);
            Log.d(TAG, "setupClickListener: DataInPost " + newsList);
            Toast.makeText(context, "URL copied to clipboard!", Toast.LENGTH_SHORT).show();
        } else {
            // Optionally handle the case where the URL is null or empty
            Toast.makeText(context, "URL is not available!", Toast.LENGTH_SHORT).show();
        }
    }

    public void storeUserReactionThingInFirestore(String action,
                                                  NewsModel newsItem,
                                                  String collectionName,
                                                  ImageView imageIcon,
                                                  int markedImageIconResource,
                                                  int unmarkedImageIconResource) {
        // Retrieve the user ID from SharedPreferences
        String userId = _Master.sharedPreferences.getString("session_user_id", null);

        if (userId != null) {
            // Check if the document already exists
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("user_document_id", userId);
            dataMap.put("article_id", newsItem.getArticle_id());
            dataMap.put("creation_date", _Master.getCurrentFormattedDateTime());

            _Master.isDocumentExistsAccordingToTwoParameters(
                    collectionName,
                    "user_document_id",
                    "article_id",
                    userId,
                    newsItem.getArticle_id(),
                    imageIcon,
                    markedImageIconResource,
                    documentId -> {
                        if (documentId == null) {
                            // If the document does not exist, prepare the data for storing
                            dataMap.put("action", "add ".concat(action));

                            _Master.addDataToCollection(collectionName, dataMap, // Call the function to add the data to the Firestore collection
                                    documentReference -> {
                                        // Handle success
                                        Log.d(TAG, "storeReactionsInFirestore: Thing added successfully! " + documentReference.getId());
                                        imageIcon.setImageResource(markedImageIconResource);
                                    },
                                    e -> {
                                        Toast.makeText(context, "Failed to add Thing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Log and inform user that bookmark already exists
                            Log.d(TAG, "storeReactionsInFirestore: Thing already exists for this user and article. Document ID: " + documentId);
                            dataMap.put("action", "remove ".concat(action));

                            // Call the function to delete the document by ID
                            _Master.deleteDocumentById(collectionName, documentId, context, imageIcon, unmarkedImageIconResource);
                        }

                        // Save to archive the reactions
                        _Master.addDataToCollection("ReactionsArchive", dataMap, // Call the function to add the data to the Firestore collection
                                documentReference -> {
                                    // Handle success
                                    Log.d(TAG, "storeReactionsInFirestore: Thing added successfully to ReactionsArchive! " + documentReference.getId());
                                },
                                e -> {
                                    Toast.makeText(context, "Failed to add Thing to ReactionsArchive: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
            );


        } else {
            // Handle case where user ID is null
            Toast.makeText(context, "User ID not found!", Toast.LENGTH_SHORT).show();
        }
    }


}
