package ru.prokatvros.veloprokat.model.requests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;

public class LoadAllDataRequest extends StringRequest {

    private static String PARAM_JSON_DATA = "data";

    private Map<String, String> params;

    public LoadAllDataRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }
    public LoadAllDataRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    public static LoadAllDataRequest requestCollectData(/*boolean employment,*/ Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ConstantsBikeRentalApp.URL_SERVER+"/data/collect";
        Log.d("REQUEST_SET_EMPLOYMENT", "Url requestCollectData(): " + url);
        return new LoadAllDataRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static JsonObjectRequest jsonRequestAllData( Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = ConstantsBikeRentalApp.URL_SERVER+"/files/results.json";
        Log.d("REQUEST_SET_EMPLOYMENT", "Url JsonObjectRequest() : " + url);
        return new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
    }

    public static LoadAllDataRequest saveRequestAllData( String jsonData, final PostResponseListener listener ) {
        String url = ConstantsBikeRentalApp.URL_SERVER+"/data/load";

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_JSON_DATA, jsonData);

        Log.d("REQUEST_SET_EMPLOYMENT", "Url JsonObjectRequest() : " + url);

        return new LoadAllDataRequest(Request.Method.POST, url, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/x-www-form-urlencoded");
        return params;
    }

}
