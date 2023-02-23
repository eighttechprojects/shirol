package com.eighttechprojects.propertytaxshirol.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.eighttechprojects.propertytaxshirol.Model.FormDBModel;
import com.eighttechprojects.propertytaxshirol.Model.FormListModel;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.GeoJsonModel;
import com.eighttechprojects.propertytaxshirol.Model.LastKeyModel;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String TAG = DataBaseHelper.class.getSimpleName();
	// SQLiteDatabase
	SQLiteDatabase db;
	// Context
	Context ctx;
	// DataBase Name
	public static final String DATABASE_NAME = "PropertyTaxShirol3.db";
	// DataBase Version
	public static final int DATABASE_VERSION = 12;

	// param
	public static final String keyParamID           = "id";
	public static final String keyParamPolygonID    = "polygon_id";
	public static final String keyParamGISID        = "gis_id";
	public static final String keyParamUserID       = "user_id";
	public static final String keyParamFormID       = "form_id";
	public static final String keyParamData         = "data";
	public static final String keyParamLat          = "latitude";
	public static final String keyParamLon          = "longitude";
	public static final String keyParamIsOnlineSave = "isOnlineSave";
	public static final String keyParamToken        = "token";
	public static final String keyParamFile         = "file";
	public static final String keyParamCamera       = "camera";
	public static final String keyParamGeoJsonLatLon = "geojsonlatlong";
	public static final String keyParamGeoJsonForm   = "geojsonform";
	public static final String keyParamFormLastID =  "last_id";
	public static final String keyParamPolygonStatus = "polygon_status";

	public static final String keyParamWardNo        = "ward_no";


	// Table Name
	private static final String TABLE_GEO_JSON_POLYGON      = "GeoJsonPolygon";
	private static final String TABLE_GEO_JSON_POLYGON_FORM = "GeoJsonPolygonForm";
	private static final String TABLE_GEO_JSON_POLYGON_FORM_LOCAL = "GeoJsonPolygonFormLocal";
	private static final String TABLE_FORM_ID_GENERATE = "FormIDCount";
	private static final String TABLE_FORM_ID_GENERATE_LOCAL = "FormIDCountLocal";



//---------------------------------------------------------- Create Table Query -----------------------------------------------------------------------------------------------


	// GeoJson Polygon Data  ----------------------------------------------------------------------------
	public static final String CREATE_TABLE_GEO_JSON_POLYGON = "CREATE TABLE " + TABLE_GEO_JSON_POLYGON +"(id INTEGER PRIMARY KEY AUTOINCREMENT, gis_id VARCHAR(100), polygon_id VARCHAR(100), geojsonlatlong TEXT, polygon_status TEXT, ward_no TEXT)";
	public static final String DROP_TABLE_GEO_JSON_POLYGON   = "DROP TABLE "   + TABLE_GEO_JSON_POLYGON;
	public static final String DELETE_TABLE_GEO_JSON_POLYGON = "DELETE FROM "  + TABLE_GEO_JSON_POLYGON;
	public static final String GET_GEO_JSON_POLYGON          = "SELECT * FROM "+ TABLE_GEO_JSON_POLYGON;


	// GeoJson Polygon Form Data  ----------------------------------------------------------------------------
	public static final String CREATE_TABLE_GEO_JSON_POLYGON_FORM = "CREATE TABLE " + TABLE_GEO_JSON_POLYGON_FORM +"(id INTEGER PRIMARY KEY AUTOINCREMENT, polygon_id VARCHAR(100), geojsonform TEXT,isOnlineSave VARCHAR(10), file TEXT, camera TEXT)";
	public static final String DROP_TABLE_GEO_JSON_POLYGON_FORM   = "DROP TABLE "   + TABLE_GEO_JSON_POLYGON_FORM;
	public static final String DELETE_TABLE_GEO_JSON_POLYGON_FORM = "DELETE FROM "  + TABLE_GEO_JSON_POLYGON_FORM;
	public static final String GET_GEO_JSON_POLYGON_FORM          = "SELECT * FROM "+ TABLE_GEO_JSON_POLYGON_FORM;


	// GeoJson Polygon Form Local Data  ----------------------------------------------------------------------------
	public static final String CREATE_TABLE_GEO_JSON_POLYGON_FORM_LOCAL = "CREATE TABLE " + TABLE_GEO_JSON_POLYGON_FORM_LOCAL +"(id INTEGER PRIMARY KEY AUTOINCREMENT, polygon_id VARCHAR(100), geojsonform TEXT,isOnlineSave VARCHAR(10), file TEXT, camera TEXT)";
	public static final String DROP_TABLE_GEO_JSON_POLYGON_FORM_LOCAL   = "DROP TABLE "   + TABLE_GEO_JSON_POLYGON_FORM_LOCAL;
	public static final String DELETE_TABLE_GEO_JSON_POLYGON_FORM_LOCAL = "DELETE FROM "  + TABLE_GEO_JSON_POLYGON_FORM_LOCAL;
	public static final String GET_GEO_JSON_POLYGON_FORM_LOCAL          = "SELECT * FROM "+ TABLE_GEO_JSON_POLYGON_FORM_LOCAL;


	// Generate Form ID Counter  ----------------------------------------------------------------------------
	public static final String CREATE_TABLE_FORM_ID_GENERATE = "CREATE TABLE " + TABLE_FORM_ID_GENERATE +"(id INTEGER PRIMARY KEY AUTOINCREMENT, polygon_id VARCHAR(100) , last_id Text)";
	public static final String DROP_TABLE_FORM_ID_GENERATE   = "DROP TABLE "   + TABLE_FORM_ID_GENERATE;
	public static final String DELETE_TABLE_FORM_ID_GENERATE = "DELETE FROM "  + TABLE_FORM_ID_GENERATE;
	public static final String GET_TABLE_FORM_ID_GENERATE    = "SELECT * FROM "+ TABLE_FORM_ID_GENERATE;



	// Generate Form ID Counter  Local ----------------------------------------------------------------------------
	public static final String CREATE_TABLE_FORM_ID_GENERATE_LOCAL = "CREATE TABLE " + TABLE_FORM_ID_GENERATE_LOCAL +"(id INTEGER PRIMARY KEY AUTOINCREMENT, polygon_id VARCHAR(100) , last_id Text)";
	public static final String DROP_TABLE_FORM_ID_GENERATE_LOCAL   = "DROP TABLE "   + TABLE_FORM_ID_GENERATE_LOCAL;
	public static final String DELETE_TABLE_FORM_ID_GENERATE_LOCAL = "DELETE FROM "  + TABLE_FORM_ID_GENERATE_LOCAL;
	public static final String GET_TABLE_FORM_ID_GENERATE_LOCAL    = "SELECT * FROM "+ TABLE_FORM_ID_GENERATE_LOCAL;



