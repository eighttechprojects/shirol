package com.eighttechprojects.propertytaxshirol.Activity.GoogleMap;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.eighttechprojects.propertytaxshirol.Activity.Form.FormActivity;
import com.eighttechprojects.propertytaxshirol.Activity.Form.ResurveyFormActivity;
import com.eighttechprojects.propertytaxshirol.Activity.SplashActivity;
import com.eighttechprojects.propertytaxshirol.Adapter.AdapterFormListView;
import com.eighttechprojects.propertytaxshirol.Adapter.AdapterFormTable;
import com.eighttechprojects.propertytaxshirol.Adapter.FileUploadViewAdapter;
import com.eighttechprojects.propertytaxshirol.Database.DataBaseHelper;
import com.eighttechprojects.propertytaxshirol.Model.FileUploadViewModel;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.Model.FormFields;
import com.eighttechprojects.propertytaxshirol.Model.FormListModel;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.GeoJsonModel;
import com.eighttechprojects.propertytaxshirol.Model.LastKeyModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.eighttechprojects.propertytaxshirol.WMSMap.WMSProvider;
import com.eighttechprojects.propertytaxshirol.WMSMap.WMSTileProviderFactory;
import com.eighttechprojects.propertytaxshirol.databinding.ActivityMapsBinding;
import com.eighttechprojects.propertytaxshirol.volly.AndroidMultiPartEntity;
import com.eighttechprojects.propertytaxshirol.volly.BaseApplication;
import com.eighttechprojects.propertytaxshirol.volly.URL_Utility;
import com.eighttechprojects.propertytaxshirol.volly.WSResponseInterface;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.Task;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener, WSResponseInterface {

    // TAG
    public static final String TAG = MapsActivity.class.getSimpleName();
    // Map
    GoogleMap mMap;
    // Binding
    ActivityMapsBinding binding;
    // Activity
    Activity mActivity;
    // DataBase
    private DataBaseHelper dataBaseHelper;
    // Base Application
    BaseApplication baseApplication;
    // ProgressDialog
    private ProgressDialog progressDialog;
    // Location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest mRequest;
    private Location mCurrentLocation = null;
    private static final float DEFAULT_ZOOM = 20f;
    private boolean isGoToCurrentLocation = false;
    // Form
    private static final String selectYesOption = "होय";
    private static final String selectNoOption  = "नाही";
    private static final int FORM_REQUEST_CODE = 1001;

    // Logout ----------------------------------------------------------------
    // ArrayList
    private ArrayList<FormDBModel> formDBModelList = new ArrayList<>();

    private ArrayList<FormDBModel> formSyncList = new ArrayList<>();

    private ArrayList<LastKeyModel> lastKeysList = new ArrayList<>();

    private LastKeyModel lastKeyModel;
    private FormDBModel formDBModel;

    // File Upload
    public long totalSize = 0;
    public static final String TYPE_FILE   = "file";
    public static final String TYPE_CAMERA = "cameraUploader";
    public static boolean isFileUpload   = true;
    public static boolean isCameraUpload = true;
    public static boolean isLastKeyUpload = true;
    public static boolean isFormUpload = true;
    private ArrayList<Marker> geoJsonMarkerList = new ArrayList<>();
    private HashMap<String,Polygon> geoJsonPolygonLists = new HashMap<>();
    boolean isMultipleForm = false;
    boolean isMarkerVisible = false;
    private static final String droneLayer = "http://173.249.24.149:8080/geoserver/shirol/wms?service=WMS&version=1.1.0&request=GetMap&layers=shirol%3AShirol_Base&bbox=74.58111479319136%2C16.72872028532293%2C74.62853852632936%2C16.76589925517693&width=768&height=602&srs=EPSG%3A4326&styles=&format=application/openlayers";

//------------------------------------------------------- onCreate ---------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Binding
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Activity
        mActivity = this;
        // init Database
        initDatabase();
        // base Application
        baseApplication = (BaseApplication) getApplication();
        // FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
//        // logout 24hr
//        LogoutAfter24hr();
        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

//
//            // Location Call Back
//            locationCallback = new LocationCallback() {
//                @Override
//                public void onLocationResult(@NonNull LocationResult locationResult) {
//                    for (Location loc : locationResult.getLocations()) {
//                        mCurrentLocation = loc;
//                        if(mCurrentLocation != null){
//                            if(isGoToCurrentLocation){
//                                isGoToCurrentLocation = false;
//                                // Current LatLon
//
//                            }
//                        }
//                    }
//                }
//            };

      //  LocationPermission();

    }

//------------------------------------------------------- InitDatabase --------------------------------------------------------------------------------------------------------------------------

    private void initDatabase() {
        dataBaseHelper = new DataBaseHelper(this);
    }

