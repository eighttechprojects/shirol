package com.eighttechprojects.propertytaxshirol.Utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eighttechprojects.propertytaxshirol.BuildConfig;
import com.eighttechprojects.propertytaxshirol.Model.FormFields;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.FormTableModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class Utility {

    public static final String  SyncServiceOn = "com.eighttechprojects.propertytaxshirol.syncserviceon";
    // Pref key
    private final static String PREF_KEY = "EShop";
    public static final String ERROR_MESSAGE = "something is wrong";
    // Internet Connection Constants
    public static final String NO_NETWORK_CONNECTED = "No Network Connected";
    public static final String NETWORK_CONNECTED    = "Network Connected";
    // Bar Code
    public static final int BARCODE_SCAN_CODE = 1001;
    public static final String BarCodeResult = "barcodeResult";
    // Date and Time
    public static final String OLD_DATE = "old_date";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // Login
    public static final String IS_USER_SUCCESSFULLY_LOGGED_IN = "IS_USER_SUCCESSFULLY_LOGGED_IN";
    public static final String LOGGED_USERID       = "user_id";
    // Profile
    public static final String PROFILE_FIRSTNAME     = "profile-first-name";
    public static final String PROFILE_LASTNAME      = "profile-last-name";
    public static final String PROFILE_EMAIL_ID      = "profile-email";
    public static final String PROFILE_PASSWORD      = "profile-password";
    public static final String PROFILE_IMAGE         = "profile-image";
    public static final String PROFILE_MOBILE_NUMBER = "profile-mobile-number";
    // Pick And Request
    public static final int REQUEST_TAKE_PHOTO = 1002;
    public static final int PICK_IMAGE_REQUEST = 1001;
    public static final int PICK_FILE_RESULT_CODE = 1004;
    public static final int PICK_AUDIO_FILE_RESULT_CODE = 1005;
    public static final int PICK_VIDEO_FILE_RESULT_CODE = 1006;
    public static final int PICK_BAR_CODE_RESULT_CODE = 1007;

    // Form
    public static final String PASS_GEOM_ARRAY = "geom-array";
    public static final String PASS_GEOM_TYPE  = "geom-type";
    public static final String PASS_LAT        = "latitude";
    public static final String PASS_LONG       = "longitude";
    public static final String PASS_ID         = "id";
    public static final String PASS_USER_ID    = "user_id";




//------------------------------------------------------- SharedPreferences ----------------------------------------------------------------------------------------------------------------

    public static void saveData(Context ctx, String TAG, String data) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_KEY, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TAG, data);
        editor.apply();
    }

    public static void saveData(Context ctx, String TAG, boolean value) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_KEY, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(TAG, value);
        editor.apply();
    }

    public static String getSavedData(Context ctx, String TAG) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_KEY, 0);
        return prefs.getString(TAG, "");
    }

    public static boolean getBooleanSavedData(Context ctx, String TAG) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_KEY, 0);
        return prefs.getBoolean(TAG, false);
    }

    public static void clearData(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_KEY, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static void setPermissionDenied(Context ctx, String keyPermission, boolean isDenied) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_KEY, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(keyPermission, isDenied);
        editor.apply();
    }

    public static boolean isPermissionDenied(Context ctx, String keyPermission) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREF_KEY, 0);
        return prefs.getBoolean(keyPermission, false);
    }

//------------------------------------------------------- isEmptyString ----------------------------------------------------------------------------------------------------------------

    public static boolean isEmptyString(String str) {
        if (str == null) {
            return true;
        } else if (str.isEmpty()) {
            return true;
        } else return str.equalsIgnoreCase("null");
    }

    public static String getStringValue(String value){
        return isEmptyString(value) ? "" : value;
    }

    public static String getEditTextValue(EditText editText){
        return isEmptyString(editText.getText().toString()) ? "" : editText.getText().toString();
    }

