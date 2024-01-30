package app.zingnow.zingkiosk.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.List;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.adapters.MenuAdapter;
import app.zingnow.zingkiosk.model.Menu;


public class MenuPage extends AppCompatActivity implements MenuAdapter.OnItemClickListener {

    //Defining Views
    RelativeLayout view_cart;
    RecyclerView menu_recyclerview;
    MenuAdapter menu_adapter;
    List<Menu> menu_items = new ArrayList<>();
    RelativeLayout checkout_btn;

    //Defining Instances
    private static final long TIMEOUT_DURATION = 60000L; // 3 seconds
    private static final int ACTION_LOGOUT = 1;

    private final Handler timeoutHandler = new Handler();
    private static final int MAX_RETRIES = 5; // Maximum number of retries
    private static final int INITIAL_TIMEOUT_MS = 10000; // Initial timeout in milliseconds

    JsonObjectRequest jsonObjectRequest;
    private final Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            logoutUser();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        disableBackButton();

        setUI();
    }

    public void setUI()
    {
        view_cart = findViewById(R.id.view_cart);
        checkout_btn = findViewById(R.id.checkout_btn);
        menu_recyclerview = findViewById(R.id.menu_recycler_view);
        menu_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        menu_recyclerview.setLayoutManager(gridLayoutManager);


        //Make transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        fetchMenu();

    }

    public void fetchMenu() {
        SharedPreferences shared = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String device_id = (shared.getString("device_id", ""));

        RequestQueue queue = Volley.newRequestQueue(this);
        String prod_url = "https://zingkiosk.onrender.com/deviceInfo?deviceId=" + device_id;
        String dev_url = "https://zingkiosktest.onrender.com/deviceInfo?deviceId=" + device_id;
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                dev_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response here
                        Log.d("Volley", "Response: " + response.toString());

                        try {
                            JSONObject menu_object = response.getJSONObject("menu");
                            JSONArray menu_items_array = menu_object.getJSONArray("items");

                            for (int i = 0; i < menu_items_array.length(); i++) {
                                JSONObject menuObject = menu_items_array.getJSONObject(i);
                                Menu menu = parseOrder(menuObject);
                                menu.setQuantity(1);
                                menu.setSelected(false);
                                menu_items.add(menu);
                            }
                            updateUI();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Log.e("Volley", "Error: " + error.toString());
                    }
                });
        queue.add(jsonObjectRequest);
    }


    private Menu parseOrder(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), Menu.class);
    }

    public void updateUI()
    {
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        menu_recyclerview.setLayoutAnimation(animation);
        menu_adapter = new MenuAdapter(menu_items, MenuPage.this);
        menu_recyclerview.setAdapter(menu_adapter);
        menu_adapter.notifyDataSetChanged();
    }

    public void viewCart(View view)
    {
        List<Menu> selectedItems = getSelectedItems(menu_items);
        for (Menu selectedItem : selectedItems) {
            //Log.e("Selected Item", selectedItem.getName() + " : "+  selectedItem.getId());
        }
        if(selectedItems.size()==0)
        {
            Toast.makeText(this, "Add some items first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(),CartScreen.class);
            intent.putExtra("selectedMenu", (Serializable) selectedItems);
            intent.putExtra("menuList",(Serializable)menu_items);
            startActivity(intent);
        }


    }

    private List<Menu> getSelectedItems(List<Menu> menuList) {
        List<Menu> selectedItems = new ArrayList<>();
        for (Menu menu : menuList) {
            if (menu.getSelected()) {
                selectedItems.add(menu);
            }
        }
        return selectedItems;
    }




    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetTimeout();
    }

    private void resetTimeout() {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_DURATION);
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

    private void logoutUser() {
        Intent intent = new Intent(this, ScreenSaver.class);  // Change it back to screen saver
        intent.putExtra("menuList", (Serializable) menu_items);
        startActivity(intent);
    }


    @Override
    public void onButtonClick(int position, Boolean status) {
        List<Menu> selectedItems = getSelectedItems(menu_items);
        if(selectedItems.size()==0)
        {
            //checkout_btn.setVisibility(View.GONE);

            checkout_btn.setTranslationY(-10); // Move the layout down initially

            checkout_btn.animate()
                    .translationY(checkout_btn.getHeight()*2)  // Move the layout to its original position
                    .setDuration(200)// Set duration of the animation in milliseconds
                    .setInterpolator(new DecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {
                            checkout_btn.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(@NonNull Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animator) {

                        }
                    })  // Optional listener for animation events
                    .start();
        }
        else
        {
            if(checkout_btn.getVisibility()!= View.VISIBLE)
            {
                checkout_btn.setVisibility(View.VISIBLE);

                checkout_btn.setTranslationY(checkout_btn.getHeight()*2); // Move the layout down initially

                checkout_btn.animate()
                        .translationY(0)  // Move the layout to its original position
                        .setDuration(200)// Set duration of the animation in milliseconds
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(@NonNull Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(@NonNull Animator animator) {
                            }

                            @Override
                            public void onAnimationCancel(@NonNull Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(@NonNull Animator animator) {

                            }
                        })  // Optional listener for animation events
                        .start();
            }

        }
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