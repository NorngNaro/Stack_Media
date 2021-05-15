package com.naro.newsocial.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivitySignUpBinding;

import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


public class Sign_up_Activity extends Activity {

    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    private String spinner;
    private String[] countryList ;
    private boolean newUser = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // init firebase
        mAuth = FirebaseAuth.getInstance();

        // list code country
        countryList = getResources().getStringArray(R.array.spinnerItems);


        // Call function
        btn_back();
        login();
        spinnerItemSelected();
        signUp();


    }

    private void spinnerItemSelected(){

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemSelected: Position" +position );
                Log.e(TAG, "Item Selected"+countryList[position] );
                spinner = countryList[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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





    private void signUp(){
        Log.e(TAG, "signUp: Work " );

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkUsername = false;
                boolean checkPhone = false;
                boolean checkPassword = false;
                boolean checkRePassword = false;

                if(binding.inputUsername.getText().toString().length() == 0){
                    binding.inputUsername.setError("Username is required!");
                }else {
                    checkUsername = true;
                    Log.e(TAG, "checkUsername "+checkUsername );
                }
                if(binding.inputPhone.getText().toString().length() == 0){
                    binding.inputPhone.setError("Phone is required!");
                }else {
                    checkPhone = true;
                    Log.e(TAG, "checkPhone"+checkPhone );
                }
                if(binding.inputPassword.getText().toString().length() < 6){
                    binding.inputPassword.setError("Password 6 digits is required!");
                }else {
                    checkPassword = true;
                    Log.e(TAG, "checkPass "+checkPassword );
                }
                if(binding.inputRePassword.getText().toString().length() < 6){
                    binding.inputRePassword.setError("RePassword 6 digits is required!");
                }else {
                    checkRePassword = true;
                    Log.e(TAG, "checkRePass "+ checkRePassword );
                }

                if(checkUsername){
                    if (checkPhone){
                        if (checkPassword){
                            if (checkRePassword){
                                if(!binding.inputPassword.getText().toString().equals(binding.inputRePassword.getText().toString())){
                                    binding.inputPassword.setError("Password not match!");
                                    binding.inputRePassword.setError("RePassword not match!");
                                    Toast.makeText(Sign_up_Activity.this, "Password and RePassword not match!", Toast.LENGTH_SHORT).show();
                                }else {
                                        Log.e(TAG, "Sign UP " );
                                        createUser();
                                    }

                                }
                            }
                        }
                    }
                }
        });

    }


    private void phoneSignUp(){

        Log.e(TAG, "phoneSignUp: Work" );
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                spinner + binding.inputPhone.getText().toString(),
                60, TimeUnit.SECONDS,
                Sign_up_Activity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        binding.progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(Sign_up_Activity.this, "Have something went wrong!", Toast.LENGTH_SHORT).show();

                    }


                    @Override
                    public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        binding.progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(Sign_up_Activity.this , Verify_Code_Activity.class);
                        String phoneNumber = spinner + binding.inputPhone.getText().toString();
                        intent.putExtra("phoneNumber" , phoneNumber);
                        intent.putExtra("userName", binding.inputUsername.getText().toString());
                        intent.putExtra("password" , binding.inputRePassword.getText().toString());
                        intent.putExtra("verificationID" , verificationID);
                        intent.putExtra("verify","create");
                        startActivity(intent);
                    }
                }
        );

    }




    private void createUser(){
        // Write a message to the database
        FirebaseFirestore dbFireStoreUser = FirebaseFirestore.getInstance();
        CollectionReference dbSignIn = dbFireStoreUser.collection("User");

        dbSignIn
                .whereEqualTo("phone",spinner+binding.inputPhone.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (DocumentSnapshot snapshot : task.getResult()){
                                Log.e(TAG, "Data"+snapshot.getData() );
                                newUser = false;
                                Log.e(TAG, "This user is already have in data base");
                                Toast.makeText(Sign_up_Activity.this, "Please Login you have been registered!", Toast.LENGTH_LONG).show();
                            }
                        }
                        if(newUser){
                            Log.e(TAG, "This user is already not have in data base");
                            binding.progressBar.setVisibility(View.VISIBLE);
                            phoneSignUp();
                        }
                    }
                });

        Log.e(TAG, "createUser down: "+ newUser );

    }


    }



