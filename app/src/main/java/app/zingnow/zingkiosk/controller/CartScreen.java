package app.zingnow.zingkiosk.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.adapters.CartAdapter;
import app.zingnow.zingkiosk.model.Menu;


public class CartScreen extends AppCompatActivity implements CartAdapter.CartAdapterListener{

    //Defining views
    TextView final_price;
    TextView total_price_wt;
    RelativeLayout checkout;
    RecyclerView cart_recyclerview;

    //Deifining important variables
    CartAdapter cart_adapter;
    ArrayList<Menu> received_list;
    ArrayList<Menu> all_menulist = new ArrayList<>();
    int item_total;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    ImageView back_btn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_screen);

        setUI();
    }

    public void setUI()
    {

        final_price = findViewById(R.id.total_price);
        total_price_wt = findViewById(R.id.total_txt);
        cart_recyclerview = findViewById(R.id.cart_recyclerview);
        back_btn = findViewById(R.id.back_btn);

        item_total = 0;


        cart_recyclerview.setLayoutManager(new LinearLayoutManager(this));



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        received_list = (ArrayList<Menu>) getIntent().getSerializableExtra("selectedMenu");
        all_menulist = (ArrayList<Menu>) getIntent().getSerializableExtra("menuList");

            cart_adapter = new CartAdapter(received_list);
            cart_adapter.setListener(this);
            cart_recyclerview.setAdapter(cart_adapter);
            cart_adapter.notifyDataSetChanged();

            for (Menu menu : received_list) {
                int price = (int)menu.getSellingPrice();
                item_total = item_total + price;
            }
            double final_price_amount = findFinalPrice(item_total);
            String final_price_text = decimalFormat.format(final_price_amount);
        final_price.setText("₹" + final_price_text);
        total_price_wt.setText("Total (₹" + item_total + "+ 5% GST)");

    }

    private double findFinalPrice(int itemTotal) {
        double item_final = itemTotal + itemTotal*0.05;
        return item_final;
    }


    public void checkOut(View view)
    {
        Intent intent = new Intent(getApplicationContext(), UserDetailsScreen.class);
        intent.putExtra("received_list", (Serializable) received_list);
        intent.putExtra("menuList",(Serializable) all_menulist);
        startActivity(intent);
    }
    public void onItemAddedOrRemoved(int position, Boolean is_added) {
        // Update the total price text here
        if(position<received_list.size())
        {
            Menu menu = received_list.get(position);
            Log.e("Menu Update", menu.getNameOfItem() + is_added + position);
            updateTotalPrice();
        }
        if(received_list.isEmpty())
        {
            Intent intent = new Intent(getApplicationContext(), MenuPage.class);
            intent.putExtra("menuList",all_menulist);
            startActivity(intent);
        }

    }

    private void updateTotalPrice() {
        if(received_list.isEmpty())
        {
            Intent intent = new Intent(getApplicationContext(), MenuPage.class);
            startActivity(intent);
        }
        else {
            int total = 0;
            for(Menu menu:received_list)
            {
                int price =(int) menu.getSellingPrice();
                total += price*menu.getQuantity();
            }
            double final_price_amount = findFinalPrice(total);
            String final_price_text = decimalFormat.format(final_price_amount);

            final_price.setText("₹" + final_price_text);
            total_price_wt.setText("Total (₹" + total + "+ 5% GST)");
        }


    }
    public void backClicked(View view)
    {
        onBackPressed();
    }


}