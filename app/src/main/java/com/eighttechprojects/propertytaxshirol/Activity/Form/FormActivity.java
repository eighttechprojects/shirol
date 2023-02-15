package com.eighttechprojects.propertytaxshirol.Activity.Form;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.eighttechprojects.propertytaxshirol.Adapter.AdapterFormTable;
import com.eighttechprojects.propertytaxshirol.Database.DataBaseHelper;
import com.eighttechprojects.propertytaxshirol.Model.FormFields;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.FormTableModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.ImageFileUtils;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.eighttechprojects.propertytaxshirol.databinding.ActivityFormBinding;
import com.eighttechprojects.propertytaxshirol.volly.AndroidMultiPartEntity;
import com.eighttechprojects.propertytaxshirol.volly.BaseApplication;
import com.eighttechprojects.propertytaxshirol.volly.URL_Utility;
import com.eighttechprojects.propertytaxshirol.volly.WSResponseInterface;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FormActivity extends AppCompatActivity implements View.OnClickListener, WSResponseInterface {

    // TAG
    private static final String TAG = FormActivity.class.getSimpleName();
    // Binding
    private ActivityFormBinding binding;
    // Activity
    private Activity mActivity;
    // Database
    private DataBaseHelper dataBaseHelper;
    // progress Dialog
    private static ProgressDialog progressDialog;

    private final String selectYesOption = "होय";
    private final String selectNoOption  = "नाही";

    // Form Spinner Selected
    private String selectedPropertyUserType       = "";
    private String selectedPropertyType           = "";
    private String selectedBuildPermission        = "";
    private String selectedBuildCompletionForm    = "";
    private String selectedMetalRoad              = "";
    private String selectedIsToiletAvailable      = "";
    private String selectedToiletType             = "";
    private String selectedIsStreetlightAvailable = "";
    private String selectedIsWaterLineAvailable   = "";
    private String selectedTotalWaterLine         = "";
    private String selectedWaterUseType           = "";
    private String selectedSolarPanelAvailable    = "";
    private String selectedSolarPanelType         = "";
    private String selectedRaniWaterHarvesting    = "";

    // Form Table Model List
    ArrayList<FormTableModel> formTableModels = new ArrayList<>();
    // Adapter
    AdapterFormTable adapterFormTable;

    // Form Data
    FormModel formModel;
    private String latitude  = "";
    private String longitude = "";

    // Dialog box value
    String db_form_sp_building_type     = "";
    String db_form_sp_building_use_type = "";

    // Camera
    private File cameraDestFileTemp;
    ImageFileUtils imageFileUtils;
    StringBuilder sbCameraPathLocal;
    StringBuilder sbCameraImagePath = new StringBuilder();

    // File
    StringBuilder sbFilePath = new StringBuilder();

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
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Tool bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Activity
        mActivity = this;
        // ImageFileUtils
        imageFileUtils = new ImageFileUtils();
        // init Spinner
        initSpinner();
        // Adapter
        adapterFormTable  = new AdapterFormTable(mActivity,formTableModels);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity,DividerItemDecoration.VERTICAL);
        binding.rvFormTableView.addItemDecoration(dividerItemDecoration);
        Utility.setToVerticalRecycleView(mActivity,binding.rvFormTableView,adapterFormTable);
        // init Database
        initDatabase();
        // setOnClickListener
        setOnClickListener();
        // init Extra
        initExtra();
        // Update PreviewUI
        updatePreviewUI(false);
        // Photo Upload
        binding.imgCaptured.setOnClickListener(view -> {
            cameraPhotoUpload();
        });
    }

//------------------------------------------------------- initExtra ----------------------------------------------------------------------------------------------------------------------

    private void initExtra(){
        Intent intent = getIntent();
        // Latitude Contain or not
        if(intent.getExtras().containsKey(Utility.PASS_LAT)){
            latitude = intent.getStringExtra(Utility.PASS_LAT);
            Log.e(TAG,"Form Lat:  "+latitude);
        }
        // Longitude Contains or not
        if(intent.getExtras().containsKey(Utility.PASS_LONG)) {
            longitude = intent.getStringExtra(Utility.PASS_LONG);
            Log.e(TAG, "Form Long: "+longitude);
        }
    }

