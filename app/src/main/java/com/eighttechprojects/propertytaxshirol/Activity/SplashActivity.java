package com.eighttechprojects.propertytaxshirol.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.eighttechprojects.propertytaxshirol.Activity.GoogleMap.MapsActivity;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;

import java.io.File;
import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "Splash";

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
//                if(isPackageInstalled("com.eighttechprojects.propertytaxshirol", this.getPackageManager())){
//                    Log.e(TAG, "Install..");
//                    deleteCache(this);
//                }
//                else{
//                    Log.e(TAG,"Not Install");
//                }

//              reDirectMap();
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

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
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