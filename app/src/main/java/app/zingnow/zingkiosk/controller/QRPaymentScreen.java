package app.zingnow.zingkiosk.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.model.Menu;
import app.zingnow.zingkiosk.utils.ApiCallback;
import app.zingnow.zingkiosk.utils.ApiCaller;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QRPaymentScreen extends AppCompatActivity implements ApiCallback {

    private static final String AUTH_TOKEN = "Basic OTk3RjUwQkI4OTM0NEY5QkE0Rjg5RTkyRDM3QTM2Og=="; // Replace with your authorization token
    private static final String merchant_id = "zingnow";
    private ApiCaller apiCaller;
    private static final int TOTAL_DURATION_MILLISECONDS = 600000; // 10 minutes

    ArrayList<Menu> all_menu_list = new ArrayList<Menu>();

    private TextView timer_textview;
    private CountDownTimer count_downtimer;
   long initialTimeMillis = 15 * 60 * 1000; // 15 minutes in milliseconds
//long initialTimeMillis =  10000;

    RelativeLayout cancel_transaction;




    //Timer Handler for expiry of session
    //Back button handle with dialog
    //continous api fetch
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpayment_screen);

        Intent intent = getIntent();
        String upiCode = intent.getStringExtra("finalUPI");
        String order_id = intent.getStringExtra("orderId");
        all_menu_list = (ArrayList<Menu>) getIntent().getSerializableExtra("menuList");
        timer_textview = findViewById(R.id.timer_time);
        cancel_transaction = findViewById(R.id.cancel_button);
        timer_textview.setPaintFlags(timer_textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        ImageView imageView = findViewById(R.id.imageView);
        // Generate QR code and set it to the ImageView
        Bitmap bitmap = generateQRCode(upiCode);
        imageView.setImageBitmap(bitmap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        count_downtimer = new CountDownTimer(initialTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining time
                 updateTimerText(millisUntilFinished);

            }

            @Override
            public void onFinish() {
                // Timer finished, perform any action needed
                Intent intent1 = new Intent(getApplicationContext(), FinalScreen.class);
                intent1.putExtra("status","Failed");
                intent1.putExtra("menuList",(Serializable) all_menu_list);
                startActivity(intent1);
            }
        };

        // Start the timer
        count_downtimer.start();


        apiCaller = new ApiCaller(this, order_id);
        apiCaller.startApiCalls();

    }

    private void updateTimerText(long millisUntilFinished) {
        // Calculate minutes and seconds from milliseconds
        long minutes = millisUntilFinished / 1000 / 60;
        long seconds = (millisUntilFinished / 1000) % 60;

        // Update the TextView with the remaining time
        timer_textview.setText(String.format("valid for %02d:%02d", minutes, seconds));
    }

    @Override
    public void onDesiredStatusReceived(JSONObject response) {
                    try {
                            Toast.makeText(QRPaymentScreen.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                            Log.e("Status: " , response.getString("status"));
                            Intent intent1 = new Intent(getApplicationContext(),FinalScreen.class);
                            intent1.putExtra("menuList",(Serializable) all_menu_list);
                            intent1.putExtra("status","success");
                            startActivity(intent1);
                            //statusTextView.setText("Desired Status Received: " + response.getString("status"));
                        }
                    catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
    }

    @Override
    public void onTimeout() {
        Intent intent1 = new Intent(getApplicationContext(), FinalScreen.class);
        intent1.putExtra("status","TimedOut");
        intent1.putExtra("menuList",(Serializable) all_menu_list);
        startActivity(intent1);

    }

    private Bitmap generateQRCode(String text) {
        try {
            // Set up the QR code writer
            QRCodeWriter writer = new QRCodeWriter();

            // Configure encoding hints (optional)
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 2); // Set margin

            // Generate the BitMatrix (matrix of bits)
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints);

            // Create a Bitmap from the BitMatrix
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showQuitConfirmationDialog();
    }

    private void showQuitConfirmationDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to quit transactions?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked Yes
                            // Call a function or use Intent to navigate to the next activity
                            navigateToNextActivity();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked No
                            // Dismiss the dialog, nothing will happen
                            dialog.dismiss();
                        }
                    });
            // Create and show the AlertDialog
            builder.create().show();
        }
    }


    private void navigateToNextActivity() {
        // Replace NextActivity.class with the actual activity you want to navigate to
        Intent intent = new Intent(this, FinalScreen.class);
        intent.putExtra("status","Failed");
        intent.putExtra("menuList",(Serializable) all_menu_list);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // Cancel the timer to prevent memory leaks when the activity is destroyed
        if (count_downtimer != null) {
            count_downtimer.cancel();
        }
        super.onDestroy();
    }

    public void cancelTransaction(View view)
    {
        if (count_downtimer != null) {
            count_downtimer.cancel();
        }
        Intent intent = new Intent(getApplicationContext(), FinalScreen.class);
        intent.putExtra("status","Failure");
        intent.putExtra("menuList",(Serializable) all_menu_list);
        startActivity(intent);
    }
}