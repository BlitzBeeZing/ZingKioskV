package app.zingnow.zingkiosk.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.adapters.DeliveryAdapter;
import app.zingnow.zingkiosk.adapters.MenuAdapter;
import app.zingnow.zingkiosk.model.Delivery;
import app.zingnow.zingkiosk.model.Menu;
import app.zingnow.zingkiosk.model.UpiResponse;
import in.juspay.hyperinteg.HyperServiceHolder;
import in.juspay.hypersdk.data.JuspayResponseHandler;
import in.juspay.hypersdk.ui.HyperPaymentsCallbackAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class UserDetailsScreen extends AppCompatActivity implements DeliveryAdapter.OnItemClickListener {
    EditText username_edittext,userphone_edittext;
    ArrayList<Menu> order_list = new ArrayList<Menu>();
    String phone_no;

    ImageView back_btn;

    RecyclerView delivery_recyclerview;
    DeliveryAdapter delivery_adapter;
    ArrayList<Delivery> delivery_list = new ArrayList<>();
    ArrayList<Menu> selected_menu = new ArrayList<>();
    ArrayList<Menu> all_menulist = new ArrayList<>();

    //Payment Instances
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
        setContentView(R.layout.activity_user_details_screen);

        setUI();

        userphone_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Hide the keyboard
                    hideKeyboard();
                    return true; // Consume the event
                }
                return false;
            }
        });
    }

    public void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            // Use the InputMethodManager to hide the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void setUI()
    {

        username_edittext = findViewById(R.id.user_name);
        userphone_edittext = findViewById(R.id.user_phoneno);
        delivery_recyclerview = findViewById(R.id.delivery_recyclerview);
        delivery_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        back_btn = findViewById(R.id.back_btn);



        //order_list = (ArrayList<Menu>) getIntent().getSerializableExtra("orderList");
        selected_menu = (ArrayList<Menu>) getIntent().getSerializableExtra("received_list");
        all_menulist = (ArrayList<Menu>) getIntent().getSerializableExtra("menuList");



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        fetchDeliverySlots();

    }

    public void fetchDeliverySlots()
    {
        Delivery delivery = new Delivery("Default Delivery Slot", "FREE","Delivery to Mensa Office,1st Floor,Vaishnavi Towers",false,"1:00-1:30PM");
        delivery_list.add(delivery);
        Delivery delivery1 = new Delivery("Second Delivery Slot","FREE","Delivery to Mensa Office,1st Floor,Vaishnavi Towers",false,"1:00-1:30PM");
        delivery_list.add(delivery1);
        delivery_adapter = new DeliveryAdapter(delivery_list,UserDetailsScreen.this);
        delivery_recyclerview.setAdapter(delivery_adapter);
        delivery_adapter.notifyDataSetChanged();

    }


    public void initiateCheckout(View view)
    {

        String username = username_edittext.getText().toString();
        String userphone = userphone_edittext.getText().toString();

        if(username.length()<=2 && userphone.length()!=10)
        {
            Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(),PaymentInitialisation.class);
//            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Moving to checkout activity", Toast.LENGTH_SHORT).show();
            initiatePayment();
//            Intent intent = new Intent(getApplicationContext(),PaymentInitialisation.class);
//            startActivity(intent);
        }

    }

    void initiatePayment()
    {
        intialiseAPI();
    }

    private void intialiseAPI() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL1, null,
                new com.android.volley.Response.Listener<JSONObject>() {
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


    public void constructUPI()
    {

        String upi = "upi://pay?pa=zingnow.esbz@icici&pn=BLITZBEE&tr=EZV2024011701390963410267&tn=test-663490478665&am=1.00&cu=INR&mc=5812";
        String finalUPI = "upi://pay?pa="+vpa + "&pn=BLITZBEE&tr="+tr+"&tn="+order_id+"&am="+amount+"&cu=INR&mc="+mcc;
        Log.e("Final UPI :" , finalUPI);

        Intent intent = new Intent(getApplicationContext(), QRPaymentScreen.class);
        intent.putExtra("finalUPI",finalUPI);
        intent.putExtra("orderId",order_id);
        intent.putExtra("menuList", (Serializable) all_menulist);

        startActivity(intent);

    }

    @Override
    public void onItemClick(Delivery delivery) {
        for(int i=0;i<delivery_list.size();i++)
        {
            Delivery delivery1 = delivery_list.get(i);
            delivery1.setSelected(false);
        }
        delivery.setSelected(true);
    }
    public void onBackClicked(View view)
    {
        onBackPressed();
    }
}