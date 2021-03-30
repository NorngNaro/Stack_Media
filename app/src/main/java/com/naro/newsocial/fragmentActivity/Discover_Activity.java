package com.naro.newsocial.fragmentActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.naro.newsocial.Activity.View_Activity;
import com.naro.newsocial.Adapter.ListHomeActivity;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.R;

import java.util.ArrayList;


public class Discover_Activity extends Fragment {


    private ListHomeActivity listHomeActivity;

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

    swipeDown();
    setUpRecycler();

        Glide.with(discover)
                .load(user.getPhotoUrl())
                .into(profile);

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
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
}





