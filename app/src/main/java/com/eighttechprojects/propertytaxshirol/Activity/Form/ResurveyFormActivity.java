package com.eighttechprojects.propertytaxshirol.Activity.Form;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.VolleyError;
import com.bumptech.glide.util.Util;
import com.eighttechprojects.propertytaxshirol.Adapter.AdapterFormTable;
import com.eighttechprojects.propertytaxshirol.Database.DataBaseHelper;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.Model.FormFields;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.FormTableModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.eighttechprojects.propertytaxshirol.databinding.ActivityResurveyFormBinding;
import com.eighttechprojects.propertytaxshirol.volly.BaseApplication;
import com.eighttechprojects.propertytaxshirol.volly.URL_Utility;
import com.eighttechprojects.propertytaxshirol.volly.WSResponseInterface;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResurveyFormActivity extends AppCompatActivity implements View.OnClickListener,WSResponseInterface {

    // TAG
    private static final String TAG = ResurveyFormActivity.class.getSimpleName();
    // Binding
    private ActivityResurveyFormBinding binding;
    // Activity
    private Activity mActivity;
    // Database
    private DataBaseHelper dataBaseHelper;
    // progress Dialog
    private static ProgressDialog progressDialog;
    final String selectYesOption = "होय";
    final String selectNoOption  = "नाही";
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
    // Form model
    FormFields bin;
    FormModel formModel;
    private String formID = "";
    private String latitude  = "";
    private String longitude = "";
    private String resurveyID = "";
    // Dialog box value
    String db_form_sp_building_type     = "";
    String db_form_sp_building_use_type = "";

    private String polygonID = "";

//------------------------------------------------------- onCreate ----------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Binding
        binding = ActivityResurveyFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Tool bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Activity
        mActivity = this;
        // Adapter
        adapterFormTable = new AdapterFormTable(mActivity, formTableModels);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        binding.rvFormTableView.addItemDecoration(dividerItemDecoration);
        Utility.setToVerticalRecycleView(mActivity, binding.rvFormTableView, adapterFormTable);
        // init Database
        initDatabase();
        // setOnClickListener
        setOnClickListener();
        // init Extra
        initExtra();
        // init Form
        initForm();
        // init Spinner
        initSpinner();
        // init Form Table Recycle View
        initFormTable();
    }

//------------------------------------------------------- initExtra ----------------------------------------------------------------------------------------------------------------------

    private void initExtra(){
        Intent intent = getIntent();
        // Latitude Contain or not
        if(intent.getExtras().containsKey(Utility.PASS_LAT)){
            latitude = intent.getStringExtra(Utility.PASS_LAT);
            Log.e(TAG,"Resurvey Form DB Lat:  "+latitude);
        }
        // Longitude Contains or not
        if(intent.getExtras().containsKey(Utility.PASS_LONG)) {
            longitude = intent.getStringExtra(Utility.PASS_LONG);
            Log.e(TAG, "Resurvey Form DB Long: "+longitude);
        }
        // Polygon ID Contains or not
        if(intent.getExtras().containsKey(Utility.PASS_POLYGON_ID)) {
            polygonID = intent.getStringExtra(Utility.PASS_LONG);
            Log.e(TAG, "Resurvey Form DB Polygon ID: "+polygonID);
        }

        // Id
        if(intent.getExtras().containsKey(Utility.PASS_ID)) {
            resurveyID = intent.getStringExtra(Utility.PASS_ID);
            Log.e(TAG, "Resurvey Form DB ID: " + resurveyID);
            FormDBModel formDBModel = dataBaseHelper.getResurveyMapFormByID(resurveyID);
            formModel = Utility.convertStringToFormModel(formDBModel.getFormData());
            bin = formModel.getFormFields();
            formID = bin.getForm_id();
            Log.e(TAG,"Resurvey Form ID: "+formID);
        }
    }

