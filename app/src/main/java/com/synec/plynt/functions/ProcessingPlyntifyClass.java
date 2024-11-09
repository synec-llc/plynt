package com.synec.plynt.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessingPlyntifyClass {
    private static final String TAG = "ProcessingPlyntifyClass";
    private Context context;
    private Button plyntifyButton;
    private String[] topicsArray;
    private int totalLength;

    public ProcessingPlyntifyClass(Context context, Button plyntifyButton, String[] topicsArray, int totalLength) {
        this.context = context;
        this.plyntifyButton = plyntifyButton;
        this.topicsArray = topicsArray;
        this.totalLength = totalLength;

        // Start preprocessing
        preprocessTopics();
    }

    private void preprocessTopics() {
        // Step 1 & 2: Convert topics array to a single string
        String topicsString = Arrays.toString(topicsArray);

        // Step 3: Set up GPT prompt
        String prompt = "Here are the topics for analysis:\n" +
                topicsString + "\n\n" +
                "Strictly dont add more topics here. Just examine this text with the topics, identify any that are essentially the same or have similar meanings, and delete things that are not a topic at all like 'hello', 'sample', and many more." +
                "Then create a finalized list. Return the list with each topic numbered, e.g.,:\n\n" +
                "1. Topic 1\n" +
                "2. Topic 2\n" +
                "...\n\n" +
                "Final list:";

        // Step 4: Call GPT for processing
        GPTCallClass gptCall = new GPTCallClass(context);
        gptCall.sendRequest(prompt, new GPTCallClass.GPTResponseCallback() {
            @Override
            public void onResponseReceived(String response) {
                Log.d(TAG, "PlyntifyTopics GPT Response: " + response);

                List<String> extractedTopics = extractTopics(response);
                Log.d(TAG, "extractedTopics: " + extractedTopics.toString());

                //Aggregate News
                // Call the AggregateNewsByTopic class with extracted topics
                AggregateNewsByTopic aggregator = new AggregateNewsByTopic();
                aggregator.processAndSaveNewsDataForTopics(extractedTopics, new AggregateNewsByTopic.DataCallback() {
                    @Override
                    public void onDataProcessed(JSONObject summaryJson) {
                        Log.d(TAG, "Summary JSON: " + summaryJson.toString());

                        // Convert JSON to a string and construct a prompt
                        String summaryStringFinal;
                        try {
                            summaryStringFinal = formatJsonByTopics(summaryJson);
                        } catch (JSONException e) {
                            Log.d(TAG, "onDataProcessed: Formatting JSON Failed");
                            throw new RuntimeException(e);
                        }
                        Log.d(TAG, "RequestJSON: " + summaryJson.toString());
                        Log.d(TAG, "RequestJSONFormatted: " + summaryStringFinal);

                        String prompt = "Generate a radio-style narration. Begin with the topic name, followed by a continuous analysis of each article in a conversational and engaging tone, with smooth transitions between each topic. Avoid any bullet points or explicit labels like 'Title:', 'Author:', or 'Publisher:'. Instead, let each article flow seamlessly within the narration as if it were a natural spoken commentary. Your name is Plynt. Here is a sample opening to help with the style: \n" +
                                "\"Hello, welcome to Plyntify. Here is a rundown of the recent updates on [Topic]. In today’s analysis, we explore how...\" " +
                                "Now, proceed with the analysis of the given data as if you were narrating aloud in front of people. Here is the data: \n" + summaryStringFinal;

                        getPlyntifyAnalysisPerTopc(prompt);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error processing news data", e);
                    }
                });
            }


            @Override
            public void onErrorReceived(String error) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getPlyntifyAnalysisPerTopc(String prompt) {
        GPTCallClass gptCall = new GPTCallClass(context);
        Log.d(TAG, "getPlyntifyAnalysisPerTopc: " + prompt);
        gptCall.sendRequest(prompt, new GPTCallClass.GPTResponseCallback() {
            @Override
            public void onResponseReceived(String response) {

                Log.d(TAG, "GPT Plyntify Analysis Response: " + response);
//                Toast.makeText(context, "Response: " + response, Toast.LENGTH_LONG).show();
//                String textToAudioAfterAnalysis = "Here's a rundown of the news under the topic you're interested. " + response + ". Thank you and have a good day!";
                //convert to speech
                Log.d(TAG, "onResponseReceived: Converting plyntify GPT Response to Speech Now");
                EdenAIWorkflowRunner ttsProcessor = new EdenAIWorkflowRunner(context);
                ttsProcessor.runWorkflow(response, "en");
            }

            @Override
            public void onErrorReceived(String error) {
                Log.e(TAG, "Error: " + error);
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Function to extract topics from GPT response
    private List<String> extractTopics(String response) {
        List<String> topics = new ArrayList<>();
        String[] lines = response.split("\n"); // Split the response by lines

        // Regex pattern to match lines like "1. Topic"
        Pattern pattern = Pattern.compile("^\\d+\\.\\s+(.+)$");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.find()) {
                // Add the extracted topic (group 1) to the list
                topics.add(matcher.group(1).trim());
            }
        }

        return topics;
    }


    public static String formatJsonByTopics(JSONObject jsonObject) throws JSONException {
        Log.d(TAG, "formatJsonByTopics: sadasdas");
        StringBuilder formattedText = new StringBuilder();
        int topicCount = 1;  // To enumerate the topics

        // Get keys from the JSONObject
        Iterator<String> keys = jsonObject.keys();

        // Iterate through each key in the JSONObject
        while (keys.hasNext()) {
            String key = keys.next();
            Object item = jsonObject.get(key);  // Get the object associated with the key

            // Check if the item is an instance of JSONArray
            if (item instanceof JSONArray) {
                JSONArray articles = (JSONArray) item;
                formattedText.append("Topic ").append(topicCount++).append(": ").append(key).append("\n\n");

                // Iterate through each article in the JSONArray
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject article = articles.getJSONObject(i);
                    formattedText.append("Title: ").append(article.optString("title", "No title provided")).append("\n");
                    formattedText.append("Publisher: ").append(article.optString("publisher", "No publisher provided")).append("\n");
                    formattedText.append("Publishing Date: ").append(article.optString("publishing_date", "No date provided")).append("\n");
                    formattedText.append("Author: ").append(article.optString("author", "No author provided")).append("\n");
                    formattedText.append("Category: ").append(article.optString("category", "No category provided")).append("\n");
                    formattedText.append("Description: ").append(article.optString("description", "No description provided")).append("\n\n");
                }
                formattedText.append("\n");  // Add extra newline for separation between topics
            } else {
                // Handle non-JSONArray data types appropriately
                formattedText.append("Key ").append(key).append(" has non-array data: ").append(item.toString()).append("\n\n");
            }
        }

        return formattedText.toString();
    }
}
