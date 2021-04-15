package com.naro.newsocial.fragmentActivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.naro.newsocial.Activity.View_Activity;
import com.naro.newsocial.Adapter.ListAllPost;
import com.naro.newsocial.Adapter.ListHomeActivity;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;

import static com.facebook.GraphRequest.TAG;


public class Myblog_Activity extends Fragment  {


    private View myBlog;
    private ListAllPost listAllPost;
    private ProgressBar loading;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostModel postModel;
    private UserModel userModel;
    private   AppCompatImageView profile;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myBlog = inflater.inflate(R.layout.activity_myblog, container, false);

        loading = myBlog.findViewById(R.id.progressBar);
        swipeRefreshLayout = myBlog.findViewById(R.id.swipe);

        profile = myBlog.findViewById(R.id.pic_user);

        userQuery(user.getUid());

        setUpRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                setUpRecyclerView();
            }
        });





        return myBlog;

    }



    private void userQuery(String userID) {

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

                                Glide.with(myBlog)
                                        .load(userModel.getImageUrl())
                                        .into(profile);

                            }
                        } else {
                            Toast.makeText(getContext(), "Have something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void setUpRecyclerView(){



        FirebaseFirestore dbPost = FirebaseFirestore.getInstance();
        CollectionReference dbViewPost = dbPost.collection("Post");

        Query query = dbViewPost.whereEqualTo("userID",user.getUid()).orderBy("date", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();
         listAllPost = new ListAllPost(options);
        RecyclerView recyclerView = myBlog.findViewById(R.id.my_blog_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listAllPost);




        listAllPost.setOnItemClickListener(new ListAllPost.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(getContext(), View_Activity.class);
                intent.putExtra("postID", documentSnapshot.getId());
                startActivity(intent);

            }
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO
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
        loading.setVisibility(View.INVISIBLE);


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
    public void onResume() {
        super.onResume();
        listAllPost.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        listAllPost.stopListening();
    }
}