//------------------------------------------------------- initSpinner ----------------------------------------------------------------------------------------------------------------------

    private void initSpinner(){

        // 6 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterPropertyUserType = ArrayAdapter.createFromResource(mActivity, R.array.sp_property_user_type,android.R.layout.simple_spinner_item);
        adapterPropertyUserType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpPropertyUserType.setAdapter(adapterPropertyUserType);
        binding.formSpPropertyUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedPropertyUserType = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 17 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterPropertyType = ArrayAdapter.createFromResource(mActivity, R.array.sp_property_type,android.R.layout.simple_spinner_item);
        adapterPropertyType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpPropertyType.setAdapter(adapterPropertyType);
        binding.formSpPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedPropertyType = parent.getItemAtPosition(position).toString();

                if(selectedPropertyType.equalsIgnoreCase("इतर")){
                    binding.ll171.setVisibility(View.VISIBLE);
                }
                else{
                    binding.ll171.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 19 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterBuildPermission = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no,android.R.layout.simple_spinner_item);
        adapterBuildPermission.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpBuildPermission.setAdapter(adapterBuildPermission);
        binding.formSpBuildPermission.setSelection(1);
        binding.formSpBuildPermission.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedBuildPermission = parent.getItemAtPosition(position).toString();
                // select Yes Option
                if(selectedBuildPermission.equals(selectYesOption)){
                    binding.ll20.setVisibility(View.VISIBLE);
                }
                else{
                    binding.ll20.setVisibility(View.GONE);
                    selectedBuildCompletionForm = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 20 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterBuildCompletionForm = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no,android.R.layout.simple_spinner_item);
        adapterBuildCompletionForm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpBuildCompletionForm.setAdapter(adapterBuildCompletionForm);
        binding.formSpBuildCompletionForm.setSelection(1);
        binding.formSpBuildCompletionForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedBuildCompletionForm = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 21 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterMetalRoad = ArrayAdapter.createFromResource(mActivity, R.array.sp_metal_road,android.R.layout.simple_spinner_item);
        adapterMetalRoad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpMetalRoad.setAdapter(adapterMetalRoad);
        binding.formSpMetalRoad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedMetalRoad = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 22 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterIsToiletAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no,android.R.layout.simple_spinner_item);
        adapterIsToiletAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpIsToiletAvailable.setAdapter(adapterIsToiletAvailable);
        binding.formSpIsToiletAvailable.setSelection(1);
        binding.formSpIsToiletAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedIsToiletAvailable = parent.getItemAtPosition(position).toString();

                if(selectedIsToiletAvailable.equals(selectYesOption)){
                    binding.ll23.setVisibility(View.VISIBLE);
                    binding.ll24.setVisibility(View.VISIBLE);
                }
                else{
                    binding.ll23.setVisibility(View.GONE);
                    binding.ll24.setVisibility(View.GONE);
                    binding.formTotalToilet.setText("");
                    selectedToiletType = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 24 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterToiletType = ArrayAdapter.createFromResource(mActivity, R.array.sp_toilet_type,android.R.layout.simple_spinner_item);
        adapterToiletType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpToiletType.setAdapter(adapterToiletType);
        binding.formSpToiletType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedToiletType = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 25 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterIsStreetLightAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no,android.R.layout.simple_spinner_item);
        adapterIsStreetLightAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpIsStreetlightAvailable.setAdapter(adapterIsStreetLightAvailable);
        binding.formSpIsStreetlightAvailable.setSelection(1);
        binding.formSpIsStreetlightAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedIsStreetlightAvailable = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 26 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterIsWaterLineAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no,android.R.layout.simple_spinner_item);
        adapterIsWaterLineAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpIsWaterLineAvailable.setAdapter(adapterIsWaterLineAvailable);
        binding.formSpIsWaterLineAvailable.setSelection(1);
        binding.formSpIsWaterLineAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedIsWaterLineAvailable = parent.getItemAtPosition(position).toString();

                if(selectedIsWaterLineAvailable.equals(selectYesOption)){
                    binding.ll27.setVisibility(View.VISIBLE);
                    binding.ll271.setVisibility(View.VISIBLE);
                    binding.ll272.setVisibility(View.VISIBLE);
                    binding.ll28.setVisibility(View.VISIBLE);
                }
                else{
                    binding.ll27.setVisibility(View.GONE);
                    binding.ll271.setVisibility(View.GONE);
                    binding.ll272.setVisibility(View.GONE);
                    binding.ll28.setVisibility(View.GONE);
                    selectedTotalWaterLine = "";
                    selectedWaterUseType   = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


//        // 27 - Spinner -----------------------------------------------------------------------------
//        ArrayAdapter<CharSequence> adapterTotalWaterLine = ArrayAdapter.createFromResource(mActivity, R.array.sp_total_water_line,android.R.layout.simple_spinner_item);
//        adapterTotalWaterLine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.formSpTotalWaterLine.setAdapter(adapterTotalWaterLine);
//        binding.formSpTotalWaterLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
//                selectedTotalWaterLine = parent.getItemAtPosition(position).toString();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {}});

        // 28 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterWaterUseType = ArrayAdapter.createFromResource(mActivity, R.array.sp_water_use_type,android.R.layout.simple_spinner_item);
        adapterWaterUseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpWaterUseType.setAdapter(adapterWaterUseType);
        binding.formSpWaterUseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedWaterUseType = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});



        // 29 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterSolarPanelAvailable = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no,android.R.layout.simple_spinner_item);
        adapterSolarPanelAvailable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpSolarPanelAvailable.setAdapter(adapterSolarPanelAvailable);
        binding.formSpSolarPanelAvailable.setSelection(1);
        binding.formSpSolarPanelAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedSolarPanelAvailable = parent.getItemAtPosition(position).toString();
                if(selectedSolarPanelAvailable.equals(selectYesOption)){
                    binding.ll30.setVisibility(View.VISIBLE);
                }
                else{
                    binding.ll30.setVisibility(View.GONE);
                    selectedSolarPanelType = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 30 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterSolarPanelType = ArrayAdapter.createFromResource(mActivity, R.array.sp_solar_panel_type,android.R.layout.simple_spinner_item);
        adapterSolarPanelType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpSolarPanelType.setAdapter(adapterSolarPanelType);
        binding.formSpSolarPanelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedSolarPanelType = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // 31 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterRaniWaterHarvesting = ArrayAdapter.createFromResource(mActivity, R.array.sp_yes_no,android.R.layout.simple_spinner_item);
        adapterRaniWaterHarvesting.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpRainWaterHarvesting.setAdapter(adapterRaniWaterHarvesting);
        binding.formSpRainWaterHarvesting.setSelection(1);
        binding.formSpRainWaterHarvesting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedRaniWaterHarvesting = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


    }

