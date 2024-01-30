package app.zingnow.zingkiosk.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import app.zingnow.zingkiosk.R;


public class LoginPage extends AppCompatActivity {

    //Defining Views
    TextInputEditText email_input;
    TextInputEditText password_input;
    MaterialButton login_button;


    //Defining Required Functional Instances
    FirebaseAuth m_auth;
    FirebaseUser user;

    //Defining Required variables
    String email;
    String password;
    String auth_id;

    //Defining const variables
    String url = "https://zingkiosk.onrender.com/deviceLogin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        setUI();

    }

    private void setUI()
    {
        //Initalising views
        email_input = findViewById(R.id.emailEditText);
        password_input = findViewById(R.id.passwordEditText);
        login_button = findViewById(R.id.loginButton);


        //Make transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


    }

    public void loginFunction(View view)
    {
        Boolean is_valid_credentials = checkvalidCredentials();

        if(is_valid_credentials)
        {
            deviceLogin();
        }
        else
        {
            Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    public void deviceLogin() {
        fetchData();
    }


    public void fetchData()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest json_object_response = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                              Log.e("Response : " , response.toString());
                              String device_id = response.getString("_id");
                              saveCompanyData(device_id);
                        } catch (Exception e) {
                            Log.e("HERE:", e.getLocalizedMessage());
                            Toast.makeText(getApplicationContext(), "Error fetching response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(getApplicationContext(), "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", email);
                    jsonBody.put("password",password);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException | org.json.JSONException e) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of the request body");
                    return null;
                }
            }
        };
        queue.add(json_object_response);


    }

    private void saveCompanyData(String deviceId) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Put the access token string into the editor
        editor.putString("device_id", deviceId);

        // Apply the changes
        editor.apply();
        editor.commit();
        updateUI();
    }

    private void updateUI()
    {
        Intent intent = new Intent(getApplicationContext(),ScreenSaver.class);
        startActivity(intent);
    }

    private Boolean checkvalidCredentials() {
         email = email_input.getText().toString();
         password = password_input.getText().toString();

        if(email.length()>0 && password.length()>=6)
        {
            return true;
        }
        else
            return false;
    }
}