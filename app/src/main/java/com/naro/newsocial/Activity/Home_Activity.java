package com.naro.newsocial.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naro.newsocial.R;
import com.naro.newsocial.databinding.ActivityHomeBinding;
import com.naro.newsocial.fragmentActivity.Account_Activity;
import com.naro.newsocial.fragmentActivity.Discover_Activity;
import com.naro.newsocial.fragmentActivity.Search_Activity;
import com.naro.newsocial.fragmentActivity.Notification_activity;


public class Home_Activity extends AppCompatActivity {
    ActivityHomeBinding binding;
    private boolean isCreate = false;
    private BottomNavigationView bottomNav;
    private int back;
    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        onFabClick();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);




        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                    new Discover_Activity())
                    .commit();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG", "Fragemtn"+  getSupportFragmentManager().findFragmentByTag("Account_Fragment"));

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.my_blog:
                            selectedFragment = new Search_Activity();
                            break;
                        case R.id.discover:
                            selectedFragment = new Discover_Activity();
                            break;
                        case R.id.notification:
                            selectedFragment = new Notification_activity();
                            break;
                        case R.id.account:
                            selectedFragment = new Account_Activity();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_fragment, selectedFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    back = 2;

                    return true;
                }
            };



    public void changeItemSelect( int index){
        Log.e("TAG", "changeItemSelect: change item selected " + index);
        bottomNav.getMenu().getItem(index).setChecked(true);

    }

    @Override
    public void onBackPressed() {

        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,
                new Discover_Activity())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        bottomNav.getMenu().getItem(0).setChecked(true);
        back = back - 1 ;
        if (back == 0){
            super.onBackPressed();
        }
        Log.e("TAG", "onBackPressed: Back " );

    }


    private void onFabClick() {
            binding.fabBtn.setOnClickListener(v -> {
                 isCreate = true;
                Intent intent = new Intent(this, CreateArticleActivity.class);
                intent.putExtra("postID", "001");
                 intent.putExtra("action", "Create");
                startActivity(intent);

            });

        }


    }
