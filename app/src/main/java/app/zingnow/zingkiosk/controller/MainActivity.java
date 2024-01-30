package app.zingnow.zingkiosk.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import app.zingnow.zingkiosk.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }



    public void animateImage(View view) {
        ImageView animatedImage = findViewById(R.id.animatedImage);

        // Create a scale animation for zooming in
        Animation scaleInAnimation = new ScaleAnimation(1, 0.8f, 1, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleInAnimation.setDuration(150); // in milliseconds

        // Create an alpha animation for fading in
        Animation fadeInAnimation = new AlphaAnimation(1, 0.5f);
        fadeInAnimation.setDuration(150); // in milliseconds

        // Set up a listener for the animation set
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, perform any necessary actions

                // You can add additional actions here, such as starting a new activity or executing code.
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated, if set to repeat
                Intent intent = new Intent(getApplicationContext(), MenuPage.class);
                startActivity(intent);
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
}