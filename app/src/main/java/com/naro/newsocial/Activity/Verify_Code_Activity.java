package com.naro.newsocial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.databinding.ActivityVerifyCodeBinding;

public class Verify_Code_Activity extends AppCompatActivity {


    ActivityVerifyCodeBinding binding;
    private String phoneNumber;
    private String password;
    private String username;
    private String verificationID;
    private String verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyCodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        Intent intent = getIntent();
        phoneNumber = intent.getExtras().getString("phoneNumber","None");
        password = intent.getExtras().getString("password","None");
        username = intent.getExtras().getString("userName","None");
        verificationID = intent.getExtras().getString("verificationID","None");
        verify = intent.getExtras().getString("verify","None");
        Log.e("TAG", "" + phoneNumber);
        Log.e("TAG", "" + password);
        Log.e("TAG", "" + username);
        Log.e("TAG", "" + verificationID);
        Log.e("TAG", "" + verify);

        binding.txtInfo.setText("Please type the verify code sent via SMS to " + phoneNumber);

        btnSubmit();


        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void btnSubmit(){
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.inputCodeVerify.getText().toString().trim().isEmpty()){
                    Toast.makeText(Verify_Code_Activity.this, "Please input valid code", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code =  binding.inputCodeVerify.getText().toString();

                if (verificationID != null){

                    binding.progressBar.setVisibility(View.VISIBLE);

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationID,
                            code
                    );

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    binding.progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()){
                                        if (verify.equals("create")) {
                                            createDatabase();
                                            save_Login();
                                            Intent intent = new Intent(Verify_Code_Activity.this , Home_Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        if(verify.equals("forgot")){
                                            Intent intent = new Intent(Verify_Code_Activity.this , Change_Password_Activity.class);
                                            intent.putExtra("userPostID",password);
                                            startActivity(intent);
                                        }

                                        if (verify.equals("login")){
                                            save_Login();
                                            Intent intent = new Intent(Verify_Code_Activity.this , Home_Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }

                                    }else {
                                        Toast.makeText(Verify_Code_Activity.this, "The Verification code entered is invalid", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }


            }
        });


    }



    private void createDatabase(){
        FirebaseAuth mAuth =  FirebaseAuth.getInstance();
        FirebaseFirestore dbFireStoreUser = FirebaseFirestore.getInstance();
        CollectionReference dbSignIn = dbFireStoreUser.collection("User");

        String userName = username;
        String email = "";
        String phoneNum = phoneNumber;
        String bio ="";
        String imageUrl = "https://i.stack.imgur.com/l60Hf.png";
        String userID = mAuth.getUid();


        UserModel userModel = new UserModel(userName,email,phoneNum,imageUrl,userID,bio,password,0,0,0);

        dbSignIn.add(userModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Verify_Code_Activity.this, "You have SignIn as " + userName , Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Verify_Code_Activity.this, "Have something went wrong! " , Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void save_Login(){

        SharedPreferences prefs = getSharedPreferences(Flash_screen_Activity.MyPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putBoolean(Flash_screen_Activity.LOGIN, true);

        myEditor.apply();

    }


}