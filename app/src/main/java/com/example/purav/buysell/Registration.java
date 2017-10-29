package com.example.purav.buysell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;


import static com.example.purav.buysell.Login.urlx;

public class Registration extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void registerCredentials(View v) {

        Button reg_btn = (Button) findViewById(R.id.registration_button);
        reg_btn.setEnabled(false);

        EditText field_uid = (EditText) findViewById(R.id.uid);
        String uid = field_uid.getText().toString();

        EditText field_user_name = (EditText) findViewById(R.id.reg_email_id);
        String username = field_user_name.getText().toString();

        EditText field_password_1 = (EditText) findViewById(R.id.password_1);
        String password1 = field_password_1.getText().toString();

        EditText field_password_2 = (EditText) findViewById(R.id.password_2);
        String password2 = field_password_2.getText().toString();

        EditText field_common_name = (EditText) findViewById(R.id.common_name);
        final String common_name = field_common_name.getText().toString();

        if (!password1.equals(password2)) {
            Toast.makeText(getApplicationContext(), "The two password Fields have Different Entries", Toast.LENGTH_SHORT).show();
            reg_btn.setEnabled(true);
            return;
        }

        sendRegForm(username, password1, common_name, uid);

    }

    public void sendRegForm(final String username, final String password, final String common_name, final String uid){

        final Button reg_btn = (Button) findViewById(R.id.registration_button);
        reg_btn.setEnabled(false);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlx + "/Register";

        StringRequest str = new StringRequest(Request.Method.POST,  url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("The response is ", response);

                        try{
                            JSONObject jobj = new JSONObject(response);
                            Boolean successlogin = jobj.getBoolean("status");
                            if(successlogin.equals(true)){
                                Toast.makeText(getApplicationContext(), "You have Successfully Registered. Login with your email & password", Toast.LENGTH_LONG).show();
                                reg_btn.setEnabled(true);
                                Intent i = new Intent(getApplicationContext(), Login.class);
                                startActivity(i);
                            }
                            else{
                                String message = jobj.getString("message");
                                String duplicate_user = jobj.getString("duplicate_user");
                                if(duplicate_user.equals("yes") ) {
                                    EditText field_uid = (EditText) findViewById(R.id.uid);
                                    field_uid.setError(Html.fromHtml("<font color='red'>" + message + "</font>"));
                                }else{
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                                reg_btn.setEnabled(true);
                            }
                        }
                        catch (JSONException jsonex){
                            Log.e("Error in Json Parsing", "Shit");
                        }
                        Log.e("done with response", "yaaay");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error In HTTP Response", "Shit");
                        Log.e("Error", error.toString());
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        reg_btn.setEnabled(true);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", username);
                params.put("password", password);
                params.put("name", common_name);
                params.put("id", uid);
                return params;
            }
        };

        queue.add(str);

    }

}

