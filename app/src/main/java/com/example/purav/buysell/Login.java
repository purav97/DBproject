package com.example.purav.buysell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Login extends AppCompatActivity {

    public static final String urlx = "http://172.20.10.5:8080/Buy_Sell";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    private void sendJSONArrayRequest(final String username, final String password) throws JSONException
    {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = urlx + "/Login";

        StringRequest str = new StringRequest(Request.Method.POST,  url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("The response is ", response);
                        // Display the first 500 characters of the response string.
                        try{
                            JSONObject jobj = new JSONObject(response);
                            Boolean successlogin = jobj.getBoolean("status");
                            if(successlogin.equals(true)){
                                String userid = jobj.getString("data");
                                Toast.makeText(getApplicationContext(), "You have logged in user " + userid, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(),Home.class);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Wrong Username/Password", Toast.LENGTH_LONG).show();
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", username);
                params.put("password", password);
                return params;
            }
        };

// Add the request to the RequestQueue.



//        Log.e("The cookie:", cookieManager.toString());

        queue.add(str);

    }

    public void startRegisterActivity(View v)
    {
        Intent i = new Intent(getApplicationContext(), Registration.class);
        startActivity(i);
    }

    public void onClickLogin(View v)
    {
        EditText editusername = (EditText) findViewById(R.id.username) ;
        String username = editusername.getText().toString();
        EditText editpassword = (EditText) findViewById(R.id.password) ;
        String password = editpassword.getText().toString();

        Log.e("The username is: ",username);
        Log.e("The password is: ",password);

        try {
            sendJSONArrayRequest(username, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
