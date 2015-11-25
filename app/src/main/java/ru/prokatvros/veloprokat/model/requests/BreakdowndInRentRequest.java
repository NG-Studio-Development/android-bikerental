package ru.prokatvros.veloprokat.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;

public class BreakdowndInRentRequest extends StringRequest {

    private final static String TAG = "BREAK_RNT_REQ";

    public BreakdowndInRentRequest (int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static BreakdowndInRentRequest requestAllBreakdowns(Response.Listener<String> listener, Response.ErrorListener errorListener) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/breakdown";
        Log.d(TAG, "Request all mess = " + url);
        return  new BreakdowndInRentRequest(Request.Method.GET, url, listener, errorListener);
    }


}
