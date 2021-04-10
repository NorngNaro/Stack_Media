package com.naro.newsocial.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivityViewBinding;
import com.naro.newsocial.fragmentActivity.Myblog_Activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.facebook.GraphRequest.TAG;


public class View_Activity extends Activity {
        private ActivityViewBinding binding;
        private PostModel postModel;
        private UserModel userModel;
        private String postID;
        private String url;
    FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityViewBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);




            postID = getIntent().getStringExtra("postID");

            postQuery(postID);

            // On action edit post

            editClick(postID);

            // On delete click

            deleteClick(postID);

            imageClick();




            binding.btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            binding.progressBar.setVisibility(View.VISIBLE);

        }


    private void imageClick(){
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(View_Activity.this , ImageView_Activity.class);
                intent.putExtra("ImageUrl", postModel.getUrl());
                Log.e(TAG, "onClick: URL" + postModel.getUrl() );
                startActivity(intent);

            }
        });
    }




        private void editClick( String PostID){

            binding.btnViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(View_Activity.this, CreateArticleActivity.class);
                    intent.putExtra("postID", postID);
                    intent.putExtra("action", "Edit");
                    startActivity(intent);

                }
            });

        }



        private void deleteClick(String postID){

            binding.btnViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Myblog_Activity myblog_activity = new Myblog_Activity();

                    new AlertDialog.Builder(View_Activity.this)
                            .setTitle("Delete this post?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  //  postQuery(documentSnapshot.getId(),position);

                                    deleteImage(url);
                                    deletePost(postID);
                                    finish();

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(true)
                            .show();

                }
            });

        }


    private void deleteImage(String imageUrl){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(imageUrl);
        Log.e(TAG, "deleteImage: "+imageUrl );
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Picture","#deleted");
            }
        });
    }



    private void deletePost(String postID){
        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStore.collection("Post");

        dbPost
                .document(postID)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(View_Activity.this, "Post Deleted", Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(View_Activity.this, "Post Can Not Delete", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }



    private void postQuery(String postID){
            CollectionReference dbView = dbFireStore.collection("Post");

            dbView
                    .document(postID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot snapshot = task.getResult();
                                postModel = snapshot.toObject(PostModel.class);
                                Log.e(TAG, "onComplete: " + snapshot.getData() );
                                userQuery();
                                binding.title.setText(postModel.getTitle());

                                binding.date.setText(postModel.getDate());
                                binding.text.setText(postModel.getDescription());
                                url = postModel.getUrl();
                                Glide.with(View_Activity.this)
                                        .load(postModel.getUrl())
                                        .into(binding.imageView);


                            }else {
                                Log.e(TAG, "onComplete: Not Work" );
                                Toast.makeText(View_Activity.this, "Have something error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    private void userQuery(){
        CollectionReference dbUser = dbFireStore.collection("User");

        dbUser
                .whereEqualTo("userID", postModel.getUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                userModel = documentSnapshot.toObject(UserModel.class);
                                Glide.with(View_Activity.this)
                                        .load(userModel.getImageUrl())
                                        .into(binding.picUser);
                                binding.writer.setText(userModel.getUserName());
                                binding.progressBar.setVisibility(View.INVISIBLE);

                                checkUser();
                            };
                        }else {
                            Toast.makeText(View_Activity.this, "Have something error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void checkUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Log.e(TAG, "checkUser: "+user.getUid() );
        Log.e(TAG, "checkUser: "+userModel.getUserID() );

            if(!user.getUid().equals(userModel.getUserID())){
                binding.btnViewDelete.setVisibility(View.INVISIBLE);
                binding.btnViewEdit.setVisibility(View.INVISIBLE);
            }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        postQuery(postID);
    }
}