//------------------------------------------------------- initFormValue/Data ----------------------------------------------------------------------------------------------------------------------

    private void initForm() {
        if(bin != null){
            binding.formOwnerName.setText(Utility.getStringValue(bin.getOwner_name()));
            binding.formOldPropertyNo.setText(Utility.getStringValue(bin.getOld_property_no()));
            binding.formNewPropertyNo.setText(Utility.getStringValue(bin.getNew_property_no()));
            binding.formPropertyName.setText(Utility.getStringValue(bin.getProperty_name()));
            binding.formPropertyAddress.setText(Utility.getStringValue(bin.getProperty_address()));
            binding.formPropertyUser.setText(Utility.getStringValue(bin.getProperty_user()));
            binding.formResurveyNo.setText(Utility.getStringValue(bin.getResurvey_no()));
            binding.formGatNo.setText(Utility.getStringValue(bin.getGat_no()));
            binding.formZone.setText(Utility.getStringValue(bin.getZone()));
            binding.formWard.setText(Utility.getStringValue(bin.getWard()));
            binding.formMobile.setText(Utility.getStringValue(bin.getMobile()));
            binding.formEmail.setText(Utility.getStringValue(bin.getEmail()));
            binding.formAadharNo.setText(Utility.getStringValue(bin.getAadhar_no()));
            binding.formGridNo.setText(Utility.getStringValue(bin.getGrid_no()));
            binding.formGisId.setText(Utility.getStringValue(bin.getGis_id()));
            binding.formPropertyReleaseDate.setText(Utility.getStringValue(bin.getProperty_release_date()));
            binding.formTotalToilet.setText(Utility.getStringValue(bin.getTotal_toilet()));
            // 17
            binding.formNoOfFloors.setText(Utility.getStringValue(bin.getNo_of_floor()));
            // 32
            binding.formPlotArea.setText(Utility.getStringValue(bin.getPlot_area()));
            //33
            binding.formPropertyArea.setText(Utility.getStringValue(bin.getProperty_area()));
            // 34
            binding.formTotalArea.setText(Utility.getStringValue(bin.getTotal_area()));
            // 27.1
            binding.formTotalWaterLine1.setText(Utility.getStringValue(bin.getTotal_water_line1()));
            // 27.2
            binding.formTotalWaterLine2.setText(Utility.getStringValue(bin.getTotal_water_line2()));
        }
    }

