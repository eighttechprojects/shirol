package com.eighttechprojects.propertytaxshirol.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.eighttechprojects.propertytaxshirol.Activity.GoogleMap.MapsActivity;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;

public class PermissionActivity extends AppCompatActivity {

    Button permissionApproveButton;
    private static final int RequestPermissionCode = 1;

//------------------------------------------------------- On Create ---------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        // Button
        permissionApproveButton = findViewById(R.id.permissionApproveButton);
        // Permission Granted
        if(isPermissionGranted()){
            reDirectToScreen();
        }
        // Permission Approve Button
        permissionApproveButton.setOnClickListener(view -> {
            // permission
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, RequestPermissionCode);
        });

    }

//------------------------------------------------------- isAllPermissionGranted ---------------------------------------------------------------------------------------------------------------------------

    private boolean isPermissionGranted(){
        boolean isStorage  = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean isCamera   = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean isLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return isLocation && isStorage && isCamera;
    }

//------------------------------------------------------- Re Direct ---------------------------------------------------------------------------------------------------------------------------

    private void reDirectToScreen() {
        if (Utility.getBooleanSavedData(this, Utility.IS_USER_SUCCESSFULLY_LOGGED_IN)) {
            reDirectToMap();
        } else {
            reDirectToLogin();
        }
    }

    private void reDirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void reDirectToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

//------------------------------- On Request permission Result --------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                    && grantResults[5] == PackageManager.PERMISSION_GRANTED){
                reDirectToScreen();
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                SystemPermission.redirectToPermissionSettings(this);
            }
        }
    }
}