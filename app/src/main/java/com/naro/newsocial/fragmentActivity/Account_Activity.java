package com.naro.newsocial.fragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.naro.newsocial.Activity.Edit_profile_Activity;
import com.naro.newsocial.Activity.Flash_screen_Activity;
import com.naro.newsocial.R;


public class Account_Activity extends Fragment {

    private ImageView imageButton;
    private TextView username;
    private TextView bio;
    private View account;
    private AppCompatButton editProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       account = inflater.inflate(R.layout.activity_account,container,false);
        // Init view
        imageButton = account.findViewById(R.id.image_profile);
        username = account.findViewById(R.id.username);
        bio = account.findViewById(R.id.bio);
        editProfile = account.findViewById(R.id.edit_profile);



        profile();
        set_profile(account);


        return account;
    }

    @Override
    public void onResume() {
        super.onResume();

        set_profile(account);

    }

    private void set_profile(View account){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        username.setText(user.getDisplayName());
        Glide.with(account)
                .load(user.getPhotoUrl())
                .into(imageButton);



    }


    private void profile(){
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Edit_profile_Activity.class));
            }
        });
    }

}