//---------------------------------------------------------- Constructor ----------------------------------------------------------------------------------------------------------------------

	public DataBaseHelper(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
		ctx = c;
	}

//---------------------------------------------------------- onCreate ----------------------------------------------------------------------------------------------------------------

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_GEO_JSON_POLYGON);
		db.execSQL(CREATE_TABLE_GEO_JSON_POLYGON_FORM);
		db.execSQL(CREATE_TABLE_GEO_JSON_POLYGON_FORM_LOCAL);
		db.execSQL(CREATE_TABLE_FORM_ID_GENERATE);
		db.execSQL(CREATE_TABLE_FORM_ID_GENERATE_LOCAL);
	}

//---------------------------------------------------------- onUpgrade ----------------------------------------------------------------------------------------------------------------

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// DROP -----------------------
		db.execSQL(DROP_TABLE_GEO_JSON_POLYGON);
		db.execSQL(DROP_TABLE_GEO_JSON_POLYGON_FORM);
		db.execSQL(DROP_TABLE_FORM_ID_GENERATE);
		db.execSQL(DROP_TABLE_FORM_ID_GENERATE_LOCAL);


		// Insert ---------------------
		db.execSQL(CREATE_TABLE_GEO_JSON_POLYGON);
		db.execSQL(CREATE_TABLE_GEO_JSON_POLYGON_FORM);
		db.execSQL(CREATE_TABLE_GEO_JSON_POLYGON_FORM_LOCAL);
		db.execSQL(CREATE_TABLE_FORM_ID_GENERATE);
		db.execSQL(CREATE_TABLE_FORM_ID_GENERATE_LOCAL);
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

	//---------------------------------------------------------- Insert Form Id Generate -------------------------------------------------------

	public void insertGenerateID(String polygonID,String lastID){
		open();
		ContentValues cv = new ContentValues();
		cv.put(keyParamFormLastID,lastID);
		cv.put(keyParamPolygonID,polygonID);
		db.insert(TABLE_FORM_ID_GENERATE, null, cv);
	//	Log.e(TAG, "Insert into Generate ID -> "+ polygonID + " " + lastID);
		close();
	}


	public void insertGenerateIDLocal(String polygonID,String lastID){
		open();
		ContentValues cv = new ContentValues();
		cv.put(keyParamFormLastID,lastID);
		cv.put(keyParamPolygonID,polygonID);
		db.insert(TABLE_FORM_ID_GENERATE_LOCAL, null, cv);
	//	Log.e(TAG, "Insert into Generate ID Local -> "+ polygonID + " " + lastID);
		close();
	}

	//---------------------------------------------------------- Insert Geo Json Polygon -------------------------------------------------------

	public void insertGeoJsonPolygon(String gisID, String polygonID, String LatLong, String polygonStatus,String ward_no){
		open();
		ContentValues cv = new ContentValues();
		cv.put(keyParamGISID,gisID);
		cv.put(keyParamPolygonID,polygonID);
		cv.put(keyParamGeoJsonLatLon,LatLong);
		cv.put(keyParamPolygonStatus,polygonStatus);
		cv.put(keyParamWardNo,ward_no);
		db.insert(TABLE_GEO_JSON_POLYGON, null, cv);
		close();
	}

	//---------------------------------------------------------- Insert Geo Json Polygon Form -------------------------------------------------------

	public void insertGeoJsonPolygonForm(String polygonID, String Form,String isOnlineSave,String file, String camera){
		open();
		ContentValues cv = new ContentValues();
		cv.put(keyParamPolygonID,polygonID);
		cv.put(keyParamGeoJsonForm,Form);
		cv.put(keyParamIsOnlineSave,isOnlineSave);
		cv.put(keyParamFile,file);
		cv.put(keyParamCamera,camera);
		db.insert(TABLE_GEO_JSON_POLYGON_FORM, null, cv);
		close();
	}

