package com.eighttechprojects.propertytaxshirol.Activity.GoogleMap;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.eighttechprojects.propertytaxshirol.Activity.Form.FormActivity;
import com.eighttechprojects.propertytaxshirol.Activity.SplashActivity;
import com.eighttechprojects.propertytaxshirol.Adapter.AdapterFormTable;
import com.eighttechprojects.propertytaxshirol.Database.DataBaseHelper;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.Model.FormFields;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.GeoJson.Feature;
import com.eighttechprojects.propertytaxshirol.Model.GeoJson.GeoJson;
import com.eighttechprojects.propertytaxshirol.Model.GeoJson.Geometry;
import com.eighttechprojects.propertytaxshirol.Model.GeoJson.Point;
import com.eighttechprojects.propertytaxshirol.Model.GeoJson.ShirolGeoModel;
import com.eighttechprojects.propertytaxshirol.Model.GeoJson.ShirolLatLon;
import com.eighttechprojects.propertytaxshirol.Model.GeoJson.ShirolModel;
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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.OverloadResolutionByLambdaReturnType;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnPolygonClickListener, View.OnClickListener, WSResponseInterface {

    // TAG
    public static final String TAG = MapsActivity.class.getSimpleName();
    public static final String TAG_GEO_JSON = "Shirol GeoJson";
    // Map
    GoogleMap mMap;
    // Zoom Map
    GoogleMap zoomMap;
    SupportMapFragment zoomMapFragment;
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
    private static final float DEFAULT_ZOOM_MAP = 19f;
    private boolean isGoToCurrentLocation = false;
    // Form
    private static final String selectYesOption = "होय";
    private static final String selectNoOption  = "नाही";
    private static final int FORM_REQUEST_CODE = 1001;
    private boolean isFormClick = false;
    private int formCount = 0;
    // Point
    private Marker formMarker;

    // Logout ----------------------------------------------------------------
    // ArrayList
    private ArrayList<FormDBModel> formDBModelList = new ArrayList<>();

    private ArrayList<FormDBModel> formSyncList = new ArrayList<>();
    private FormDBModel formDBModel;

    // Broadcast Receiver
    BroadcastReceiver broadcastReceiver;


    // File Upload

    public long totalSize = 0;
    private String unique_number ="", datetime = "";
    public static final String TYPE_FILE   = "file";
    public static final String TYPE_CAMERA = "cameraUploader";
    public static boolean isFileUpload   = true;
    public static boolean isCameraUpload = true;

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
        // logout 24hr
        LogoutAfter24hr();
        // Zoom Map
        zoomMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.zoom_map);
        assert zoomMapFragment != null;
        zoomMapFragment.getMapAsync(onZoomMapReadyCallback());
        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        // Location Call Back
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location loc : locationResult.getLocations()) {
                    mCurrentLocation = loc;
                    if(mCurrentLocation != null){
                        if(!isGoToCurrentLocation){
                            isGoToCurrentLocation = true;
                            // Current LatLon
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),DEFAULT_ZOOM));
                            Log.e(TAG,"Current Location: " + mCurrentLocation.getLatitude() + " , " + mCurrentLocation.getLongitude());
                        }
                    }
                }
            }
        };
        LocationPermission();

        registerReceiver();

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
        // Marker Click Listener
        mMap.setOnMarkerClickListener(this);
        // Polygon Click Listener
        mMap.setOnPolygonClickListener(this);
        // Marker Drag Listener
        mMap.setOnMarkerDragListener(this);
        // setOnClickListener
        setOnClickListener();
        // show All Form Data
        showAllForm();
        // Database Contains Some Data or not
        if(isFormDataNotSync()){
            Utility.showSyncYourDataAlert(this);
        }


//        LatLng latLng = new LatLng(16.740298501194108,74.582463077023661);
//        Utility.addMapFormMarker(mMap, latLng, BitmapDescriptorFactory.HUE_GREEN);
//
//        fetchGeoJsonFile();
        //showWMSLayer();
        //   showShirolGeoJson();

    }

//------------------------------------------------------- setOnClickListener ------------------------------------------------------------------------------------------------------------------------------------------------

    private void setOnClickListener(){
        binding.fabForm.setOnClickListener(this);
        binding.imgMyLocation.setOnClickListener(this);
        binding.rlSaveForm.setOnClickListener(this);
    }

