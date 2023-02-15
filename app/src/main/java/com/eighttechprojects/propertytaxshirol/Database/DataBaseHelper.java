package com.eighttechprojects.propertytaxshirol.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.Model.FormListModel;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

	// SQLiteDatabase
	SQLiteDatabase db;
	// Context
	Context ctx;
	// DataBase Name
	public static final String DATABASE_NAME = "PropertyTaxShirol2.db";
	// DataBase Version
	public static final int DATABASE_VERSION = 7;

	// param

	public static final String keyParamID           = "id";
	public static final String keyParamPolygonID    = "polygon_id";
	public static final String keyParamUserID       = "user_id";

	public static final String keyParamFormID       = "form_id";
	public static final String keyParamData         = "data";
	public static final String keyParamLat          = "latitude";
	public static final String keyParamLon          = "longitude";
	public static final String keyParamIsOnlineSave = "isOnlineSave";
	public static final String keyParamToken        = "token";

	public static final String keyParamFile         = "file";
	public static final String keyParamCamera       = "camera";


	// Table Names
	private static final String TABLE_MAP_FORM_LOCAL = "FormLocal";
	private static final String TABLE_MAP_FORM       = "Form";
	private static final String TABLE_RESURVEY_MAP_FORM = "ResurveyForm";


//---------------------------------------------------------- Create Table Query -----------------------------------------------------------------------------------------------

	// Resurvey Map Data store ----------------------------------------------------------------------------
	public static final String CREATE_TABLE_RESURVEY_MAP_FORM = "CREATE TABLE " + TABLE_RESURVEY_MAP_FORM +"(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id VARCHAR(100), latitude VARCHAR(100), longitude VARCHAR(100), data TEXT, file TEXT, camera TEXT)";
	public static final String DROP_TABLE_RESURVEY_MAP_FORM   = "DROP TABLE "   + TABLE_RESURVEY_MAP_FORM;
	public static final String DELETE_TABLE_RESURVEY_MAP_FORM = "DELETE FROM "  + TABLE_RESURVEY_MAP_FORM;
	public static final String GET_RESURVEY_MAP_FORM          = "SELECT * FROM "+ TABLE_RESURVEY_MAP_FORM;

	// Local as well as Server Data store into it! -------------------------------------------------
	public static final String CREATE_TABLE_MAP_FORM = "CREATE TABLE " + TABLE_MAP_FORM +"(id INTEGER PRIMARY KEY AUTOINCREMENT,form_id VARCHAR(100),polygon_id VARCHAR(100) ,user_id VARCHAR(100), latitude VARCHAR(100), longitude VARCHAR(100), data TEXT, isOnlineSave VARCHAR(10), token VARCHAR(500), file TEXT, camera TEXT)";
	public static final String DROP_TABLE_MAP_FORM   = "DROP TABLE "   + TABLE_MAP_FORM;
	public static final String DELETE_TABLE_MAP_FORM = "DELETE FROM "  + TABLE_MAP_FORM;
	public static final String GET_MAP_FORM          = "SELECT * FROM "+ TABLE_MAP_FORM;

	// Local Data store only ----------------------------------------------------------------------------
	public static final String CREATE_TABLE_MAP_FORM_LOCAL = "CREATE TABLE " + TABLE_MAP_FORM_LOCAL +"(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id VARCHAR(100), latitude VARCHAR(100), longitude VARCHAR(100), data TEXT, token VARCHAR(500), file TEXT, camera TEXT)";
	public static final String DROP_TABLE_MAP_FORM_LOCAL   = "DROP TABLE "   + TABLE_MAP_FORM_LOCAL;
	public static final String DELETE_TABLE_MAP_FORM_LOCAL = "DELETE FROM "  + TABLE_MAP_FORM_LOCAL;
	public static final String GET_MAP_FORM_LOCAL          = "SELECT * FROM "+ TABLE_MAP_FORM_LOCAL;

//---------------------------------------------------------- Constructor ----------------------------------------------------------------------------------------------------------------------

	public DataBaseHelper(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
		ctx = c;
	}