//------------------------------------------------------- initDatabase ----------------------------------------------------------------------------------------------------------------------

    private void initDatabase() {
        dataBaseHelper = new DataBaseHelper(mActivity);
    }

//------------------------------------------------------- setOnClickListener ----------------------------------------------------------------------------------------------------------------------

    private void setOnClickListener(){
        binding.btSubmit.setOnClickListener(this);
        binding.btExit.setOnClickListener(this);
        binding.btAddFormTable.setOnClickListener(this);
        binding.btFileUpload.setOnClickListener(this);
    }

//------------------------------------------------------- Menu ----------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return false;
    }

//------------------------------------------------------- onClickView ----------------------------------------------------------------------------------------------------------------------

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btSubmit:
                onFormSubmit();
                break;

            case R.id.btExit:
                onFormExit();
                break;

            case R.id.btAddFormTable:
                addFormTable();
                break;

            case R.id.btFileUpload:
                fileUpload();
                break;

        }
    }

//------------------------------------------------------- File Upload ----------------------------------------------------------------------------------------------------------------------

    private void fileUpload(){
        try{
            Utility.openMultipleFilePicker(mActivity);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

//------------------------------------------------------- Camera Photo Upload ----------------------------------------------------------------------------------------------------------------------

    private void updatePreviewUI(boolean isUpdate) {
        binding.llMain.setVisibility(isUpdate ? View.GONE : View.VISIBLE);
        binding.llPreview.setVisibility(isUpdate ? View.VISIBLE : View.GONE);
    }

    private void cameraPhotoUpload(){
        // Camera
        try{
            Utility.openCamera(mActivity, imageFileUtils, path -> {
                if(path != null){
                    cameraDestFileTemp = new File(path);
                }
            });
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private StringBuilder getGeoTagData() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!Utility.isEmptyString(latitude) && !Utility.isEmptyString(longitude)) {
            stringBuilder.append("Latitude : " + "" + latitude);
            stringBuilder.append("\n");
            stringBuilder.append("Longitude : " + "" + longitude);
            stringBuilder.append("\n");
        }
        stringBuilder.append("Date: " + Utility.getRecordDate());
        return stringBuilder;
    }

//------------------------------------------------------- Add Form Table ----------------------------------------------------------------------------------------------------------------------

    private void addFormTable(){
        Dialog fDB = new Dialog(this);
        fDB.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fDB.setCancelable(false);
        fDB.setContentView(R.layout.dialogbox_form_table_item);
        fDB.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        // Exit Button
        Button btExit       = fDB.findViewById(R.id.dbExit);
        btExit.setOnClickListener(view -> fDB.dismiss());
        // Init Edit Text
        EditText sr_no             = fDB.findViewById(R.id.form_table_sr_no);
        EditText floor             = fDB.findViewById(R.id.form_table_floor);
        EditText length            = fDB.findViewById(R.id.form_table_length);
        EditText height            = fDB.findViewById(R.id.form_table_height);
        EditText area              = fDB.findViewById(R.id.form_table_area);
        EditText building_age      = fDB.findViewById(R.id.form_table_building_age);
        EditText annual_rent       = fDB.findViewById(R.id.form_table_annual_rent);
        EditText tag_no            = fDB.findViewById(R.id.form_table_tag_no);
        // Spinner
        Spinner building_type     = fDB.findViewById(R.id.form_sp_building_type);
        Spinner building_use_type = fDB.findViewById(R.id.form_sp_building_use_type);

        // Building Type Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterBuildingType = ArrayAdapter.createFromResource(mActivity, R.array.sp_building_type,android.R.layout.simple_spinner_item);
        adapterBuildingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_type.setAdapter(adapterBuildingType);
        building_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                db_form_sp_building_type = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // Building Use Type Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterBuildingUseType = ArrayAdapter.createFromResource(mActivity, R.array.sp_building_use_type,android.R.layout.simple_spinner_item);
        adapterBuildingUseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_use_type.setAdapter(adapterBuildingUseType);
        building_use_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                db_form_sp_building_use_type = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});

        // Add Button
        Button addFormTable = fDB.findViewById(R.id.dbAdd);
        addFormTable.setOnClickListener(view -> {
            if(adapterFormTable != null){
                formTableModels.add(new FormTableModel(
                        sr_no.getText().toString(),
                        floor.getText().toString(),
                        db_form_sp_building_type,
                        db_form_sp_building_use_type,
                        length.getText().toString(),
                        height.getText().toString(),
                        area.getText().toString(),
                        building_age.getText().toString(),
                        annual_rent.getText().toString(),
                        tag_no.getText().toString()
                ));
                adapterFormTable.notifyDataSetChanged();

                db_form_sp_building_type = "";
                db_form_sp_building_use_type = "";
            }
            fDB.dismiss();
        });
        fDB.show();
    }

