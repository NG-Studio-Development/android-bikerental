package ru.prokatvros.veloprokat.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;

public class InventoryRequest extends StringRequest {

    public InventoryRequest (int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static InventoryRequest requestAllInventory(String point, /*boolean employment,*/ Response.Listener<String> listener, Response.ErrorListener errorListener) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/inventory/1";
        Log.d("REQUEST_SET_EMPLOYMENT", "Request = " + url);
        return  new InventoryRequest(Request.Method.GET, url, listener, errorListener);
    }

}