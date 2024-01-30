package app.zingnow.zingkiosk.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import app.zingnow.zingkiosk.R;

public class ResponsePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_page);

        Toast.makeText(this, "Response Page", Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        String status = intent.getStringExtra("status");
        Toast.makeText(this, "Status : " + status, Toast.LENGTH_LONG).show();

    }
}