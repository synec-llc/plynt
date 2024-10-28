package com.synec.plynt.functions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
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
    public void goToWebView(String url) {
        if (url != null && !url.isEmpty()) {
            WebViewActivity.start(context, url); // Using the getter for context
            Log.d(TAG, "setupClickListener: "+url);
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

    public void storeBookmarkInFirestore(NewsModel newsItem) {
        // Retrieve the user ID from SharedPreferences
        String userId = _Master.sharedPreferences.getString("session_user_id", null);

        if (userId != null) {
            // Check if the document already exists
            if (!_Master.isDocumentExistsAccordingToTwoParameters("Bookmarks" , "Bookmarks","user_document_id", userId, newsItem.getArticle_id())) {
                // If document does not exist, prepare the data for storing
                Map<String, Object> bookmarkData = new HashMap<>();
                bookmarkData.put("user_document_id", userId);
                bookmarkData.put("article_id", newsItem.getArticle_id());
                bookmarkData.put("bookmark_date", _Master.getCurrentFormattedDateTime()); // Assuming you have a method for this

                // Call the function to add the data to the Firestore collection
                _Master.addDataToCollection("Bookmarks", bookmarkData,
                        documentReference -> {
                            // Handle success
                            Toast.makeText(context, "Bookmark added successfully!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "storeBookmarkInFirestore: Bookmark added successfully! " + documentReference.getId());
                        },
                        e -> {
                            // Handle failure
                            Toast.makeText(context, "Failed to add bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Log and inform user that bookmark already exists
                Log.d(TAG, "storeBookmarkInFirestore: Bookmark already exists for this user and article.");
                Toast.makeText(context, "This article is already bookmarked!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case where user ID is null
            Toast.makeText(context, "User ID not found!", Toast.LENGTH_SHORT).show();
        }
    }



}
