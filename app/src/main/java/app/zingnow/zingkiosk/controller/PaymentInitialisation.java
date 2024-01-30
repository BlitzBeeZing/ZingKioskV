package app.zingnow.zingkiosk.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.model.Menu;
import app.zingnow.zingkiosk.model.UpiResponse;

public class PaymentInitialisation extends AppCompatActivity {
    private static final int DELAY_MILLIS = 5000; // Adjust the delay time as needed
    private static final String API_URL1 = "https://api.juspay.in/session"; // Replace with your API endpoint
    private static final String API_URL2 = "https://api.juspay.in/txns";
    private static final String AUTH_TOKEN = "Basic OTk3RjUwQkI4OTM0NEY5QkE0Rjg5RTkyRDM3QTM2Og=="; // Replace with your authorization token
    private static final String merchant_id = "zingnow";
    long randomOrderId = (long) (Math.random()*Math.pow(10,12));
    String order_id = "test-" + Long.toString(randomOrderId);

    String upi = "upi://pay?pa=zingnow.esbz@icici&pn=BLITZBEE&tr=EZV2024011701390963410267&tn=test-663490478665&am=1.00&cu=INR&mc=5812";
    String amount;
    String mcc;
    String tr;
    String vpa;
    String tn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_initialisation);

        intialiseAPI();




    }

    private void intialiseAPI() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("Success : ", response.toString());
                        callSecondAPI();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Log.e("Error is", error.getLocalizedMessage());
                        }
                        else
                        {
                            // Handle other types of errors
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set custom headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization",   AUTH_TOKEN);
                headers.put("x-merchantid",merchant_id);
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("order_id", order_id);
                    jsonBody.put("amount","1.00");
                    jsonBody.put("customer_id","9876543201");
                    jsonBody.put("customer_email","test@gmail.com");
                    jsonBody.put("customer_phone","7551092283");
                    jsonBody.put("payment_page_client_id","zingnow");
                    jsonBody.put("action","paymentPage");
                    return jsonBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException | org.json.JSONException e) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of the request body");
                    return null;
                }
            }
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        queue.add(request);

    }

    void callSecondAPI() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("Success : 2 ", response.toString());
//
//                            UpiResponse upiResponse = parseOrder(response);
//                            Log.e("Mcc", upiResponse.getPayment().)

                            if(response.has("payment"))
                            {
                                JSONObject paymentObject = response.getJSONObject("payment").getJSONObject("sdk_params");
                                if (paymentObject.has("mcc")) {
                                    mcc = paymentObject.getString("mcc");
                                }
                                if (paymentObject.has("tr")) {
                                    tr = paymentObject.getString("tr");
                                }
                                if (paymentObject.has("amount")) {
                                    amount = paymentObject.getString("amount");
                                }
                                if (paymentObject.has("merchant_vpa")) {
                                vpa = paymentObject.getString("merchant_vpa");
                            }
                                constructUPI();
                            }

//                            mcc = response.getJSONObject("payment").getString("mcc");
//                            tr = response.getJSONObject("payment").getString("tr");
//                            amount = response.getJSONObject("payment").getString("amount");
//                            vpa = response.getJSONObject("payment").getString("merchant_vpa");
//                            constructUPI();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Log.e("Error is", error.getLocalizedMessage());
                        } else {
                            // Handle other types of errors
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set custom headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }


            @Override
            protected Map<String, String> getParams() {
                // Provide the parameters in x-www-form-urlencoded format
                Map<String, String> params = new HashMap<>();
                params.put("merchant_id", merchant_id);
                params.put("order_id", order_id);
                params.put("payment_method_type", "UPI");
                params.put("payment_method", "UPI");
                params.put("txn_type", "UPI_PAY");
                params.put("redirect_after_payment", "true");
                params.put("format", "json");
                params.put("sdk_params", "true");

                return params;
            }

            @Override
            public byte[] getBody() {
                // Convert the parameters to a byte array
                try {
                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, String> entry : getParams().entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    }
                    return postData.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            public String getBodyContentType() {
                // Specify the content type
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


        };
        requestQueue.add(request);
        };


    private UpiResponse parseOrder(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), UpiResponse.class);
    }
public void constructUPI()
{
    String upi = "upi://pay?pa=zingnow.esbz@icici&pn=BLITZBEE&tr=EZV2024011701390963410267&tn=test-663490478665&am=1.00&cu=INR&mc=5812";
    String finalUPI = "upi://pay?pa="+vpa + "&pn=BLITZBEE&tr="+tr+"&tn="+order_id+"&am="+amount+"&cu=INR&mc="+mcc;
    Log.e("Final UPI :" , finalUPI);

    Intent intent = new Intent(getApplicationContext(), QRPaymentScreen.class);
    intent.putExtra("finalUPI",finalUPI);
    intent.putExtra("orderId",order_id);
    startActivity(intent);

}




}
