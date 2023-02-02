package com.eighttechprojects.propertytaxshirol.volly;

import static com.eighttechprojects.propertytaxshirol.Service.SyncService.START_SYNC;
import static com.eighttechprojects.propertytaxshirol.Service.SyncService.STOP_SYNC;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eighttechprojects.propertytaxshirol.Service.SyncService;
import com.eighttechprojects.propertytaxshirol.volly.URL_Utility.ResponseCode;
import java.util.HashMap;
import java.util.Map;

public class BaseApplication extends MultiDexApplication {

	public AppLifecycleObserver appLifecycleObserver;

	private static BaseApplication sInstance;
	private static RequestQueue mVolleyQueue;
	private static final int SOCKET_TIME_OUT = 600000; // 10 minutes

//------------------------------------------------- onCreate --------------------------------------------------------------------------------------------------------------------------

	@Override
	public void onCreate() {
		super.onCreate();
		initVolley(getApplicationContext());
		appLifecycleObserver = new AppLifecycleObserver();
		ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);

	}

//------------------------------------------------- initVolley ------------------------------------------------------------------------------------------------------------------------

	private void initVolley(Context context) {
		sInstance = this;
		mVolleyQueue = Volley.newRequestQueue(context);
	}

//------------------------------------------------- getInstance -----------------------------------------------------------------------------------------------------------------------

    public static synchronized BaseApplication getInstance() {
        return sInstance;
    }

//################################################# Post Request ######################################################################################################################

//------------------------------------------------- makeHttpPostRequest ---------------------------------------------------------------------------------------------------------------

	public void makeHttpPostRequest(Context activity, ResponseCode responseCode, String url, Map<String, String> params, boolean showInternetError, boolean shouldCache) {
		final WSResponseInterface responseInterface;
		if (activity instanceof WSResponseInterface) {
			responseInterface = (WSResponseInterface) activity;
		} else {
			throw new ClassCastException(activity.getClass().getSimpleName() + " must implement the WSResponseInterface");
		}
		Log.e("http", "responseCode: "+responseCode);
		Log.e("http", "url: "+url);
		//Log.e("http", "params: "+new Gson().toJson(params));
		makePostRequest(responseInterface, responseCode, url, params, showInternetError, shouldCache);
	}

//------------------------------------------------- makeHttpPostRequest ---------------------------------------------------------------------------------------------------------------

	public void makeHttpPostRequest(Activity activity, ResponseCode responseCode, String url, Map<String, String> params, boolean showInternetError, boolean shouldCache) {
		final WSResponseInterface responseInterface;
		if (activity instanceof WSResponseInterface) {
			responseInterface = (WSResponseInterface) activity;
		} else {
			throw new ClassCastException(activity.getLocalClassName() + " must implement the WSResponseInterface");
		}
		Log.e("http", "responseCode: "+responseCode);
		Log.e("http", "url: "+url);
		//Log.e("http", "params: "+new Gson().toJson(params));
		makePostRequest(responseInterface, responseCode, url, params, showInternetError, shouldCache);
	}

//------------------------------------------------- makeHttpPostRequest ---------------------------------------------------------------------------------------------------------------

	public void makeHttpPostRequest(Fragment fragment, ResponseCode responseCode, String url, Map<String, String> params, boolean showInternetError, boolean shouldCache) {
		final WSResponseInterface responseInterface;
		if (fragment instanceof WSResponseInterface) {
			responseInterface = (WSResponseInterface) fragment;
		} else {
			throw new ClassCastException(fragment.getClass().getName() + " must implement the WSResponseInterface");
		}
		makePostRequest(responseInterface, responseCode, url, params, showInternetError, shouldCache);
	}

//------------------------------------------------- makePostRequest -------------------------------------------------------------------------------------------------------------------

	// post Code to server
	public void makePostRequest(final WSResponseInterface responseInterface, final ResponseCode responseCode, final String url, final Map<String, String> params, final boolean showInternetError, final boolean shouldCache) {

		StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> responseInterface.onSuccessResponse(responseCode, response), error -> responseInterface.onErrorResponse(responseCode, error)){
			@Override
			protected Map<String, String> getParams(){
				return params;
			}

			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");
				return params;
			}
		};

		RetryPolicy policy = new DefaultRetryPolicy(SOCKET_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		stringRequest.setRetryPolicy(policy);
		stringRequest.setShouldCache(shouldCache);
		mVolleyQueue.add(stringRequest);
	}


//################################################# Get Request #######################################################################################################################

//------------------------------------------------- makeHttpGetRequest ------------------------------------------------------------------------------------------------------------------------

	public void makeHttpGetRequest(Activity activity, ResponseCode responseCode, String url, Map<String, String> params, boolean showInternetError, boolean shouldCache) {
		final WSResponseInterface responseInterface;
		if (activity instanceof WSResponseInterface) {
			responseInterface = (WSResponseInterface) activity;
		} else {
			throw new ClassCastException(activity.getLocalClassName() + " must implement the WSResponseInterface");
		}
		makeGetRequest(responseInterface, responseCode, url, params, showInternetError, shouldCache);
	}

//------------------------------------------------- makeHttpGetRequest ----------------------------------------------------------------------------------------------------------------

	public void makeHttpGetRequest(Fragment fragment, ResponseCode responseCode, String url, Map<String, String> params, boolean showInternetError, boolean shouldCache) {
		final WSResponseInterface responseInterface;
		if (fragment instanceof WSResponseInterface) {
			responseInterface = (WSResponseInterface) fragment;
		} else {
			throw new ClassCastException(fragment.getClass().getName() + " must implement the WSResponseInterface");
		}
		makeGetRequest(responseInterface, responseCode, url, params, showInternetError, shouldCache);
	}

//------------------------------------------------- makeGetRequest --------------------------------------------------------------------------------------------------------------------

	public void makeGetRequest(final WSResponseInterface responseInterface, final ResponseCode responseCode, final String url, final Map<String, String> params, final boolean showInternetError, final boolean shouldCache) {
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> responseInterface.onSuccessResponse(responseCode, response), error -> responseInterface.onErrorResponse(responseCode, error)){
			@Override
			protected Map<String, String> getParams(){
				return params;
			}

			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");
				return params;
			}
		};

		RetryPolicy policy = new DefaultRetryPolicy(SOCKET_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		stringRequest.setRetryPolicy(policy);
		stringRequest.setShouldCache(shouldCache);
		mVolleyQueue.add(stringRequest);
	}


//------------------------------------------------------- Sync Service ----------------------------------------------------------------------------------------------------------------

	public boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer
				.MAX_VALUE)) {
			if (service != null && serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void startSyncService() {
		if(!isMyServiceRunning(SyncService.class)) {
			Intent intent = new Intent(this, SyncService.class);
			intent.setAction(START_SYNC);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				startForegroundService(intent);
			} else {
				startService(intent);
			}
		}
	}

}