//---------------------------------------------------------- Insert Geo Json Polygon Form Local -------------------------------------------------------

	public void insertGeoJsonPolygonFormLocal(String polygonID, String Form,String isOnlineSave,String file, String camera){
		open();
		ContentValues cv = new ContentValues();
		cv.put(keyParamPolygonID,polygonID);
		cv.put(keyParamGeoJsonForm,Form);
		cv.put(keyParamIsOnlineSave,isOnlineSave);
		cv.put(keyParamFile,file);
		cv.put(keyParamCamera,camera);
		db.insert(TABLE_GEO_JSON_POLYGON_FORM_LOCAL, null, cv);
		close();
	}

	public void updateGeoJsonPolygon(String polygonID, String polygonStatus){
		open();
		ContentValues cv = new ContentValues();
		String where = "polygon_id = ?";
		String[] whereArgs = { polygonID };
		cv.put(keyParamPolygonStatus,polygonStatus);
		db.update(TABLE_GEO_JSON_POLYGON, cv,where, whereArgs);
		close();
	}

	public void updateGenerateID(String polygonID, String lastID){
		open();
		ContentValues cv = new ContentValues();
		String where = "polygon_id = ?";
		String[] whereArgs = { polygonID };
		cv.put(keyParamFormLastID,lastID);
		db.update(TABLE_FORM_ID_GENERATE, cv,where, whereArgs);
	//	Log.e(TAG, "Update into Generate ID -> "+ polygonID + " " + lastID);

		close();
	}

	public void updateGenerateIDLocal(String polygonID, String lastID) {
		open();
		ContentValues cv = new ContentValues();
		String where = "polygon_id = ?";
		String[] whereArgs = {polygonID};
		cv.put(keyParamFormLastID, lastID);
		db.update(TABLE_FORM_ID_GENERATE_LOCAL, cv, where, whereArgs);
	//	Log.e(TAG, "Update into Generate ID Local-> "+ polygonID + " " + lastID);
		close();
	}

// ######################################################### Delete Query ####################################################################################################

	//---------------------------------------------------------- Delete Map Form Local ------------------------------------------------------------
	public void deleteMapFormLocalData(String id)
	{
		open();
		String whereClause = "id = ?";
		String[] whereArgs = { id };
		db.delete(TABLE_GEO_JSON_POLYGON_FORM_LOCAL, whereClause, whereArgs);
		close();
	}

	public void deleteGenerateID(String id)
	{
		open();
		String whereClause = "id = ?";
		String[] whereArgs = { id };
		db.delete(TABLE_FORM_ID_GENERATE, whereClause, whereArgs);
		close();
	}

	public void deleteGenerateIDLocal(String id)
	{
		open();
		String whereClause = "id = ?";
		String[] whereArgs = { id };
		db.delete(TABLE_FORM_ID_GENERATE_LOCAL, whereClause, whereArgs);
		Log.e(TAG,"Delete Key ->" + id);
		close();
	}
	//---------------------------------------------------------- Delete Geo Json Polygon Form Local ------------------------------------------------------------
	public void deleteGeoJsonPolygonFormLocalData(String id)
	{
		open();
		String whereClause = "id = ?";
		String[] whereArgs = { id };
		db.delete(TABLE_GEO_JSON_POLYGON_FORM_LOCAL, whereClause, whereArgs);
		close();
	}