//---------------------------------------------------------- onCreate ----------------------------------------------------------------------------------------------------------------

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_MAP_FORM);
		db.execSQL(CREATE_TABLE_MAP_FORM_LOCAL);
		db.execSQL(CREATE_TABLE_RESURVEY_MAP_FORM);
	}

//---------------------------------------------------------- onUpgrade ----------------------------------------------------------------------------------------------------------------

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// DROP -----------------------
		db.execSQL(DROP_TABLE_MAP_FORM);
		db.execSQL(DROP_TABLE_MAP_FORM_LOCAL);
		db.execSQL(DROP_TABLE_RESURVEY_MAP_FORM);

		// Insert ---------------------
		db.execSQL(CREATE_TABLE_MAP_FORM);
		db.execSQL(CREATE_TABLE_MAP_FORM_LOCAL);
		db.execSQL(CREATE_TABLE_RESURVEY_MAP_FORM);
	}

//---------------------------------------------------------- Open Database ----------------------------------------------------------------------------------------------------------------

	public void open() {
		db = this.getWritableDatabase();
	}

//---------------------------------------------------------- Close Database ----------------------------------------------------------------------------------------------------------------

	@Override
	public void close() {
		db.close();
	}

//---------------------------------------------------------- Execute Query ----------------------------------------------------------------------------------------------------------------

	public void executeQuery(String query) {
		db.execSQL(query);
	}

//---------------------------------------------------------- Execute Cursor ----------------------------------------------------------------------------------------------------------------

	public Cursor executeCursor(String selectQuery) {
		return db.rawQuery(selectQuery, null);
	}


// ######################################################### Insert Query ######################################################################################################

	//---------------------------------------------------------- Insert Resurvey Form -------------------------------------------------------
	public void insertResurveyMapForm(String user_id,String lat, String lon,String data, String filePath, String cameraPath) {
		open();
		ContentValues cv = new ContentValues();
		cv.put(keyParamUserID, user_id);
		cv.put(keyParamLat, lat);
		cv.put(keyParamLon, lon);
		cv.put(keyParamData,data);
		cv.put(keyParamFile,filePath);
		cv.put(keyParamCamera,cameraPath);
		db.insert(TABLE_RESURVEY_MAP_FORM, null, cv);
		close();
	}

	//---------------------------------------------------------- Insert Map Form ------------------------------------------------------------
	public void insertMapForm(String user_id,String polygon_id,String form_id,String lat, String lon,String data,String isOnlineSave, String token, String filePath, String cameraPath){
		open();
		ContentValues cv = new ContentValues();
		cv.put(keyParamUserID, user_id);
		cv.put(keyParamPolygonID,polygon_id);
		cv.put(keyParamFormID,form_id);
		cv.put(keyParamLat, lat);
		cv.put(keyParamLon, lon);
		cv.put(keyParamData,data);
		cv.put(keyParamIsOnlineSave,isOnlineSave);
		cv.put(keyParamToken,token);
		cv.put(keyParamFile,filePath);
		cv.put(keyParamCamera,cameraPath);
		db.insert(TABLE_MAP_FORM, null, cv);
		close();
	}

	//---------------------------------------------------------- Insert Map Form Local -------------------------------------------------------
	public void insertMapFormLocal(String user_id,String lat, String lon,String data,String token, String filePath, String cameraPath) {
		open();
		ContentValues cv = new ContentValues();
		cv.put(keyParamUserID, user_id);
		cv.put(keyParamLat, lat);
		cv.put(keyParamLon, lon);
		cv.put(keyParamData,data);
		cv.put(keyParamToken,token);
		cv.put(keyParamFile,filePath);
		cv.put(keyParamCamera,cameraPath);
		db.insert(TABLE_MAP_FORM_LOCAL, null, cv);
		close();
	}

// ######################################################### Update Query ####################################################################################################

	public void updateMapData(String token, String isOnlineSave){
		open();
		ContentValues cv = new ContentValues();
		String where = "token = ?";
		String[] whereArgs = { token };
		cv.put(keyParamIsOnlineSave,isOnlineSave);
		db.update(TABLE_MAP_FORM, cv,where, whereArgs);
		close();
	}

