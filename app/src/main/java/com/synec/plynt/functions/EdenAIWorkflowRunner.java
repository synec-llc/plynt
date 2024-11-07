package com.synec.plynt.functions;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class EdenAIWorkflowRunner {
    private static final String TAG = "EdenAIWorkflowRunner";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZWI5NjViNDctZjQ1MS00MWU5LWIzYjEtMzc3MWMwYmJiMDNlIiwidHlwZSI6ImFwaV90b2tlbiJ9.my34DidvTlyknXbXL60E0ymJ21mVA0DpZsoMOtxJR0g";
    private static final String WORKFLOW_ID = "13bb8f40-c32a-4d44-8d16-52f14c391206";
    private static final String BASE_URL = "https://api.edenai.run/v2/workflow/";
    private FloatingActionButton stopButton;

    private Context context;
    private Queue<String> textQueue; // Queue for text segments
    private String language;
    private MediaPlayer mediaPlayer;

    public EdenAIWorkflowRunner(Context context) {
        this.context = context;
        this.textQueue = new LinkedList<>();
    }

    //without button
    public void runWorkflow(String text, String language) {
        this.language = language;
        Log.d(TAG, "runTTSWorkflow:");
        Toast.makeText(context, "Preparing audio playback...", Toast.LENGTH_SHORT).show();


        // Segment the text if needed and add to the queue
        if (text.length() <= 1500) {
            textQueue.add(text);
            Log.d(TAG, "runWorkflow: Textlength is below 1500 char");
        } else {
            Log.d(TAG, "runWorkflow: Textlength is more than 1500 char, segmenting now");
            int segmentSize = 1500;
            int textLength = text.length();
            int start = 0;

            // Loop to add each text segment to the queue
            while (start < textLength) {
                int end = Math.min(start + segmentSize, textLength);
                String textSegment = text.substring(start, end);
                textQueue.add(textSegment); // Add segment to queue
                start = end;
            }
        }

        // Start processing the first segment
        processNextSegment();
    }

    //with btn
    public void runWorkflow(String text, String language, FloatingActionButton stopBtn) {
        this.language = language;
        Log.d(TAG, "runTTSWorkflow:");
        this.stopButton = stopBtn;
        stopButton.setOnClickListener(v -> stopPlayback());
        Toast.makeText(context, "Preparing audio playback...", Toast.LENGTH_SHORT).show();


        // Segment the text if needed and add to the queue
        if (text.length() <= 1500) {
            textQueue.add(text);
            Log.d(TAG, "runWorkflow: Textlength is below 1500 char");
        } else {
            Log.d(TAG, "runWorkflow: Textlength is more than 1500 char, segmenting now");
            int segmentSize = 1500;
            int textLength = text.length();
            int start = 0;

            // Loop to add each text segment to the queue
            while (start < textLength) {
                int end = Math.min(start + segmentSize, textLength);
                String textSegment = text.substring(start, end);
                textQueue.add(textSegment); // Add segment to queue
                start = end;
            }
        }

        // Start processing the first segment
        processNextSegment();
    }

    // Method to process the next text segment in the queue
    private void processNextSegment() {
        if (!textQueue.isEmpty()) {
            Toast.makeText(context, "Preparing audio playback...", Toast.LENGTH_SHORT).show();
            String nextTextSegment = textQueue.poll();
            new StartWorkflowTask().execute(nextTextSegment, language);
        } else {
            Log.d(TAG, "All segments have been processed.");
        }
    }



    private class StartWorkflowTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String text = params[0];
            String language = params[1];

            try {
                Log.d(TAG, "Preparing request to start workflow...");
                URL url = new URL(BASE_URL + WORKFLOW_ID + "/execution/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON payload
                JSONObject payload = new JSONObject();
                payload.put("text", text);
                payload.put("input_language", language);

                Log.d(TAG, "Request URL: " + url.toString());
                Log.d(TAG, "JSON Payload: " + payload.toString());

                // Send the request
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = payload.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }catch (Exception e){
                    Log.d(TAG, "doInBackground: Request sending failed");
                    e.printStackTrace();
                }

                // Check the response code and process the response
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode); //this is working, it gets to put the execution in the execution list. Now I want you to get

                if (responseCode == 201 || responseCode == 200) {
                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        responseBuilder.append(inputLine);
                    }
                    in.close();

                    // Log and parse the JSON response
                    String responseString = responseBuilder.toString();
                    Log.d(TAG, "EdenAI Response received: " + responseString);

                    JSONObject responseJson = new JSONObject(responseString);
                    // Get the "id" from the JSON response
                    String id = responseJson.getString("id");
                    Log.d(TAG, "Extracted ID: " + id); //THIS  IS WORKING NOW!

                    // Call the function to fetch the audio resource URL
                    return fetchAudioResourceUrl(id); // Call method with execution ID

                } else {
                    Log.e(TAG, "Failed to send request. HTTP code??: " + responseCode);
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception while sending request: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String audioResourceUrl) {
            if (audioResourceUrl != null) {
                Log.d(TAG, "Audio Resource URL: " + audioResourceUrl);

                // Download and play the audio file
                new DownloadAndPlayAudioTask().execute(audioResourceUrl);

            } else {
                Log.e(TAG, "Failed to retrieve audio resource URL.");
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }


    }
    private String fetchAudioResourceUrl(String executionId) {
        String audioResourceUrl = null;
        int maxRetries = 10; // Maximum number of retries
        int retryCount = 0;
        int delayMs = 1000; // 1-second delay between retries

        while (retryCount < maxRetries) {
            try {
                Log.d(TAG, "Fetching audio resource URL for execution ID: " + executionId);
                URL url = new URL(BASE_URL + WORKFLOW_ID + "/execution/" + executionId + "/");
                Log.d(TAG, "Fetching audio link: " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + TOKEN);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Fetch Response Code: " + responseCode);

                if (responseCode == 200 || responseCode == 201) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        responseBuilder.append(inputLine);
                    }
                    in.close();

                    // Parse the response to check status and extract the audio resource URL
                    String responseString = responseBuilder.toString();
                    Log.d(TAG, "Audio resource response: " + responseString);
                    JSONObject responseJson = new JSONObject(responseString);

                    // Check the status field inside the content
                    JSONObject content = responseJson.getJSONObject("content");
                    String status = content.getString("status");
                    Log.d(TAG, "Execution Status: " + status);

                    if ("success".equalsIgnoreCase(status)) {
                        // Proceed to extract audio resource URL if status is "success"
                        JSONObject results = content.getJSONObject("results");
                        JSONObject audioTextToSpeech = results.getJSONObject("audio__text_to_speech");
                        JSONArray audioResults = audioTextToSpeech.getJSONArray("results");

                        if (audioResults.length() > 0) {
                            JSONObject firstResult = audioResults.getJSONObject(0);
                            audioResourceUrl = firstResult.getString("audio_resource_url");
                            Log.d(TAG, "Extracted Audio Resource URL: " + audioResourceUrl);
                            return audioResourceUrl; // Exit the loop once URL is obtained
                        } else {
                            Log.e(TAG, "No results found in audio__text_to_speech.");
                        }
                    } else {
                        Log.d(TAG, "Status is not 'success' yet. Current status: " + status);
                    }
                } else {
                    Log.e(TAG, "Failed to fetch audio resource URL. HTTP code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception while fetching audio resource URL: " + e.getMessage());
            }

            // Increment retry count and wait before retrying
            retryCount++;
            try {
                Log.d(TAG, "Waiting for " + delayMs + " ms before retrying... (Attempt " + retryCount + ")");
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Log.e(TAG, "Sleep interrupted: " + e.getMessage());
                break; // Exit if sleep is interrupted
            }
        }

        Log.e(TAG, "Exceeded maximum retries. Audio resource URL not retrieved.");
        return audioResourceUrl;
    }



    private class DownloadAndPlayAudioTask extends AsyncTask<String, Void, File> {
        @Override
        protected File doInBackground(String... params) {
            String audioUrl = params[0];
            File audioFile = null;

            try {
                Log.d(TAG, "Downloading audio from: " + audioUrl);
                URL url = new URL(audioUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                // Check for successful response code
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Failed to download audio. HTTP code: " + conn.getResponseCode());
                    return null;
                }

                // Save the file to cache directory
                audioFile = new File(context.getCacheDir(), "downloaded_audio.mp3");
                InputStream inputStream = conn.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(audioFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                Log.d(TAG, "Audio file downloaded successfully: " + audioFile.getAbsolutePath());

            } catch (Exception e) {
                Log.e(TAG, "Exception while downloading audio: " + e.getMessage());
            }

            return audioFile;
        }

        @Override
        protected void onPostExecute(File audioFile) {
            if (audioFile != null) {
                playAudio(audioFile);
            } else {
                Log.e(TAG, "Failed to download audio.");
                Toast.makeText(context, "Failed to download audio.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playAudio(File audioFile) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d(TAG, "Playing audio: " + audioFile.getAbsolutePath());

            // Show the stop button when playback starts
            if (stopButton != null) {
                stopButton.setVisibility(View.VISIBLE);
            }

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                Log.d(TAG, "Audio playback completed.");

                // Hide the stop button and process the next segment
                if (stopButton != null) {
                    stopButton.setVisibility(View.GONE);
                }
                processNextSegment();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error playing audio: " + e.getMessage());
            Toast.makeText(context, "Error playing audio.", Toast.LENGTH_SHORT).show();
        }
    }
    private void stopPlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(TAG, "Audio playback stopped by user.");
        }

        // Hide the stop button
        if (stopButton != null) {
            stopButton.setVisibility(View.GONE);
        }

        Toast.makeText(context, "Audio playback stopped", Toast.LENGTH_SHORT).show();
//        processNextSegment();
    }


}
