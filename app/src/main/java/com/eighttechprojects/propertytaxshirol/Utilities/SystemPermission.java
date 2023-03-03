package com.eighttechprojects.propertytaxshirol.Utilities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.eighttechprojects.propertytaxshirol.R;

public class SystemPermission
{
    private static final int REQUEST_PERMISSIONS = 1234;
    public static final String permission_ext_storage = "permission_ext_storage";
    public static final String permission_int_storage = "permission_int_storage";
    public static final String permission_camera      = "permission_camera";
    public static final String permission_location    = "permission_location";


//---------------------------------------------- Storage Permission ------------------------------------------------------------------------------------------------------------------------

    public static boolean isInternalGranted(Activity mActivity){
        // Above Android 11 Storage permission
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        // Below Android 11 Storage permission
        else
        {
            boolean read_external_storage_permission  = (ContextCompat.checkSelfPermission(mActivity, READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED);
            boolean write_external_storage_permission = (ContextCompat.checkSelfPermission(mActivity, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            return read_external_storage_permission && write_external_storage_permission;
        }
    }

    public static boolean isInternalStorage(Activity mActivity){

        if(isInternalGranted(mActivity)){
            return true;
        }
        else {
            requestPermissionForInternalStorage(mActivity);
        }

//            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.MANAGE_EXTERNAL_STORAGE))
//            {
//                Utility.setPermissionDenied(mActivity, permission_int_storage, true);
//                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//                builder.setTitle(R.string.permission_storage_title);
//                builder.setPositiveButton(android.R.string.ok, null);
//                builder.setMessage(R.string.permission_storage_description);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    builder.setOnDismissListener(dialog ->{
//                        requestPermissionForInternalStorage(mActivity);
//                    });
//                }
//                builder.show();
//            }
//            else
//            {
//                if((Utility.isPermissionDenied(mActivity, permission_int_storage)))
//                {
//                    // redirect to settings
//                    Utility.showToast(mActivity, mActivity.getString(R.string.permission_storage_description));
//                    redirectToPermissionSettings(mActivity);
//                }
//                else {
//                    requestPermissionForInternalStorage(mActivity);
//                }
//            }
//        }

        return false;
    }

    // Android 11 Permission
    public static void requestPermissionForInternalStorage(Activity activity){

        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",activity.getApplicationContext().getPackageName())));
                activity.startActivityForResult(intent,REQUEST_PERMISSIONS);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, REQUEST_PERMISSIONS);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(activity,new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSIONS);
        }
    }


//------------------------------------------------------- isExternalStorage ----------------------------------------------------------------------------------------------------------------

    public static boolean isExternalStorage(Activity mActivity) {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                Utility.setPermissionDenied(mActivity, permission_ext_storage, true);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(R.string.permission_storage_title);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage(R.string.permission_storage_description);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    builder.setOnDismissListener(dialog -> mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS));
                }
                builder.show();
            }
            else
            {
                if((Utility.isPermissionDenied(mActivity, permission_ext_storage)))
                {
                    // redirect to settings
                    Utility.showToast(mActivity, mActivity.getString(R.string.permission_storage_description));
                    redirectToPermissionSettings(mActivity);
                }
                else {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);
                }
            }
        }
        return false;
    }

//------------------------------------------------------- isCamera ----------------------------------------------------------------------------------------------------------------

    public static boolean isCamera(Activity mActivity) {
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.CAMERA))
            {
                Utility.setPermissionDenied(mActivity, permission_camera, true);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(R.string.permission_camera_title);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage(R.string.permission_camera_description);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    builder.setOnDismissListener(dialog -> mActivity.requestPermissions(
                            new String[]
                                    {Manifest.permission.CAMERA}
                            , REQUEST_PERMISSIONS));
                }
                builder.show();
            }
            else
            {   // became - shouldshowrequestRationale = true from here on permission denial.
                if((Utility.isPermissionDenied(mActivity, permission_camera)))
                {
                    // redirect to settings
                    Utility.showToast(mActivity, mActivity.getString(R.string.permission_camera_description));
                    redirectToPermissionSettings(mActivity);
                }
                else {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_PERMISSIONS);
                }
            }
        }
        return false;
    }

//------------------------------------------------------- isLocation ----------------------------------------------------------------------------------------------------------------

    public static boolean isLocation(Activity mActivity){
        if( (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) )
        {
            return true;
        }
        else
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                Utility.setPermissionDenied(mActivity, permission_location, true);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(R.string.permission_fine_location_title);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage(R.string.permission_location_description);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    builder.setOnDismissListener(dialog -> mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS));
                }
                builder.show();
            }
            else
            {   // became - shouldshowrequestRationale = true from here on permission denial.
                if((Utility.isPermissionDenied(mActivity, permission_location)))
                {
                    // redirect to settings
                    Utility.showToast(mActivity, mActivity.getString(R.string.permission_location_description));
                    redirectToPermissionSettings(mActivity);
                }
                else {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_PERMISSIONS);
                }
            }
        }
        return false;
    }

//------------------------------------------------------- isInternetConnected ----------------------------------------------------------------------------------------------------------------

    public static boolean isInternetConnected(Context context) {
        //boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }

    }

//------------------------------------------------------- reDirect ----------------------------------------------------------------------------------------------------------------

    public static void redirectToPermissionSettings(Activity mContext) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        mContext.startActivity(intent);
    }

}
