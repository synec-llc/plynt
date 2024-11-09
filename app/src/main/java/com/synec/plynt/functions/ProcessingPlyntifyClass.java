package com.synec.plynt.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
                "Please examine these topics, identify any that are essentially the same or have similar meanings, and delete things that are not a topic at all like 'hello', 'sample', and many more." +
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
                        String summaryString = summaryJson.toString();
                        String prompt = "Begin with the topic name. For each topic, provide an analysis of the articles listed, discussing insights and " +
                                "implications in a conversational style. Please ensure there is a clear flow with smooth transitions between each article, " +
                                "and avoid bullet points or structured lists. Here is the summary:\n\n" + summaryString;

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
        gptCall.sendRequest(prompt, new GPTCallClass.GPTResponseCallback() {
            @Override
            public void onResponseReceived(String response) {
                Log.d(TAG, "GPT Plyntify Prompt: "+prompt);
                Log.d(TAG, "GPT Plyntify Analysis Response: " + response);
                Toast.makeText(context, "Response: " + response, Toast.LENGTH_LONG).show();

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
}
