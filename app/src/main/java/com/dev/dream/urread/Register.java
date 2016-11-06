package com.dev.dream.urread;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dev.dream.urread.app.AppController;
import com.dev.dream.urread.app.app_config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;

public class Register extends AppCompatActivity {

    public EditText NameId;
    public EditText PhoneId;
    public EditText EmailId;
    public EditText PasswordId;
    public Button Register;
    public String Name1;
    public String Phone1;
    public String Email1;
    public String Password1;

    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        NameId = (EditText) findViewById(R.id.NameId);
        PhoneId = (EditText) findViewById(R.id.PhoneId);
        EmailId = (EditText) findViewById(R.id.EmailId);
        PasswordId = (EditText) findViewById(R.id.PasswordId);
        Register = (Button) findViewById(R.id.RegisterId);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Name1 = NameId.getText().toString().trim();
                Phone1 = PhoneId.getText().toString().trim();
                Email1 = EmailId.getText().toString().trim();
                Password1 = PasswordId.getText().toString().trim();

                registerMe(Name1, Phone1, Email1, Password1);
            }
        });
    }

    private void registerMe(final String Name, final String Phone, final String Email, final String Password)
    {
        //Tag used to cancel the request

        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST, app_config.URL_REGISTER, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        showLoadingDialog();
                        
                        // Launch main activity
                        Intent intent = new Intent(
                                Register.this, Login.class);
                        startActivity(intent);

                        finish();
                    } else {
                        dismissLoadingDialog();
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    dismissLoadingDialog();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("Name", Name1);
                params.put("Phone", Phone1);
                params.put("email", Email1);
                params.put("Password", Password1);

                return params;
            }

        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void showLoadingDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("SIGNING UP");
        }
        progress.show();
    }
    public void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
}