// ######################################################### Delete Query ####################################################################################################

	//---------------------------------------------------------- Delete Map Form Local ------------------------------------------------------------
	public void deleteMapData(String id)
	{
		open();
		String whereClause = "id = ?";
		String[] whereArgs = { id };
		db.delete(TABLE_MAP_FORM, whereClause, whereArgs);
		close();
	}

	//---------------------------------------------------------- Delete Resurvey Map Form ------------------------------------------------------------
	public void deleteResurveyMapFormData(String id)
	{
		open();
		String whereClause = "id = ?";
		String[] whereArgs = { id };
		db.delete(TABLE_RESURVEY_MAP_FORM, whereClause, whereArgs);
		close();
	}

	//---------------------------------------------------------- Delete Map Form Local ------------------------------------------------------------
	public void deleteMapFormLocalData(String id)
	{
		open();
		String whereClause = "id = ?";
		String[] whereArgs = { id };
		db.delete(TABLE_MAP_FORM_LOCAL, whereClause, whereArgs);
		close();
	}

// ######################################################### Select Query #######################################################################################################

	// Map Form Data List
	@SuppressLint("Range")
	public ArrayList<FormDBModel> getMapFormDataList() {
		ArrayList<FormDBModel> list = new ArrayList<>();
		open();
		Cursor cv = executeCursor(GET_MAP_FORM);
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			for(int i=0; i<cv.getCount(); i++) {
				FormDBModel bin = new FormDBModel();
				bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
				bin.setPolygon_id(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
				bin.setUser_id(cv.getString(cv.getColumnIndex(keyParamUserID)));
				bin.setLatitude(cv.getString(cv.getColumnIndex(keyParamLat)));
				bin.setLongitude(cv.getString(cv.getColumnIndex(keyParamLon)));
				bin.setFormData(cv.getString(cv.getColumnIndex(keyParamData)));
				bin.setIsOnlineSave(cv.getString(cv.getColumnIndex(keyParamIsOnlineSave)));
				bin.setToken(cv.getString(cv.getColumnIndex(keyParamToken)));
				bin.setFilePath(cv.getString(cv.getColumnIndex(keyParamFile)));
				bin.setCameraPath(cv.getString(cv.getColumnIndex(keyParamCamera)));
				list.add(bin);
				cv.moveToNext();
			}
		}
		close();
		return list;
	}


	@SuppressLint("Range")
	public ArrayList<FormListModel> getFormIDByPolygonID(String polygonID) {
		ArrayList<FormListModel> list = new ArrayList<>();
		open();
		Cursor cv = executeCursor("Select * From "+TABLE_MAP_FORM+" Where polygon_id ="+ polygonID);
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			for(int i=0; i<cv.getCount(); i++) {
				FormListModel bin = new FormListModel();
				bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
				bin.setForm_id(cv.getString(cv.getColumnIndex(keyParamFormID)));
				bin.setPolygon_id(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
				list.add(bin);
				cv.moveToNext();
			}
		}
		close();
		return list;
	}
	@SuppressLint("Range")
	public FormDBModel getFormByPolygonAndFormID(String polygonID,String id) {
		FormDBModel bin = new FormDBModel();
		open();
		Cursor cv = executeCursor("Select * From "+TABLE_MAP_FORM+" Where polygon_id ="+ polygonID +"&& id = "+id);
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
			bin.setForm_id(cv.getString(cv.getColumnIndex(keyParamFormID)));
			bin.setPolygon_id(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
			bin.setUser_id(cv.getString(cv.getColumnIndex(keyParamUserID)));
			bin.setLatitude(cv.getString(cv.getColumnIndex(keyParamLat)));
			bin.setLongitude(cv.getString(cv.getColumnIndex(keyParamLon)));
			bin.setFormData(cv.getString(cv.getColumnIndex(keyParamData)));
			bin.setIsOnlineSave(cv.getString(cv.getColumnIndex(keyParamIsOnlineSave)));
			bin.setToken(cv.getString(cv.getColumnIndex(keyParamToken)));
			bin.setFilePath(cv.getString(cv.getColumnIndex(keyParamFile)));
			bin.setCameraPath(cv.getString(cv.getColumnIndex(keyParamCamera)));
		}
		close();
		return bin;
	}


	// Map Form Local Data List
	@SuppressLint("Range")
	public ArrayList<FormDBModel> getMapFormLocalDataList() {
		ArrayList<FormDBModel> list = new ArrayList<>();
		open();
		Cursor cv = executeCursor(GET_MAP_FORM_LOCAL);
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			for(int i=0; i<cv.getCount(); i++) {
				FormDBModel bin = new FormDBModel();
				bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
				bin.setUser_id(cv.getString(cv.getColumnIndex(keyParamUserID)));
				bin.setLatitude(cv.getString(cv.getColumnIndex(keyParamLat)));
				bin.setLongitude(cv.getString(cv.getColumnIndex(keyParamLon)));
				bin.setFormData(cv.getString(cv.getColumnIndex(keyParamData)));
				bin.setToken(cv.getString(cv.getColumnIndex(keyParamToken)));
				bin.setFilePath(cv.getString(cv.getColumnIndex(keyParamFile)));
				bin.setCameraPath(cv.getString(cv.getColumnIndex(keyParamCamera)));
				list.add(bin);
				cv.moveToNext();
			}
		}
		close();
		return list;
	}

	// Resurvey Map Form Data List
	@SuppressLint("Range")
	public ArrayList<FormDBModel> getResurveyMapFormDataList() {
		ArrayList<FormDBModel> list = new ArrayList<>();
		open();
		Cursor cv = executeCursor(GET_RESURVEY_MAP_FORM);
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			for(int i=0; i<cv.getCount(); i++) {
				FormDBModel bin = new FormDBModel();
				bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
				bin.setUser_id(cv.getString(cv.getColumnIndex(keyParamUserID)));
				bin.setLatitude(cv.getString(cv.getColumnIndex(keyParamLat)));
				bin.setLongitude(cv.getString(cv.getColumnIndex(keyParamLon)));
				bin.setFormData(cv.getString(cv.getColumnIndex(keyParamData)));
				bin.setFilePath(cv.getString(cv.getColumnIndex(keyParamFile)));
				bin.setCameraPath(cv.getString(cv.getColumnIndex(keyParamCamera)));
				list.add(bin);
				cv.moveToNext();
			}
		}
		close();
		return list;
	}

	// Resurvey Map Form Data List
	@SuppressLint("Range")
	public FormDBModel getResurveyMapFormByID(String id) {
		FormDBModel bin = new FormDBModel();
		open();
		Cursor cv = executeCursor("Select * From "+TABLE_RESURVEY_MAP_FORM+" Where id ="+ id);
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
			bin.setUser_id(cv.getString(cv.getColumnIndex(keyParamUserID)));
			bin.setLatitude(cv.getString(cv.getColumnIndex(keyParamLat)));
			bin.setLongitude(cv.getString(cv.getColumnIndex(keyParamLon)));
			bin.setFormData(cv.getString(cv.getColumnIndex(keyParamData)));
			bin.setFilePath(cv.getString(cv.getColumnIndex(keyParamFile)));
			bin.setCameraPath(cv.getString(cv.getColumnIndex(keyParamCamera)));
		}
		close();
		return bin;
	}

// ######################################################### Logout ##############################################################################################################

	public void logout() {
		open();
		executeQuery(DELETE_TABLE_MAP_FORM);
		executeQuery(DELETE_TABLE_MAP_FORM_LOCAL);
		executeQuery(DELETE_TABLE_RESURVEY_MAP_FORM);
		close();
	}

// ######################################################### CLear ##############################################################################################################

	public void clearAllDatabaseTable(){
		open();
		executeQuery(DELETE_TABLE_MAP_FORM);
		executeQuery(DELETE_TABLE_MAP_FORM_LOCAL);
		executeQuery(DELETE_TABLE_RESURVEY_MAP_FORM);
		close();
	}

	public void clearResurveyDatabaseTable(){
		open();
		executeQuery(DELETE_TABLE_RESURVEY_MAP_FORM);
		close();
	}

}