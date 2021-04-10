package com.naro.newsocial.Activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.databinding.ActivityCreateBlogBinding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.facebook.GraphRequest.TAG;


public class CreateArticleActivity extends AppCompatActivity {

    ActivityCreateBlogBinding binding;
    private static final int REQUEST_CODE = 1;
    public Uri imageUri;
    private DatabaseReference mDatabase;
    private String url;
    private String edit;
    private String postID;
    private PostModel postModel;
    private String message;
    private boolean imageEdit = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBlogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Data from my post Activity
        edit = getIntent().getStringExtra("action");
        postID = getIntent().getStringExtra("postID");

        // Check and update UI
        if(edit.equals("Edit")){
            binding.buttonCreate.setText("Save");
            binding.textTitle.setText("Edit Post");
            message = "Post Updating...";
            postQuery(postID);
        }else {
            message = "Post Uploading...";
        }



        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.imageArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePick();
            }
        });

        binding.buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 boolean title ;
                 boolean description ;
                 boolean image ;

                if(binding.editTitle.getText().toString().equals("")){
                    Toast.makeText(CreateArticleActivity.this, "Please enter title!", Toast.LENGTH_SHORT).show();
                    title = false;
                }else {
                    title = true;
                }
                if(binding.editContent.getText().toString().equals("")) {
                    Toast.makeText(CreateArticleActivity.this, "Please enter description!", Toast.LENGTH_SHORT).show();
                    description = false;
                } else {
                    description = true;
                }

//                if(binding.imageArticle.getDrawable() == imagedra){
//                    Toast.makeText(CreateArticleActivity.this, "Please add image!", Toast.LENGTH_SHORT).show();
//                    image = false;
//                }else {
//                    image = true;
//                }

                if(title && description){

                    if(edit.equals("Edit")){
                        imageUpload();
                        update(postID);
                    }else {
                        uploadNews();
                    }

                }

            }
        });

    }

    private void deleteImage(){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(postModel.getUrl());
        Log.e(TAG, "deleteImage: "+postModel.getUrl() );
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Picture","#deleted");
            }
        });
    }

    private void postQuery(String postID){

        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStore.collection("Post");

        dbPost
                .document(postID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();

                            postModel = snapshot.toObject(PostModel.class);
                            Log.e(TAG, "onComplete: " + snapshot.getData() );

                            binding.editTitle.setText(postModel.getTitle());

                            binding.editContent.setText(postModel.getDescription());
                            Glide.with(CreateArticleActivity.this)
                                    .load(postModel.getUrl())
                                    .into(binding.imageArticle);
                        }else {
                            Log.e(TAG, "onComplete: Not Work" );
                            Toast.makeText(CreateArticleActivity.this, "Have something error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void uploadNews(){
        imageUpload();

    }


    private String getFile(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap  mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
     }



    private void imageUpload(){

      //  binding.progressBar.setVisibility(View.VISIBLE);

        dialog();


        Log.e(TAG, "imageUpload: "+ imageEdit );
        Log.e(TAG, "imageUpload: "+ edit );

        if(edit.equals("Edit") && imageEdit == false){

                Log.e(TAG, "imageUpload: work " );
                url = postModel.getUrl();

        }else {
            if (imageUri != null){
                StorageReference storeRef = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getFile(imageUri));

                storeRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onSuccess(Uri uri) {
                                url = uri.toString();
                                Log.e("TAG", "onSuccess: " + url );
                                uploadData();
                                finish();
                            }
                        });
                    }
                });
            }
        }


    }


    private void update (String postID){

        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbUpdatePost = dbFireStore.collection("Post");

        String title = binding.editTitle.getText().toString();
        String description = binding.editContent.getText().toString();


        dbUpdatePost
                .document(postID)
                .update("title" ,title , "description",description, "url",url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateArticleActivity.this, "Post Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateArticleActivity.this, "Have something wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadData(){

        // Write a message to the database
        FirebaseFirestore dbFireStorePost = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStorePost.collection("Post");


                Log.e("TAG", " onDataChange: " + url );
                // For date
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDateTime now = LocalDateTime.now();

                String title = binding.editTitle.getText().toString();
                String description = binding.editContent.getText().toString();
                String userID = FirebaseAuth.getInstance().getUid();
                String date = dtf.format(now);

                PostModel postModel = new PostModel(title,description,url,userID,date);

                dbPost.add(postModel)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TAG", "onSuccess: Post Success ");
                               // binding.progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateArticleActivity.this ,"Post Uploaded", Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "onSuccess: Post Fail");
                            }
                        });

    }

    private void imagePick(){
        Intent intent = new Intent();
        intent.setType("image/Gallery/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            binding.imageArticle.setImageURI(imageUri);
            Log.e("TAG", "onActivityResult: work "+ imageUri);

            if(edit.equals("Edit")) {
                deleteImage();
                Log.e(TAG, "onActivityResult: delete image" );
                imageEdit = true;
            }

        }

    }


    private void dialog(){

        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(message)
                .show();
    }

}















