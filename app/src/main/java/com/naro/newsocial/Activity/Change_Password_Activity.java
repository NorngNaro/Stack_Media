package com.naro.newsocial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naro.newsocial.databinding.ActivityChangePasswordBinding;

public class Change_Password_Activity extends AppCompatActivity {
private ActivityChangePasswordBinding binding;
private String userPostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        Intent intent = getIntent();
        userPostID = intent.getExtras().getString("userPostID");


        saveData();


        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }


    private void saveData(){
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pass = false;
                boolean repass = false;
                if(binding.inputPassword.getText().toString().length()== 0){
                    binding.inputPassword.setError("Password is required!");
                }else {
                    pass = true;
                }
                if(binding.inputRePassword.getText().toString().length()== 0){
                    binding.inputRePassword.setError("Password is required!");
                }else {
                    repass = true;
                }

                if(pass){
                    if (repass){
                        if(binding.inputPassword.getText().toString().equals(binding.inputRePassword.getText().toString())){
                            binding.progressBar.setVisibility(View.VISIBLE);
                            editPassword(userPostID);
                            save_Login();
                            Intent intent = new Intent(Change_Password_Activity.this , Home_Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else {
                            binding.inputPassword.setError("Password not match!");
                            binding.inputRePassword.setError("Password not match!");
                        }
                    }
                }
            }
        });


    }

    private void save_Login(){

        SharedPreferences prefs = getSharedPreferences(Flash_screen_Activity.MyPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putBoolean(Flash_screen_Activity.LOGIN, true);

        myEditor.apply();

    }




    private void editPassword(String userPostID) {

        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbUpdatePost = dbFireStore.collection("User");


        dbUpdatePost
                .document(userPostID)
                .update("password", binding.inputPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Change_Password_Activity.this, "Password saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Change_Password_Activity.this, "Have something wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}