//------------------------------------------------------- Submit ----------------------------------------------------------------------------------------------------------------------

    private void onFormSubmit(){
        // Geom Array not Null
        if(!Utility.isEmptyString(latitude) && !Utility.isEmptyString(longitude)){


            unique_number = String.valueOf(Utility.getToken());
            datetime      = Utility.getDateTime();
            // Form
            formModel = new FormModel();
            // 1 -----------------------------
            FormFields bin = new FormFields();
            // Default Fields
            bin.setForm_id("");
            bin.setUser_id(Utility.getSavedData(mActivity,Utility.LOGGED_USERID));
            bin.setLatitude(latitude);
            bin.setLongitude(longitude);
            bin.setCreated_on(Utility.getDateTime());
            // Form Fields
            bin.setOwner_name(Utility.getEditTextValue(binding.formOwnerName));
            bin.setOld_property_no(Utility.getEditTextValue(binding.formOldPropertyNo));
            bin.setNew_property_no(Utility.getEditTextValue(binding.formNewPropertyNo));
            bin.setProperty_name(Utility.getEditTextValue(binding.formPropertyName));
            bin.setProperty_address(Utility.getEditTextValue(binding.formPropertyAddress));
            bin.setProperty_user_type(Utility.getStringValue(selectedPropertyUserType));
            bin.setProperty_user(Utility.getEditTextValue(binding.formPropertyUser));
            bin.setResurvey_no(Utility.getEditTextValue(binding.formResurveyNo));
            bin.setGat_no(Utility.getEditTextValue(binding.formGatNo));
            bin.setZone(Utility.getEditTextValue(binding.formZone));
            bin.setWard(Utility.getEditTextValue(binding.formWard));
            bin.setMobile(Utility.getEditTextValue(binding.formMobile));
            bin.setEmail(Utility.getEditTextValue(binding.formEmail));
            bin.setAadhar_no(Utility.getEditTextValue(binding.formAadharNo));
            bin.setGrid_no(Utility.getEditTextValue(binding.formGridNo));
            bin.setGis_id(Utility.getEditTextValue(binding.formGisId));
            bin.setProperty_type(Utility.getStringValue(selectedPropertyType));

            bin.setNo_of_floors(Utility.getEditTextValue(binding.formNoOfFloors));

            bin.setProperty_release_date(Utility.getEditTextValue(binding.formPropertyReleaseDate));
            bin.setBuild_permission(Utility.getStringValue(selectedBuildPermission));
            bin.setBuild_completion_form(Utility.getStringValue(selectedBuildCompletionForm));
            bin.setMetal_road(Utility.getStringValue(selectedMetalRoad));
            bin.setIs_toilet_available(Utility.getStringValue(selectedIsToiletAvailable));
            bin.setTotal_toilet(Utility.getEditTextValue(binding.formTotalToilet));
            bin.setToilet_type(Utility.getStringValue(selectedToiletType));
            bin.setIs_streetlight_available(Utility.getStringValue(selectedIsStreetlightAvailable));
            bin.setIs_water_line_available(Utility.getStringValue(selectedIsWaterLineAvailable));
            bin.setTotal_water_line(Utility.getStringValue(selectedTotalWaterLine));
            bin.setWater_use_type(Utility.getStringValue(selectedWaterUseType));
            bin.setSolar_panel_available(Utility.getStringValue(selectedSolarPanelAvailable));
            bin.setSolar_panel_type(Utility.getStringValue(selectedSolarPanelType));
            bin.setRain_water_harvesting(Utility.getStringValue(selectedRaniWaterHarvesting));

            formModel.setFormFields(bin);
            formModel.setForm_detail(adapterFormTable.getFormTableModels());

            // Upload Form
            if(SystemPermission.isInternetConnected(mActivity)){
                SaveFormToServe(formModel);
            }
            else{
                SaveFormToDatabase(formModel);
            }
        }
    }

