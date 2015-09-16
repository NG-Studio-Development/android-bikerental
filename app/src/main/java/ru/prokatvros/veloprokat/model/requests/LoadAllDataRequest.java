package ru.prokatvros.veloprokat.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;

public class LoadAllDataRequest extends StringRequest {

    public LoadAllDataRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
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


}
