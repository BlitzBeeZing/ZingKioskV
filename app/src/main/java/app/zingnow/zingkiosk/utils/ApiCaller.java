package app.zingnow.zingkiosk.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.zingnow.zingkiosk.ZingKiosk;
import app.zingnow.zingkiosk.controller.QRPaymentScreen;

public class ApiCaller {
    private String API_ENDPOINT = "https://api.juspay.in/orders/";
    private static final int INTERVAL_MILLISECONDS = 2000; // Adjust as needed

    private static final long TOTAL_DURATION_MILLISECONDS = 900000;

    private static final String TARGET_STATUS = "CHARGED"; // Replace with the status you're waiting for
    private static final String AUTH_TOKEN = "Basic OTk3RjUwQkI4OTM0NEY5QkE0Rjg5RTkyRDM3QTM2Og=="; // Replace with your authorization token
    private static final String merchant_id = "zingnow";
    private RequestQueue requestQueue;
    private Handler handler;
    //private QRPaymentScreen.Callback callback;
    String orderId;
    private ApiCallback apiCallback;
    private long startTime;



    public ApiCaller(ApiCallback apiCallback, String orderId) {
        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(ZingKiosk.getInstance().getApplicationContext());
        this.orderId = orderId;
        this.apiCallback = apiCallback; // Set the ApiCallback interface

        // Initialize Handler on the main thread
        handler = new Handler(Looper.getMainLooper());
    }
    public void startApiCalls() {
        // Record the start time when the ApiCaller is created
        startTime = System.currentTimeMillis();
        // Start making continuous API calls
        makeApiCall();
    }
    @SuppressLint("SuspiciousIndentation")
    private void makeApiCall() {
        // Check if the total duration has elapsed
        if (System.currentTimeMillis() - startTime > TOTAL_DURATION_MILLISECONDS) {
            // Stop making API calls and handle timeout
            handleTimeout();
            return;
        }

        // Make a JsonObjectRequest (replace with your specific request)
        if (API_ENDPOINT.length() == 29)
            API_ENDPOINT = API_ENDPOINT + orderId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_ENDPOINT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Check if the response status matches the target status
                            String currentStatus = response.getString("status"); // Replace with your actual key

                            if (TARGET_STATUS.equals(currentStatus)) {
                                // Handle the desired status response
                                handleDesiredStatus(response);
                            } else {
                                // Continue making API calls with a delay
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        makeApiCall();
                                    }
                                }, INTERVAL_MILLISECONDS);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            // Handle timeout error, retry the API call
                            makeApiCall();
                        } else {
                            // Handle other types of errors
                            // Log.e("ApiCaller", "Error: " + error.toString());
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set custom headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", AUTH_TOKEN);
                headers.put("x-merchantid", merchant_id);
                return headers;
            }

            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(request);
    }

    private void handleDesiredStatus(JSONObject response) {
        // Implement logic to handle the desired status response
        // For example, update UI, stop making further calls, etc.
        apiCallback.onDesiredStatusReceived(response);
    }

    private void handleTimeout() {
        // Implement logic to handle the timeout
        // For example, stop making further calls, notify the user, etc.
        apiCallback.onTimeout();
    }
}
