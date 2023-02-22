package com.eighttechprojects.propertytaxshirol.Utilities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eighttechprojects.propertytaxshirol.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PermissionUtils {

    // TAG
    private static final String TAG = "PermissionUtils";
    // Storage Permission
    public static final int STORAGE_PERMISSION = 1;
    // Storage Permission above Android 11
    public static final int ANDROID_11_STORAGE_PERMISSION = 333;
    // Permission Granted
    public static final String PERMISSION_GRANTED = "Permission Granted";
    // Permission not Granted
    public static final String PERMISSION_NOT_GRANTED = "Permission Granted";
    public static final String PERMISSION_ALREADY_GRANTED = "Permission Already Granted";

    // Need Permission Alert
    public static final String NEED_MANAGEMENT_STORAGE_PERMISSION_ALERT  = "Need Storage Permission to Store File. \n\nGo to Setting -> App info -> Mine Survey -> Permissions -> Allow Storage -> Allow Management of All Files";


//---------------------------------------------- Storage Permission ------------------------------------------------------------------------------------------------------------------------

    public static boolean isInternalStorage(Context context){
        // Above Android 11 Storage permission
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        // Below Android 11 Storage permission
        else
        {
            boolean read_external_storage_permission  = (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED);
            boolean write_external_storage_permission = (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            return read_external_storage_permission && write_external_storage_permission;
        }
    }

    // Android 11 Permission
    public static void requestPermissionForAndroid_11_Above_And_Below_Versions(Activity activity){

        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",activity.getApplicationContext().getPackageName())));
                activity.startActivityForResult(intent,333);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, 333);
            }
        } else {
            //below android 11
            requestPermissionsStorage(activity);
        }
    }

    public static void requestPermissionsStorage(Activity activity){
        ActivityCompat.requestPermissions(activity,new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION);
    }

    public static boolean onStorageRequestPermissionsResult(int[] grantResults){
        // Permission Granted or Not
            boolean READ_EXTERNAL_STORAGE  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            return READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE;
    }


}
