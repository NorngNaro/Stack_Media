package com.naro.newsocial.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.naro.newsocial.R;


public class Flash_screen_Activity extends Activity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String LOGIN = "login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);

        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(prefs.getBoolean(LOGIN,false)){
                    Intent intent = new Intent(Flash_screen_Activity.this,Home_Activity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(Flash_screen_Activity.this, Sign_in_Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);





    }
}