//------------------------------------------------------- initSpinner ----------------------------------------------------------------------------------------------------------------------

    private void initSpinner(){

        // 6 - Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterPropertyUserType = ArrayAdapter.createFromResource(mActivity, R.array.sp_property_user_type,android.R.layout.simple_spinner_item);
        adapterPropertyUserType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formSpPropertyUserType.setAdapter(adapterPropertyUserType);
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getProperty_user_type())) {
                switch (bin.getProperty_user_type()) {
                    case "स्वत":
                        binding.formSpPropertyUserType.setSelection(0);
                        break;

                    case "भाडेकरू":
                        binding.formSpPropertyUserType.setSelection(1);
                        break;

                    case "भोगवटदर":
                        binding.formSpPropertyUserType.setSelection(2);
                        break;
                }
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getProperty_type())) {
                switch (bin.getProperty_type()) {
                    case "G.F":
                        binding.formSpPropertyUserType.setSelection(0);
                        break;

                    case "G.F + 1":
                        binding.formSpPropertyUserType.setSelection(1);
                        break;

                    case "G.F + 2":
                        binding.formSpPropertyUserType.setSelection(2);
                        break;

                    case "इतर":
                        binding.formSpPropertyUserType.setSelection(3);
                        break;
                }
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getBuild_permission())) {
                switch (bin.getBuild_permission()) {
                    case selectYesOption:
                        binding.formSpBuildPermission.setSelection(0);
                        break;

                    case selectNoOption:
                        binding.formSpBuildPermission.setSelection(1);
                        break;
                }
            } else {
                binding.formSpBuildPermission.setSelection(1);
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getBuild_completion_form())) {
                switch (bin.getBuild_completion_form()) {
                    case selectYesOption:
                        binding.formSpBuildCompletionForm.setSelection(0);
                        break;

                    case selectNoOption:
                        binding.formSpBuildCompletionForm.setSelection(1);
                        break;
                }
            } else {
                binding.formSpBuildCompletionForm.setSelection(1);
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getMetal_road())) {
                switch (bin.getMetal_road()) {
                    case "कच्चा रस्ता":
                        binding.formSpMetalRoad.setSelection(0);
                        break;

                    case "६ मी. पेक्षा कमी पक्का रस्ता":
                        binding.formSpMetalRoad.setSelection(1);
                        break;

                    case "६ मी.ते १२ मी. पक्का रस्ता":
                        binding.formSpMetalRoad.setSelection(2);
                        break;

                    case "१२ मी.ते ३० मी. पक्का रस्ता":
                        binding.formSpMetalRoad.setSelection(3);
                        break;
                }
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getIs_toilet_available())) {
                switch (bin.getIs_toilet_available()) {
                    case selectYesOption:
                        binding.formSpIsToiletAvailable.setSelection(0);
                        break;

                    case selectNoOption:
                        binding.formSpIsToiletAvailable.setSelection(1);
                        break;
                }
            } else {
                binding.formSpIsToiletAvailable.setSelection(1);
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getToilet_type())) {
                switch (bin.getToilet_type()) {
                    case "सेप्टिक":
                        binding.formSpToiletType.setSelection(0);
                        break;

                    case "लीचिपट":
                        binding.formSpToiletType.setSelection(1);
                        break;
                }
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getIs_streetlight_available())) {
                switch (bin.getIs_streetlight_available()) {
                    case selectYesOption:
                        binding.formSpIsStreetlightAvailable.setSelection(0);
                        break;

                    case selectNoOption:
                        binding.formSpIsStreetlightAvailable.setSelection(1);
                        break;
                }
            } else {
                binding.formSpIsStreetlightAvailable.setSelection(1);
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getIs_water_line_available())) {
                switch (bin.getIs_water_line_available()) {
                    case selectYesOption:
                        binding.formSpIsWaterLineAvailable.setSelection(0);
                        break;

                    case selectNoOption:
                        binding.formSpIsWaterLineAvailable.setSelection(1);
                        break;
                }
            } else {
                binding.formSpIsWaterLineAvailable.setSelection(1);
            }
        }
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
                    selectedWaterUseType   = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


//        // 27 - Spinner -----------------------------------------------------------------------------
//        ArrayAdapter<CharSequence> adapterTotalWaterLine = ArrayAdapter.createFromResource(mActivity, R.array.sp_total_water_line,android.R.layout.simple_spinner_item);
//        adapterTotalWaterLine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.formSpTotalWaterLine.setAdapter(adapterTotalWaterLine);
//        // set Data
//        if(bin != null) {
//            if (!Utility.isEmptyString(bin.getTotal_water_line())) {
//                switch (bin.getTotal_water_line()) {
//                    case "१/२”":
//                        binding.formSpTotalWaterLine.setSelection(0);
//                        break;
//
////                    case "२”":
////                        binding.formSpTotalWaterLine.setSelection(1);
////                        break;
//
//                    case "१”":
//                        binding.formSpTotalWaterLine.setSelection(1);
//                        break;
//                }
//            }
//        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getProperty_user_type())) {
                switch (bin.getWater_use_type()) {
                    case "रहिवास":
                        binding.formSpWaterUseType.setSelection(0);
                        break;

                    case "व्यवसायिक":
                        binding.formSpWaterUseType.setSelection(1);
                        break;

                    case "औद्योगिक":
                        binding.formSpWaterUseType.setSelection(2);
                        break;
                }
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getSolar_panel_available())) {
                switch (bin.getSolar_panel_available()) {
                    case selectYesOption:
                        binding.formSpSolarPanelAvailable.setSelection(0);
                        break;

                    case selectNoOption:
                        binding.formSpSolarPanelAvailable.setSelection(1);
                        break;
                }
            } else {
                binding.formSpSolarPanelAvailable.setSelection(1);
            }
        }
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
        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getSolar_panel_type())) {
                switch (bin.getSolar_panel_type()) {
                    case "गरम पाण्यासाठी":
                        binding.formSpSolarPanelType.setSelection(0);
                        break;

                    case "विजेसाठी":
                        binding.formSpSolarPanelType.setSelection(1);
                        break;

                    case "दोन्हीसाठी":
                        binding.formSpSolarPanelType.setSelection(2);
                        break;
                }
            }
        }
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
        // set Data
        if(bin != null){
            if(!Utility.isEmptyString(bin.getRain_water_harvesting())){
                switch (bin.getRain_water_harvesting()){
                    case selectYesOption:
                        binding.formSpRainWaterHarvesting.setSelection(0);
                        break;

                    case selectNoOption:
                        binding.formSpRainWaterHarvesting.setSelection(1);
                        break;
                }
            }
            else{
                binding.formSpRainWaterHarvesting.setSelection(1);
            }
        }
        binding.formSpRainWaterHarvesting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedRaniWaterHarvesting = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});
    }