//------------------------------------------------------- SaveFormToServe/Local ----------------------------------------------------------------------------------------------------------------------

    private void SaveFormToServe(FormModel formModel){
        showProgressBar("Form Uploading...");
        Map<String, String> params = new HashMap<>();
        params.put("data", Utility.convertFormModelToString(formModel));
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM, URL_Utility.WS_FORM, params, false, false);
    }

    private void SaveFormToDatabase(FormModel formModel){
        String token = String.valueOf(Utility.getToken());
        dataBaseHelper.insertMapForm(
                Utility.getSavedData(mActivity,Utility.LOGGED_USERID),
                latitude,
                longitude,
                Utility.convertFormModelToString(formModel),
                "t",
                token
        );

        dataBaseHelper.insertMapFormLocal(
                Utility.getSavedData(mActivity,Utility.LOGGED_USERID),
                latitude,
                longitude,
                Utility.convertFormModelToString(formModel),
                token
        );

        Log.e(TAG,"Form Save To Local Database");
        Utility.showOKDialogBox(mActivity, URL_Utility.SAVE_SUCCESSFULLY, okDialogBox -> {
            okDialogBox.dismiss();
            setResult(RESULT_OK);
            finish();
        });
    }

//------------------------------------------------------- onSuccessResponse ----------------------------------------------------------------------------------------------------------------------

    @Override
    public void onSuccessResponse(URL_Utility.ResponseCode responseCode, String response) {
        Log.e(TAG,"Response: " + response);
        if(responseCode == URL_Utility.ResponseCode.WS_FORM){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "Form Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                        Log.e(TAG,"Form Upload to Server SuccessFully");

                        // Changes
                        boolean isFile   = false;
                        boolean isCamera = false;

                        // File
                        if(sbFilePath != null && !Utility.isEmptyString(sbFilePath.toString())){
                            Log.e(TAG,"Form Contain File");
                            isFile = true;
                        }

                        // Camera
                        if(sbCameraImagePath != null && !Utility.isEmptyString(sbCameraImagePath.toString())){
                            Log.e(TAG, "Form Contain Camera Image");
                            isCamera = true;
                        }

                        // When User Select Any File or Camera
                        if(isFile || isCamera){

                            if(isFile){
                                new FormActivity.FileUploadServer(sbFilePath,unique_number,TYPE_FILE).execute();
                            }
                            else{
                                isFileUpload = true;
                            }

                            if(isCamera){
                                new FormActivity.FileUploadServer(sbCameraImagePath,unique_number, TYPE_CAMERA).execute();
                            }
                            else{
                                isCameraUpload = true;
                            }
                        }
                        // When User Select Only Form
                        else{
                            Log.e(TAG,"User Upload only Form not Camera File or File");
                            dismissProgressBar();
                            if(formModel != null){
                                dataBaseHelper.insertMapForm(
                                        Utility.getSavedData(mActivity,Utility.LOGGED_USERID),
                                        latitude,
                                        longitude,
                                        Utility.convertFormModelToString(formModel),
                                        "f",
                                        String.valueOf(Utility.getToken())
                                );
                            }
                            Utility.showOKDialogBox(mActivity, URL_Utility.SAVE_SUCCESSFULLY, okDialogBox -> {
                                okDialogBox.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            });
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
                Log.e(TAG, "Form Response Empty");
                dismissProgressBar();
                Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
            }
        }

    }

