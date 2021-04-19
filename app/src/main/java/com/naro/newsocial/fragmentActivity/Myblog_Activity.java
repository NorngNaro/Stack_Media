package com.naro.newsocial.fragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naro.newsocial.Activity.View_Activity;
import com.naro.newsocial.Adapter.ListHomeActivity;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;
import static com.facebook.GraphRequest.TAG;


public class Myblog_Activity extends Fragment  {

    private View myBlog;
    private ProgressBar loading;
    private AppCompatEditText searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserModel userModel;
    private AppCompatImageView profile;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myBlog = inflater.inflate(R.layout.activity_myblog, container, false);

        // Init view
        loading = myBlog.findViewById(R.id.progressBar);
        swipeRefreshLayout = myBlog.findViewById(R.id.swipe);
        searchView = myBlog.findViewById(R.id.searchView);
        profile = myBlog.findViewById(R.id.pic_user);

        userQuery(user.getUid());

        Log.e(TAG, "onCreateView: " +searchView.getText().toString() );

        // when user swipe to update view
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "onCreateView: " +searchView.getText().toString() );
                setUpRecyclerView(searchView.getText().toString());
            }
        });

        // when user click search in keyboard
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loading.setVisibility(View.VISIBLE);
                    setUpRecyclerView(searchView.getText().toString());
                    InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    return true;
                }
                return false;
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


    private void setUpRecyclerView(String find){

        FirebaseFirestore dbPost = FirebaseFirestore.getInstance();
        CollectionReference dbViewPost = dbPost.collection("Post");

        Query query = dbViewPost.orderBy("title").startAt(find);

        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();
        Log.e(TAG, "setUpRecyclerView: " + options );
        ListHomeActivity listHomeActivity = new ListHomeActivity(options);
        RecyclerView recyclerView = myBlog.findViewById(R.id.my_blog_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listHomeActivity);

        listHomeActivity.setOnItemClickListener(new ListHomeActivity.OnItemClickListener() {
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

        listHomeActivity.startListening();
        loading.setVisibility(View.INVISIBLE);


    }



}