//------------------------------------------------------- onMapReady ---------------------------------------------------------------------------------------------------------------------------

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Google Map
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // set Map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Map Click Listener
        mMap.setOnMapClickListener(this);
        // Polygon Click Listener
        mMap.setOnPolygonClickListener(this);
        // Marker Click Listener
        mMap.setOnMarkerClickListener(this);
        // setOnClickListener
        setOnClickListener();

        // logout 24hr
        LogoutAfter24hr();

        //------------------------------------------------------- On Camera Idle ------------------------------------------------------------

        mMap.setOnCameraIdleListener(() -> {
                if(isMarkerVisible) {
                try {
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    service.execute(() -> handler.post(() -> {
                        if (mMap != null) {
                            float mapZoomLevel = mMap.getCameraPosition().zoom;
                            if (mapZoomLevel < 19f) {
                                Log.e(TAG, "Zoom < then required");
                                if (geoJsonMarkerList != null) {
                                    if (geoJsonMarkerList.size() > 0) {
                                        for (Marker m : geoJsonMarkerList) {
                                            m.setVisible(false);
                                        }
                                    }
                                }

                            } else {
                                if (geoJsonMarkerList != null) {
                                    if (geoJsonMarkerList.size() > 0) {
                                        for (Marker m : geoJsonMarkerList) {
                                            m.setVisible(true);
                                        }
                                    }
                                }
                            }
                        }
                    }));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        //------------------------------------------------------- On Camera Move Started ------------------------------------------------------------

        mMap.setOnCameraMoveStartedListener(i -> {});


        addWMSLayer(droneLayer);
    }

//------------------------------------------------------- setOnClickListener ------------------------------------------------------------------------------------------------------------------------------------------------

    private void setOnClickListener(){
        binding.rlCurrentLocation.setOnClickListener(this);
        binding.rlMapType.setOnClickListener(this);
    }

//------------------------------------------------------- Menu ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuSync:
                if(SystemPermission.isInternetConnected(mActivity)){
                    Sync();
                }
                else{
                    Utility.showOKDialogBox(mActivity, "Sync Alert", "Need Internet Connection To Sync Data", DialogInterface::dismiss);
                }
                break;


            case R.id.menuLogout:
                if(SystemPermission.isInternetConnected(mActivity)){
                    Logout();
                }
                else{
                    Utility.showOKDialogBox(mActivity, "Connection Error", "Need Internet Connection to Logout", DialogInterface::dismiss);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//------------------------------------------------------- LogOut ------------------------------------------------------------------------------------------------------------------------------------------------

    private void Logout(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
        alertDialog.setMessage("Are you sure want to Logout?");
        alertDialog.setPositiveButton("Logout", (dialog, which) -> {
            dialog.dismiss();
            showProgressBar();
            LogoutSync();
        });
        alertDialog.setNegativeButton("Cancel", null);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    private void LogoutSync(){
        ArrayList<FormDBModel> formDBModels = dataBaseHelper.getAllForms();
        ArrayList<LastKeyModel> lastKeys    = dataBaseHelper.getAllGenerateID();

        if(formDBModels.size() == 0 && lastKeys.size() == 0){
            reDirectToLoginPage();
            Log.e(TAG,"Logout No Data Found in Local DataBase");
        }
        else{
            Log.e(TAG, "Logout Sync DataBase Contain some Data");
            if(formDBModels.size() > 0){
                Log.e(TAG, "Logout Sync Form On");
                formDBModelList = dataBaseHelper.getAllForms();
                LogoutSyncFormDetails();
            }
            else{
                isFormUpload = true;
            }

            if(lastKeys.size() > 0){
                Log.e(TAG,"Logout Sync LastKey On");
                lastKeysList = dataBaseHelper.getAllGenerateID();
                LogoutSyncLastKeyDetails();
            }
            else{
                isLastKeyUpload = true;
            }

        }
    }
    private void LogoutSyncFormDetails(){
        if(formDBModelList != null && formDBModelList.size() > 0){
            isFormUpload = false;
            isFileUpload = true;
            isCameraUpload = true;
            formDBModel = formDBModelList.get(0);
            formDBModelList.remove(0);
            LogoutSyncFormDataToServer(formDBModel);
        }
        else{
            isFormUpload = true;
            isFileUpload = true;
            isCameraUpload = true;
            if(isLastKeyUpload){
                Log.e(TAG, "Logout Sync Form Off");
                Log.e(TAG, "Logout Sync lastKey Off");
                Log.e(TAG,  "Logout Sync Successfully");
                reDirectToLoginPage();
            }
        }
    }
    private void LogoutSyncLastKeyDetails(){
        if(lastKeysList != null && lastKeysList.size() > 0){
            isLastKeyUpload = false;
            lastKeyModel = lastKeysList.get(0);
            lastKeysList.remove(0);
            LogoutSyncLastKeyToServer(lastKeyModel);
        }
        else{
            isLastKeyUpload = true;
            if(isFormUpload){
                Log.e(TAG, "Logout Sync Form Off");
                Log.e(TAG, "Logout Sync lastKey Off");
                Log.e(TAG,  "Logout Sync Successfully");
                reDirectToLoginPage();
            }

        }
    }
    private void LogoutSyncLastKeyToServer(LastKeyModel lastKeyModel){
        Map<String, String> params = new HashMap<>();
        params.put("data",Utility.convertlastKeyModelToString(lastKeyModel));
        Log.e(TAG, "Last key  Uploaded -> " + params.toString());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_SET_COUNTER1, URL_Utility.WS_SET_COUNTER1, params, false, false);
    }
    private void LogoutSyncFormDataToServer(FormDBModel formDBModel){
        Map<String, String> params = new HashMap<>();
        params.put("data", formDBModel.getFormData());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM, URL_Utility.WS_FORM, params, false, false);
    }
    private void LogoutAfter24hr(){
        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String date = Utility.getSavedData(mActivity,Utility.OLD_DATE);
       // Log.e(TAG,"Current Date: "+ currentDate);
       // Log.e(TAG,"Old Date: "+ date);
        if(!Utility.isEmptyString(date)){
            if(date.equals(currentDate)){
                Log.e(TAG, "true");
                showAllGeoJsonPolygon();
            }
            else {
                Log.e(TAG, "false");
                if(SystemPermission.isInternetConnected(mActivity)){
                    showProgressBar();
                    LogoutSync();
                }
            }
        }
        else{
            showAllGeoJsonPolygon();
        }
        Utility.saveData(mActivity,Utility.OLD_DATE, currentDate);

    }
    private void reDirectToLoginPage(){
        String date = Utility.getSavedData(mActivity,Utility.OLD_DATE);
        Utility.clearData(this);
        Utility.saveData(mActivity,Utility.OLD_DATE,date);
        // Database Clear
        dataBaseHelper.logout();
        dismissProgressBar();
        startActivity(new Intent(this, SplashActivity.class));
    }

//------------------------------------------------------- ProgressBar Show/ Dismiss ------------------------------------------------------------------------------------------------------

    private void dismissProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    private void showProgressBar() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading and Sync Data...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }
    private void showProgressBar(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(msg);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }

//------------------------------------------------------- Sync ------------------------------------------------------------------------------------------------------------------------------------------------

    private void Sync() {
        ArrayList<FormDBModel> formDBModels = dataBaseHelper.getAllForms();
        ArrayList<LastKeyModel> lastKeys    = dataBaseHelper.getAllGenerateID();
        Log.e(TAG, "Sync Last Keys Size -> "+lastKeys.size());
        if(formDBModels.size() == 0 && lastKeys.size() == 0){
            dismissProgressBar();
            Log.e(TAG, "Sync Local Database Contain no Data");
            Utility.showOKDialogBox(this, "Sync", "Data Already Sync", DialogInterface::dismiss);
        }
        else{
            if(SystemPermission.isInternetConnected(mActivity)){
                showProgressBar("Sync...");
                Log.e(TAG, "Sync Database Contain some Data");
                if(formDBModels.size() > 0){
                    Log.e(TAG, "Sync Service Form On");
                    formSyncList = dataBaseHelper.getAllForms();
                    Log.e(TAG, "Sync Form Size: "+ formSyncList.size());
                    SyncFormDetails();
                }
                else{
                    isFormUpload = true;
                }
                // Last Keys
                if(lastKeys.size() > 0){
                    Log.e(TAG,"Sync Service last Keys On");
                    lastKeysList = dataBaseHelper.getAllGenerateID();
                    SyncLastKeyDetails();
                }
                else{
                    isLastKeyUpload = true;
                }
            }

        }
    }
    private void SyncLastKeyDetails(){
        if(lastKeysList != null && lastKeysList.size() > 0){
            isLastKeyUpload = false;
            lastKeyModel = lastKeysList.get(0);
            lastKeysList.remove(0);
            SyncLastKeyToServer(lastKeyModel);
        }
        else{
           isLastKeyUpload = true;
           if(isFormUpload){
               Log.e(TAG, "Sync Service Form Off");
               Log.e(TAG, "Sync Service lastKey Off");
               Log.e(TAG,  "Data Sync Successfully");
               dismissProgressBar();
               Utility.showOKDialogBox(this, "Sync", "Data Sync Successfully", DialogInterface::dismiss);
           }
        }
    }
    private void SyncLastKeyToServer(LastKeyModel lastKeyModel){
        Map<String, String> params = new HashMap<>();
        params.put("data",Utility.convertlastKeyModelToString(lastKeyModel));
        Log.e(TAG, "Last key  Uploaded -> " + params.toString());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_SET_COUNTER, URL_Utility.WS_SET_COUNTER, params, false, false);
    }
    private void SyncFormDetails(){
        if(formSyncList != null && formSyncList.size() > 0){
            isFormUpload = false;
            isFileUpload = true;
            isCameraUpload = true;
            formDBModel = formSyncList.get(0);
            formSyncList.remove(0);
            SyncFormDataToServer(formDBModel);
        }
        else{
            isFormUpload = true;
            isFileUpload = true;
            isCameraUpload = true;
            if(isLastKeyUpload){
                Log.e(TAG, "Sync Service Form Off");
                Log.e(TAG, "Sync Service lastKey Off");
                Log.e(TAG,  "Data Sync Successfully");
                dismissProgressBar();
                Utility.showOKDialogBox(this, "Sync", "Data Sync Successfully", DialogInterface::dismiss);
            }
        }
    }
    private void SyncFormDataToServer(FormDBModel formDBModel){
        Log.e(TAG, "Upload to Server.........!");
        Map<String, String> params = new HashMap<>();
        params.put("data", formDBModel.getFormData());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM_SYNC, URL_Utility.WS_FORM_SYNC, params, false, false);
    }
    private boolean isFormDataNotSync(){
        ArrayList<FormDBModel> formDBModels = dataBaseHelper.getAllForms();
        return formDBModels.size() > 0;
    }

