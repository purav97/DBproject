package com.example.purav.buysell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class SellFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private LinearLayout layout;
    private Button btnAddPhots;
    int selected_images = 0;
    private final int PICK_IMAGE_MULTIPLE = 1;
    private Bitmap bitmap;
    //SliderLayout sliderLayout;
    //HashMap<String,String> Hash_file_maps ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell, container, false);
        //lnrImages = (LinearLayout) view.findViewById(R.id.lnrImages);
        btnAddPhots = (Button) view.findViewById(R.id.btnAddPhots);
        //btnSaveImages = (Button) view.findViewById(R.id.btnSaveImages);
        btnAddPhots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
        layout = (LinearLayout) view.findViewById(R.id.linear);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            ImageView imageView = new ImageView(getActivity());
            imageView.setId(selected_images);
            selected_images = selected_images + 1;
            imageView.setPadding(2, 2, 2, 2);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                layout.addView(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
