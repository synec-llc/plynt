package com.synec.plynt.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GPTCallClass {
    private static final String TAG = "GPTCallClass";
    private static final String ENDPOINT_URL = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";

    private Context context;

    // Callback interface
    public interface GPTResponseCallback {
        void onResponseReceived(String response);
        void onErrorReceived(String error);
    }

    public GPTCallClass(Context context) {
        this.context = context;
    }

    public void sendRequest(String prompt, final GPTResponseCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            Log.d(TAG, "Final Prompt: "+prompt);
            jsonObject.put("model", "gpt-3.5-turbo-instruct");
//            jsonObject.put("model", "text-davinci-003");
            jsonObject.put("prompt", prompt);
            jsonObject.put("max_tokens", 1000);
            jsonObject.put("temperature", 0);

        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
            callback.onErrorReceived("JSON error: " + e.getMessage());
            return; // Exit the method if JSON setup fails
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                ENDPOINT_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String output = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getString("text");
//                    Log.d(TAG, "GPT Response: " + output);
                    Log.d(TAG, "GPT Response Successful");
                    callback.onResponseReceived(output); // Pass the output to callback
                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    callback.onErrorReceived("JSON parsing error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = null;
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    message = new String(error.networkResponse.data);
                    Log.e(TAG, "Volley error: " + message);  // Log the detailed error message from the server
                    Toast.makeText(context, "Max analysis token exceeded.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Please lessen the num of topics 😄", Toast.LENGTH_SHORT).show();
                    callback.onErrorReceived("Volley error: " + message);
                } else {
                    Log.e(TAG, "Volley error: " + error.toString());
                    callback.onErrorReceived("Volley error: " + error.toString());
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000, // 60 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
}