//------------------------------------------------------- init Form Table RecycleView ----------------------------------------------------------------------------------------------------------------------

    private void initFormTable(){
        if(formModel != null){
            if(formModel.getForm_detail().size() > 0){
                formTableModels.addAll(formModel.getForm_detail());
            }
        }
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
        }
    }

//------------------------------------------------------- Add Form Table ----------------------------------------------------------------------------------------------------------------------

    private void addFormTable(){
        Dialog fDB = new Dialog(this);
        fDB.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fDB.setCancelable(false);
        fDB.setContentView(R.layout.dialogbox_form_table_item);
        fDB.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        // Exit Button
        Button btExit = fDB.findViewById(R.id.dbExit);
        btExit.setOnClickListener(view -> fDB.dismiss());
        // Init Edit Text
        EditText sr_no            = fDB.findViewById(R.id.form_table_sr_no);
        EditText floor            = fDB.findViewById(R.id.form_table_floor);
        EditText length          = fDB.findViewById(R.id.form_table_length);
        EditText height           = fDB.findViewById(R.id.form_table_height);
        EditText area             = fDB.findViewById(R.id.form_table_area);
        EditText building_age     = fDB.findViewById(R.id.form_table_building_age);
        EditText annual_rent      = fDB.findViewById(R.id.form_table_annual_rent);
        EditText tag_no           = fDB.findViewById(R.id.form_table_tag_no);
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
            // 1 -----------------------------
            FormFields bin = new FormFields();
            // Default Fields
            bin.setForm_id(Utility.getStringValue(formID));
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

            bin.setNo_of_floor(Utility.getEditTextValue(binding.formNoOfFloors));

            bin.setProperty_release_date(Utility.getEditTextValue(binding.formPropertyReleaseDate));
            bin.setBuild_permission(Utility.getStringValue(selectedBuildPermission));
            bin.setBuild_completion_form(Utility.getStringValue(selectedBuildCompletionForm));
            bin.setMetal_road(Utility.getStringValue(selectedMetalRoad));
            bin.setIs_toilet_available(Utility.getStringValue(selectedIsToiletAvailable));
            bin.setTotal_toilet(Utility.getEditTextValue(binding.formTotalToilet));
            bin.setToilet_type(Utility.getStringValue(selectedToiletType));
            bin.setIs_streetlight_available(Utility.getStringValue(selectedIsStreetlightAvailable));
            bin.setIs_water_line_available(Utility.getStringValue(selectedIsWaterLineAvailable));

            bin.setTotal_water_line1(Utility.getEditTextValue(binding.formTotalWaterLine1));
            bin.setTotal_water_line2(Utility.getEditTextValue(binding.formTotalWaterLine2));

            bin.setWater_use_type(Utility.getStringValue(selectedWaterUseType));
            bin.setSolar_panel_available(Utility.getStringValue(selectedSolarPanelAvailable));
            bin.setSolar_panel_type(Utility.getStringValue(selectedSolarPanelType));
            bin.setRain_water_harvesting(Utility.getStringValue(selectedRaniWaterHarvesting));

            bin.setPlot_area(Utility.getEditTextValue(binding.formPlotArea));
            bin.setProperty_area(Utility.getEditTextValue(binding.formPropertyArea));
            bin.setTotal_area(Utility.getEditTextValue(binding.formTotalArea));

            // Form
            formModel = new FormModel(bin,adapterFormTable.getFormTableModels());
            // Upload Form
            if(SystemPermission.isInternetConnected(mActivity)){
                SaveFormToServe(formModel);
            }
            else
            {
                SaveFormToDatabase(formModel);
            }
        }
    }