//------------------------------------------------------- Toast ----------------------------------------------------------------------------------------------------------------

    public static void showToast(Context context, String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

//------------------------------------------------------- Dialog box ----------------------------------------------------------------------------------------------------------------

    public static void showSelectDialog(Context context, String[] arr, onItemClick onItemClick) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle(context.getString(R.string.select));
        pictureDialog.setItems(arr, (dialog, position) -> {
            onItemClick.itemSelected(position);
            dialog.dismiss();
        });
        pictureDialog.show();
    }

    public static String validateDoubleDigit(int digit) {
        if (String.valueOf(digit).length() < 2) {
            return "0" + digit;
        } else {
            return "" + digit;
        }
    }

    public static void showDatePickerDialogBox(Context context, onDateSelection onDateSelection) {
        int mYear, mMonth, mDay;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> onDateSelection.onDateSelected( validateDoubleDigit(year)+"-"+validateDoubleDigit((monthOfYear + 1)) + "-"+ validateDoubleDigit(dayOfMonth)), mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public static void showTimePickerDialogBox(Context context, onTimeSelection onTimeSelection) {
        int mHour, mMinute, mSec;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSec = c.get(Calendar.SECOND);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minute) -> onTimeSelection.onTimeSelected(validateDoubleDigit(hourOfDay) + ":" + validateDoubleDigit(minute) + ":" + validateDoubleDigit(mSec)), mHour, mMinute, true);
        timePickerDialog.show();
    }

    public static void showLocationDialog(Context context, String title, String[] items, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(items, onClickListener);
        builder.setCancelable(false);
        builder.create().show();
    }

    public static void showSingleBtnDialog(Context context, String title, String message, String OkBtnText, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(!Utility.isEmptyString(title))
            builder.setTitle(title);
        if(!Utility.isEmptyString(message))
            builder.setMessage(message);
        builder.setPositiveButton(OkBtnText, onClickListener);
        builder.setCancelable(false);
        builder.create().show();
    }

    public static void showDoubleBtnDialog(Context context, String title, String message, String OkBtnText, String CancelBtnText, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(!Utility.isEmptyString(title))
            builder.setTitle(title);
        if(!Utility.isEmptyString(message))
            builder.setMessage(message);
        builder.setPositiveButton(OkBtnText, onClickListener);
        builder.setNegativeButton(CancelBtnText, onClickListener);
        builder.setCancelable(false);
        builder.create().show();
    }

    public static void showOKCancelDialogBox(Context context, String title,String  msg, DialogBoxOKClick dialogBoxOKClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, i) -> dialogBoxOKClick.OkClick(dialog))
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        alertDialog.show();
    }

    public static void showSyncCancelDialogBox(Context context, String title,String  msg, DialogBoxOKClick dialogBoxOKClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Sync", (dialog, i) -> dialogBoxOKClick.OkClick(dialog))
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        alertDialog.show();
    }

    public static void showYesNoDialogBox(Context context, String title,String  msg, DialogBoxYesClick dialogBoxYesClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, i) -> dialogBoxYesClick.YesClick(dialog) )
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        alertDialog.show();
    }

    public static void showYesNoDialogBox(Context context, String  msg, DialogBoxYesClick dialogBoxYesClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, i) -> dialogBoxYesClick.YesClick(dialog) )
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        alertDialog.show();
    }

    public static void showYesNoDialogBox(Context context,String title,String str, DialogBoxYesClick dialogBoxYesClick, DialogBoxNoClick dialogBoxNoClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(str)
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogOk, i) -> dialogBoxYesClick.YesClick(dialogOk))
                .setNegativeButton("No", (dialogCancel, i) -> dialogBoxNoClick.NoClick(dialogCancel))
                .create();
        alertDialog.show();
    }


    public static void showYesNoDialogBox(Context context,String positiveButtonName, String negativeButtonName,String title,String str, DialogBoxYesClick dialogBoxYesClick, DialogBoxNoClick dialogBoxNoClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(str)
                .setPositiveButton(positiveButtonName, (dialogOk, i) -> dialogBoxYesClick.YesClick(dialogOk))
                .setNegativeButton(negativeButtonName, (dialogCancel, i) -> dialogBoxNoClick.NoClick(dialogCancel))
                .create();
        alertDialog.show();
    }


    public static void showOKDialogBox(Context context, String title,String  msg, DialogBoxOKClick dialogBoxOKClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, i) -> dialogBoxOKClick.OkClick(dialog) )
                .create();
        alertDialog.show();
    }

    public static void showOKDialogBox(Context context, String  msg, DialogBoxOKClick dialogBoxOKClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, i) -> dialogBoxOKClick.OkClick(dialog) )
                .create();
        alertDialog.show();
    }

    public static void showSelectedItemDialogBox(Context context,String title, String[] itemsLists, DialogBoxSelectedItem dialogBoxSelectedItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(itemsLists, (dialog, which) -> {
            dialogBoxSelectedItem.onSelectedItem(itemsLists[which]);
            dialog.dismiss();
        });
        builder.show();
    }

    public static void showCallDialogBox(Context context, DialogBoxOKClick dialogBoxOKClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("You Want to Call this Person?")
                .setCancelable(false)
                .setPositiveButton("Call", (dialog, i) -> dialogBoxOKClick.OkClick(dialog))
                .setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    public static void showMailDialogBox(Context context, DialogBoxOKClick dialogBoxOKClick){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("You Want to Mail this Person?")
                .setCancelable(false)
                .setPositiveButton("Mail", (dialog, i) -> dialogBoxOKClick.OkClick(dialog))
                .setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    public static void showSyncYourDataAlert(Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Sync Alert")
                .setMessage("Please Sync your Data for your Safety else your data may be loss.")
                .setPositiveButton("OK", (dialog, i) -> dialog.dismiss())
                .create();
        alertDialog.show();

    }
//------------------------------------------------------- Picker ----------------------------------------------------------------------------------------------------------------

    public void openFilePicker(Activity context, int position, onPhotoCaptured onPhotoCaptured) {
        if (SystemPermission.isExternalStorage(context)) {
            Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            onPhotoCaptured.getPath(null, position);
            context.startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE);
        }
    }

    public void openFilePickerMultiple(Activity context, int position, onPhotoCaptured onPhotoCaptured) {
        if (SystemPermission.isExternalStorage(context)) {
            Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            onPhotoCaptured.getPath(null, position);
            context.startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE);
        }
    }

    public void openAudioFilePicker(Activity context, int position, onPhotoCaptured onPhotoCaptured) {
        if (SystemPermission.isExternalStorage(context)) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("audio/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            onPhotoCaptured.getPath(null, position);
            context.startActivityForResult(chooseFile, PICK_AUDIO_FILE_RESULT_CODE);
        }
    }

    public void openVideoFilePicker(Activity context, int position, onPhotoCaptured onPhotoCaptured) {
        if (SystemPermission.isExternalStorage(context)) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("video/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            onPhotoCaptured.getPath(null, position);
            context.startActivityForResult(chooseFile, PICK_VIDEO_FILE_RESULT_CODE);
        }
    }

    public void openImagePicker(Activity context, ImageFileUtils imageFileUtils, int position, onPhotoCaptured onPhotoCaptured) {
        String[] items = getPhotoSelectionOptions();
        showLocationDialog(context, context.getString(R.string.select), items,
                (dialogInterface, item) -> {
                    switch (items[item]) {
                        case PHOTO_SELECTION.TAKE_PHOTO:
                            if (SystemPermission.isExternalStorage(context)) {
                                if (SystemPermission.isCamera(context)) {
                                    takePhoto(context, imageFileUtils, position, onPhotoCaptured);
                                }
                            }
                            break;
                        case PHOTO_SELECTION.CHOOSE_FROM_GAL:
                            if (SystemPermission.isExternalStorage(context)) {
                                pickFromGallery(context, position, onPhotoCaptured);
                            }
                            break;
                        case PHOTO_SELECTION.CANCEL:
                            dialogInterface.dismiss();
                            break;
                    }
                });
    }

    private void pickFromGallery(Activity context, int position, onPhotoCaptured onPhotoCaptured) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        onPhotoCaptured.getPath(null, position);
        context.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    private void takePhoto(Activity context, ImageFileUtils imageFileUtils, int position, onPhotoCaptured onPhotoCaptured) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File destFileTemp = imageFileUtils.getDestinationFile(imageFileUtils.getRootDirFile(context));
        Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", destFileTemp);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        onPhotoCaptured.getPath(destFileTemp.getAbsolutePath(), position);
        context.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }



//------------------------------------------------------- Calender ----------------------------------------------------------------------------------------------------------------

    public static Calendar getEveningStartTime() {
        Calendar calendar8pm = Calendar.getInstance(Locale.US);
        calendar8pm.set(Calendar.HOUR_OF_DAY, 18);
        calendar8pm.set(Calendar.MINUTE, 0);
        calendar8pm.set(Calendar.SECOND, 0);
        return calendar8pm;
    }

    public static Calendar getNoonStartTime() {
        Calendar calendar12pm = Calendar.getInstance(Locale.US);

        calendar12pm.set(Calendar.HOUR_OF_DAY, 12);
        calendar12pm.set(Calendar.MINUTE, 0);
        calendar12pm.set(Calendar.SECOND, 0);
        return calendar12pm;
    }

    public static String getRecordDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return df.format(c.getTime());
    }

    public static String getDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return df.format(c.getTime());
    }

//------------------------------------------------------- reDirect ----------------------------------------------------------------------------------------------------------------

    public static void reDirectToCallDial(Context context , String number){
        if(!isEmptyString(number)){
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        }
        else{
            Log.e("Error","Number is Empty/Null");
        }
    }

    public static void reDirectToEmail(Context context , String emailID){
        if(!isEmptyString(emailID)){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailID));
            context.startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
        }
        else{
            Log.e("Error","Email ID is Empty/Null");
        }
    }

    public static void reDirectTo(Context mActivity, Class<?> cls){
        Intent intent = new Intent(mActivity,cls);
        mActivity.startActivity(intent);
    }

