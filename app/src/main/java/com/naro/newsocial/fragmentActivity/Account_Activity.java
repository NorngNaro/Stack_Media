package com.naro.newsocial.fragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naro.newsocial.Activity.CreateArticleActivity;
import com.naro.newsocial.Activity.Edit_profile_Activity;
import com.naro.newsocial.Activity.Flash_screen_Activity;
import com.naro.newsocial.Activity.Setting_Activity_new;
import com.naro.newsocial.Activity.View_Activity;
import com.naro.newsocial.Adapter.ListAllPost;
import com.naro.newsocial.Adapter.ListHomeActivity;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;

import static com.facebook.GraphRequest.TAG;


public class Account_Activity extends Fragment {

    private ListAllPost listAllPost;
    private PostModel postModel;
    private ImageView imageButton;
    private TextView username;
    private AppCompatImageView setting;
    private UserModel userModel;
    private TextView bio;
    private View account;
    private ProgressBar progressBar;
    private AppCompatButton editProfile;
    private RecyclerView recyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       account = inflater.inflate(R.layout.activity_account,container,false);
        // Init view
        imageButton = account.findViewById(R.id.image_profile);
        username = account.findViewById(R.id.username);
        bio = account.findViewById(R.id.bio);
        editProfile = account.findViewById(R.id.edit_profile);
        setting = account.findViewById(R.id.btn_setting);
        progressBar = account.findViewById(R.id.progressBar);


        profile();
        userQuery(user.getUid());
        setting();

        setUpRecycler();


        return account;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
        userQuery(user.getUid());
        listAllPost.startListening();

    }




    private void setting(){
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Setting_Activity_new.class);
                startActivity(intent);
            }
        });
    }

    private void profile(){
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Edit_profile_Activity.class));
            }
        });
    }

    private void userQuery(String userID) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

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

                                Log.e("TAG", "onComplete: " + userModel.getUserID());

                                username.setText(userModel.getUserName());
                                if (userModel.getBio().equals("")){
                                    //TODO
                                }else {
                                    bio.setText(userModel.getBio());
                                }

                                Glide.with(account)
                                        .load(userModel.getImageUrl())
                                        .into(imageButton);
                                // Hide progress bar
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        } else {
                            Toast.makeText(getContext(), "Have something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void setUpRecycler() {

        FirebaseFirestore dbPost = FirebaseFirestore.getInstance();
        CollectionReference dbViewPost = dbPost.collection("Post");

        Log.e(TAG, "setUpRecycler: before query");

        Query query = dbViewPost.whereEqualTo("userID",user.getUid()).orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();
        listAllPost = new ListAllPost(options);
        Log.e(TAG, "setUpRecycler: Option "+options );
        recyclerView = account.findViewById(R.id.accountRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listAllPost);


        Log.e(TAG, "setUpRecycler: after query");


        listAllPost.setOnItemClickListener(new ListAllPost.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

//                Toast.makeText(getContext(),
//                        "Position: " + position + " ID: " + id, Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(getContext(), View_Activity.class);
                intent.putExtra("postID", documentSnapshot.getId());
                startActivity(intent);

            }

        });


        listAllPost.setOnEditClickListener(new ListAllPost.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(getContext(), CreateArticleActivity.class);
                intent.putExtra("postID", documentSnapshot.getId());
                intent.putExtra("action", "Edit");
                startActivity(intent);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO
            }
        });

        listAllPost.setOnDeleteClickListener(new ListAllPost.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                Log.e(TAG, "onItemClick: "+ documentSnapshot.getId() +position );

                new AlertDialog.Builder(getContext())
                        .setTitle("Delete this post?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                postQuery(documentSnapshot.getId(),position);

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

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        listAllPost.startListening();


    }



    private void postQuery(String postID , int position){

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

                            Log.e(TAG, "onComplete: delete "+postModel.getUrl() );

                            Log.e(TAG, "onComplete: delete "+snapshot.getData());

                            if(postModel.getUrl() != null){
                                deletePost(postID , position);
                                deleteImage(postModel.getUrl());
                            }

                        }else {
                            Log.e(TAG, "onComplete: Not Work" );
                            Toast.makeText(getContext(), "Have something error!", Toast.LENGTH_SHORT).show();
                        }
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

    private void deletePost(String postID , int position){
        FirebaseFirestore dbFireStore = FirebaseFirestore.getInstance();
        CollectionReference dbPost = dbFireStore.collection("Post");

        dbPost
                .document(postID)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){



                            listAllPost.deleteItem(position);
                            Toast.makeText(getContext(), "Post Deleted", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(getContext(), "Post Can Not Delete", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }



    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: " );
        listAllPost.stopListening();
    }



}
