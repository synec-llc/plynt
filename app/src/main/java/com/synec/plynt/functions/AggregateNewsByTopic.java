package com.synec.plynt.functions;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synec.plynt._Master;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AggregateNewsByTopic {
    private static final String TAG = "AggregateNewsByTopic";

    // Callback interface for returning results
    public interface DataCallback {
        void onDataProcessed(JSONObject summaryJson);
        void onError(Exception e);
    }

    public void processAndSaveNewsDataForTopics(List<String> extractedTopics, DataCallback callback) {
        FirebaseFirestore db = _Master.db;
        JSONObject summaryJson = new JSONObject();
        AtomicInteger topicsProcessed = new AtomicInteger(0);

        for (String topic : extractedTopics) {
            Log.d(TAG, "Processing topic: " + topic);
            Set<String> matchedDocumentIds = new HashSet<>(); // To avoid duplicates
            List<JSONObject> newsItems = new ArrayList<>();

            // Query by keywords
            Query keywordQuery = db.collection("NewsData")
                    .whereArrayContains("keywords", topic);
            keywordQuery.get().addOnCompleteListener(task -> {
                Log.d(TAG, "Keyword query completed for topic: " + topic);
                handleQueryResults(task, topic, newsItems, matchedDocumentIds, summaryJson, extractedTopics.size(), topicsProcessed, callback);
            });
        }
    }

    private void handleQueryResults(Task<QuerySnapshot> task, String topic, List<JSONObject> newsItems, Set<String> matchedDocumentIds, JSONObject summaryJson, int totalTopics, AtomicInteger topicsProcessed, DataCallback callback) {
        if (task.isSuccessful()) {
            Log.d(TAG, "Handling query results for topic: " + topic);
            for (QueryDocumentSnapshot document : task.getResult()) {
                String documentId = document.getId();
                if (!matchedDocumentIds.contains(documentId)) {
                    matchedDocumentIds.add(documentId);
                    try {
                        JSONObject newsJson = new JSONObject();
                        newsJson.put("title", document.getString("title"));
                        newsJson.put("publisher", document.getString("publisher"));
                        newsJson.put("publishing_date", document.getString("publishing_date"));
                        newsJson.put("author", document.getString("author"));
                        newsJson.put("category", document.get("category"));
                        newsJson.put("description", document.getString("description"));
                        newsJson.put("keywords", document.get("keywords"));
                        newsJson.put("url", document.getString("url"));
                        newsItems.add(newsJson);
                    } catch (Exception e) {
                        Log.e(TAG, "Error converting document to JSON for document ID: " + documentId, e);
                    }
                }
            }

            try {
                JSONArray itemsArray = new JSONArray(newsItems);
                summaryJson.put(topic, itemsArray);
            } catch (Exception e) {
                Log.e(TAG, "Error adding topic data to summary JSON", e);
            }

            if (topicsProcessed.incrementAndGet() == totalTopics) {
                try {
                    summaryJson.put("totalMatches", summaryJson.names().length()); // Total topics that have matches
                    Log.d(TAG, "Final Summary JSON after processing all topics: " + summaryJson.toString());
                    callback.onDataProcessed(summaryJson);
                } catch (Exception e) {
                    Log.e(TAG, "Error finalizing summary JSON", e);
                }
            }
        } else {
            Log.e(TAG, "Error fetching news data for topic: " + topic, task.getException());
            callback.onError(task.getException());
        }
    }
}