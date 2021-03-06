  package com.naro.newsocial.Adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.Model.UserModel;
import com.naro.newsocial.R;


  public class ListHomeActivity extends FirestoreRecyclerAdapter<PostModel , ListHomeActivity.HomeHolder> {

    private OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ListHomeActivity(@NonNull FirestoreRecyclerOptions<PostModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_discover,
                parent, false);
        return new HomeHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull HomeHolder homeHolder, int i, @NonNull PostModel postModel) {



        // Query data of user that post this post
        FirebaseFirestore dbFireStoreUser = FirebaseFirestore.getInstance();
        CollectionReference dbSignIn = dbFireStoreUser.collection("User");
        dbSignIn
                .whereEqualTo("userID",postModel.getUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                UserModel userModel = documentSnapshots.toObject(UserModel.class);

                                // User
                                Glide.with(homeHolder.itemView.getContext())
                                        .load(userModel.getImageUrl())
                                        .into(homeHolder.picUser);
                                homeHolder.writer.setText(userModel.getUserName());

                            }
                        }else {
                            Log.e("TAG", "Have something wrong");

                        }

                    }
                });

        // Set view to list
        homeHolder.title.setText(postModel.getTitle());
        homeHolder.date.setText(postModel.getDate());
        homeHolder.countLove.setText(postModel.getView() + " Views");
        Glide.with(homeHolder.itemView.getContext())
                .load(postModel.getUrl())
                .into(homeHolder.imageView);




    }


    class HomeHolder extends RecyclerView.ViewHolder{

        AppCompatImageView imageView;
        AppCompatTextView title;
        AppCompatImageView picUser;
        AppCompatTextView writer;
        AppCompatTextView date;
        AppCompatImageView love;
        AppCompatTextView countLove;


        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_show);
            title = itemView.findViewById(R.id.title);
            picUser = itemView.findViewById(R.id.pic_user);
            writer = itemView.findViewById(R.id.writer);
            date = itemView.findViewById(R.id.date);
            love = itemView.findViewById(R.id.btn_love);
            countLove = itemView.findViewById(R.id.countLike);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null)
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            });


        }

    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener =  listener;
    }

    public interface OnItemClickListener extends AdapterView.OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);


    }


}
