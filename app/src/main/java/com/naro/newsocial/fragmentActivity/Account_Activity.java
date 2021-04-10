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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naro.newsocial.Activity.Edit_profile_Activity;
import com.naro.newsocial.Activity.Flash_screen_Activity;
import com.naro.newsocial.Activity.Setting_Activity_new;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;

import static com.facebook.GraphRequest.TAG;


public class Account_Activity extends Fragment {

    private ImageView imageButton;
    private TextView username;
    private AppCompatImageView setting;
    private UserModel userModel;
    private TextView bio;
    private View account;
    private ProgressBar progressBar;
    private AppCompatButton editProfile;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       account = inflater.inflate(R.layout.activity_account,container,false);
        // Init view
        imageButton = account.findViewById(R.id.image_profile);
        username = account.findViewById(R.id.username);
        bio = account.findViewById(R.id.bio);
        editProfile = account.findViewById(R.id.edit_profile);
        setting = account.findViewById(R.id.btn_setting);
        progressBar = account.findViewById(R.id.progressBar);


        profile();
        userQuery(user.getUid());
        setting();


        return account;
    }


    @Override
    public void onResume() {
        super.onResume();
        userQuery(user.getUid());

    }




    private void setting(){
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Setting_Activity_new.class);
                startActivity(intent);
            }
        });
    }

    private void profile(){
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Edit_profile_Activity.class));
            }
        });
    }

    private void userQuery(String userID) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore dbFireStoreUser = FirebaseFirestore.getInstance();
        CollectionReference dbUser = dbFireStoreUser.collection("User");

        dbUser
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                userModel = documentSnapshot.toObject(UserModel.class);

                                Log.e("TAG", "onComplete: " + userModel);

                                username.setText(userModel.getUserName());
                                if (userModel.getBio().equals("")){
                                    //TODO
                                }else {
                                    bio.setText(userModel.getBio());
                                }

                                Glide.with(account)
                                        .load(userModel.getImageUrl())
                                        .into(imageButton);

                                // Hide progress bar
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        } else {
                            Toast.makeText(getContext(), "Have something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }



}
