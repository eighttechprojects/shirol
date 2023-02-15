package com.eighttechprojects.propertytaxshirol.Activity.GoogleMap;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.eighttechprojects.propertytaxshirol.Activity.Form.ResurveyFormActivity;
import com.eighttechprojects.propertytaxshirol.Activity.SplashActivity;
import com.eighttechprojects.propertytaxshirol.Database.DataBaseHelper;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.FormTableModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.eighttechprojects.propertytaxshirol.databinding.ActivityResurveyBinding;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResurveyActivity extends AppCompatActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, WSResponseInterface {

    // TAG
    public static final String TAG = ResurveyActivity.class.getSimpleName();
    // Map
    GoogleMap mMap;
    // Zoom Map
    GoogleMap zoomMap;
    SupportMapFragment zoomMapFragment;
    // Binding
    ActivityResurveyBinding binding;
    // DataBase
    private DataBaseHelper dataBaseHelper;
    // Base Application
    BaseApplication baseApplication;
    // Activity
    Activity mActivity;
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
    private static final int FORM_REQUEST_CODE = 1001;

    // Logout ----------------------------------------------------------------
    // ArrayList
    private ArrayList<FormDBModel> formDBModelList = new ArrayList<>();
    private ArrayList<FormDBModel> formSyncList = new ArrayList<>();

    private FormDBModel formDBModel;

//------------------------------------------------------- onCreate ---------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Binding
        binding = ActivityResurveyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Tool bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Activity
        mActivity = this;
        // init Database
        initDatabase();
        // base Application
        baseApplication = (BaseApplication) getApplication();
        // FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        // Zoom Map
        zoomMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.zoom_map);
        assert zoomMapFragment != null;
        zoomMapFragment.getMapAsync(onZoomMapReadyCallback());
        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.reSurveyMap);
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

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Set Map
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Location
        mMap.setMyLocationEnabled(true);
        // Map Click Listener
        mMap.setOnMapClickListener(this);
        // Marker Click Listener
        mMap.setOnMarkerClickListener(this);
        // Marker Drag Listener
        mMap.setOnMarkerDragListener(this);
        // show All Resurvey Form
        showAllResurveyForm();
        // Database Contains Some Data or not
        if(isFormDataNotSync()){
            Utility.showSyncYourDataAlert(this);
        }
    }

//------------------------------------------------------- Menu ----------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.resurvey_map_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                Utility.reDirectTo(this,MapsActivity.class);
                finish();
                break;

            case R.id.menuSync:
                if(SystemPermission.isInternetConnected(mActivity)){
                    Sync();

                }
                else{
                    Utility.showOKDialogBox(mActivity, "Sync Alert", "Need Internet Connection To Sync Data", DialogInterface::dismiss);
                }
                break;

            case R.id.menuShowAllResurveyForm:
                if(SystemPermission.isInternetConnected(mActivity)){
                    showAllServerResurveyForm();
                }
                else{
                    Utility.showOKDialogBox(mActivity, "Connection Error", "Need Internet Connection to Logout", DialogInterface::dismiss);
                }
                break;

            case R.id.menuLogout:
                if(SystemPermission.isInternetConnected(mActivity)){
                    Logout();
                }
                else {
                    Utility.showOKDialogBox(mActivity, "Connection Error", "Need Internet Connection to Logout", DialogInterface::dismiss);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

//------------------------------------------------------- Menu Show All Resurvey Form  ------------------------------------------------------------------------------------------------------------------------------------------------

    private void showAllServerResurveyForm(){
        Map<String, String> params = new HashMap<>();
        String data = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(URL_Utility.PARAM_EMAIL_ID, Utility.getSavedData(mActivity,Utility.PROFILE_EMAIL_ID));
            jsonObject.put(URL_Utility.PARAM_PASSWORD, Utility.getSavedData(mActivity,Utility.PROFILE_PASSWORD));
            jsonObject.put(URL_Utility.APP_VERSION, URL_Utility.APP_VERSION);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            data =  jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("data",data);
        URL_Utility.ResponseCode responseCode = URL_Utility.ResponseCode.WS_RESURVEY_FORM;
        BaseApplication.getInstance().makeHttpPostRequest(mActivity, responseCode, URL_Utility.WS_RESURVEY_FORM, params, false, false);

    }

//------------------------------------------------------- Sync ------------------------------------------------------------------------------------------------------------------------------------------------
//
//    private void Sync(){
//        ArrayList<FormDBModel> formDBModels = dataBaseHelper.getMapFormLocalDataList();
//        if(formDBModels.size() == 0){
//            Utility.showOKDialogBox(this, "Sync", "Data Already Sync", DialogInterface::dismiss);
//        }
//        else
//        {
//            if(SystemPermission.isInternetConnected(mActivity)){
//                baseApplication.startSyncService();
//            }
//
//        }
//    }

//------------------------------------------------------- Form ------------------------------------------------------------------------------------------------------------------------------------------------

    private void showAllResurveyForm(){
        try{
            ArrayList<FormDBModel> list = dataBaseHelper.getResurveyMapFormDataList();
            if(list.size() > 0){
                Log.e(TAG, "Total Resurvey Form: " + list.size());
                for(int i=0; i < list.size(); i++){
                    FormDBModel formDBModel = list.get(i);
                    if(!Utility.isEmptyString(formDBModel.getLatitude()) && !Utility.isEmptyString(formDBModel.getLongitude())){
                        LatLng latLng = new LatLng(Double.parseDouble(formDBModel.getLatitude()), Double.parseDouble(formDBModel.getLongitude()));
                        Marker marker = Utility.addResurveyMapFormMarker(mMap, latLng);
                        marker.setDraggable(false);
                        marker.setTag(formDBModel);
                    }
                    else{
                        Log.e(TAG,"Lat Lon Data null Found in Some Forms");
                    }
                }
            }
            else{
                Log.e(TAG, "Not Resurvey Form Found");
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

//------------------------------------------------------- onMapClick ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
    }

//------------------------------------------------------- onMarkerClick ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        if(marker.getTag() instanceof FormDBModel){
            Utility.showYesNoDialogBox(mActivity, "Move","Open Form","Info", "you want to Open Form or Change Marker/Form Position?", okDialogBox -> {
                Toast.makeText(mActivity, "Move your Marker", Toast.LENGTH_SHORT).show();
                LatLng latLng = marker.getPosition();
                marker.setDraggable(true);
                marker.setPosition(latLng);
                okDialogBox.dismiss();
            }, noDialogBox -> {
                marker.setDraggable(false);
                FormDBModel formDBModel = (FormDBModel) marker.getTag();
                reDirectToResurveyForm(marker,formDBModel.getId());
                noDialogBox.dismiss();
            });
        }
        return false;
    }

    private void reDirectToResurveyForm(Marker marker, String id){
        Intent intent = new Intent(mActivity, ResurveyFormActivity.class);
        intent.putExtra(Utility.PASS_LAT,String.valueOf(marker.getPosition().latitude));
        intent.putExtra(Utility.PASS_LONG,String.valueOf(marker.getPosition().longitude));
        intent.putExtra(Utility.PASS_ID,id);
        startActivityForResult(intent,FORM_REQUEST_CODE);
    }

//------------------------------------------------------- onActivityResult ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Form Successfully Submit
        if(requestCode == FORM_REQUEST_CODE && resultCode == RESULT_OK){
            Log.e(TAG, "Resurvey Form Submit Successfully");
            mMap.clear();
            showAllResurveyForm();
        }
    }

