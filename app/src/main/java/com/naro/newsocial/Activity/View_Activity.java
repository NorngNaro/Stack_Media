package com.naro.newsocial.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naro.newsocial.Model.LoveModel;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivityViewBinding;
import com.naro.newsocial.fragmentActivity.Search_Activity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.facebook.GraphRequest.TAG;


public class View_Activity extends Activity {
        private ActivityViewBinding binding;
        private PostModel postModel;
        private UserModel userModel;
        private String postID;
        private String url;
        private boolean love = false;
        private boolean create = false;
        private boolean createLove = false;
        private boolean cancelLove = false;
        private boolean delete = false;
        private int newLove;
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

            checking_love();



            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(!delete){
                        checkPostForView();
                    }

                }
            }, 5000);



            binding.btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(View_Activity.this, "This function is not available now! ", Toast.LENGTH_SHORT).show();
                }
            });



            binding.btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });
            binding.progressBar.setVisibility(View.VISIBLE);


            // User click on love button
            btnLove_click();
        }



    private void checkPost(){
        CollectionReference dbView = dbFireStore.collection("Post");


        dbView
                .document(postID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            PostModel newPostModel = snapshot.toObject(PostModel.class);

                            newLove = newPostModel.getLove();

                        }
                    }
                });
    }


    private void checkPostForView(){
        CollectionReference dbView = dbFireStore.collection("Post");


        dbView
                .document(postID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            PostModel newPostModel = snapshot.toObject(PostModel.class);

                            addView(newPostModel.getView());

                        }
                    }
                });
    }


        private void addView( int newLove){
            CollectionReference dbView = dbFireStore.collection("Post");
            int result = newLove + 1;
            dbView.document(postID)
                    .update("view",result)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "Add view complete");
                        }
                    });
        }



    private void checking_love(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStore.collection("Post").document(postID).collection("love");
        dbPost.whereEqualTo("lover" , mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.e(TAG, "onComplete: check love " );
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.e(TAG, "onComplete: Check love finish" );
                                love = true;
                                binding.btnLove.setImageResource(R.drawable.loved);

                            }
                        }
                    }
                });
    }



    private void cancel_love(String loveID){
        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStore.collection("Post").document(postID).collection("love");

        dbPost.document(loveID)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.e(TAG, "onComplete: love Cancel " );
                        }

                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void create_Love(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore dbFireStorePost = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStorePost.collection("Post").document(postID).collection("love");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        LoveModel loveModel = new LoveModel(mAuth.getUid() , date );
        dbPost.add(loveModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "onSuccess: Love created " );
                    }
                });
    }


    private void check_love(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (!delete){
            checkPost();
        }

        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStore.collection("Post").document(postID).collection("love");
        dbPost.whereEqualTo("lover" , mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.e(TAG, "onComplete: check love " );
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String loveID = documentSnapshot.getId();
                                Log.e(TAG, "onComplete: Check love finish" );
                                create = true;
                                if(cancelLove){
                                    cancel_love(loveID);
                                    add_love(newLove , -1);
                                }
                            }
                            if (createLove && !create){
                                create_Love();
                                add_love(newLove , 1);
                            }
                        }
                    }
                });
    }



    @Override
    protected void onPause() {
        super.onPause();
        check_love();
    }

    private void add_love(int newLove, int update){
        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStore.collection("Post");
        int new_update = newLove + update;

        dbPost.document(postID)
                .update("love" ,  new_update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "onSuccess: love is updated " );
                    }
                });
    }



    private void btnLove_click(){
        binding.btnLove.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String currentLove = binding.textLove.getText().toString();

                if(love){
                    binding.btnLove.setImageResource(R.drawable.not_love);
                    binding.textLove.setText((Integer.parseInt(currentLove) - 1) + "");
                    love = false;
                    cancelLove = true;
                }else {
                    binding.btnLove.setImageResource(R.drawable.loved);
                    binding.textLove.setText((Integer.parseInt(currentLove) + 1) + "");
                    love = true;
                    createLove = true;
                }

            }
        });

    }


    private void imageClick(){
            binding.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    binding.progressBar.setVisibility(View.VISIBLE);
                    Intent intent =  new Intent(View_Activity.this , ImageView_Activity.class);
                    intent.putExtra("ImageUrl", postModel.getUrl());
                    Log.e(TAG, "onClick: URL" + postModel.getUrl() );
                    startActivity(intent);

                }
            });
        }




        private void editClick(String PostID){

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

                    new AlertDialog.Builder(View_Activity.this)
                            .setTitle("Delete this post?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  //  postQuery(documentSnapshot.getId(),position);

                                    deleteImage(url);
                                    deletePost(postID);
                                    delete = true;
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
                                binding.textShowView.setText(postModel.getView()+"");
                                url = postModel.getUrl();
                                Glide.with(View_Activity.this)
                                        .load(postModel.getUrl())
                                        .into(binding.imageView);
                                binding.textLove.setText(postModel.getLove()+"");

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
        binding.progressBar.setVisibility(View.INVISIBLE);
    }
}

