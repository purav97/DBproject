package com.example.purav.buysell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.purav.buysell.Login.urlx;

public class SelectCategory extends Fragment {

    //private String category_list[] = {"Select Category", "Electronics", "Clothes", "Laptop Accessories"};

    //private HashMap<String, ArrayList<String>> cat_subcat_map = new HashMap<String, ArrayList<String>>(category_list.length);

    //private ArrayList<String> empty_list = new ArrayList<String>();

    private JSONArray subcategory_json;
    ArrayList<String> categories;
    ArrayAdapter dataAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_category, container, false);
        try {
            sendJSONArrayRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Spinner spinner = (Spinner) view.findViewById(R.id.category_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Toast.makeText(getActivity(), item.toString(),
                            Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getActivity(), "Selected",
                        Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        categories=new ArrayList<String>();
        dataAdapter=new ArrayAdapter(getActivity(),android.R.layout.select_dialog_singlechoice,categories);
        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                String category = categories.get(i);
                List<String> listItems = new ArrayList<String>();

                try {
                    for(int k=0;k<subcategory_json.length();k++){
                        JSONObject jfk = (JSONObject) subcategory_json.get(k);
                        Log.d("Category names ", jfk.getString("category_name") + "    " + category);
                        if(jfk.getString("category_name").equals(category)){
                            JSONArray subcategory_list = jfk.getJSONArray("subcategories");
                            for(int index = 0;index<subcategory_list.length();index++){
                                listItems.add(subcategory_list.getString(index));
                                Log.d("gsb","LIST ITEMS ENTRY");
                            }
                        }
                    }
                    final CharSequence[] charSequenceItems = listItems.toArray(new CharSequence[listItems.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle("Choose from given subcategories");
                    builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                        }
                    });
                    builder.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void sendJSONArrayRequest() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = urlx + "/GetCategories";

        StringRequest str = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("The response is ", response);
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jobj = new JSONObject(response);
                            boolean success = jobj.getBoolean("status");
                            if (success) {
                                JSONArray json = jobj.getJSONArray("categories");
                                subcategory_json = jobj.getJSONArray("subcategories");
                                for(int i = 0; i < json.length(); i++){
                                    categories.add(json.getJSONObject(i).getString("category_name"));
                                }
                                dataAdapter.notifyDataSetChanged();
                                Log.d("dvdb","GOT CATEGORIES");
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
                return params;
            }
        };
        queue.add(str);

    }
}
