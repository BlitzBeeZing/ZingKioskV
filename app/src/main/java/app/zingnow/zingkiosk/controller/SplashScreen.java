package app.zingnow.zingkiosk.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.adapters.MenuAdapter;
import app.zingnow.zingkiosk.model.Menu;


public class SplashScreen extends AppCompatActivity {
    //Defining required instances
    LottieAnimationView lottie_animation_view;
    FirebaseAuth m_auth;
    FirebaseUser user;
    ArrayList<Menu> menu_items = new ArrayList<>();
    Boolean is_data_fetched = false;
    JsonObjectRequest jsonObjectRequest;
    private static final int MAX_RETRIES = 5; // Maximum number of retries
    private static final int INITIAL_TIMEOUT_MS = 10000; // Initial timeout in milliseconds
    String device_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setUI();



    }

    private void setUI()
    {
        lottie_animation_view = findViewById(R.id.lottieAnimationView);

        //Make transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initialiseLottie();
    }

    private void initialiseLottie()
    {

        if(!isAccessTokenPresent())
        {
            lottie_animation_view.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
        }

        else {
            lottie_animation_view.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    if(!is_data_fetched)
                    {
                        lottie_animation_view.playAnimation();
                    }
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            }); // Ensure the animation plays
            fetchMenuItems();
        }
    }

    private void fetchMenuItems() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String prod_url = "https://zingkiosk.onrender.com/deviceInfo?deviceId=" + device_id;
        String dev_url = "https://zingkiosktest.onrender.com/deviceInfo?deviceId=" + device_id;
        String url = dev_url;
         jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                 url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response here
                        Log.d("Volley", "Response: " + response.toString());

                        try {
                            JSONObject menu_object = response.getJSONObject("menu");
                            JSONArray menu_items_array = menu_object.getJSONArray("items");
                            for(int i=0;i<menu_items_array.length();i++)
                            {
                                JSONObject menuObject = menu_items_array.getJSONObject(i);
                                Menu menu = parseOrder(menuObject);
                                menu.setQuantity(1);
                                menu.setSelected(false);
                                menu_items.add(menu);


                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        lottie_animation_view.cancelAnimation();
                        lottie_animation_view.setVisibility(View.GONE);

                        Intent intent = new Intent(getApplicationContext(), ScreenSaver.class);
                        intent.putExtra("menuList", (Serializable) menu_items);
                        startActivity(intent);

                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            // Retry the request if it's a timeout error
                            //Log.e("Error is: ", error.getLocalizedMessage());

                            handleRetry(url, jsonObjectRequest);
                        } else {
                            //Log.e("Error is: ", error.getLocalizedMessage());
                        }
                        // Handle error
                        //Log.e(TAG, "Error: " + error.toString());
                    }
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                INITIAL_TIMEOUT_MS,
                MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    private void handleRetry(final String url, final JsonObjectRequest request) {
        // Retry the request using the same RequestQueue
        request.setRetryPolicy(new DefaultRetryPolicy(
                INITIAL_TIMEOUT_MS,
                MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue again
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private Menu parseOrder(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), Menu.class);
    }

    private boolean isAccessTokenPresent() {
        SharedPreferences shared = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        device_id = (shared.getString("device_id", ""));
        Log.e("Device ID", device_id);
        if(device_id==null || device_id.equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }


}