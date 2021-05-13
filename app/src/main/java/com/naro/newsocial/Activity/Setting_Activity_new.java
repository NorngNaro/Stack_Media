package com.naro.newsocial.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.naro.newsocial.databinding.ActivityAccountBinding;
import com.naro.newsocial.databinding.ActivityCreateBlogBinding;
import com.naro.newsocial.databinding.ActivitySettingNewBinding;

public class Setting_Activity_new extends AppCompatActivity {

    ActivitySettingNewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingNewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



    binding.editAcc.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Setting_Activity_new.this , Edit_profile_Activity.class);
            startActivity(intent);
        }
    });


    binding.btnBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });


        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  FirebaseAuth.getInstance().signOut();
                save_Login();
                Intent intent = new Intent(Setting_Activity_new.this, Sign_in_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });







    }


    private void save_Login(){

        SharedPreferences prefs = getSharedPreferences(Flash_screen_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();

        myEditor.putBoolean(String.valueOf(Flash_screen_Activity.LOGIN),false);
        myEditor.apply();
    }

}
