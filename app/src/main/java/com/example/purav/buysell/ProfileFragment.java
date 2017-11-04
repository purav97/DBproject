package com.example.purav.buysell;

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.util.Base64;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.app.Fragment;
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
    public View view;
    public Bitmap bitmap;
    public boolean image_set = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile , container, false);
        final ImageView image = (ImageView) view.findViewById(R.id.post_add_image);
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!image_set) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });
        Button post_add = (Button) view.findViewById(R.id.post_add_button);
        post_add.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
//                final EditText editusername = view.findViewById(R.id.post_content);
//                final String content = editusername.getText().toString();
//                if (content == null){
//                    Toast.makeText(getActivity().getApplicationContext(), "Empty Text", Toast.LENGTH_LONG).show();
//                }
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String url = urlx+ "/CreatePost";
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
                                        Toast.makeText(getActivity(), "Post Added", Toast.LENGTH_LONG).show();
//                                        editusername.setText("");
                                    }
                                    else{
                                        Toast.makeText(getActivity(), "Could't add Post", Toast.LENGTH_LONG).show();
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
                        if(image_set) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();
                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                            params.put("image", encodedImage);
                        }
                        return params;
                    }
                };
                queue.add(str);
            }
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data)
        {
            Uri selectedImg = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImg);
                ImageView imgcover = (ImageView) view.findViewById(R.id.post_add_image);
                imgcover.setImageBitmap(bitmap);
                image_set = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
