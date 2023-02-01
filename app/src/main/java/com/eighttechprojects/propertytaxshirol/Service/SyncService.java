package com.eighttechprojects.propertytaxshirol.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.android.volley.VolleyError;
import com.eighttechprojects.propertytaxshirol.Database.DataBaseHelper;
import com.eighttechprojects.propertytaxshirol.InternetConnection.ConnectivityReceiver;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.eighttechprojects.propertytaxshirol.volly.BaseApplication;
import com.eighttechprojects.propertytaxshirol.volly.URL_Utility;
import com.eighttechprojects.propertytaxshirol.volly.WSResponseInterface;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncService extends Service implements WSResponseInterface {

    // TAG
    private static final String TAG  = SyncService.class.getSimpleName();
    // Static
    public static final String START_SYNC = "provider.startSync";
    public static final String STOP_SYNC  = "provider.stopSync";
    public  static final int SYNC_NOTIFICATION_ID              = 1;
    public  static final int SYNC_SUCCESSFULLY_NOTIFICATION_ID = 2;
    public  static final String CHANNEL_ID1 = "channel_01";
    public  static final String CHANNEL_ID2 = "channel_02";
    // Database
    private DataBaseHelper dataBaseHelper;
    // Broadcast Receiver
    BroadcastReceiver broadcastReceiver;

    // ArrayList
    private ArrayList<FormDBModel> formDBModelList = new ArrayList<>();
    private FormDBModel formDBModel;

//------------------------------------------------------- onCreate -------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
        // network check broadcastReceiver!
        broadcastReceiver = new ConnectivityReceiver() {
            @Override
            protected void onNetworkChange(String alert) {
                if(Utility.NO_NETWORK_CONNECTED.equals(alert)){
                    ArrayList<FormDBModel> formDataList = dataBaseHelper.getMapFormLocalDataList();
                    if(formDataList.size() == 0){
                        Toast.makeText(SyncService.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        //syncNotify();
                        stopSyncService();
                        Log.e(TAG, "Sync Service No Data Found in Local DataBase");
                    }
                    else{
                        //syncNotify();
                        Toast.makeText(SyncService.this, "Sync Fail", Toast.LENGTH_SHORT).show();
                        stopService();
                    }
                }
            }
        };
        registerNetworkBroadcast();

    }

//------------------------------------------------------- onStartCommand -------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            if (intent.getAction().equals(START_SYNC)) {
                Log.e(TAG, "Start Foreground Sync Service");
                startForeground(SYNC_NOTIFICATION_ID, getNotification());
                Sync();
            }
            else if (intent.getAction().equals(STOP_SYNC)) {
                Log.e(TAG, "Stop Foreground Sync Service");
                //syncNotify();
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

//------------------------------------------------------- InitDatabase -------------------------------------------------------------------------------------------------------------------------------------------------

    private void initDatabase() {dataBaseHelper = new DataBaseHelper(this);    }

//------------------------------------------------------- Notification -------------------------------------------------------------------------------------------------------------------------------------------------

    @SuppressLint("ObsoleteSdkInt")
    private Notification getNotification() {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID1, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            mChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Notification!
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon))
                .setContentTitle("Sync")
                .setContentText("Data Sync Start Please Wait.....")
                .setSound(sound);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID1); // Channel ID
        }
        builder.setAutoCancel(true);

        // here we again call For groundLocationService to turn off the tracking!
        Intent exitIntent = new Intent(this, SyncService.class);
        exitIntent.setAction(STOP_SYNC);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pexitIntent = PendingIntent.getService(this, 0, exitIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.addAction(R.mipmap.ic_launcher, getResources().getString(R.string.exit).toUpperCase(), pexitIntent);
        }

        return builder.build();
    }

    private void getSyncSuccessfullyNotification(){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID2, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            mChannel.enableVibration(true);
            manager.createNotificationChannel(mChannel);
        }

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Sync")
                    .setContentText("Data Sync Successfully")
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID2)
                    .setSound(sound)
                    .build();
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID2, "Sync", NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            mChannel.enableVibration(true);
            manager.createNotificationChannel(mChannel);
        }
        else{
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Sync")
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setVibrate(new long[]{1000,500,1000,500})
                    .setContentText("Data Sync Successfully")
                    .build();

        }

        manager.notify(SYNC_SUCCESSFULLY_NOTIFICATION_ID,notification);
    }

    private void getSyncFailNotification(){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID2, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            mChannel.enableVibration(true);
            manager.createNotificationChannel(mChannel);
        }

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Sync")
                    .setContentText("Data Sync Fail")
                    .setSound(sound)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID2)
                    .build();
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID2, "Sync", NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            mChannel.enableVibration(true);
            manager.createNotificationChannel(mChannel);
        }
        else{
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Sync")
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setVibrate(new long[]{1000,500,1000,500})
                    .setContentText("Data Sync Successfully")
                    .build();
        }
        manager.notify(SYNC_SUCCESSFULLY_NOTIFICATION_ID,notification);
    }