//------------------------------------------------------- SaveFormToServe/Local ----------------------------------------------------------------------------------------------------------------------

    private void SaveFormToServe(FormModel formModel){
        showProgressBar();
        Map<String, String> params = new HashMap<>();
        params.put("data", Utility.convertFormModelToString(formModel));
        BaseApplication.getInstance().makeHttpPostRequest(this, URL_Utility.ResponseCode.WS_FORM, URL_Utility.WS_FORM, params, false, false);
    }

    private void SaveFormToDatabase(FormModel formModel){
        String token = String.valueOf(Utility.getToken());
        dataBaseHelper.insertMapForm(
                Utility.getSavedData(mActivity,Utility.LOGGED_USERID),
                polygonID,
                formID,
                latitude,
                longitude,
                Utility.convertFormModelToString(formModel),
                "t",
                token,"",""
        );

        dataBaseHelper.insertMapFormLocal(
                Utility.getSavedData(mActivity,Utility.LOGGED_USERID),
                latitude,
                longitude,
                Utility.convertFormModelToString(formModel),
                token,"",""
        );

        // Delete Resurvey Form Data by ID
        dataBaseHelper.deleteResurveyMapFormData(resurveyID);

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
        // Form
        if(responseCode == URL_Utility.ResponseCode.WS_FORM){
            if(!response.equals("")){
                try {
                    JSONObject mObj = new JSONObject(response);
                    String status = mObj.optString(URL_Utility.STATUS);
                    Log.e(TAG, "Form Status : " + status);
                    // Status -> Success
                    if(status.equalsIgnoreCase(URL_Utility.STATUS_SUCCESS)){
                        Log.e(TAG,"Form Upload to Server SuccessFully");
                        dismissProgressBar();
                        if(formModel != null){
                            dataBaseHelper.insertMapForm(
                                    Utility.getSavedData(mActivity,Utility.LOGGED_USERID),
                                    polygonID,
                                    formID,
                                    latitude,
                                    longitude,
                                    Utility.convertFormModelToString(formModel),
                                    "f",
                                    String.valueOf(Utility.getToken()),
                                    "",
                                    ""
                            );
                        }
                        Utility.showOKDialogBox(mActivity, URL_Utility.SAVE_SUCCESSFULLY, okDialogBox -> {
                            okDialogBox.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        });
                        // Delete Resurvey Form Data by ID
                        dataBaseHelper.deleteResurveyMapFormData(resurveyID);
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
        Log.e(TAG,"Error Response Code: "+ responseCode);
        Log.e(TAG,"Error: "+ error.getMessage());
        Utility.showToast(mActivity,Utility.ERROR_MESSAGE);
    }

//------------------------------------------------------- Exit ----------------------------------------------------------------------------------------------------------------------

    private void onFormExit(){
        setResult(RESULT_CANCELED);
        finish();
    }

//------------------------------------------------------- progressBar ----------------------------------------------------------------------------------------------------------------------

    private void showProgressBar() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Form Uploading...");
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

}