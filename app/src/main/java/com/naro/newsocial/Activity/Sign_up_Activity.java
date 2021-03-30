package com.naro.newsocial.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;

import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivitySignInBinding;
import com.naro.newsocial.databinding.ActivitySignUpBinding;


public class Sign_up_Activity extends Activity {

ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        btn_back();
        login();

    }

    private void btn_back(){

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void login(){
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
