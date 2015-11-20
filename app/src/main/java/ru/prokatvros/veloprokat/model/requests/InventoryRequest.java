package ru.prokatvros.veloprokat.model.requests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;
import ru.prokatvros.veloprokat.model.db.Inventory;

public class InventoryRequest extends StringRequest {

    private static final String PARAM_INVENTORY = "inventory";
    private Map<String, String> params;

    public InventoryRequest (int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public InventoryRequest (int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    public static InventoryRequest requestAllInventory(String point, /*boolean employment,*/ Response.Listener<String> listener, Response.ErrorListener errorListener) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/inventory/1";
        Log.d("REQUEST_SET_EMPLOYMENT", "Request = " + url);
        return  new InventoryRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static InventoryRequest requestPutInventory(Inventory inventory, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/inventory";///"+inventory.serverId;
        Log.d("REQUEST_SET_EMPLOYMENT", "Request = " + url);
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_INVENTORY, inventory.toJsonString());
        return  new InventoryRequest(Request.Method.PUT, url, params, listener, errorListener);
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