//------------------------------------------------------- Stop Sync Service -----------------------------------------------------------------------------------------------------------------------------------------

    private void stopSyncService(){
        Toast.makeText(this, "Sync Data Successfully", Toast.LENGTH_SHORT).show();
        stopForeground(true);
        stopSelf();
        // Set Sync Successfully Notification
        getSyncSuccessfullyNotification();
    }

    private void stopService(){
        getSyncFailNotification();
        stopForeground(true);
        stopSelf();
    }

//-----------------------------------------------------------------------------------Network Register-----------------------------------------------------------------------------------------------

    // Register Network
    protected void registerNetworkBroadcast(){
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    // UnRegister Network
    protected void unregisterNetwork(){
        try{
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

//------------------------------------------------------- onDestroy -------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetwork();
    }

//------------------------------------------------------- onBind -------------------------------------------------------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//------------------------------------------------------- Sync -------------------------------------------------------------------------------------------------------------------------------------------------

    private void Sync() {
        ArrayList<FormDBModel> formDataList = dataBaseHelper.getMapFormLocalDataList();
        if(formDataList.size() == 0){
         //   syncNotify();
            stopSyncService();
        }
        else{
            Toast.makeText(this, "Sync Started", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Sync Service Local Database Contain some Data");

            if(formDataList.size() > 0){
                Log.e(TAG, "Sync Service Form On");
                formDBModelList = dataBaseHelper.getMapFormLocalDataList();
                SyncFormDetails();
            }
        }
    }

    private void SyncFormDetails(){
        if(formDBModelList != null && formDBModelList.size() > 0){
            formDBModel = formDBModelList.get(0);
            formDBModelList.remove(0);
            SyncFormDataToServer(formDBModel);
        }
        else{
            Log.e(TAG, "Sync Service Form Off");
            Log.e(TAG,  "Data Sync Successfully");
          //  syncNotify();
            stopSyncService();
        }
    }

    private void SyncFormDataToServer(FormDBModel formDBModel){
        Map<String, String> params = new HashMap<>();
        params.put("data", formDBModel.getFormData());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM, URL_Utility.WS_FORM, params, false, false);
    }

//------------------------------------------------------- onSuccessResponse -----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onSuccessResponse(URL_Utility.ResponseCode responseCode, String response) {

        if(responseCode == URL_Utility.ResponseCode.WS_FORM){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "Form Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                        if (formDBModel != null && formDBModel.getId() != null) {
                            // then
                            if (dataBaseHelper.getMapFormLocalDataList().size() > 0) {
                                dataBaseHelper.deleteMapFormLocalData(formDBModel.getId());
                            }
                            SyncFormDetails();
                        }
                    }
                    // Status -> Fail
                    else{
                      //  syncNotify();
                        Utility.showToast(this,Utility.ERROR_MESSAGE);
                        stopService();
                    }
                }
                catch (JSONException e){
                   // syncNotify();
                    Log.e(TAG,"Sync Json Error: "+ e.getMessage());
                    Utility.showToast(this,Utility.ERROR_MESSAGE);
                    stopService();
                }
            }
            else{
               // syncNotify();
                Utility.showToast(this,Utility.ERROR_MESSAGE);
                stopService();
                Log.e(TAG, "Sync Response Empty");
            }
        }
    }

//------------------------------------------------------- onErrorResponse -----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onErrorResponse(URL_Utility.ResponseCode responseCode, VolleyError error) {
        Log.e(TAG, "Error Response Code: "+responseCode+" Error Message: "+error.getMessage());
       // syncNotify();
        Utility.showToast(this,Utility.ERROR_MESSAGE);
        stopService();

    }


    private void syncNotify(){
        Intent intent = new Intent(Utility.SyncServiceOn);
        intent.putExtra("sync", String.valueOf("on"));
        sendBroadcast(intent);
    }

}