//------------------------------------------------------- Token ----------------------------------------------------------------------------------------------------------------

    public static long getToken() {
        Random rand = new Random();
        //long x = (long)(rand.nextDouble()*100000000000000L);
        long x = (long)(rand.nextDouble()*100000000L);
        long y = (long)(rand.nextDouble()*100000000L);
        @SuppressLint("DefaultLocale") String s =  String.format("%08d", x)+ String.format("%08d", y);
        return Long.parseLong(s);
    }

//------------------------------------------------------- Base 64 Image ----------------------------------------------------------------------------------------------------------------

    public static Bitmap decodeBase64Image(String base64Image){
        // decode base64 string
        byte[] bytes= Base64.decode(base64Image,Base64.DEFAULT);
        // Initialize bitmap
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

//------------------------------------------------------- Interface -------------------------------------------------------------------------------------------------------------------------------------------------

    public interface DialogBoxOKClick{ void OkClick(DialogInterface okDialogBox);}

    public interface DialogBoxCancelClick{ void CancelClick(DialogInterface cancelDialogBox);}

    public interface DialogBoxNoClick{ void NoClick(DialogInterface noDialogBox);}

    public interface DialogBoxYesClick{ void YesClick(DialogInterface yesDialogBox);}

    public interface DialogBoxSelectedItem{ void onSelectedItem(String selectedItem);}

    public interface onDialogClickListener {
        void onPositiveAction(String value);
        void onCancelAction();
    }

    public interface onUpdatedSelection { void onResult();}

    public interface onItemClick {
        void itemSelected(int position);
    }

    public interface onDateSelection {
        void onDateSelected(String date);
    }

    public interface onTimeSelection {
        void onTimeSelected(String time);
    }

    public interface onPhotoCaptured {
        void getPath(String path, int position);
    }

    public interface COLOR_CODE {
        String ORANGE     = "#E78B13";
        String PURPLE     = "#800080";
        String BLUE       = "#0000FF";
        String RED        = "#FF0000";
        String LIGHT_BLUE = "#1976D2";
        String GREEN      = "#00FF00";
        String BLACK      = "#000000";
        String PINK       = "#FF10F0";
        String YELLOW     = "#FFEA00";
        String DARK_GREY  = "#BDBDBD";
        String GREY       = "#EEEEEE";
        String LIGHT_GREY = "#BDBDBD";
        String SEA_DARK   = "#26648E";
        String SEA_MID    = "#4F8FC0";
        String SEA_LIGHT  = "#53D2DC";
    }


    public interface PHOTO_SELECTION {
        String TAKE_PHOTO = "Take Photo";
        String CHOOSE_FROM_GAL = "Choose from Gallery";
        String CANCEL = "Cancel";
    }


//------------------------------------------------------- String Array -------------------------------------------------------------------------------------------------------------------------------------------------

    public static String[] getPhotoSelectionOptions() {
        return new String[]{PHOTO_SELECTION.TAKE_PHOTO,
                PHOTO_SELECTION.CHOOSE_FROM_GAL,
                PHOTO_SELECTION.CANCEL};
    }


    public static void setToVerticalRecycleView(Context mActivity, RecyclerView recyclerView, RecyclerView.Adapter<?> adapter ){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    // Add Marker
    public static Marker addFormToMap(GoogleMap Map, LatLng latLng){
        if(latLng != null){
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .position(latLng)
                    .draggable(true);
            return Map.addMarker(markerOptions);
        }
        return null;
    }

    // Add Marker
    public static Marker addMapFormMarker(GoogleMap Map, LatLng latLng, float Icon){
        if(latLng != null){
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(Icon))
                    .position(latLng)
                    .draggable(true);
            return Map.addMarker(markerOptions);
        }
        return null;
    }


    // Add Marker
    public static Marker addResurveyMapFormMarker(GoogleMap Map, LatLng latLng){
        if(latLng != null){
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(latLng)
                    .draggable(true);
            return Map.addMarker(markerOptions);
        }
        return null;
    }

//------------------------------------------------------- converter ----------------------------------------------------------------------------------------------------------------

    public static FormModel convertStringToFormModel(String data){
            java.lang.reflect.Type listType = new TypeToken<FormModel>() {}.getType();
            return new Gson().fromJson(data, listType);
    }

    public static String convertFormModelToString(FormModel formModel){
        return new Gson().toJson(formModel);
    }

    public static FormFields convertStringToFormFields(String data){
        java.lang.reflect.Type listType = new TypeToken<FormFields>() {}.getType();
        return new Gson().fromJson(data, listType);
    }

    public static FormTableModel convertStringToFormTable(String data){
        java.lang.reflect.Type listType = new TypeToken<FormTableModel>() {}.getType();
        return new Gson().fromJson(data, listType);
    }



}