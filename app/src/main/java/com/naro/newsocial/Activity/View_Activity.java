package com.naro.newsocial.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivityViewBinding;
import static com.facebook.GraphRequest.TAG;


public class View_Activity extends Activity {
        private ActivityViewBinding binding;
        private PostModel postModel;
        private UserModel userModel;
        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityViewBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);

            String postID = getIntent().getStringExtra("postID");
            postQuery(postID);

            binding.btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            binding.progressBar.setVisibility(View.VISIBLE);


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




    }

