package com.eighttechprojects.propertytaxshirol.volly;

import com.android.volley.VolleyError;
import com.eighttechprojects.propertytaxshirol.volly.URL_Utility.ResponseCode;

public interface WSResponseInterface {

	void onSuccessResponse(ResponseCode responseCode, String response);
	void onErrorResponse(ResponseCode responseCode, VolleyError error);

}