//------------------------------------------------------- Menu ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(isFormClick){
            inflater.inflate(R.menu.menu_empty, menu);
        }
        else{
            inflater.inflate(R.menu.map_menu, menu);
        }
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

            case R.id.menuResurvey:
                ArrayList<FormDBModel> dbModels = dataBaseHelper.getResurveyMapFormDataList();
                if(dbModels.size() > 0){
                    Utility.reDirectTo(mActivity, ResurveyActivity.class);
                }
                else{
                    Utility.showOKDialogBox(mActivity, "Alert", "No Data Found", DialogInterface::dismiss);
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

    private void reDirectToLoginPage(){
        String date = Utility.getSavedData(mActivity,Utility.OLD_DATE);
        Utility.clearData(this);
        Utility.saveData(mActivity,Utility.OLD_DATE,date);
        // Database Clear
        dataBaseHelper.logout();
        dismissProgressBar();
        startActivity(new Intent(this, SplashActivity.class));
    }

    private void LogoutSync(){
        ArrayList<FormDBModel> formDBModels = dataBaseHelper.getMapFormLocalDataList();

        if(formDBModels.size() == 0){
            reDirectToLoginPage();
            Log.e(TAG,"Logout No Data Found in Local DataBase");
        }
        else{
            Log.e(TAG, "Logout DataBase Contain some Data");

            if(formDBModels.size() > 0){
                Log.e(TAG, "Logout Sync Form On");
                formDBModelList = dataBaseHelper.getMapFormLocalDataList();
                LogoutSyncFormDetails();
            }

        }
    }

    private void LogoutSyncFormDetails(){
        if(formDBModelList != null && formDBModelList.size() > 0){
            formDBModel = formDBModelList.get(0);
            formDBModelList.remove(0);
            LogoutSyncFormDataToServer(formDBModel);
        }
        else{
            Log.e(TAG, "Logout Sync Form Off");
            Log.e(TAG,  "Logout Sync Successfully");
            reDirectToLoginPage();
        }
    }

    private void LogoutSyncFormDataToServer(FormDBModel formDBModel){
        Map<String, String> params = new HashMap<>();
        params.put("data", formDBModel.getFormData());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM, URL_Utility.WS_FORM, params, false, false);
    }

    private void LogoutAfter24hr(){
        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String date = Utility.getSavedData(mActivity,Utility.OLD_DATE);
        Log.e(TAG,"Current Date: "+ currentDate);
        Log.e(TAG,"Old Date: "+ date);
        if(!Utility.isEmptyString(date)){
            if(date.equals(currentDate)){
                Log.e(TAG, "true");
            }
            else {
                Log.e(TAG, "false");
                if(SystemPermission.isInternetConnected(mActivity)){
                    showProgressBar();
                    LogoutSync();
                }
            }
        }
        Utility.saveData(mActivity,Utility.OLD_DATE, currentDate);

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

//------------------------------------------------------- onSuccessResponse -----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onSuccessResponse(URL_Utility.ResponseCode responseCode, String response) {
        // Form
        if(responseCode == URL_Utility.ResponseCode.WS_FORM){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "Logout Form Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                        if (formDBModel != null && formDBModel.getId() != null) {
                            // then
                            if (dataBaseHelper.getMapFormLocalDataList().size() > 0) {
                                dataBaseHelper.deleteMapFormLocalData(formDBModel.getId());
                            }
                            LogoutSyncFormDetails();
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
                    Log.e(TAG,"Logout Sync Json Error: "+ e.getMessage());
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                }
            }
            else{
                dismissProgressBar();
                Log.e(TAG, "Logout Sync Response Empty");
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
                        if (formDBModel != null && formDBModel.getId() != null) {
                            // then
                            if (dataBaseHelper.getMapFormLocalDataList().size() > 0) {
                                dataBaseHelper.deleteMapFormLocalData(formDBModel.getId());
                                dataBaseHelper.updateMapData(formDBModel.getToken(),"f");
                            }
                            SyncFormDetails();
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
    }

//------------------------------------------------------- onErrorResponse -----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onErrorResponse(URL_Utility.ResponseCode responseCode, VolleyError error){
        dismissProgressBar();
        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
        Log.e(TAG, "Logout Error Response Code: "+responseCode);
        Log.e(TAG, "Logout Error Message: "+error.getMessage());
    }

