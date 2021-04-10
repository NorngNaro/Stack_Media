package com.naro.newsocial.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivityCreateBlogBinding;
import com.naro.newsocial.databinding.ActivityImageViewBinding;
import com.naro.newsocial.databinding.ActivityViewBinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.facebook.GraphRequest.TAG;

public class ImageView_Activity extends AppCompatActivity {

    ActivityImageViewBinding binding;
    private String ImageShowUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ImageShowUrl = getIntent().getStringExtra("ImageUrl");

        btnCloseImage();
        setImage(ImageShowUrl);






    }

    private void btnCloseImage(){
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setImage(String imageUrl){
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            binding.imageViewZoom.setImage(ImageSource.bitmap(myBitmap));

        } catch (IOException e) {
            // Log exception
            Log.w(getClass().toString(), e);
        }
    }





}