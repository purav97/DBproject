
package com.example.purav.buysell;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.purav.buysell.Login.urlx;

public class ProfileFragment extends Fragment {

    private static int RESULT_LOAD_IMAGE = 1;
    String username = "";
    public View view;
    public Bitmap bitmap;
    public boolean image_set = false;
    public ImageView image;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile , container, false);
        image = (ImageView) view.findViewById(R.id.post_add_image);
//        Intent i = getActivity().getIntent();
//        username = i.getStringExtra("username");
        username = "12345";
        Log.d("yay","reached profilefragment");
        try {
            if(!image_set)
                sendJSONArrayRequest(username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button post_add = (Button) view.findViewById(R.id.post_add_button);
        post_add.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
                Log.d("aaf","selected image");
            }
        });

        return view;
    }

    private void sendJSONArrayRequest(final String username) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = urlx + "/GetProfileInfo";

        StringRequest str = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("The response is ", response);
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jobj = new JSONObject(response);
                            boolean success = jobj.getBoolean("status");
                            image_set = jobj.getBoolean("image_set");
                            if (success && image_set) {
                                String encodedImg = jobj.getString("profile_photo");
                                byte[] img = Base64.decode(encodedImg, Base64.DEFAULT);
                                Bitmap bmp= BitmapFactory.decodeByteArray(img,0,img.length);
                                image.setImageBitmap(bmp);
                            } else {
                                Toast.makeText(getActivity(),jobj.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonex) {
                            Log.e("Error in Json Parsing", "Shit");
                        }
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
                return params;
            }
        };
        queue.add(str);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data)
        {
            Uri selectedImg = data.getData();
            Log.d("av","GOT PHOTO");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImg);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String url = urlx + "/AddProfilePhoto";
                StringRequest str = new StringRequest(Request.Method.POST,  url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("The response is ", response);
                                // Display the first 500 characters of the response string.
                                try{
                                    JSONObject jobj = new JSONObject(response);
                                    boolean success = jobj.getBoolean("status");
                                    String message = jobj.getString("message");
                                    Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                                    if(success){
                                        image.setImageBitmap(bitmap);
                                        image_set = true;
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
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("content", content);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        Log.d("sfv","SENDING IMAGE TO SERVER");
                        params.put("image", encodedImage);
                        return params;
                    }
                };
                queue.add(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
