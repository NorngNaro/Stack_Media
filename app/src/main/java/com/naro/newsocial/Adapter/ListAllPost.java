package com.naro.newsocial.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.naro.newsocial.Model.PostModel;
import com.naro.newsocial.R;

public class ListAllPost  extends FirestoreRecyclerAdapter<PostModel,ListAllPost.AllPost> {

    private OnItemClickListener listener;
    private OnItemClickListener edit;
    private OnItemClickListener delete;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ListAllPost(@NonNull FirestoreRecyclerOptions<PostModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AllPost allPost, int i, @NonNull PostModel postModel) {
     // Set view to list
        allPost.title.setText(postModel.getTitle());
        allPost.date.setText(postModel.getDate());
        allPost.view.setText(postModel.getView() + "");
        Glide.with(allPost.itemView.getContext())
                .load(postModel.getUrl())
                .into(allPost.imageView);
    }

    @NonNull
    @Override
    public AllPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_my_blog,
                parent, false);
        return new AllPost(v);
    }

    class AllPost extends RecyclerView.ViewHolder{

        AppCompatImageView imageView;
        AppCompatTextView title;
        AppCompatTextView date;
        AppCompatTextView view;


        CardView btnEdit;
        CardView btnDelete;



        public AllPost(@NonNull View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_show);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.txt_postdate);
            view = itemView.findViewById(R.id.textViewPost);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_view_delete);


            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TAG", "onClick: On Edit " );
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && edit != null)
                        edit.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null)
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && delete != null)
                        delete.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            });

        }
    }

    public void deleteItem(int position) {
        notifyItemRemoved(position);
    }



    public void setOnDeleteClickListener(ListAllPost.OnItemClickListener delete){
        this.delete = delete;
    }

    public void setOnEditClickListener(ListAllPost.OnItemClickListener edit){
        this.edit = edit;
    }

    public void setOnItemClickListener(ListAllPost.OnItemClickListener listener) {
        this.listener =  listener;
    }

    public interface OnItemClickListener extends AdapterView.OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }


}