//------------------------------------------------------- Sync ------------------------------------------------------------------------------------------------------------------------------------------------

    private void Sync(){
        ArrayList<FormDBModel> formDBModels = dataBaseHelper.getMapFormLocalDataList();
        if(formDBModels.size() == 0){
            dismissProgressBar();
            Log.e(TAG, "Sync Local Database Contain no Data");
            Utility.showOKDialogBox(this, "Sync", "Data Already Sync", DialogInterface::dismiss);
        }
        else{
            if(SystemPermission.isInternetConnected(mActivity)){
                //baseApplication.startSyncService();
                showProgressBar("Sync...");
                Log.e(TAG, "Sync Database Contain some Data");
                if(formDBModels.size() > 0){
                    Log.e(TAG, "Sync Service Form On");
                    formSyncList = dataBaseHelper.getMapFormLocalDataList();
                    Log.e(TAG, "Sync Form Size: "+ formSyncList.size());
                    SyncFormDetails();
                }
            }

        }
    }


    private void SyncFormDetails(){
        if(formSyncList != null && formSyncList.size() > 0){
            formDBModel = formSyncList.get(0);
            formSyncList.remove(0);
            SyncFormDataToServer(formDBModel);
        }
        else{
            Log.e(TAG, "Sync Service Form Off");
            Log.e(TAG,  "Data Sync Successfully");
            dismissProgressBar();
            refreshFormData();
            Utility.showOKDialogBox(this, "Sync", "Data Sync Successfully", DialogInterface::dismiss);
        }
    }

    private void SyncFormDataToServer(FormDBModel formDBModel){
        Log.e(TAG, "Upload to Server.........!");
        Map<String, String> params = new HashMap<>();
        params.put("data", formDBModel.getFormData());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM_SYNC, URL_Utility.WS_FORM_SYNC, params, false, false);
    }


//------------------------------------------------------- onClick ------------------------------------------------------------------------------------------------------------------------------------------------

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.imgMyLocation:
                if (mCurrentLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),DEFAULT_ZOOM));
                }
                break;

            case R.id.fabForm:
                if(!isFormClick){
                    binding.fabMenu.close(true);
                    isFormClick = true;
                    showFormClickToolBar(true);
                    showSaveFormButton(true);
                }
                break;

            case R.id.rlSaveForm:
                if(formMarker != null){
                    Intent intent = new Intent(mActivity, FormActivity.class);
                    intent.putExtra(Utility.PASS_LAT,String.valueOf(formMarker.getPosition().latitude));
                    intent.putExtra(Utility.PASS_LONG,String.valueOf(formMarker.getPosition().longitude));
                    startActivityForResult(intent,FORM_REQUEST_CODE);
                }
                else{
                    Toast.makeText(mActivity, "Please Mark your Location", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

//------------------------------------------------------- onActivityResult ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Form Successfully Submit
        if(requestCode == FORM_REQUEST_CODE && resultCode == RESULT_OK){
            clearForm();
            mMap.clear();
            showAllForm();
        }
    }

//------------------------------------------------------- onMapClick ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if(isFormClick){
            if(formCount < 1){
                formCount++;
                formMarker = Utility.addFormToMap(mMap,latLng);
                moveZoomCamera(latLng);
            }
        }
    }

//------------------------------------------------------- onMarkerClick ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onMarkerClick(@NonNull Marker marker){

        if(marker.getTag() instanceof FormModel){
            dialogBoxForm(marker);
        }
        return false;
    }

//------------------------------------------------------- onMarkerDragStart ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        if(isFormClick){
            onFormDrag(marker);
        }
    }

//------------------------------------------------------- onMarkerDrag ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        if(isFormClick){
            onFormDrag(marker);
        }
    }

//------------------------------------------------------- onMarkerDragEnd ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        if(isFormClick){
            onFormDrag(marker);
            offZoomCamera();
        }
    }