//------------------------------------------------------- onSuccessResponse -----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onSuccessResponse(URL_Utility.ResponseCode responseCode, String response) {
        // Form Logout
        if(responseCode == URL_Utility.ResponseCode.WS_FORM){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "Logout Form Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){

                        if(formDBModel != null){
                            boolean isFile = false;
                            boolean isCamera = false;

                            FormModel formModel = Utility.convertStringToFormModel(formDBModel.getFormData());
                            String unique_number =  formModel.getForm().getUnique_number();

                            if(!Utility.isEmptyString(formDBModel.getFilePath())){
                                Log.e(TAG,"Logout Form Contains File Path");
                                isFile = true;
                            }
                            if(!Utility.isEmptyString(formDBModel.getCameraPath())){
                                Log.e(TAG,"Logout Form Contains Camera Path");
                                isCamera = true;
                            }

                            if(isFile || isCamera){

                                if(isFile){
                                    new FileUploadServerLogout(new StringBuilder(formDBModel.getFilePath()),"",unique_number,TYPE_FILE,false).execute();
                                }
                                else{
                                    isFileUpload = true;
                                }

                                if(isCamera){
                                    new FileUploadServerLogout(new StringBuilder(formDBModel.getCameraPath()),"",unique_number, TYPE_CAMERA,true).execute();
                                }
                                else{
                                    isCameraUpload = true;
                                }

                            }
                            else{
                                // Only Form Contains
                                Log.e(TAG,"Logout User Upload only Form not Camera File or File");
                                if (formDBModel != null && formDBModel.getId() != null) {
                                    // then
                                    if (dataBaseHelper.getAllForms().size() > 0) {
                                        dataBaseHelper.deleteMapFormLocalData(formDBModel.getId());
                                    }
                                    LogoutSyncFormDetails();
                                }
                            }
                        }
                    }
                    // Status -> Fail
                    else{
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }
                }
                catch (JSONException e){
                    dismissProgressBar();
                    Log.e(TAG,"Sync Json Error: "+ e.getMessage());
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                }
            }
            else{
                dismissProgressBar();
                Log.e(TAG, "Sync Response Empty");
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
        }
        // Sync Form
        if(responseCode == URL_Utility.ResponseCode.WS_FORM_SYNC){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "Sync Form Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){

                        if(formDBModel != null){
                            boolean isFile   = false;
                            boolean isCamera = false;

                            FormModel formModel = Utility.convertStringToFormModel(formDBModel.getFormData());
                            String unique_number =  formModel.getForm().getUnique_number();

                            if(!Utility.isEmptyString(formDBModel.getFilePath())){
                                Log.e(TAG,"Form Contains File Path");
                                isFile = true;
                            }
                            if(!Utility.isEmptyString(formDBModel.getCameraPath())){
                                Log.e(TAG,"Form Contains Camera Path");
                                isCamera = true;
                            }

                            if(isFile || isCamera){

                                if(isFile){
                                    new FileUploadServer(new StringBuilder(formDBModel.getFilePath()),"",unique_number,TYPE_FILE,false).execute();
                                }
                                else{
                                    isFileUpload = true;
                                }

                                if(isCamera){
                                    new FileUploadServer(new StringBuilder(formDBModel.getCameraPath()),"",unique_number, TYPE_CAMERA,true).execute();
                                }
                                else{
                                    isCameraUpload = true;
                                }

                            }
                            else{
                                // Only Form Contains
                                Log.e(TAG,"User Upload only Form not Camera File or File");
                                if (formDBModel != null && formDBModel.getId() != null) {
                                    // then
                                    if (dataBaseHelper.getAllForms().size() > 0) {
                                        dataBaseHelper.deleteMapFormLocalData(formDBModel.getId());
                                    }
                                    SyncFormDetails();
                                }
                            }
                        }
                    }
                    // Status -> Fail
                    else{
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }
                }
                catch (JSONException e){
                    dismissProgressBar();
                    Log.e(TAG,"Sync Json Error: "+ e.getMessage());
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                }
            }
            else{
                dismissProgressBar();
                Log.e(TAG, "Sync Response Empty");
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
        }

        if(responseCode == URL_Utility.ResponseCode.WS_SET_COUNTER){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "SET Counter Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                        if (lastKeyModel != null && lastKeyModel.getId() != null) {
                            // then
                            if (dataBaseHelper.getAllGenerateID().size() > 0) {
                                dataBaseHelper.deleteGenerateIDLocal(lastKeyModel.getId());
                            }
                            SyncLastKeyDetails();
                        }
                    }
                    // Status -> Fail
                    else{
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }
                }
                catch (JSONException e){
                    Log.e(TAG,"Json Error: "+ e.getMessage());
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }
            }
            else{
                Log.e(TAG, "SET Counter Response Empty");
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
        }

        if(responseCode == URL_Utility.ResponseCode.WS_SET_COUNTER1){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "Logout SET Counter Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                        if (lastKeyModel != null && lastKeyModel.getId() != null) {
                            // then
                            if (dataBaseHelper.getAllGenerateID().size() > 0) {
                                dataBaseHelper.deleteGenerateIDLocal(lastKeyModel.getId());
                            }
                            LogoutSyncLastKeyDetails();
                        }
                    }
                    // Status -> Fail
                    else{
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }
                }
                catch (JSONException e){
                    Log.e(TAG,"Json Error: "+ e.getMessage());
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }
            }
            else{
                Log.e(TAG, "SET Counter Response Empty");
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
        }

    }

//------------------------------------------------------- onErrorResponse -----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onErrorResponse(URL_Utility.ResponseCode responseCode, VolleyError error){
        dismissProgressBar();
        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
        Log.e(TAG, "Logout Error Response Code: "+responseCode);
        Log.e(TAG, "Logout Error Message: "+error.getMessage());
    }

//------------------------------------------------------- onClick ------------------------------------------------------------------------------------------------------------------------------------------------

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.rlCurrentLocation:
                if (mCurrentLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),DEFAULT_ZOOM));
                }
                else{
                    Log.e(TAG, "Location null");
                }
                break;

            case R.id.rlMapType:
                Utility.showMapTypeDialog(mActivity, mapType -> {
                    Utility.saveData(MapsActivity.this, Utility.BASE_MAP,mapType);
                    Utility.setBaseMap(mActivity,mMap);
                    Toast.makeText(mActivity, mapType + " Mode", Toast.LENGTH_SHORT).show();
                });
                break;
        }

    }

//------------------------------------------------------- onMapClick ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMapClick(@NonNull LatLng latLng) {}

