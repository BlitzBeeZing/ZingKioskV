package app.zingnow.zingkiosk.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.model.Menu;

import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.ArrayList;


public class FinalScreen extends AppCompatActivity {

    RelativeLayout relativeLayout;
    TextView status_text,status_description,proceed;
    ImageView status_image;
    RelativeLayout navigate_to_home;


    long initialTimeMillis = 10000; // 15 minutes in milliseconds

    ArrayList<Menu> all_menu_list = new ArrayList<Menu>();
    private CountDownTimer count_downtimer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);

        setUI();


        count_downtimer = new CountDownTimer(initialTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining time
                updateTimerText(millisUntilFinished);

            }

            @Override
            public void onFinish() {
                navigateToNextScreen();

            }



        };
        count_downtimer.start();

    }
    private void updateTimerText(long millisUntilFinished) {
        // Calculate minutes and seconds from milliseconds
        long seconds = (millisUntilFinished / 1000) % 60;

        // Update the TextView with the remaining time
        proceed.setText(String.format("Proceed to home(%02ds)",seconds));
    }

    public void navigateToHome(View view)
    {
        if (count_downtimer != null) {
            count_downtimer.cancel();
        }
        navigateToNextScreen();
    }


    private void navigateToNextScreen() {
        for(int i=0;i<all_menu_list.size();i++)
        {
            Menu menu = all_menu_list.get(i);
            menu.setSelected(false);
            menu.setQuantity(1);
        }
        Intent intent = new Intent(getApplicationContext(), ScreenSaver.class);
        intent.putExtra("menuList",(Serializable) all_menu_list);
        startActivity(intent);
        finish();
    }

    void setUI()
    {
        relativeLayout = findViewById(R.id.background_layout);
        status_text = findViewById(R.id.status_text);
        status_description = findViewById(R.id.status_description);
        status_image = findViewById(R.id.status_image);
        navigate_to_home = findViewById(R.id.next_btn);
        proceed = findViewById(R.id.procced_text);
        GradientDrawable gradientDrawable = new GradientDrawable();

        all_menu_list = (ArrayList<Menu>) getIntent().getSerializableExtra("menuList");

        int green_color_light = ContextCompat.getColor(this, R.color.green_gradient_start);
        int green_color_dark= ContextCompat.getColor(this, R.color.green_gradient_end);
        int red_color_light = ContextCompat.getColor(this, R.color.red_gradient_start);
        int red_color_dark = ContextCompat.getColor(this, R.color.red_gradient_end);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Intent intent = getIntent();
        String status = intent.getStringExtra("status");

        if(status.equals("success"))
        {
            gradientDrawable.setColors(new int[]{green_color_light, green_color_dark});
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            relativeLayout.setBackground(gradientDrawable);
            status_image.setImageResource(R.drawable.success_png);
            navigate_to_home.setBackgroundResource(R.drawable.green_background_rounded);


        }
        else
        {
            gradientDrawable.setColors(new int[]{red_color_light, red_color_dark});
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            relativeLayout.setBackground(gradientDrawable);
            status_image.setImageResource(R.drawable.failure_png);
            status_text.setText("Transaction Unsuccessful");
            navigate_to_home.setBackgroundResource(R.drawable.dark_orange_background_rounded);




        }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (count_downtimer != null) {
            count_downtimer.cancel();
        }
        navigateToNextScreen();
    }

    @Override
    protected void onDestroy() {
        // Cancel the timer to prevent memory leaks when the activity is destroyed
        if (count_downtimer != null) {
            count_downtimer.cancel();
        }
        super.onDestroy();
    }
}