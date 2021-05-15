package com.naro.newsocial.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivitySignInBinding;


public class Sign_in_Activity extends AppCompatActivity {


    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ActivitySignInBinding binding;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private boolean newUser = true;
    private boolean checkUser = true;
    private String spinner;
    private String[] countryList ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        countryList = getResources().getStringArray(R.array.spinnerItems);

        spinnerItemSelected();
        loginClick();


        binding.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sign_in_Activity.this,Sign_up_Activity.class);
                startActivity(intent);
            }
        });

        binding.forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sign_in_Activity.this, Find_Account_Activity.class);
                startActivity(intent);
            }
        });


    }




    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        Log.d("DEIP","request code = 9001? ="+requestCode);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG," isSucess? result.toString  ="+result.toString());
            Log.d(TAG," +result.isSuccess()   ="+result.isSuccess());
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
                Log.d(TAG,"Failed into onActivityResult");
            }
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser user) {

           Log.d(TAG, "Display Name" + user.getDisplayName());
           Log.d(TAG, "Display Email" + user.getEmail());
           Log.d(TAG, "Display Phone" + user.getPhoneNumber());
           Log.d(TAG, "Display Phone" + user.getPhotoUrl());
           Log.d(TAG, "Display Phone" + user.getUid());
           createUser(user);
           save_Login();

           Intent intent = new Intent(this,Home_Activity.class);
           startActivity(intent);

    }

    private void save_Login(){

        SharedPreferences prefs = getSharedPreferences(Flash_screen_Activity.MyPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor myEditor = prefs.edit();
        myEditor.putBoolean(Flash_screen_Activity.LOGIN, true);

        myEditor.apply();

    }

    private void createUser(FirebaseUser user){
        // Write a message to the database
        FirebaseFirestore dbFireStoreUser = FirebaseFirestore.getInstance();

        CollectionReference dbSignIn = dbFireStoreUser.collection("User");


        String username = user.getDisplayName();
                String email = user.getEmail();
                String phone = user.getPhoneNumber();
                if (phone == null){
                    phone = "";
                }
                String bio ="";
                String imageUrl = user.getPhotoUrl().toString();
                String userID = user.getUid();
                String password = "";


                 UserModel userModel = new UserModel(username,email,phone,imageUrl,userID,bio,password);


        dbSignIn
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (DocumentSnapshot snapshot : task.getResult()){
                                Log.e(TAG, "Data"+snapshot.getData() );
                                newUser = false;
                            }

                            Log.e(TAG, "After query: " + newUser );
                            if(newUser){
                                createDatabase(userModel,user,dbSignIn);
                            }


                        }else {
                            Log.e(TAG, "This user is already not have in data base");

                        }

                    }
                });


                   Log.e(TAG, "createUser down: "+ newUser );




    }


    private void createDatabase( UserModel userModel , FirebaseUser firebaseUser , CollectionReference dbSignIn){

            dbSignIn.add(userModel)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(Sign_in_Activity.this, "You have SignIn as " + firebaseUser.getDisplayName() , Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Sign_in_Activity.this, "Have something went wrong! " , Toast.LENGTH_SHORT).show();
                        }
                    });



    }




    // Login with database

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


    private void loginClick(){

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.inputPhone.getText().toString().length() == 0){
                    binding.inputPhone.setError("Phone is required!");
                }
                if (binding.inputPassword.getText().toString().length() == 0){
                    binding.inputPassword.setError("Phone is required!");
                }
                if (binding.inputPhone.getText().toString().length() != 0 && binding.inputPassword.getText().toString().length() != 0){
                    checkDataUser();
                }
            }
        });

    }



    private void checkDataUser(){
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
                                if (binding.inputPassword.getText().toString().equals(userModel.getPassword())){
                                    save_Login();
                                    checkUser = false;
                                    Intent intent = new Intent(Sign_in_Activity.this ,Home_Activity.class);
                                    startActivity(intent);
                                }else {
                                    binding.inputPassword.setError("Incorrect Password!");
                                }
                            }
                            if (checkUser){
                                Toast.makeText(Sign_in_Activity.this, "Your phone is not registered yet!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}