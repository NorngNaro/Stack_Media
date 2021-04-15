package com.naro.newsocial.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.databinding.ActivityEditProfileBinding;

import static com.facebook.GraphRequest.TAG;


public class Edit_profile_Activity extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    private static final int REQUEST_CODE = 1;
    public Uri imageUri;
    private boolean imageFireStore = false;
    private boolean imagePick = false;
    private String url;
    private UserModel userModel;
    private FirebaseAuth mAuth;
    private String userPostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        userQuery(mAuth.getUid());
        saveClick();


        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePick();
            }
        });



        binding.editEmail.setEnabled(false);


    }


    private void saveClick() {

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: image FireStore "+ imageFireStore );
                if(imageFireStore){
                    Log.e(TAG, "Save Click : have an old photo " );
                    if(imagePick == true){
                        deleteImage(userModel.getImageUrl());
                        imageUpload();
                    }
                    editData(userPostID);

                }else{
                    Log.e(TAG, "Save Click : not have image photo " );
                    imageUpload();
                }
            }
        });
    }

    private String checkUrl(String urlCut){
        String urlCheck = urlCut.substring(0,23);
        return urlCheck;
    }


    private void userQuery(String userID) {

        binding.progressBar.setVisibility(View.VISIBLE);
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

                                binding.editUsername.setText(userModel.getUserName());
                                binding.editPhone.setText(userModel.getPhone());
                                binding.editEmail.setText(userModel.getEmail());
                                Glide.with(Edit_profile_Activity.this)
                                        .load(userModel.getImageUrl())
                                        .into(binding.imageProfile);
                                binding.editBio.setText(userModel.getBio());

                                Log.e(TAG, "onComplete: check url "+ checkUrl(userModel.getImageUrl()) );

                                if (checkUrl(userModel.getImageUrl()).equals("https://firebasestorage")){
                                    imageFireStore = true;
                                    Log.e(TAG, "Check work " );
                                }

                                userPostID = documentSnapshot.getId();
                                Log.e("TAG", "onComplete: " + userPostID);

                                // Hide progress bar
                                binding.progressBar.setVisibility(View.INVISIBLE);

                            }
                        } else {
                            Toast.makeText(Edit_profile_Activity.this, "Have something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void editData(String userPostID) {

        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbUpdatePost = dbFireStore.collection("User");

        String userName = binding.editUsername.getText().toString();
        String bio = binding.editBio.getText().toString();
        String phone = binding.editPhone.getText().toString();

        if (imagePick == false){
            url = userModel.getImageUrl();
        }

        Log.e(TAG, "Log url"+ url );

        dbUpdatePost
                .document(userPostID)
                .update("userName", userName, "bio", bio, "phone", phone , "imageUrl" , url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Edit_profile_Activity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Edit_profile_Activity.this, "Have something wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void imagePick() {
        Intent intent = new Intent();
        intent.setType("image/Gallery/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.imageProfile.setImageURI(imageUri);
            Log.e("TAG", "onActivityResult: work " + imageUri);
            imagePick = true;

        }

    }

    private String getFile(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void imageUpload() {

        Log.e(TAG, "imageUpload: work " );

        dialog();

        if (imageUri != null) {
            StorageReference storeRef = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getFile(imageUri));

            storeRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onSuccess(Uri uri) {
                            url = uri.toString();
                            editData(userPostID);
                        }
                    });
                }
            });
        }
    }

    private void deleteImage(String profileUrl){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(profileUrl);
        Log.e(TAG, "deleteImage: "+profileUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Picture","#deleted");
            }
        });
    }



    private void dialog(){

        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Profile editing....")
                .show();
    }




}