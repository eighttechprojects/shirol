package com.eighttechprojects.propertytaxshirol.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.eighttechprojects.propertytaxshirol.Activity.GoogleMap.MapsActivity;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


//---------------------------------------------------------- OnCreate -------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Hide Action Bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        new Handler().postDelayed(() -> {
            try
            {
//                reDirectMap();
                if(Utility.getBooleanSavedData(this, Utility.IS_USER_SUCCESSFULLY_LOGGED_IN)) {
                    reDirectMap();
                }
                else
                {
                    reDirectPermission();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }, 1000);


    }

//---------------------------------------------------------- reDirect -------------------------------------------------------------------------------------------------------------

    private void reDirectMap() {
        Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void reDirectPermission() {
        Intent intent = new Intent(SplashActivity.this, PermissionActivity.class);
        startActivity(intent);
        finish();
    }
}