//------------------------------------------------------- Form ------------------------------------------------------------------------------------------------------------------------------------------------
    private void viewFormDialogBox(String ID){
        try{
            boolean isSno6Selected = false;
            FormDBModel formDBModel = dataBaseHelper.getFormByPolygonIDAndID(ID);
            // Form Model
            FormModel formModel = Utility.convertStringToFormModel(formDBModel.getFormData());
            // Form Fields
            FormFields bin = formModel.getForm();
           // if(formDBModel.isOnlineSave()){
                Log.e(TAG, "Image - > "+formModel.getForm().getProperty_images());
                Log.e(TAG, "File -> "+formModel.getForm().getPlan_attachment());
           //     formDBModel.setCameraPath(formModel.getForm().getProperty_images());
           //     formDBModel.setFilePath(formModel.getForm().getPlan_attachment());
           // }

            // Dialog Box
            Dialog fDB = new Dialog(this);
            fDB.requestWindowFeature(Window.FEATURE_NO_TITLE);
            fDB.setCancelable(false);
            fDB.setContentView(R.layout.dialogbox_form);
            fDB.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            // Camera
            ImageView imgCaptured = fDB.findViewById(R.id.db_imgCaptured);
            // File View Button
            Button db_btFileUpload = fDB.findViewById(R.id.db_btFileUpload);
            // TextView  File Name
            TextView db_tv_fileUploadName = fDB.findViewById(R.id.db_tv_fileUploadName);

            // Exit Button
            Button btExit = fDB.findViewById(R.id.btExit);
            btExit.setOnClickListener(view -> fDB.dismiss());
            // init Linear Layout ------------------------------
            LinearLayout ll_7    = fDB.findViewById(R.id.ll_7);
            LinearLayout ll_17_1 = fDB.findViewById(R.id.ll_17_1);
            LinearLayout ll_20   = fDB.findViewById(R.id.ll_20);
            LinearLayout ll_23   = fDB.findViewById(R.id.ll_23);
            LinearLayout ll_24   = fDB.findViewById(R.id.ll_24);
            LinearLayout ll_27   = fDB.findViewById(R.id.ll_27);
            LinearLayout ll_27_1 = fDB.findViewById(R.id.ll_27_1);
            LinearLayout ll_27_2 = fDB.findViewById(R.id.ll_27_2);
            LinearLayout ll_28 = fDB.findViewById(R.id.ll_28);
            LinearLayout ll_30 = fDB.findViewById(R.id.ll_30);

            // init Text View ----------------------------------------

            TextView tv_form_owner_name                  = fDB.findViewById(R.id.db_form_owner_name);
            TextView tv_form_old_property_no             = fDB.findViewById(R.id.db_form_old_property_no);
            TextView tv_form_new_property_no             = fDB.findViewById(R.id.db_form_new_property_no);
            TextView tv_form_property_name               = fDB.findViewById(R.id.db_form_property_name);
            TextView tv_form_property_address            = fDB.findViewById(R.id.db_form_property_address);
            TextView tv_form_sp_property_user_type       = fDB.findViewById(R.id.db_form_sp_property_user_type);
            TextView tv_form_property_user               = fDB.findViewById(R.id.db_form_property_user);
            TextView tv_form_resurvey_no                 = fDB.findViewById(R.id.db_form_resurvey_no);
            TextView tv_form_gat_no                      = fDB.findViewById(R.id.db_form_gat_no);
            TextView tv_form_zone                        = fDB.findViewById(R.id.db_form_zone);
            TextView tv_form_ward                        = fDB.findViewById(R.id.db_form_ward);
            TextView tv_form_mobile                      = fDB.findViewById(R.id.db_form_mobile);
            TextView tv_form_email                       = fDB.findViewById(R.id.db_form_email);
            TextView tv_form_aadhar_no                   = fDB.findViewById(R.id.db_form_aadhar_no);
            TextView tv_form_gis_id                      = fDB.findViewById(R.id.db_form_gis_id);
            TextView tv_form_sp_property_type            = fDB.findViewById(R.id.db_form_sp_property_type);
            TextView tv_form_no_of_floors                = fDB.findViewById(R.id.db_form_no_of_floor);
            TextView tv_form_property_release_date       = fDB.findViewById(R.id.db_form_property_release_date);
            TextView tv_form_sp_build_permission         = fDB.findViewById(R.id.db_form_sp_build_permission);
            TextView tv_form_sp_build_completion_form    = fDB.findViewById(R.id.db_form_sp_build_completion_form);
            TextView tv_form_sp_metal_road               = fDB.findViewById(R.id.db_form_sp_metal_road);
            TextView tv_form_sp_is_toilet_available      = fDB.findViewById(R.id.db_form_sp_is_toilet_available);
            TextView tv_form_total_toilet                = fDB.findViewById(R.id.db_form_total_toilet);
            TextView tv_form_sp_toilet_type              = fDB.findViewById(R.id.db_form_sp_toilet_type);
            TextView tv_form_sp_is_streetlight_available = fDB.findViewById(R.id.db_form_sp_is_streetlight_available);
            TextView tv_form_sp_is_water_line_available  = fDB.findViewById(R.id.db_form_sp_is_water_line_available);
            TextView tv_form_sp_total_water_line_27_1    = fDB.findViewById(R.id.db_form_sp_total_water_line_27_1);
            TextView tv_form_sp_total_water_line_27_2    = fDB.findViewById(R.id.db_form_sp_total_water_line_27_2);
            TextView tv_form_sp_water_use_type           = fDB.findViewById(R.id.db_form_sp_water_use_type);
            TextView tv_form_sp_solar_panel_available    = fDB.findViewById(R.id.db_form_sp_solar_panel_available);
            TextView tv_form_sp_solar_panel_type         = fDB.findViewById(R.id.db_form_sp_solar_panel_type);
            TextView tv_form_sp_rain_water_harvesting    = fDB.findViewById(R.id.db_form_sp_rain_water_harvesting);
            TextView tv_form_plot_area                   = fDB.findViewById(R.id.db_form_plot_area);
            TextView tv_form_property_area               = fDB.findViewById(R.id.db_form_property_area);
            TextView tv_form_total_area                  = fDB.findViewById(R.id.db_form_total_area);

            // Set Text -------------------------------
            // 1
            tv_form_owner_name.setText(Utility.getStringValue(bin.getOwner_name()));
            // 2
            tv_form_old_property_no.setText(Utility.getStringValue(bin.getOld_property_no()));
            // 3
            tv_form_new_property_no.setText(Utility.getStringValue(bin.getNew_property_no()));
            // 4
            tv_form_property_name.setText(Utility.getStringValue(bin.getProperty_name()));
            // 5
            tv_form_property_address.setText(Utility.getStringValue(bin.getProperty_address()));
            // 6
            tv_form_sp_property_user_type.setText(Utility.getStringValue(bin.getProperty_user_type()));

            if(bin.getProperty_user_type().equalsIgnoreCase("भोगवटादार")){
                isSno6Selected = false;
                ll_7.setVisibility(View.VISIBLE);
                // 7
                tv_form_property_user.setText(Utility.getStringValue(bin.getProperty_user()));
            }
            else if(bin.getProperty_user_type().equalsIgnoreCase("भाडेकरू")){
                ll_7.setVisibility(View.GONE);
                tv_form_property_user.setText("");
                isSno6Selected = true;
            }
            else{
                isSno6Selected = false;
                ll_7.setVisibility(View.GONE);
                tv_form_property_user.setText("");
            }

            // 8
            tv_form_resurvey_no.setText(Utility.getStringValue(bin.getResurvey_no()));
            // 9
            tv_form_gat_no.setText(Utility.getStringValue(bin.getGat_no()));
            // 10
            tv_form_zone.setText(Utility.getStringValue(bin.getZone()));
            // 11
            tv_form_ward.setText(Utility.getStringValue(bin.getWard()));
            // 12
            tv_form_mobile.setText(Utility.getStringValue(bin.getMobile()));
            // 13
            tv_form_email.setText(Utility.getStringValue(bin.getEmail()));
            // 14
            tv_form_aadhar_no.setText(Utility.getStringValue(bin.getAadhar_no()));
            // 16
            tv_form_gis_id.setText(Utility.getStringValue(bin.getGis_id()));
            // 17
            tv_form_sp_property_type.setText(Utility.getStringValue(bin.getProperty_type()));
            // 17.1
            if(!Utility.isEmptyString(bin.getProperty_type()) && bin.getProperty_type().equalsIgnoreCase("इतर")){
                ll_17_1.setVisibility(View.VISIBLE);
                tv_form_no_of_floors.setText(Utility.getStringValue(bin.getNo_of_floor()));
            }
            else{
                ll_17_1.setVisibility(View.GONE);
                tv_form_no_of_floors.setText("");
            }
            // 18
            tv_form_property_release_date.setText(Utility.getStringValue(bin.getProperty_release_date()));
            //19
            tv_form_sp_build_permission.setText(Utility.getStringValue(bin.getBuild_permission())); // ----------- spinner 19
            // 20
            // if spinner 19 is yes then show else don't show
            if(!Utility.isEmptyString(bin.getBuild_permission()) && bin.getBuild_permission().equals(selectYesOption)){
                ll_20.setVisibility(View.VISIBLE);
                tv_form_sp_build_completion_form.setText(Utility.getStringValue(bin.getBuild_completion_form())); // depend upon spinner 19
            }
            else{
                ll_20.setVisibility(View.GONE);
            }

            // 21
            tv_form_sp_metal_road.setText(Utility.getStringValue(bin.getMetal_road()));

            // 22
            tv_form_sp_is_toilet_available.setText(Utility.getStringValue(bin.getIs_toilet_available())); // ----------------- spinner 22
            if(!Utility.isEmptyString(bin.getIs_toilet_available()) && bin.getIs_toilet_available().equals(selectYesOption)){
                ll_23.setVisibility(View.VISIBLE);
                ll_24.setVisibility(View.VISIBLE);
                // 23
                tv_form_total_toilet.setText(Utility.getStringValue(bin.getTotal_toilet())); // depend upon spinner 22
                // 24
                tv_form_sp_toilet_type.setText(Utility.getStringValue(bin.getToilet_type())); // depend upon spinner 22
            }
            else{
                ll_23.setVisibility(View.GONE);
                ll_24.setVisibility(View.GONE);
            }
            // 25
            tv_form_sp_is_streetlight_available.setText(Utility.getStringValue(bin.getIs_streetlight_available()));

            // 26
            tv_form_sp_is_water_line_available.setText(Utility.getStringValue(bin.getIs_water_line_available())); // ----------------- spinner 26
            if(!Utility.isEmptyString(bin.getIs_water_line_available()) && bin.getIs_water_line_available().equals(selectYesOption)){
                ll_27.setVisibility(View.VISIBLE);
                ll_27_1.setVisibility(View.VISIBLE);
                ll_27_2.setVisibility(View.VISIBLE);
                ll_28.setVisibility(View.VISIBLE);
                // 27
                tv_form_sp_total_water_line_27_1.setText(Utility.getStringValue(bin.getTotal_water_line1())); // depend upon spinner 26
                tv_form_sp_total_water_line_27_2.setText(Utility.getStringValue(bin.getTotal_water_line2())); // depend upon spinner 26
                // 28
                tv_form_sp_water_use_type.setText(Utility.getStringValue(bin.getWater_use_type())); // depend upon spinner 26
            }
            else{
                ll_27.setVisibility(View.GONE);
                ll_27_1.setVisibility(View.GONE);
                ll_27_2.setVisibility(View.GONE);
                ll_28.setVisibility(View.GONE);
            }
            // 29
            tv_form_sp_solar_panel_available.setText(Utility.getStringValue(bin.getSolar_panel_available())); // ---------------- spinner 29
            // 30
            if(!Utility.isEmptyString(bin.getSolar_panel_available()) && bin.getSolar_panel_available().equals(selectYesOption)){
                ll_30.setVisibility(View.VISIBLE);
                tv_form_sp_solar_panel_type.setText(Utility.getStringValue(bin.getSolar_panel_type())); // depend upon spinner 29
            }
            else{
                ll_30.setVisibility(View.GONE);
            }

            // 31
            tv_form_sp_rain_water_harvesting.setText(Utility.getStringValue(bin.getRain_water_harvesting()));


            // RecycleView -----------------------------
            RecyclerView rvForm = fDB.findViewById(R.id.db_rvFormTableView);
            if(formModel.getDetais().size() > 0){
                rvForm.setVisibility(View.VISIBLE);
                AdapterFormTable adapterFormTable = new AdapterFormTable(mActivity,formModel.getDetais(),true,isSno6Selected);
                Utility.setToVerticalRecycleView(mActivity,rvForm,adapterFormTable);
            }
            else{
                rvForm.setVisibility(View.GONE);
            }
            // ----------------
            // 32
            tv_form_plot_area.setText(Utility.getStringValue(bin.getPlot_area()));
            // 33
            tv_form_property_area.setText(Utility.getStringValue(bin.getProperty_area()));
            // 34
            tv_form_total_area.setText(Utility.getStringValue(bin.getTotal_area()));


            // Camera Image Upload View -----------------------
            if(!Utility.isEmptyString(formDBModel.getCameraPath())){
               // try{
                    String imagePath = formDBModel.getCameraPath().split("#")[1];
                    if(formDBModel.getCameraPath().split("#")[0].startsWith("local")){
                        Glide.with(mActivity).load(imagePath).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imgCaptured);
                    }
                    else{
                        Uri uri = Uri.parse(imagePath);
                        Glide.with(mActivity).load(uri).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imgCaptured);
                    }
//                }
//                catch (Exception e){
//                    Log.e(TAG, e.getMessage());
//                    imgCaptured.setImageResource(R.drawable.ic_no_image);
//                }
            }
            else{
                // When No Image Found
                imgCaptured.setImageResource(R.drawable.ic_no_image);
            }


            // Click on Camera Image
            imgCaptured.setOnClickListener(view -> {
                Dialog dialog = new Dialog(mActivity);
                dialog.setContentView(R.layout.image_zoom_view_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView imageView = dialog.findViewById(R.id.dialogbox_image);

              //  PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
                // Image Load!
                //  try{
                // String imagePath = formDBModel.getCameraPath().split("#")[1];
                if(formDBModel.getCameraPath().split("#")[0].startsWith("local")){
                    Glide.with(mActivity).load(formDBModel.getCameraPath().split("#")[1]).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imageView);
                }
                else{
                    Uri uri = Uri.parse(formDBModel.getCameraPath().split("#")[1]);
                    Glide.with(mActivity).load(uri).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(imageView);
                }
            //    photoViewAttacher.update();

//                        }
//                        catch (Exception e){
//                            Log.e(TAG, e.getMessage());
//                            imageView.setImageResource(R.drawable.ic_no_image);
//                        }
                dialog.show();
            });


            // File Upload View -------------------------
            ArrayList<FileUploadViewModel> fileUploadList = new ArrayList<>();
            if(!Utility.isEmptyString(formDBModel.getFilePath())){
                db_tv_fileUploadName.setText("File Found");
                int n = formDBModel.getFilePath().split(",").length;
                for(int i=0; i<n; i++){
                    if(formDBModel.getFilePath().split(",")[i].split("#")[0].startsWith("local%")) {
                        String filePath = formDBModel.getFilePath().split(",")[i].split("#")[1];
                        File file = new File(filePath);
                        String fileName = file.getName();
                        fileUploadList.add(new FileUploadViewModel(fileName, filePath, false));
                    }
                    else {
                        String filename = formDBModel.getFilePath().split(",")[i].split("#")[0];
                        String filepath = formDBModel.getFilePath().split(",")[i].split("#")[1];
                        fileUploadList.add(new FileUploadViewModel(filename, filepath, true));
                    }
                }
            }
            else{
                db_tv_fileUploadName.setText("No File Upload");
            }

            // File Upload Button Click
            db_btFileUpload.setOnClickListener(view -> {
                if(Utility.isEmptyString(formDBModel.getFilePath())){
                    Toast.makeText(mActivity, "No File Found", Toast.LENGTH_SHORT).show();
                }
                else{
                    ViewFileUploadDialogBox(fileUploadList);
                }
            });

            // Show Dialog Box
            fDB.show();
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void viewAllFormDialogBox(String polygonID){
        ArrayList<FormListModel> formList = dataBaseHelper.getFormIDByPolygonID(polygonID);

            Dialog vfBox = new Dialog(mActivity);
            vfBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
            vfBox.setCancelable(false);
            vfBox.setContentView(R.layout.dialogbox_formlist_view);
            vfBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            vfBox.show();
            // Button Exit
            Button btExit = vfBox.findViewById(R.id.btExit);
            btExit.setOnClickListener(view -> vfBox.dismiss());

            // RecycleView
            RecyclerView rvFormListView = vfBox.findViewById(R.id.rvFormListView);

            AdapterFormListView adapterFormListView = new AdapterFormListView(mActivity, formList, formListModel -> {
                    //vfBox.dismiss();
                    if(!Utility.isEmptyString(polygonID)){
                        viewFormDialogBox(formListModel.getId());
                    }
            });
            Utility.setToVerticalRecycleView(mActivity,rvFormListView,adapterFormListView);


    }

    private void viewMultipleFormDialogBox(String gisID,String polygonID, String wardNo,DialogInterface dialogBox){
            ArrayList<FormListModel> formList = dataBaseHelper.getFormIDByPolygonID(polygonID);
            dialogBox.dismiss();
            Dialog vfBox = new Dialog(this);
            vfBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
            vfBox.setCancelable(false);
            vfBox.setContentView(R.layout.dialogbox_multipleform_view);
            vfBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

            // Add Form
            Button btAddForm = vfBox.findViewById(R.id.btAddForm);

            btAddForm.setOnClickListener(view -> {
                    String key = dataBaseHelper.getGenerateID(polygonID);
                    String key1 = dataBaseHelper.getGenerateIDLocal(polygonID);
                    Log.e(TAG,"Key - > " + key);
                    Log.e(TAG,"Key1 - > " + key1);


                try{
                        if(!Utility.isEmptyString(key)){
                            reDirectToMultipleFormFunction(gisID,polygonID,wardNo,Integer.parseInt(key));
                        }
                        else{
                            Log.e(TAG, "Key Null");
                        }
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                vfBox.dismiss();

            });

            // Exit Form
            Button btExitForm = vfBox.findViewById(R.id.btExitForm);
            btExitForm.setOnClickListener(view -> vfBox.dismiss());

            // RecycleView
            RecyclerView rvFormListView = vfBox.findViewById(R.id.rvFormListView);
            AdapterFormListView adapterFormListView = new AdapterFormListView(mActivity, formList, formListModel -> {});
            Utility.setToVerticalRecycleView(mActivity,rvFormListView,adapterFormListView);
            vfBox.show();

    }

    private void ViewFileUploadDialogBox(ArrayList<FileUploadViewModel> fileUploadViewModelArrayList){
        // DialogBox
        Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_fileuploadview_layout_dialogbox);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // TextView
        TextView tvUploadName = dialog.findViewById(R.id.tvUploadName);
        tvUploadName.setText("File Upload View");
        // RecycleView
        RecyclerView recyclerView = dialog.findViewById(R.id.file_upload_view_recycle_view);
        // Cancel Button
        Button cancel_bt = dialog.findViewById(R.id.file_upload_view_cancel_bt);
        cancel_bt.setOnClickListener(view1 -> dialog.dismiss());
        // Adapter
        FileUploadViewAdapter fileUploadViewAdapter = new FileUploadViewAdapter(mActivity,fileUploadViewModelArrayList);
        // Set Adapter
        recyclerView.setAdapter(fileUploadViewAdapter);
        // Set Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        fileUploadViewAdapter.notifyDataSetChanged();
        // Dialog box Show
        dialog.show();
    }

