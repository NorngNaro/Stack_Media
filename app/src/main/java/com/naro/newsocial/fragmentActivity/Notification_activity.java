package com.naro.newsocial.fragmentActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.naro.newsocial.Activity.Flash_screen_Activity;
import com.naro.newsocial.Activity.Sign_in_Activity;
import com.naro.newsocial.R;


public class Notification_activity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View setting = inflater.inflate(R.layout.activity_notification, container, false);







        return setting;
    }


}