//------------------------------------------------------- Form ------------------------------------------------------------------------------------------------------------------------------------------------

    private void onFormDrag(Marker marker){
        formMarker.setPosition(marker.getPosition());
        onZoomCamera(marker);
    }

    private void clearForm(){
        if(formMarker != null){
            formMarker.remove();
        }
        formCount = 0;
        formMarker = null;
        isFormClick = false;
        showSaveFormButton(false);
        showFormClickToolBar(false);
    }

    private void dialogBoxForm(Marker marker){
        try{
            FormModel formModel = (FormModel) marker.getTag();
            assert  formModel != null;
            FormFields bin = formModel.getFormFields();
            // Dialog Box
            Dialog fDB = new Dialog(this);
            fDB.requestWindowFeature(Window.FEATURE_NO_TITLE);
            fDB.setCancelable(false);
            fDB.setContentView(R.layout.dialogbox_form);
            fDB.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            // Exit Button
            Button btExit = fDB.findViewById(R.id.btExit);
            btExit.setOnClickListener(view -> fDB.dismiss());
            // init Linear Layout ------------------------------
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
            TextView tv_form_grid_no                     = fDB.findViewById(R.id.db_form_grid_no);
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
            // 7
            tv_form_property_user.setText(Utility.getStringValue(bin.getProperty_user()));
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
            // 15
            tv_form_grid_no.setText(Utility.getStringValue(bin.getGrid_no()));
            // 16
            tv_form_gis_id.setText(Utility.getStringValue(bin.getGis_id()));
            // 17
            tv_form_sp_property_type.setText(Utility.getStringValue(bin.getProperty_type()));
            // 17.1
            if(!Utility.isEmptyString(bin.getProperty_type()) && bin.getProperty_type().equalsIgnoreCase("इतर")){
                ll_17_1.setVisibility(View.VISIBLE);
                tv_form_no_of_floors.setText(Utility.getStringValue(bin.getNo_of_floors()));
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
                tv_form_sp_total_water_line_27_1.setText(Utility.getStringValue(bin.getTotal_water_line())); // depend upon spinner 26
                tv_form_sp_total_water_line_27_2.setText(Utility.getStringValue(bin.getTotal_water_line())); // depend upon spinner 26
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
            if(formModel.getForm_detail().size() > 0){
                rvForm.setVisibility(View.VISIBLE);
                AdapterFormTable adapterFormTable = new AdapterFormTable(mActivity,formModel.getForm_detail(),true);
                Utility.setToVerticalRecycleView(mActivity,rvForm,adapterFormTable);
            }
            else{
                rvForm.setVisibility(View.GONE);
            }
            fDB.show();
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void showAllForm(){
        try {
            ArrayList<FormDBModel> formDBModels = dataBaseHelper.getMapFormDataList();
            if(formDBModels.size() > 0){
                Log.e(TAG,"Total Form: " + formDBModels.size());
                for(int i=0; i<formDBModels.size(); i++) {
                    FormDBModel formDBModel = formDBModels.get(i);
                    if(!Utility.isEmptyString(formDBModel.getLatitude()) && !Utility.isEmptyString(formDBModel.getLongitude())) {
                        LatLng latLng = new LatLng(Double.parseDouble(formDBModel.getLatitude()), Double.parseDouble(formDBModel.getLongitude()));
                        Marker marker;
                        if((formDBModels.get(i).getIsOnlineSave()).equalsIgnoreCase("f")){
                            marker = Utility.addMapFormMarker(mMap, latLng, BitmapDescriptorFactory.HUE_RED);
                      }
                      else{
                          marker = Utility.addMapFormMarker(mMap, latLng, BitmapDescriptorFactory.HUE_BLUE);
                      }
                        FormModel formModel = Utility.convertStringToFormModel(formDBModel.getFormData());
                        marker.setDraggable(false);
                        marker.setTag(formModel);
                    }
                    else{
                        Log.e(TAG,"Lat Lon Data null Found in Some Forms");
                    }
                }
            }
            else{
                Log.e(TAG,"Form Not Contains in Database");
            }
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private boolean isFormDataNotSync(){
        ArrayList<FormDBModel> formDBModels = dataBaseHelper.getMapFormLocalDataList();
        return formDBModels.size() > 0;
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
        fusedLocationProviderClient.requestLocationUpdates(mRequest, locationCallback, null);
    }

    protected void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

//---------------------------------------------- Zoom Map ------------------------------------------------------------------------------------------------------------------------

    // On Map Ready Zoom Map
    public OnMapReadyCallback onZoomMapReadyCallback(){
        return googleMap -> {
            zoomMap = googleMap;
            zoomMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        };
    }
    // Move Zoom Camera
    private void moveZoomCamera(LatLng latLng){
        if(zoomMap != null){
            zoomMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM_MAP));
        }
    }
    // Zoom Camera  On
    private void onZoomCamera(Marker marker){
        binding.zoomMapLayout.setVisibility(View.VISIBLE);
        binding.zoomMapMarkerLayout.setVisibility(View.VISIBLE);
        zoomMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),DEFAULT_ZOOM_MAP));
    }
    // Zoom Camera off
    private void offZoomCamera(){
        binding.zoomMapLayout.setVisibility(View.GONE);
        binding.zoomMapMarkerLayout.setVisibility(View.GONE);
    }

//---------------------------------------------- Visible or Gone ------------------------------------------------------------------------------------------------------------------------

    private void showSaveFormButton(boolean toVisible){
        binding.rlSaveForm.setVisibility(toVisible ? View.VISIBLE : View.GONE);
    }

    private void showFormClickToolBar(boolean isVisible){
        if(getSupportActionBar() != null) getSupportActionBar().setTitle(isVisible ? "Tap on Map" : "Map");
        invalidateOptionsMenu();
    }

//---------------------------------------------- onPause ------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
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

        if(isFormClick){
            Utility.showOKCancelDialogBox(mActivity, "Alert", "There are unsaved changes. Discard anyway ?", dialog -> {
                 clearForm();
                 dialog.dismiss();
            });
        }
        else{
            super.onBackPressed();
        }

    }


    private void refreshFormData(){
        mMap.clear();
        showAllForm();
    }