//------------------------------------------------------- onErrorResponse ----------------------------------------------------------------------------------------------------------------------

    @Override
    public void onErrorResponse(URL_Utility.ResponseCode responseCode, VolleyError error) {
        dismissProgressBar();
        Log.e(TAG,"Response Code: "+ responseCode);
        Log.e(TAG,"Error: "+ error.getMessage());
        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
    }

//------------------------------------------------------- Exit ----------------------------------------------------------------------------------------------------------------------

    private void onFormExit(){
        setResult(RESULT_CANCELED);
        finish();
    }

//------------------------------------------------------- progressBar ----------------------------------------------------------------------------------------------------------------------

    private void showProgressBar(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }

    private void dismissProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

//------------------------------------------------------- onBackPressed ----------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        finish();
    }

//------------------------------------------------------- onActivity Result ----------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Camera Photo/Image Request
        if(requestCode == Utility.REQUEST_TAKE_PHOTO){
            if(resultCode == Activity.RESULT_OK){
                Croperino.runCropImage(cameraDestFileTemp, mActivity, true, 1, 1, R.color.colorAccent, R.color.colorPrimary);
//                try{
//                    binding.txtGeoTag.setText(getGeoTagData());
//                    Bitmap bitmapPreview = ImageFileUtils.handleSamplingAndRotationBitmap(mActivity, Uri.fromFile(cameraDestFileTemp));
//                    File destFileTemp2 = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity) );
//                    ImageFileUtils.saveBitmapToFile(bitmapPreview, destFileTemp2);
//                    binding.imgPreview.setImageBitmap(bitmapPreview);
//                    updatePreviewUI(true);
//
//                    new Handler().postDelayed(() -> {
//                        File destFile = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity));
//                        if (ImageFileUtils.takeScreenshot(binding.llPreview, destFile)) {
//                            Log.e("Picture", "screenshot capture success");
//                        } else {
//                            destFile = destFileTemp2;
//                            Log.e("Picture", "screenshot capture failed");
//                        }
//                        sbCameraImagePath.append(destFile.getPath());
//                        updatePreviewUI(false);
//                        Log.e(TAG,"Camera Image Path: " + destFile.getAbsolutePath());
//                        // Set Image
//                        Bitmap bitmap =  (ImageFileUtils.getBitmapFromFilePath(destFile.getAbsolutePath()));
//                        Glide.with(mActivity).load(bitmap).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(binding.imgCaptured);
//                    }, 400);
//                }
//                catch (Exception e){
//                    Glide.with(mActivity).load(R.drawable.ic_no_image).into(binding.imgCaptured);
//                    Log.e(TAG, e.getMessage());
//                }
            }
        }
        // Crop Camera Photo/Image Request
        else if(requestCode == CroperinoConfig.REQUEST_CROP_PHOTO){
            //try {
                try{
                    File destFile1 = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity));
                    imageFileUtils.copyFile(cameraDestFileTemp, destFile1);

                    binding.txtGeoTag.setText(getGeoTagData());
                    Bitmap bitmapPreview = ImageFileUtils.handleSamplingAndRotationBitmap(mActivity, Uri.fromFile(destFile1));
                    File destFileTemp2 = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity) );
                    ImageFileUtils.saveBitmapToFile(bitmapPreview, destFileTemp2);
                    binding.imgPreview.setImageBitmap(bitmapPreview);
                    updatePreviewUI(true);

                    new Handler().postDelayed(() -> {
                        File destFile = imageFileUtils.getDestinationFileImageInput(imageFileUtils.getRootDirFile(mActivity));
                        if (ImageFileUtils.takeScreenshot(binding.llPreview, destFile)) {
                            Log.e("Picture", "screenshot capture success");
                        } else {
                            destFile = destFileTemp2;
                            Log.e("Picture", "screenshot capture failed");
                        }
                        sbCameraImagePath.append(destFile.getPath());
                        updatePreviewUI(false);
                        Log.e(TAG,"Camera Image Path: " + destFile.getAbsolutePath());
                        // Set Image
                        Bitmap bitmap =  (ImageFileUtils.getBitmapFromFilePath(destFile.getAbsolutePath()));
                        Glide.with(mActivity).load(bitmap).placeholder(R.drawable.loading_bar).error(R.drawable.ic_no_image).into(binding.imgCaptured);
                    }, 400);
                }
                catch (Exception e){
                    Glide.with(mActivity).load(R.drawable.ic_no_image).into(binding.imgCaptured);
                    Log.e(TAG, e.getMessage());
                }
                //listFormDetailsData.get(positionCaptureImagePojo).setValue(destFile.getAbsolutePath());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        // File Request
        else if(requestCode == Utility.PICK_FILE_RESULT_CODE){
            if(resultCode == Activity.RESULT_OK){
                assert data != null;
                Uri uri = data.getData();
                // Multiple Files Selected
                if(null != data.getClipData()){
                    Log.e(TAG,"Multiple File Selected");
                    ArrayList<Uri> multipleFileList = new ArrayList<>();
                    int n = data.getClipData().getItemCount(); // size
                    for(int i=0; i<n; i++){
                        Uri multipleUri = data.getClipData().getItemAt(i).getUri();
                        multipleFileList.add(multipleUri);
                    }
                    sbFilePath = new StringBuilder();
                    for(int i=0; i<multipleFileList.size(); i++){
                        File sourceFile = new File(imageFileUtils.getPathUri(mActivity, multipleFileList.get(i)));
                        File destFile = imageFileUtils.getDestinationFileDoc(imageFileUtils.getRootDirFileDoc(mActivity), ImageFileUtils.getExtFromUri(this, multipleFileList.get(i)));
                        try{
                            imageFileUtils.copyFile(sourceFile, destFile);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        if(destFile != null){
                            sbFilePath.append(destFile.getPath());
                            if(i < multipleFileList.size() - 1){
                                sbFilePath.append(",");
                            }
                        }
                    }

                }
                // Single File Selected
                else{
                    Log.e(TAG,"Single File Selected");
                    File sourceFile = new File(imageFileUtils.getPathUri(mActivity, uri));
                    File destFile = imageFileUtils.getDestinationFileDoc(imageFileUtils.getRootDirFileDoc(mActivity), ImageFileUtils.getExtFromUri(this, uri));
                    try{
                        imageFileUtils.copyFile(sourceFile, destFile);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if(destFile != null){
                        sbFilePath = new StringBuilder();
                        sbFilePath.append(destFile.getPath());
                    }
                }

                Log.e(TAG, "File Path: "+sbFilePath.toString());
            }

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