// ######################################################### Select Query #######################################################################################################


//---------------------------------------------------------- Geo Json Polygon List ------------------------------------------------------------

	// GeoJson Polygon
	@SuppressLint("Range")
	public ArrayList<GeoJsonModel> getAllGeoJsonPolygon(){
		ArrayList<GeoJsonModel> list = new ArrayList<>();
		open();
		Cursor cv = executeCursor(GET_GEO_JSON_POLYGON);
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			for(int i=0; i<cv.getCount(); i++) {
				GeoJsonModel bin = new GeoJsonModel();
				bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
				bin.setPolygonID(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
				bin.setGisID(cv.getString(cv.getColumnIndex(keyParamGISID)));
				bin.setLatLon(cv.getString(cv.getColumnIndex(keyParamGeoJsonLatLon)));
				bin.setPolygonStatus(cv.getString(cv.getColumnIndex(keyParamPolygonStatus)));
				bin.setWardNo(cv.getString(cv.getColumnIndex(keyParamWardNo)));
				list.add(bin);
				cv.moveToNext();
			}
		}
		close();
		return list;
	}

	@SuppressLint("Range")
	public GeoJsonModel getPolygonByPolygonId(String polygonID){
		GeoJsonModel bin = new GeoJsonModel();
		open();
		Cursor cv = executeCursor("Select * From "+TABLE_GEO_JSON_POLYGON+" Where polygon_id ='"+polygonID+"'");
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
			bin.setPolygonID(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
			bin.setGisID(cv.getString(cv.getColumnIndex(keyParamGISID)));
			bin.setLatLon(cv.getString(cv.getColumnIndex(keyParamGeoJsonLatLon)));
			bin.setPolygonStatus(cv.getString(cv.getColumnIndex(keyParamPolygonStatus)));
			bin.setWardNo(cv.getString(cv.getColumnIndex(keyParamWardNo)));
		}
		close();
		return bin;
	}