//------------------------------------------------------- BoardCast Receiver ----------------------------------------------------------------------------------------------------------------

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("sync");
            if(data.equalsIgnoreCase("on")){
                refreshFormData();
            }
        }
    };



    private void registerReceiver(){
        registerReceiver(syncReceiver, new IntentFilter(Utility.SyncServiceOn));
    }

    private void unregisterReceiver(){
        try{
            unregisterReceiver(syncReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

//------------------------------------------------------- Fetch GeoJson File ----------------------------------------------------------------------------------------------------------------------

    private void fetchGeoJsonFile(){
        Log.e(TAG_GEO_JSON, "GeoJson");
        ArrayList<ShirolGeoModel> listArrayList = new ArrayList<>();
        showProgressBar("Loading....");
//        ExecutorService service = Executors.newSingleThreadExecutor();
//        service.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                // On PreExecute Method
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        showProgressBar("Loading....");
//                    }
//                });
//
//                // On Background Method
//                try{
//                    InputStream inputStream = getAssets().open("shirol.json");
//                    int size = inputStream.available();
//                    byte[] buffer = new byte[size];
//                    inputStream.read(buffer);
//                    inputStream.close();
//                    String json = new String(buffer,"UTF-8");
//                    java.lang.reflect.Type Type = new TypeToken<GeoJson>() {}.getType();
//                    GeoJson geoJson =  new Gson().fromJson(json, Type);
//                    int n = geoJson.getFeatures().size();
//
//                    ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> coordinates;
//
//                    ArrayList<Feature> features = geoJson.getFeatures();
//                    // Features Loops
//                    for(int i=0; i<n; i++){
//                        Geometry geometry = features.get(i).getGeometry();
//                        String gisid = features.get(i).getProperties().getGISID();
//
//                        coordinates = geometry.getCoordinates();
//                        if(coordinates.size() > 0){
//                            int coo_size = coordinates.get(0).get(0).size();
//                            ArrayList<LatLng> latLngs = new ArrayList<>();
//                            for(int j=0; j<coo_size; j++){
//                                latLngs.add(new LatLng(coordinates.get(0).get(0).get(j).get(1), coordinates.get(0).get(0).get(j).get(0)));
//                            }
//                            listArrayList.add(new ShirolGeoModel(gisid,latLngs));
//                        }
//                    }
////            String json = new String(buffer,"UTF-8");
////            JSONArray jsonArray = new JSONArray(json);
////
////            ArrayList<ShirolModel> shirolModelArrayList = new ArrayList<>();
////            for(int i=0; i<jsonArray.length(); i++){
////                JSONObject object = jsonArray.getJSONObject(i);
////                String GISID = object.getString("GISID");
//
//                    //                JSONArray coordinatesJsonArray = new JSONArray(object.getString("Coordinates"));
//                    //ArrayList<ArrayList<LatLng>> latLngs = new ArrayList<>();
//
////                if(!Utility.isEmptyString(GISID)){
////                    java.lang.reflect.Type Type = new TypeToken<ShirolModel>() {}.getType();
////                    ShirolModel s =  new Gson().fromJson(object.toString(), Type);
////                    shirolModelArrayList.add(s);
////                    Log.e(TAG,"GISID: " + s.getGISID());
////                    if(s.getCoordinates().size() > 0){
////                        Log.e(TAG, "Coordinate Size: "+ s.getCoordinates().get(0).size());
////                    }
////                    else{
////                        Log.e(TAG, "Coordinate Size: ");
////                    }
////                }
//
////                java.lang.reflect.Type Type = new TypeToken<ShirolModel>() {}.getType();
////                ArrayList<ArrayList<LatLng>> latLngs =  new Gson().fromJson(coordinatesJsonArray.toString(), Type);
////
////                ShirolModel shirolModel = new ShirolModel(GISID,latLngs);
//
//                    // Log.e(TAG,"Coordinate: " + shirolModel.getCoordinates().size());
//                    //}
//
//                    // LatLng latLng = new LatLng(Double.parseDouble(formDBModel.getLatitude()), Double.parseDouble(formDBModel.getLongitude()));
//                    //               Log.e(TAG_GEO_JSON, "Size of Data: " + listArrayList.size());
//                }
//                catch (IOException e){
//                    dismissProgressBar();
//                    Log.e(TAG, "Error: "+e.getMessage());
//                }
//
//                // On PostExecute Method
//                runOnUiThread(() -> {
//                    try{
//                        for(int i=0; i<listArrayList.size(); i++){
//                            ArrayList<LatLng> latLngs = listArrayList.get(i).getLatLngs();
//                            PolygonOptions polygonOptions = new PolygonOptions()
//                                    .clickable(true)
//                                    .addAll(latLngs)
//                                    .strokeWidth(3)
//                                    .strokeColor(Color.YELLOW);
//                            Polygon polygon =  mMap.addPolygon(polygonOptions);
//                            polygon.setTag(listArrayList.get(i));
//                        }
//                        dismissProgressBar();
//                    }
//                    catch (Exception e){
//                        dismissProgressBar();
//                        Log.e(TAG, e.getMessage());
//                    }
//                });
//            }
//        });

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    InputStream inputStream = getAssets().open("shirol.json");
                    int size = inputStream.available();
                    byte[] buffer = new byte[size];
                    inputStream.read(buffer);
                    inputStream.close();
                    String json = new String(buffer,"UTF-8");
                    java.lang.reflect.Type Type = new TypeToken<GeoJson>() {}.getType();
                    GeoJson geoJson =  new Gson().fromJson(json, Type);
                    int n = geoJson.getFeatures().size();

                    ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> coordinates;

                    ArrayList<Feature> features = geoJson.getFeatures();
                    // Features Loops
                    for(int i=0; i<n; i++){
                        Geometry geometry = features.get(i).getGeometry();
                        String gisid = features.get(i).getProperties().getGISID();

                        coordinates = geometry.getCoordinates();
                        if(coordinates.size() > 0){
                            int coo_size = coordinates.get(0).get(0).size();
                            ArrayList<LatLng> latLngs = new ArrayList<>();
                            for(int j=0; j<coo_size; j++){
                                latLngs.add(new LatLng(coordinates.get(0).get(0).get(j).get(1), coordinates.get(0).get(0).get(j).get(0)));
                            }
                            listArrayList.add(new ShirolGeoModel(gisid,latLngs));
                        }
                    }
//            String json = new String(buffer,"UTF-8");
//            JSONArray jsonArray = new JSONArray(json);
//
//            ArrayList<ShirolModel> shirolModelArrayList = new ArrayList<>();
//            for(int i=0; i<jsonArray.length(); i++){
//                JSONObject object = jsonArray.getJSONObject(i);
//                String GISID = object.getString("GISID");

                    //                JSONArray coordinatesJsonArray = new JSONArray(object.getString("Coordinates"));
                    //ArrayList<ArrayList<LatLng>> latLngs = new ArrayList<>();

//                if(!Utility.isEmptyString(GISID)){
//                    java.lang.reflect.Type Type = new TypeToken<ShirolModel>() {}.getType();
//                    ShirolModel s =  new Gson().fromJson(object.toString(), Type);
//                    shirolModelArrayList.add(s);
//                    Log.e(TAG,"GISID: " + s.getGISID());
//                    if(s.getCoordinates().size() > 0){
//                        Log.e(TAG, "Coordinate Size: "+ s.getCoordinates().get(0).size());
//                    }
//                    else{
//                        Log.e(TAG, "Coordinate Size: ");
//                    }
//                }

//                java.lang.reflect.Type Type = new TypeToken<ShirolModel>() {}.getType();
//                ArrayList<ArrayList<LatLng>> latLngs =  new Gson().fromJson(coordinatesJsonArray.toString(), Type);
//
//                ShirolModel shirolModel = new ShirolModel(GISID,latLngs);

                    // Log.e(TAG,"Coordinate: " + shirolModel.getCoordinates().size());
                    //}

                    // LatLng latLng = new LatLng(Double.parseDouble(formDBModel.getLatitude()), Double.parseDouble(formDBModel.getLongitude()));
     //               Log.e(TAG_GEO_JSON, "Size of Data: " + listArrayList.size());
                }
                catch (IOException e){
                    dismissProgressBar();
                    Log.e(TAG, "Error: "+e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                for(int i=0; i<listArrayList.size(); i++){
                    ArrayList<LatLng> latLngs = listArrayList.get(i).getLatLngs();
                    PolygonOptions polygonOptions = new PolygonOptions()
                            .clickable(true)
                            .addAll(latLngs)
                            .strokeWidth(3)
                            .strokeColor(Color.YELLOW);
                    Polygon polygon =  mMap.addPolygon(polygonOptions);
                    polygon.setTag(listArrayList.get(i));
                }
                dismissProgressBar();
            }
        }.execute();



//        try{
//            InputStream inputStream = getAssets().open("shirol.json");
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            inputStream.read(buffer);
//            inputStream.close();
//            String json = new String(buffer,"UTF-8");
//
//            java.lang.reflect.Type Type = new TypeToken<GeoJson>() {}.getType();
//            GeoJson geoJson =  new Gson().fromJson(json, Type);
//            int n = geoJson.getFeatures().size();
//
//            ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> coordinates;
//
//            ArrayList<ShirolGeoModel> listArrayList = new ArrayList<>();
//
//            ArrayList<Feature> features = geoJson.getFeatures();
//            // Features Loops
//            for(int i=0; i<n; i++){
//                Geometry geometry = features.get(i).getGeometry();
//                String gisid = features.get(i).getProperties().getGISID();
//
//                coordinates = geometry.getCoordinates();
//                if(coordinates.size() > 0){
//                    int coo_size = coordinates.get(0).get(0).size();
//                    ArrayList<LatLng> latLngs = new ArrayList<>();
//                    for(int j=0; j<coo_size; j++){
//                        latLngs.add(new LatLng(coordinates.get(0).get(0).get(j).get(1), coordinates.get(0).get(0).get(j).get(0)));
//                    }
//
//                    listArrayList.add(new ShirolGeoModel(gisid,latLngs));
//                }
//            }
//
////            String json = new String(buffer,"UTF-8");
////            JSONArray jsonArray = new JSONArray(json);
////
////            ArrayList<ShirolModel> shirolModelArrayList = new ArrayList<>();
////            for(int i=0; i<jsonArray.length(); i++){
////                JSONObject object = jsonArray.getJSONObject(i);
////                String GISID = object.getString("GISID");
//
//                //                JSONArray coordinatesJsonArray = new JSONArray(object.getString("Coordinates"));
//                //ArrayList<ArrayList<LatLng>> latLngs = new ArrayList<>();
//
////                if(!Utility.isEmptyString(GISID)){
////                    java.lang.reflect.Type Type = new TypeToken<ShirolModel>() {}.getType();
////                    ShirolModel s =  new Gson().fromJson(object.toString(), Type);
////                    shirolModelArrayList.add(s);
////                    Log.e(TAG,"GISID: " + s.getGISID());
////                    if(s.getCoordinates().size() > 0){
////                        Log.e(TAG, "Coordinate Size: "+ s.getCoordinates().get(0).size());
////                    }
////                    else{
////                        Log.e(TAG, "Coordinate Size: ");
////                    }
////                }
//
////                java.lang.reflect.Type Type = new TypeToken<ShirolModel>() {}.getType();
////                ArrayList<ArrayList<LatLng>> latLngs =  new Gson().fromJson(coordinatesJsonArray.toString(), Type);
////
////                ShirolModel shirolModel = new ShirolModel(GISID,latLngs);
//
//               // Log.e(TAG,"Coordinate: " + shirolModel.getCoordinates().size());
//            //}
//
//                // LatLng latLng = new LatLng(Double.parseDouble(formDBModel.getLatitude()), Double.parseDouble(formDBModel.getLongitude()));
//
//
//            Log.e(TAG_GEO_JSON, "Size of Data: " + listArrayList.size());
//            for(int i=0; i<listArrayList.size(); i++){
//                    ArrayList<LatLng> latLngs = listArrayList.get(i).getLatLngs();
////                    StringBuilder sb = new StringBuilder();
////                    for(int j=0; j<latLngs.size(); j++){
////                        sb.append(latLngs.get(j).latitude +","+ latLngs.get(j).longitude+" $ ");
////                    }
////                    Log.e(TAG_GEO_JSON,"Latlon: " + sb.toString());
////                    Log.e(TAG_GEO_JSON,"Inside Size : "+ latLngs.size());
//////                    LatLng latLng = new LatLng(latLngs.get(0).latitude,latLngs.get(0).longitude);
////                    Utility.addMapFormMarker(mMap, latLng, BitmapDescriptorFactory.HUE_GREEN);
//
//                    PolygonOptions polygonOptions = new PolygonOptions()
//                            .clickable(true)
//                            .addAll(latLngs)
//                            .strokeWidth(3)
//                            .strokeColor(Color.YELLOW);
//                  Polygon polygon =  mMap.addPolygon(polygonOptions);
//                  polygon.setTag(listArrayList.get(i));
//
//            }
////
////            }
////
//
//        }
//        catch (IOException e){
//            Log.e(TAG, "Error: "+e.getMessage());
//        }

//        catch (IOException | JSONException e){
//            Log.e(TAG, "Error: "+e.getMessage());
//        }
    }

    private void showShirolGeoJson(){


        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.shirol, mActivity);

            layer.addLayerToMap();
         //   layer.getDefaultPolygonStyle().setFillColor(Color.BLUE);
            layer.getDefaultPolygonStyle().setStrokeColor(Color.YELLOW);

            layer.setOnFeatureClickListener(feature -> {
                // Log
                Log.i(TAG, "Feature GISID       : " + feature.getProperty("GISID"));
            //    Log.i(TAG, "Feature Geom Type: " + feature.getGeometry().getGeometryType());
            //    Log.i(TAG, "Feature LatLon   : " + feature.getGeometry().getGeometryObject());
                //layer.removeFeature((GeoJsonFeature) feature);
            });

        }
        catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage());

        }
    }

    private void showWMSLayer(){
        String link = "http://173.249.24.149:8080/geoserver/Test/wms?service=WMS&version=1.1.0&request=GetMap&layers=Test%3Ashirol&bbox=455452.85984213685%2C1849609.1985771365%2C457805.9966736671%2C1853767.5756562701&width=434&height=768&srs=EPSG%3A4326&styles=&format=application/openlayers";
        TileOverlay tileOverlay = addWMSLayer(link);

        // https://ahocevar.com/geoserver/wms?service=WMS&request=GetMap&version=1.1.1&format=image/png&width=256&height=256&transparent=true&bbox=10018754.173946,5009377.086973,15028131.260919,10018754.173946&srs=EPSG:900913&layers=topp:states
    }

 //------------------------------------------------- addWMSLayer ------------------------------------------------------------------------------------------------------------------------

    private TileOverlay addWMSLayer(String wmsLayerURl){
        TileProvider tileProvider = WMSTileProviderFactory.getWMSTileProvider(WMSProvider.getWMSLayer(wmsLayerURl));
        return mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {

        if(polygon.getTag() instanceof ShirolGeoModel){
            ShirolGeoModel shirolGeoModel = (ShirolGeoModel) polygon.getTag();
            Log.e(TAG, Utility.getStringValue(shirolGeoModel.getGISID()));
        }
    }

//------------------------------------------------- File Upload ------------------------------------------------------------------------------------------------------------------------

    private class FileUploadServer extends AsyncTask<Void, Integer, String> {
        HashMap<String,String> filePathData;
        String form_id;
        String unique_number;
        String type;

        public FileUploadServer(HashMap<String,String> filePathData, String form_id, String unique_number,String type) {
            this.filePathData = filePathData;
            this.form_id = form_id;
            this.unique_number = unique_number;
            this.type = type;

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
            try {

                if(filePathData != null){
                    if(!filePathData.isEmpty()){
                        // outer Loop
                        for(Map.Entry<String,String> entry: filePathData.entrySet()) {
                            String col_name = entry.getKey();
                            // File Path!
                            String[] path = entry.getValue().split(",");
                            Log.e(TAG, "path: "+entry.getValue());
                            for (String filepath : path) {
                                File sourceFile = new File(filepath);
                                String data = "";
                                JSONObject params = new JSONObject();
                                try {
                                    params.put("formID", form_id);
                                    params.put("unique_number", unique_number);
                                    params.put("column_name", col_name);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // Encrypt Data!
                                data = params.toString();

                                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(num -> publishProgress((int) ((num / (float) totalSize) * 100)));
                                entity.addPart(URL_Utility.PARAM_IMAGE_DATA, new FileBody(sourceFile));
                                entity.addPart("data", new StringBody(data));
                                totalSize = entity.getContentLength();
                                httppost.setEntity(entity);
                                HttpResponse response = httpclient.execute(httppost);
                                HttpEntity r_entity = response.getEntity();
                                int statusCode = response.getStatusLine().getStatusCode();

                                if (statusCode == 200) {
                                    responseString = EntityUtils.toString(r_entity);
                                    String res = (responseString);
                                    if (!res.equals("")) {
                                        try {
                                            JSONObject mLoginObj = new JSONObject(res);
                                            String status = mLoginObj.optString("status");
                                            Log.e(TAG, status);
                                            if (status.equalsIgnoreCase("Success")) {

                                            }
                                            else {
                                                dismissProgressBar();
                                                Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                                            }
                                        } catch (JSONException e) {
                                            dismissProgressBar();
                                            Log.e(TAG, e.getMessage());
                                            Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                                        }
                                    } else {
                                        dismissProgressBar();
                                        Log.e(TAG,"response null");
                                        Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                                    }
                                } else {
                                    dismissProgressBar();
                                    responseString = "Error occurred! Http Status Code: " + statusCode;
                                    Log.e(TAG, responseString);
                                    Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                    else{
                        Log.e(TAG,"filePathData is Empty");
                    }

                }
                else{
                    Log.e(TAG,"filePathData null");
                    Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                    dismissProgressBar();
                }

            } catch (IOException e) {
                dismissProgressBar();
                Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                Log.e(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                dismissProgressBar();
                Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String result) {
            String response = result;
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
                            Log.e(TAG,"Save File Successfully");
                            dismissProgressBar();
                            //SaveToSurveyFormTable();
                            Utility.showOKDialogBox(mActivity, "Save Successfully", dialog -> {
                                dialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            });
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


}