//------------------------------------------------------- onMarkerDragStart ------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        if(marker.isDraggable()){
            onZoomCamera(marker);
        }
    }

//------------------------------------------------------- onMarkerDrag ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        if(marker.isDraggable()){
            onZoomCamera(marker);
        }
    }

//------------------------------------------------------- onMarkerDragEnd ------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        if(marker.isDraggable()){
            offZoomCamera();
        }
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
        // Resurvey Form
        if (responseCode == URL_Utility.ResponseCode.WS_RESURVEY_FORM) {
            // Decrypt Response
            Log.e(TAG,"Resurvey Response: "+response);
            if(!response.equals("")) {
                try {
                    JSONObject mLoginObj = new JSONObject(response);
                    String status = mLoginObj.optString(URL_Utility.STATUS);
                    Log.e(TAG,"Resurvey Status: "+status);
                    if (status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                        // Form Data
                        JSONArray formDataArray = new JSONArray(mLoginObj.getString("form_data"));
                        if(formDataArray.length() > 0){
                            for(int i=0; i<formDataArray.length(); i++){
                                FormModel formModel = new FormModel();
                                // Form ------------------------
                                JSONObject formObject = formDataArray.getJSONObject(i).getJSONObject("form");
                                String lat = formObject.optString("latitude");
                                String lon = formObject.optString("longitude");
                                formModel.setFormFields(Utility.convertStringToFormFields(formObject.toString()));
                                // Form Details -----------
                                JSONArray detailsArrays = formDataArray.getJSONObject(i).getJSONArray("detais");
                                if(detailsArrays.length() > 0){
                                    ArrayList<FormTableModel> list = new ArrayList<>();
                                    for(int j =0 ; j<detailsArrays.length(); j++){
                                        list.add(Utility.convertStringToFormTable(detailsArrays.get(j).toString()));
                                    }
                                    formModel.setForm_detail(list);
                                }
                                else{
                                    ArrayList<FormTableModel> list = new ArrayList<>();
                                    formModel.setForm_detail(list);
                                }
                                // Clear Resurvey DataBase
                                dataBaseHelper.clearResurveyDatabaseTable();
                                // Resurvey Form
                                dataBaseHelper.insertResurveyMapForm(Utility.getSavedData(mActivity,Utility.LOGGED_USERID),lat,lon,Utility.convertFormModelToString(formModel),"","");
                            }
                            // Clear Resurvey Map
                            mMap.clear();
                            // Show All Data
                            showAllResurveyForm();
                        }
                        dismissProgressBar();
                    }
                    else {
                        dismissProgressBar();
                        Utility.showToast(mActivity, Utility.ERROR_MESSAGE);
                    }
                } catch (JSONException e) {
                    Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                    Log.e(TAG, e.getMessage());
                    dismissProgressBar();
                }
            }
            else{
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
                Log.e(TAG, "Resurvey Response Empty");
                dismissProgressBar();
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
            Utility.showOKDialogBox(this, "Sync", "Data Sync Successfully", DialogInterface::dismiss);
        }
    }

    private void SyncFormDataToServer(FormDBModel formDBModel){
        Log.e(TAG, "Upload to Server.........!");
        Map<String, String> params = new HashMap<>();
        params.put("data", formDBModel.getFormData());
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM_SYNC, URL_Utility.WS_FORM_SYNC, params, false, false);
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

//------------------------------------------------------- onBackPressed ----------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        Utility.reDirectTo(this,MapsActivity.class);
        finish();
    }

}