//---------------------------------------------------------- Geo Json Polygon Form List ------------------------------------------------------------

	@SuppressLint("Range")
	public ArrayList<FormListModel> getFormIDByPolygonID(String polygonID) {
		ArrayList<FormListModel> list = new ArrayList<>();
		open();
		Cursor cv = executeCursor("Select * From "+TABLE_GEO_JSON_POLYGON_FORM+" Where polygon_id ='"+polygonID+"'");
		//Log.e(TAG, "DB Form Size -> "+ cv.getCount());
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			for(int i=0; i<cv.getCount(); i++) {
				FormListModel bin = new FormListModel();
				String form = cv.getString(cv.getColumnIndex(keyParamGeoJsonForm));

				//Log.e(TAG,"DB Form -> " + form);
				if(!Utility.isEmptyString(form)){
					FormModel formModel = Utility.convertStringToFormModel(form);
					//Log.e(TAG, "FFFF -> " + formModel.getForm().getFid());
					if(!Utility.isEmptyString(formModel.getForm().getForm_number())){
						bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
						bin.setFid(formModel.getForm().getFid());
						bin.setForm_number(formModel.getForm().getForm_number());
						bin.setFormModel(formModel);
						bin.setPolygon_id(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
						list.add(bin);
					}
				}
				else{
					bin.setFid("");
				}
				cv.moveToNext();
			}
		}
		close();
		return list;
	}

	@SuppressLint("Range")
	public FormDBModel getFormByPolygonIDAndID(String id) {
		FormDBModel bin = new FormDBModel();
		open();
		Cursor cv = executeCursor("Select * From "+TABLE_GEO_JSON_POLYGON_FORM+" Where id=" +id);
		Log.e(TAG,"Size File-> "+cv.getCount());
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
			bin.setPolygon_id(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
			bin.setFormData(cv.getString(cv.getColumnIndex(keyParamGeoJsonForm)));
			bin.setOnlineSave((cv.getString(cv.getColumnIndex(keyParamIsOnlineSave)).equals("t")));
			bin.setFilePath(cv.getString(cv.getColumnIndex(keyParamFile)));
			bin.setCameraPath(cv.getString(cv.getColumnIndex(keyParamCamera)));
			Log.e(TAG,"Form Open-> "+cv.getString(cv.getColumnIndex(keyParamGeoJsonForm)));
		}
		close();
		return bin;
	}

	@SuppressLint("Range")
	public ArrayList<FormDBModel> getAllForms() {
		ArrayList<FormDBModel> list = new ArrayList<>();
		open();
		Cursor cv = executeCursor(GET_GEO_JSON_POLYGON_FORM_LOCAL);
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			for(int i=0; i<cv.getCount(); i++) {
				FormDBModel bin = new FormDBModel();
				bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
				bin.setPolygon_id(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
				bin.setFormData(cv.getString(cv.getColumnIndex(keyParamGeoJsonForm)));
				bin.setOnlineSave((cv.getString(cv.getColumnIndex(keyParamIsOnlineSave)).equals("t")));
				bin.setFilePath(cv.getString(cv.getColumnIndex(keyParamFile)));
				bin.setCameraPath(cv.getString(cv.getColumnIndex(keyParamCamera)));
				list.add(bin);
				//Log.e(TAG, "Form Open-> " + cv.getString(cv.getColumnIndex(keyParamGeoJsonForm)));
				cv.moveToNext();
			}
		}
		close();
		return list;
	}


	//---------------------------------------------------------- Generate ID ------------------------------------------------------------
	@SuppressLint("Range")
	public String getGenerateID(String polygonID){
		String lastID = "";
		open();
		Cursor cv = executeCursor("Select * From "+TABLE_FORM_ID_GENERATE+" Where polygon_id ='"+polygonID+"'");
		//Log.e(TAG, "Generated ID DB Size-> " + cv.getCount());
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			lastID = cv.getString(cv.getColumnIndex(keyParamFormLastID));
		}
		close();
		return lastID;
	}

	@SuppressLint("Range")
	public String getGenerateIDLocal(String polygonID){
		String lastID = "";
		open();
		Cursor cv = executeCursor("Select * From "+TABLE_FORM_ID_GENERATE_LOCAL+" Where polygon_id ='"+polygonID+"'");
	//	Log.e(TAG, "Generated ID DB Size Local-> " + cv.getCount());
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			lastID = cv.getString(cv.getColumnIndex(keyParamFormLastID));
		}
		close();
		return lastID;
	}

	@SuppressLint("Range")
	public ArrayList<LastKeyModel> getAllGenerateID(){
		ArrayList<LastKeyModel> lastID = new ArrayList<>();
		open();
		Cursor cv = executeCursor(GET_TABLE_FORM_ID_GENERATE_LOCAL);
	//	Log.e(TAG,"Last key Size: " + cv.getCount());
		if(cv.getCount() > 0) {
			cv.moveToFirst();
			for(int i=0; i<cv.getCount(); i++) {
				LastKeyModel bin = new LastKeyModel();
				bin.setId(cv.getString(cv.getColumnIndex(keyParamID)));
				bin.setCounter(cv.getString(cv.getColumnIndex(keyParamFormLastID)));
				bin.setPoly_id(cv.getString(cv.getColumnIndex(keyParamPolygonID)));
				lastID.add(bin);
				//Log.e(TAG, "ID - >" + bin.getId() + " Last Key -> " + bin.getCounter() + " Polygon ID -> "+ bin.getPoly_id());
				cv.moveToNext();
			}
		}
		close();
		return lastID;
	}


// ######################################################### Logout ##############################################################################################################

	public void logout() {
		open();
		executeQuery(DELETE_TABLE_GEO_JSON_POLYGON);
		executeQuery(DELETE_TABLE_GEO_JSON_POLYGON_FORM);
		executeQuery(DELETE_TABLE_GEO_JSON_POLYGON_FORM_LOCAL);
		executeQuery(DELETE_TABLE_FORM_ID_GENERATE);
		executeQuery(DELETE_TABLE_FORM_ID_GENERATE_LOCAL);
		close();
	}

// ######################################################### CLear ##############################################################################################################

	public void clearAllDatabaseTable(){
		open();
		// New
		executeQuery(DELETE_TABLE_GEO_JSON_POLYGON);
		executeQuery(DELETE_TABLE_GEO_JSON_POLYGON_FORM);
		executeQuery(DELETE_TABLE_GEO_JSON_POLYGON_FORM_LOCAL);
		executeQuery(DELETE_TABLE_FORM_ID_GENERATE);
		executeQuery(DELETE_TABLE_FORM_ID_GENERATE_LOCAL);
		close();
	}

}