package app.zingnow.zingkiosk.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.transition.Explode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.model.Menu;
import app.zingnow.zingkiosk.utils.TypeWriter;


public class ScreenSaver extends AppCompatActivity {


    //Defining views
    TextView timer_text;
    RelativeLayout book_meal_btn;
    ImageView zing_logo;
    RelativeLayout company_details;
    ArrayList<Menu> menu_items = new ArrayList<>();

    //Defining instances
    TypeWriter type_writer;

    //Defining important variables
    private static final long TIMEOUT_DURATION = 1000L; // 3 seconds
    private static final int ACTION_LOGOUT = 1;

    JsonObjectRequest jsonObjectRequest;
    private static final int MAX_RETRIES = 5; // Maximum number of retries
    private static final int INITIAL_TIMEOUT_MS = 10000; // Initial timeout in milliseconds
    String device_id="";



    private final Handler timeoutHandler = new Handler();
    private final Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            resetTimeout();
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        //getWindow().setExitTransition(new Explode());

        //resetTimeout();

        disableBackButton();




        setUI();
    }

        public void setUI()
        {

            book_meal_btn = findViewById(R.id.book_meal_btn);
            zing_logo = findViewById(R.id.zing_logo);
            timer_text = findViewById(R.id.timer_time);
            company_details = findViewById(R.id.company_details);

            type_writer = (TypeWriter) findViewById(R.id.main_message);

            //Make transparent status bar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            setTypeWriter();
            //menu_items = (ArrayList<Menu>) getIntent().getSerializableExtra("menuList");

        }

        public void fetchDetails()
        {
           Intent intent = new Intent(getApplicationContext(),MenuPage.class);
           startActivity(intent);
        }



        public void setTypeWriter()
        {
            type_writer.setText("");
            type_writer.setCharacterDelay(70);
            type_writer.animateText("Easy & Affordable\nMeals@Work.");
            startContinuousZoomAnimation();
        }
    private void startContinuousZoomAnimation() {
        // Define the initial and final scale values
        float initialScale = 1f;
        float finalScale = 1.2f;

        // Create ValueAnimator for continuous zooming
        ValueAnimator zoomAnimator = ValueAnimator.ofFloat(initialScale, finalScale);
        zoomAnimator.setDuration(1000); // Duration in milliseconds
        zoomAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        zoomAnimator.setRepeatCount(ValueAnimator.INFINITE);
        zoomAnimator.setRepeatMode(ValueAnimator.REVERSE);

        // Update the scale of the ImageView during animation
        zoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                zing_logo.setScaleX(animatedValue);
                zing_logo.setScaleY(animatedValue);
            }
        });

        // Start the animation
        zoomAnimator.start();
    }


    public void bookMeal(View view)
        {
            animateImage(view);
        }

    public void animateImage(View view) {
        RelativeLayout animatedImage = findViewById(R.id.book_meal_btn);

        // Create a scale animation for zooming in
        Animation scaleInAnimation = new ScaleAnimation(1, 0.8f, 1, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleInAnimation.setDuration(150); // in milliseconds

        // Create an alpha animation for fading in
        Animation fadeInAnimation = new AlphaAnimation(1, 0.6f);
        fadeInAnimation.setDuration(120); // in milliseconds

        // Set up a listener for the animation set
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                fetchDetails();

                // Animation ended, perform any necessary actions

                // You can add additional actions here, such as starting a new activity or executing code.
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
//                // Animation repeated, if set to repeat
//                Intent intent = new Intent(getApplicationContext(), MenuPage.class);
//                startActivity(intent);
            }
        };

        // Combine the scale and alpha animations into a set
        Animation animationSet = new AnimationSet(false);
        ((AnimationSet) animationSet).addAnimation(scaleInAnimation);
        ((AnimationSet) animationSet).addAnimation(fadeInAnimation);
        animationSet.setAnimationListener(animationListener);

        // Start the animation set
        animatedImage.startAnimation(animationSet);
    }

    private void resetTimeout() {
        timeoutHandler.removeCallbacks(timeoutRunnable);

        // Calculate time until 12 PM
        long timeUntil12PM = calculateTimeUntil12PM();

        // Post the runnable with the calculated time
        //timeoutHandler.postDelayed(timeoutRunnable, timeUntil12PM);
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_DURATION);


        // Display the remaining time in Toast
        showToast(timeUntil12PM);
    }
    private void showToast(long timeInMillis) {
        if(timeInMillis==0)
        {
            timer_text.setText("00:00:00");
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedTime = sdf.format(new Date(timeInMillis));
            String hrs = formattedTime.substring(0,2);
            //Toast.makeText(this, "HRS : " + hrs, Toast.LENGTH_SHORT).show();
            int hours = Integer.parseInt(hrs);
            if(hours>12)
            {
                timer_text.setText("00:00:00");
            }
            else {
                timer_text.setText(formattedTime);
            }
        }


        //Toast.makeText(this, "Time remaining: " + formattedTime, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        resetTimeout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }
    private long calculateTimeUntil12PM() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long currentTime = System.currentTimeMillis();
        long twelvePMTime = calendar.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String current = sdf.format(new Date(currentTime));
        String twelve = sdf.format(new Date(twelvePMTime));
        long remaining1 =  twelvePMTime - currentTime;

        String remaining = sdf.format(remaining1);
        Log.e("Remaining", remaining + " ");


        if (currentTime > twelvePMTime) {
            // If it's already past 12 PM, calculate the time until 12 PM of the next day
            //calendar.add(Calendar.DAY_OF_YEAR, 1);
            return 0;


        }


        return twelvePMTime - currentTime;
    }

    private void disableBackButton() {
        View rootView = findViewById(android.R.id.content);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // Handle back button press if needed
                    return true; // Consume the event, disable back button
                }
                return false;
            }
        });
    }













    }




