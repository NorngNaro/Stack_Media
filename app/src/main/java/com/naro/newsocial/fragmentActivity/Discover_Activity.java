package com.naro.newsocial.fragmentActivity;


import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naro.newsocial.Activity.Sign_in_Activity;
import com.naro.newsocial.Activity.View_Activity;
import com.naro.newsocial.Adapter.ListHomeActivity;
import com.naro.newsocial.Model.LoveModel;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class Discover_Activity extends Fragment {


    private ListHomeActivity listHomeActivity;
    private LoveModel loveModel;
    private UserModel userModel;
    private View discover;
    private ArrayList<PostModel> postList;
    private SwipeRefreshLayout swipe;
    private ProgressBar loading;
    AppCompatImageView profile;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        discover = inflater.inflate(R.layout.activity_discover,container,false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            swipe = discover.findViewById(R.id.swipe);
            profile = discover.findViewById(R.id.pic_user);
            loading = discover.findViewById(R.id.progressBar);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        userQuery(user.getUid());

    swipeDown();
    setUpRecycler();



        return discover;
    }


    private void swipeDown(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
                setUpRecycler();
            }
        });
    }


    private void setUpRecycler(){

        FirebaseFirestore dbPost = FirebaseFirestore.getInstance();
        CollectionReference dbViewPost = dbPost.collection("Post");

        Query query = dbViewPost.orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();
        listHomeActivity = new ListHomeActivity(options);
        RecyclerView recyclerView = discover.findViewById(R.id.rcv_list_article);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listHomeActivity);

        listHomeActivity.startListening();

        loading.setVisibility(View.INVISIBLE);

        listHomeActivity.setOnItemClickListener(new ListHomeActivity.OnItemClickListener() {


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


        listHomeActivity.setOnLoveClickListener(new ListHomeActivity.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    addLove(documentSnapshot.getId());
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addLove(String postID){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore dbPost = FirebaseFirestore.getInstance();
        CollectionReference dbAllPost = dbPost.collection("Post").document(postID).collection("love");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();

         loveModel = new LoveModel(user.getUid(),dtf.format(now));

        dbAllPost.add(loveModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Loved" , Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Have something went wrong! " , Toast.LENGTH_SHORT).show();
                    }
                });


    }




    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "onResume: Working " );
        listHomeActivity.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        listHomeActivity.startListening();
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

                                Glide.with(discover)
                                        .load(userModel.getImageUrl())
                                        .into(profile);

                            }
                        } else {
                            Toast.makeText(getContext(), "Have something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

}





