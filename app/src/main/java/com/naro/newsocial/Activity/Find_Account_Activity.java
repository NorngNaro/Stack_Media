package com.naro.newsocial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivityFindAccountBinding;

import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class Find_Account_Activity extends AppCompatActivity {
    private ActivityFindAccountBinding binding;
    private String spinner;
    private String[] countryList ;
    private boolean newUser = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        // list code country
        countryList = getResources().getStringArray(R.array.spinnerItems);

        spinnerItemSelected();




        binding.btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.inputPhone.getText().toString().length() !=0){
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnFind.setVisibility(View.INVISIBLE);
                    findAccount();
                }else {
                    binding.inputPhone.setError("Please input phone number!");
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



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



    private void findAccount(){
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
                                UserModel userModel = snapshot.toObject(UserModel.class);

                                String userDataID = snapshot.getId();

                                newUser = false;

                                binding.writer.setText(userModel.getUserName());
                                Glide.with(Find_Account_Activity.this)
                                        .load(userModel.getImageUrl())
                                        .into(binding.picUser);

                                binding.picHolder.setVisibility(View.VISIBLE);
                                binding.writer.setVisibility(View.VISIBLE);
                                binding.txtYourAcc.setVisibility(View.VISIBLE);
                                binding.btnNext.setVisibility(View.VISIBLE);
                                binding.progressBar.setVisibility(View.INVISIBLE);


                                binding.btnNext.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        binding.progressBar.setVisibility(View.VISIBLE);
                                        phoneSignUp(userDataID);
                                    }
                                });


                            }
                        }
                        if(newUser){
                            Log.e(TAG, "This user is already not have in data base");
                            binding.notFound.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                });

        Log.e(TAG, "createUser down: "+ newUser );

    }


    private void phoneSignUp(String userData){

        Log.e(TAG, "phoneSignUp: Work" );
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                spinner + binding.inputPhone.getText().toString(),
                60, TimeUnit.SECONDS,
                Find_Account_Activity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        binding.progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(Find_Account_Activity.this, "Have something went wrong!", Toast.LENGTH_SHORT).show();

                    }


                    @Override
                    public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        binding.progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(Find_Account_Activity.this , Verify_Code_Activity.class);
                        String phoneNumber = spinner + binding.inputPhone.getText().toString();
                        intent.putExtra("phoneNumber" , phoneNumber);
                        intent.putExtra("userName", "null");
                        intent.putExtra("password" , userData);
                        intent.putExtra("verificationID" , verificationID);
                        intent.putExtra("verify","forgot");
                        startActivity(intent);
                    }
                }
        );

    }



}