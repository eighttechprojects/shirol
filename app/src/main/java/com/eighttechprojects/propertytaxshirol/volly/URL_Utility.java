package com.eighttechprojects.propertytaxshirol.volly;

public class URL_Utility {

	// Response
	public static final String STATUS           = "status";
	public static final String STATUS_SUCCESS   = "Success";
	public static final String STATUS_DUPLICATE = "Duplicate";
	public static final String STATUS_FAIL      = "fail";
	public static final String SAVE_SUCCESSFULLY = "Save Successfully";
	// App Version
	public static final String APP_VERSION = "1.0";
	public static final String COMMON_API = "https://surveybaba.com/propertyform-shirol/api/";

// ########################################## COMMON PARAM ###########################################################################################################################

	// Common PARAM
	public static final String PARAM_USED_ID          = "user_id";
	public static final String PARAM_VERSION          = "version";
	public static final String PARAM_USERNAME 	      = "username";
	public static final String PARAM_PASSWORD         = "password";
	public static final String PARAM_NEW_PASSWORD     = "new_password";
	public static final String PARAM_CURRENT_PASSWORD = "current_password";
	public static final String PARAM_FIRST_NAME       = "first_name";
	public static final String PARAM_LAST_NAME        = "last_name";
	public static final String PARAM_EMAIL_ID         = "email";
	public static final String PARAM_MOBILE_NUMBER    = "mobile_number";
	public static final String PARAM_DATETIME         = "datetime";
	public static final String PARAM_LATITUDE         = "latitude";
	public static final String PARAM_LONGITUDE        = "longitude";
	public static final String PARAM_LOGIN_TOKEN      = "login_token";
	public static final String PARAM_UNIQUE_NUMBER    = "unique_number";


// ########################################## NEW API ###########################################################################################################################

	// Login API
	public static final String WS_LOGIN           = COMMON_API + "login";
	// Forgot Password API
	public static final String WS_FORGOT_PASSWORD = COMMON_API + "forgot-password";
	// Form Upload
	public static final String WS_FORM            = COMMON_API + "save-form";
	// Resurvey
	public static final String WS_RESURVEY_FORM   = COMMON_API + "login";


// ########################################## Response Code ####################################################################################################################

	public enum ResponseCode {
		WS_LOGIN,
		WS_FORGOT_PASSWORD,
		WS_FORM,
		WS_RESURVEY_FORM
	}

}