//------------------------------------------------------- Form Dialog Box Show ----------------------------------------------------------------------------------------------------------------------

    private void showSelectedDialogBox(GeoJsonModel geoJsonModel){
        ArrayList<FormListModel> formList = dataBaseHelper.getFormIDByPolygonID(geoJsonModel.getPolygonID());
        Log.e(TAG,"Form Size -> " + formList.size());
        isMultipleForm = formList.size() > 0;
        Log.e(TAG,"is Multiple -> " + isMultipleForm);

        boolean isFormStatusCompleted = false;

        if(formList.size() > 0)
        {
                GeoJsonModel geoJsonModel1 = dataBaseHelper.getPolygonByPolygonId(geoJsonModel.getPolygonID());

                if(!Utility.isEmptyString(geoJsonModel1.getPolygonStatus()) && geoJsonModel1.getPolygonStatus().equalsIgnoreCase(Utility.PolygonStatusCompleted)){
                    isFormStatusCompleted = true;
                }
        }


        Log.e(TAG,"is FormStatus Completed -> " + isFormStatusCompleted);
        Utility.showSelectBox(mActivity, (item, dialogBox) -> {
            switch (item){

                case Utility.ITEM_SELECTED.SingleForm:
                    isMultipleForm = false;
                    dialogBox.dismiss();
                    reDirectToSingleFormFunction(geoJsonModel,isMultipleForm);
                    break;

                case Utility.ITEM_SELECTED.MultipleForm:
                    dialogBox.dismiss();
                    isMultipleForm = true;
                    // empty form then we have to create it!
//                    if(!isMultipleForm){
//                        reDirectToSingleFormFunction(geoJsonModel,isMultipleForm);
//                    }
//                    else{
                        viewMultipleFormDialogBox(geoJsonModel.getGisID(),geoJsonModel.getPolygonID(), geoJsonModel.getWardNo(),dialogBox);
//                    }
                    break;

                case Utility.ITEM_SELECTED.VIEW:
                    if(formList.size() > 0){
                        viewAllFormDialogBox(geoJsonModel.getPolygonID());
                        dialogBox.dismiss();
                    }
                    else{
                        Utility.showToast(mActivity,"No Form Found");
                    }
                    break;

            }
        },isMultipleForm,isFormStatusCompleted);

    }

    private void reDirectToSingleFormFunction(GeoJsonModel geoJsonModel, boolean isMultipleForm){
        Intent intentSingleForm = new Intent(MapsActivity.this, FormActivity.class);
        intentSingleForm.putExtra(Utility.PASS_GIS_ID,geoJsonModel.getGisID());
        intentSingleForm.putExtra(Utility.PASS_POLYGON_ID,geoJsonModel.getPolygonID());
        intentSingleForm.putExtra(Utility.PASS_WARD_NO,geoJsonModel.getWardNo());
        intentSingleForm.putExtra(Utility.PASS_IS_MULTIPLE, isMultipleForm);
        startActivityForResult(intentSingleForm,FORM_REQUEST_CODE);
    }

    private void reDirectToMultipleFormFunction(String gisID, String polygonID,String wardNo,int lastKey){
        Intent intentMultipleForm = new Intent(MapsActivity.this, FormActivity.class);
        intentMultipleForm.putExtra(Utility.PASS_GIS_ID,gisID);
        intentMultipleForm.putExtra(Utility.PASS_POLYGON_ID,polygonID);
        intentMultipleForm.putExtra(Utility.PASS_WARD_NO,wardNo);
        intentMultipleForm.putExtra(Utility.PASS_IS_MULTIPLE, true);
        intentMultipleForm.putExtra(Utility.PASS_LAST_KEY,lastKey);
        startActivityForResult(intentMultipleForm,FORM_REQUEST_CODE);
    }

