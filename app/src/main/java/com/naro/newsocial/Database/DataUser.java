package com.naro.newsocial.Database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AdapterListUpdateCallback;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DataUser {


    public void UserQuery(String userID){
        // Write a message to the database
        FirebaseFirestore dbFireStoreUser = FirebaseFirestore.getInstance();
        CollectionReference dbSignIn = dbFireStoreUser.collection("User");


        dbSignIn
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                    }
                });







    }

}
