package com.eighttechprojects.propertytaxshirol.Utilities;

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
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.eighttechprojects.propertytaxshirol.R;

public class SystemPermission
{
    private static final int REQUEST_PERMISSIONS = 1234;
    public static final String permission_ext_storage = "permission_ext_storage";
    public static final String permission_camera      = "permission_camera";
    public static final String permission_location    = "permission_location";

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