//------------------------------------------------------- Resurvey Form ----------------------------------------------------------------------------------------------------------------------


//------------------------------------------------------- Resurvey Form Dialog Box ----------------------------------------------------------------------------------------------------------------------

    private void showEditDialogBox(GeoJsonModel geoJsonModel){

        Utility.showEditBox(mActivity, (item, dialogBox) -> {
            if (Utility.ITEM_SELECTED.EDIT.equals(item)){
                dialogBox.dismiss();
                if (geoJsonModel != null) {
                    if (!Utility.isEmptyString(geoJsonModel.getPolygonID())) {
                        viewAllResurveyForms(geoJsonModel);
                    } else {
                        Log.e(TAG, "Resurvey Polygon ID Empty");
                    }
                }
            }
        });
    }

    private void viewAllResurveyForms(GeoJsonModel geoJsonModel){
        ArrayList<FormListModel> formList = dataBaseHelper.getFormIDByPolygonID(geoJsonModel.getPolygonID());
        Dialog vfBox = new Dialog(this);
        vfBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        vfBox.setCancelable(false);
        vfBox.setContentView(R.layout.dialogbox_formlist_view);
        vfBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        // Button Exit
        Button btExit = vfBox.findViewById(R.id.btExit);
        btExit.setOnClickListener(view -> vfBox.dismiss());
        // RecycleView
        RecyclerView rvFormListView = vfBox.findViewById(R.id.rvFormListView);
        // Adapter View
        AdapterFormListView adapterFormListView = new AdapterFormListView(mActivity, formList, formListModel -> {

            if(!Utility.isEmptyString(geoJsonModel.getPolygonID())){
                vfBox.dismiss();
                reDirectToResurveyForm(formListModel.getId(),geoJsonModel);
            }

        });
        Utility.setToVerticalRecycleView(mActivity,rvFormListView,adapterFormListView);
        vfBox.show();
    }

    private void reDirectToResurveyForm(String ID,GeoJsonModel geoJsonModel){
        Intent intentResurveyForm = new Intent(MapsActivity.this, ResurveyFormActivity.class);
        intentResurveyForm.putExtra(Utility.PASS_GIS_ID,geoJsonModel.getGisID());
        intentResurveyForm.putExtra(Utility.PASS_POLYGON_ID,geoJsonModel.getPolygonID());
        intentResurveyForm.putExtra(Utility.PASS_WARD_NO,geoJsonModel.getWardNo());
        intentResurveyForm.putExtra(Utility.PASS_ID, ID);
        startActivityForResult(intentResurveyForm,FORM_REQUEST_CODE);
    }

//------------------------------------------------------- onActivityResult ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Form Successfully Submit
        if(requestCode == FORM_REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                String polygonID = data.getStringExtra(Utility.PASS_POLYGON_ID);
                Log.e(TAG,"Form Submit PolygonID -> "+polygonID);
                if(!Utility.isEmptyString(polygonID)) {
                    Polygon polygon = geoJsonPolygonLists.get(polygonID);
                    if(polygon != null){
                        GeoJsonModel geoJsonModel = dataBaseHelper.getPolygonByPolygonId(polygonID);
                        if(!Utility.isEmptyString(geoJsonModel.getPolygonStatus())){
                            String status = geoJsonModel.getPolygonStatus();
                            Log.e(TAG,"Polygon Status -> " + status);
                            if(status.equalsIgnoreCase(Utility.PolygonStatusCompleted)){
                                polygon.setStrokeColor(Color.parseColor(Utility.COLOR_CODE.GREEN));
                            }
                            else if(status.equalsIgnoreCase(Utility.PolygonStatusNotComplete)){
                                polygon.setStrokeColor(Color.parseColor(Utility.COLOR_CODE.RED));
                            }
                            else{
                                polygon.setStrokeColor(Color.parseColor(Utility.COLOR_CODE.DEFAULT_COLOR));
                            }
                        }
                    }
                }
            }
        }
    }

