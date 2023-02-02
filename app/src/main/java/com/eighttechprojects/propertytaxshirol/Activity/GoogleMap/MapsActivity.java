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
import android.location.Location;
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
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.eighttechprojects.propertytaxshirol.databinding.ActivityMapsBinding;
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
import com.google.android.gms.tasks.Task;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, View.OnClickListener, WSResponseInterface {

    // TAG
    public static final String TAG = MapsActivity.class.getSimpleName();
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
            LinearLayout ll_20 = fDB.findViewById(R.id.ll_20);
            LinearLayout ll_23 = fDB.findViewById(R.id.ll_23);
            LinearLayout ll_24 = fDB.findViewById(R.id.ll_24);
            LinearLayout ll_27 = fDB.findViewById(R.id.ll_27);
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
            TextView tv_form_property_release_date       = fDB.findViewById(R.id.db_form_property_release_date);
            TextView tv_form_sp_build_permission         = fDB.findViewById(R.id.db_form_sp_build_permission);
            TextView tv_form_sp_build_completion_form    = fDB.findViewById(R.id.db_form_sp_build_completion_form);
            TextView tv_form_sp_metal_road               = fDB.findViewById(R.id.db_form_sp_metal_road);
            TextView tv_form_sp_is_toilet_available      = fDB.findViewById(R.id.db_form_sp_is_toilet_available);
            TextView tv_form_total_toilet                = fDB.findViewById(R.id.db_form_total_toilet);
            TextView tv_form_sp_toilet_type              = fDB.findViewById(R.id.db_form_sp_toilet_type);
            TextView tv_form_sp_is_streetlight_available = fDB.findViewById(R.id.db_form_sp_is_streetlight_available);
            TextView tv_form_sp_is_water_line_available  = fDB.findViewById(R.id.db_form_sp_is_water_line_available);
            TextView tv_form_sp_total_water_line         = fDB.findViewById(R.id.db_form_sp_total_water_line);
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
                ll_28.setVisibility(View.VISIBLE);
                // 27
                tv_form_sp_total_water_line.setText(Utility.getStringValue(bin.getTotal_water_line())); // depend upon spinner 26
                // 28
                tv_form_sp_water_use_type.setText(Utility.getStringValue(bin.getWater_use_type())); // depend upon spinner 26
            }
            else{
                ll_27.setVisibility(View.GONE);
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



}