//------------------------------------------------------- Fetch GeoJson File ----------------------------------------------------------------------------------------------------------------------

    private void showAllGeoJsonPolygon(){

        showProgressBar("Loading Property.....");

        ArrayList<ArrayList<LatLng>> geoJsonLatLonLists = new ArrayList<>();
        ArrayList<GeoJsonModel> geoJsonModelLists = new ArrayList<>();

        try{
            ExecutorService service = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            service.execute(() -> {
                // To Do In Background
                ArrayList<GeoJsonModel> geoJsonModels = dataBaseHelper.getAllGeoJsonPolygon();
                //Log.e(TAG,"Geo-Json Polygon Size: "+ geoJsonModels.size());
                try{
                    if(geoJsonModels.size() > 0){
                        for(int i=0; i<geoJsonModels.size(); i++){
                            GeoJsonModel geoJsonModel = geoJsonModels.get(i);
                            if(!Utility.isEmptyString(geoJsonModel.getLatLon())){
                                ArrayList<ArrayList<LatLng>> geoJsonLatLonList = Utility.convertStringToListOfPolygon(geoJsonModel.getLatLon());
                                if(geoJsonLatLonList.size() > 0){
                                    for(int j=0; j<geoJsonLatLonList.size(); j++){
                                        ArrayList<LatLng> latLngs = geoJsonLatLonList.get(j);
                                        if(latLngs.size() > 0){
                                            geoJsonLatLonLists.add(latLngs);
                                            geoJsonModelLists.add(geoJsonModel);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Log.e(TAG, "Geo-Json Polygon Empty");
                        dismissProgressBar();
                    }
                }
                catch (Exception e){
                    dismissProgressBar();
                    Log.e(TAG, "Error: "+e.getMessage());
                }

                // Post ----------------------------------------
                handler.post(() -> {
                    if(geoJsonMarkerList.size() > 0){
                        for(Marker m : geoJsonMarkerList){
                            if(m != null){
                                m.remove();
                            }
                        }
                        geoJsonModelLists.clear();
                    }
                    if(geoJsonLatLonLists.size() > 0){
                        for(int i=0; i<geoJsonLatLonLists.size(); i++){
                            PolygonOptions polygonOptions = new PolygonOptions();
                            polygonOptions.clickable(true);
                            polygonOptions.addAll(geoJsonLatLonLists.get(i));
                            polygonOptions.strokeWidth(4);
                            polygonOptions.strokeColor(Color.parseColor(Utility.COLOR_CODE.DEFAULT_COLOR));

                            if(!Utility.isEmptyString(geoJsonModelLists.get(i).getPolygonStatus())){
                                String status  = geoJsonModelLists.get(i).getPolygonStatus();
                                String counter = dataBaseHelper.getGenerateID(geoJsonModelLists.get(i).getPolygonID());
                                //    Log.e(TAG,"Polygon Status -> " + status);
                                if(counter.equalsIgnoreCase("0")){
                                 //   Log.e(TAG,"Counter ID ->" + geoJsonModelLists.get(i).getPolygonID());
                                    polygonOptions.strokeColor(Color.parseColor(Utility.COLOR_CODE.DEFAULT_COLOR));
                                }
                                else{
                                    if(status.equalsIgnoreCase(Utility.PolygonStatusCompleted)){
                                        polygonOptions.strokeColor(Color.parseColor(Utility.COLOR_CODE.GREEN));
                                    }
                                    else if(status.equalsIgnoreCase(Utility.PolygonStatusNotComplete)){
                                        polygonOptions.strokeColor(Color.parseColor(Utility.COLOR_CODE.RED));
                                    }
                                }



                            }
                            Marker marker = Utility.addGeoJsonPolygonMarker(mActivity,mMap,Utility.getPolygonCenterPoint(geoJsonLatLonLists.get(i)),geoJsonModelLists.get(i).getPolygonID(),1);
                            marker.setFlat(true);
                            marker.setTag(geoJsonModelLists.get(i));

                            Polygon polygon = mMap.addPolygon(polygonOptions);
                            polygon.setTag(geoJsonModelLists.get(i));
                            polygon.setVisible(true);
                            polygon.setZIndex(3);
                            geoJsonPolygonLists.put(geoJsonModelLists.get(i).getPolygonID(),polygon);
                            geoJsonMarkerList.add(marker);
                        }
                    }
                    dismissProgressBar();
                    // Database Contains Some Data or not
                    if(isFormDataNotSync()){
                        Utility.showSyncYourDataAlert(this);
                    }
                    isGoToCurrentLocation = true;
                    isMarkerVisible = true;
                        LatLng latLng = new LatLng(16.751075235,74.587887456);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));

                });
            });

        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

//------------------------------------------------------- onPolygonClick ----------------------------------------------------------------------------------------------------------------------

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {

        if(polygon.getTag() instanceof GeoJsonModel){
            //Log.e(TAG,"Polygon instance of GeoJsonModel");
            GeoJsonModel geoJsonModel = (GeoJsonModel) polygon.getTag();

            if(!Utility.isEmptyString(geoJsonModel.getPolygonID())){
                Log.e(TAG, "Polygon ID: -> "+Utility.getStringValue(geoJsonModel.getPolygonID()));
                showSelectedDialogBox(geoJsonModel);
               // showEditDialogBox(geoJsonModel);
            }
            else{
                Log.e(TAG,"Polygon ID is Empty");
            }
        }
        else{
            Log.e(TAG,"Polygon not instance of GeoJsonModel");
        }

    }

//------------------------------------------------------- onMarkerClick ----------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.e(TAG,"marker");

        if(marker.getTag() instanceof GeoJsonModel){
            Log.e(TAG,"Marker instance of GeoJsonModel");
            GeoJsonModel geoJsonModel = (GeoJsonModel) marker.getTag();
            if(!Utility.isEmptyString(geoJsonModel.getPolygonID())){
                Log.e(TAG, "Polygon ID: -> "+Utility.getStringValue(geoJsonModel.getPolygonID()));
                showSelectedDialogBox(geoJsonModel);
                //showEditDialogBox(geoJsonModel);
            }
            else{
                Log.e(TAG,"Polygon ID is Empty");
            }
        }
        else{
            Log.e(TAG,"Marker not instance of GeoJsonModel");
        }
        return false;
    }

//------------------------------------------------- File Upload ------------------------------------------------------------------------------------------------------------------------

    private class FileUploadServer extends AsyncTask<Void, Integer, String> {
        StringBuilder filePathData;
        String form_id;
        String unique_number;
        String type;
        boolean isCameraFileUpload = false;


        public FileUploadServer(StringBuilder filePathData, String form_id, String unique_number,String type, boolean isCameraFileUpload) {
            this.filePathData = filePathData;
            this.form_id = form_id;
            this.unique_number = unique_number;
            this.type = type;
            this.isCameraFileUpload = isCameraFileUpload;

            if(type.equals(TYPE_FILE)){
                Log.e(TAG, "File Type ");
                isFileUpload = false;
            }
            else if(type.equals(TYPE_CAMERA) ){
                Log.e(TAG, "Camera Type ");
                isCameraUpload = false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {}
        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile(){
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL_Utility.WS_FORM_FILE_UPLOAD);
            Log.e(TAG,"File-Upload API -> " + URL_Utility.WS_FORM_FILE_UPLOAD);

            try {
                if(filePathData != null){
                    if(!Utility.isEmptyString(filePathData.toString())){
                        // File Path!
                        String[] path = filePathData.toString().split(",");
                        Log.e(TAG, "path: "+ filePathData.toString());
                        for (String filepath : path) {
                            File sourceFile = new File(filepath.split("#")[1]);
                            String data = "";
                            JSONObject params = new JSONObject();
                            try {
                                if(isCameraFileUpload){
                                    params.put(Utility.PASS_COLUMN_NUMBER, URL_Utility.PARAM_PROPERTY_IMAGES);
                                }
                                else{
                                    params.put(Utility.PASS_COLUMN_NUMBER, URL_Utility.PARAM_PLAN_ATTACHMENT);
                                }
                                params.put(Utility.PASS_UNIQUE_NUMBER, unique_number);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Encrypt Data!
                            data = params.toString();
                            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(num -> publishProgress((int) ((num / (float) totalSize) * 100)));
                            entity.addPart(URL_Utility.PARAM_FILE_UPLOAD, new FileBody(sourceFile));
                            entity.addPart("data", new StringBody(data));
                            Log.e(TAG, "File-Upload Data -> "+ data);

                            totalSize = entity.getContentLength();
                            httppost.setEntity(entity);
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity r_entity = response.getEntity();
                            int statusCode = response.getStatusLine().getStatusCode();

                            if (statusCode == 200) {
                                responseString = EntityUtils.toString(r_entity);
                            } else {
                                dismissProgressBar();
                                responseString = "Error occurred! Http Status Code: " + statusCode;
                                Log.e(TAG, responseString);
                                // Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                            }
                        }
                    }
                    else{
                        Log.e(TAG,"filePathData is Empty");
                    }
                }
                else{
                    Log.e(TAG,"filePathData null");
                    isFormUpload = true;
                    //  Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }

            } catch (IOException e) {
                dismissProgressBar();
                isFormUpload= true;
                // Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                Log.e(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                dismissProgressBar();
                isFormUpload = true;
                // Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String result) {
            String response = result;
            Log.e(TAG, response);
            if(!response.equals("")){
                try {
                    JSONObject mLoginObj = new JSONObject(response);
                    String status = mLoginObj.optString("status");
                    if (status.equalsIgnoreCase("Success")){

                        switch (type) {
                            case TYPE_FILE:
                                Log.e(TAG, "File Upload Successfully");
                                isFileUpload = true;
                                break;

                            case TYPE_CAMERA:
                                Log.e(TAG, "Camera File Upload Successfully");
                                isCameraUpload = true;
                                break;
                        }

                        if((isCameraUpload && isFileUpload )){

                            if (formDBModel != null && formDBModel.getId() != null) {
                                // then
                                if (dataBaseHelper.getAllForms().size() > 0) {
                                    dataBaseHelper.deleteMapFormLocalData(formDBModel.getId());
                                }
                                SyncFormDetails();
                            }
                        }
                    }
                    else{
                        Log.e(TAG,status);
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }

                } catch (JSONException e) {
                    Log.e(TAG,e.getMessage());
                    dismissProgressBar();
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                }
            }
            else{
                Log.e(TAG, response);
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
            super.onPostExecute(result);
        }
    }

    private class FileUploadServerLogout extends AsyncTask<Void, Integer, String> {
        StringBuilder filePathData;
        String form_id;
        String unique_number;
        String type;
        boolean isCameraFileUpload = false;


        public FileUploadServerLogout(StringBuilder filePathData, String form_id, String unique_number,String type, boolean isCameraFileUpload) {
            this.filePathData = filePathData;
            this.form_id = form_id;
            this.unique_number = unique_number;
            this.type = type;
            this.isCameraFileUpload = isCameraFileUpload;

            if(type.equals(TYPE_FILE)){
                Log.e(TAG, "File Type ");
                isFileUpload = false;
            }
            else if(type.equals(TYPE_CAMERA) ){
                Log.e(TAG, "Camera Type ");
                isCameraUpload = false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }
        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile(){
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL_Utility.WS_FORM_FILE_UPLOAD);
            Log.e(TAG,"File-Upload API -> " + URL_Utility.WS_FORM_FILE_UPLOAD);

            try {
                if(filePathData != null){
                    if(!Utility.isEmptyString(filePathData.toString())){
                        // File Path!
                        String[] path = filePathData.toString().split(",");
                        Log.e(TAG, "path: "+ filePathData.toString());
                        for (String filepath : path) {
                            File sourceFile = new File(filepath.split("#")[1]);
                            String data = "";
                            JSONObject params = new JSONObject();
                            try {
                                if(isCameraFileUpload){
                                    params.put(Utility.PASS_COLUMN_NUMBER, URL_Utility.PARAM_PROPERTY_IMAGES);
                                }
                                else{
                                    params.put(Utility.PASS_COLUMN_NUMBER, URL_Utility.PARAM_PLAN_ATTACHMENT);
                                }
                                params.put(Utility.PASS_UNIQUE_NUMBER, unique_number);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Encrypt Data!
                            data = params.toString();
                            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(num -> publishProgress((int) ((num / (float) totalSize) * 100)));
                            entity.addPart(URL_Utility.PARAM_FILE_UPLOAD, new FileBody(sourceFile));
                            entity.addPart("data", new StringBody(data));
                            Log.e(TAG, "File-Upload Data -> "+ data);

                            totalSize = entity.getContentLength();
                            httppost.setEntity(entity);
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity r_entity = response.getEntity();
                            int statusCode = response.getStatusLine().getStatusCode();

                            if (statusCode == 200) {
                                responseString = EntityUtils.toString(r_entity);
                            } else {
                                dismissProgressBar();
                                responseString = "Error occurred! Http Status Code: " + statusCode;
                                Log.e(TAG, responseString);
                                // Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                            }
                        }
                    }
                    else{
                        Log.e(TAG,"filePathData is Empty");
                    }
                }
                else{
                    Log.e(TAG,"filePathData null");
                    //  Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }

            } catch (IOException e) {
                dismissProgressBar();
                // Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                Log.e(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                dismissProgressBar();
                // Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String result) {
            String response = result;
            Log.e(TAG, response);
            if(!response.equals("")){
                try {
                    JSONObject mLoginObj = new JSONObject(response);
                    String status = mLoginObj.optString("status");
                    if (status.equalsIgnoreCase("Success")){

                        switch (type) {
                            case TYPE_FILE:
                                Log.e(TAG, "File Upload Successfully");
                                isFileUpload = true;
                                break;

                            case TYPE_CAMERA:
                                Log.e(TAG, "Camera File Upload Successfully");
                                isCameraUpload = true;
                                break;
                        }

                        if((isCameraUpload && isFileUpload )){

                            if (formDBModel != null && formDBModel.getId() != null) {
                                // then
                                if (dataBaseHelper.getAllForms().size() > 0) {
                                    dataBaseHelper.deleteMapFormLocalData(formDBModel.getId());
                                }
                                LogoutSyncFormDetails();
                            }
                        }
                    }
                    else{
                        Log.e(TAG,status);
                        dismissProgressBar();
                        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    }

                } catch (JSONException e) {
                    Log.e(TAG,e.getMessage());
                    dismissProgressBar();
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                }
            }
            else{
                Log.e(TAG, response);
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
            super.onPostExecute(result);
        }
    }

//------------------------------------------------- addWMSLayer ------------------------------------------------------------------------------------------------------------------------

    private void addWMSLayer(String wmsLayerURl){
        TileProvider tileProvider = WMSTileProviderFactory.getWMSTileProvider(WMSProvider.getWMSLayer(wmsLayerURl));
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
    }

//---------------------------------------------- Location Permission ------------------------------------------------------------------------------------------------------------------------

    private void LocationPermission() {
        if(SystemPermission.isLocation(mActivity)) {
            location();
        }
    }

    @SuppressLint("MissingPermission")
    private void location() {
        //now for receiving constant location updates:
        mRequest = LocationRequest.create();
        mRequest.setInterval(2000);//time in ms; every ~2 seconds
        mRequest.setFastestInterval(1000);
        mRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, 500);
                }
                catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
       // fusedLocationProviderClient.requestLocationUpdates(mRequest, locationCallback, null);
    }

    protected void stopLocationUpdates(){
     //   fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    //---------------------------------------------- onPause ------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        dismissProgressBar();
    }

    //---------------------------------------------- onResume ------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

//---------------------------------------------- onResume ------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
