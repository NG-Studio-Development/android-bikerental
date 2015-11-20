package ru.prokatvros.veloprokat.model.requests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;
import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.model.db.Shift;

public class ShiftRequest extends StringRequest {
    private static final String TAG ="SHIFT_REQUEST";

    private static String PARAM_JSON_INVENTORY_ARR = "json_inventory_arr";

    private static String PARAM_JSON_SHIFT = "json_shift";

    private static String PARAM_ADMIN_ID = "admin_id";

    private Map<String, String> params;

    public ShiftRequest (int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    public ShiftRequest (int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    public static ShiftRequest intercedeShift(List<Inventory> inventoryList,  Point point, Admin admin, final PostResponseListener listener) {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        final String strJsonInventoryArr = gson.toJson(inventoryList);

        String url = ConstantsBikeRentalApp.URL_SERVER+"/inventory/commit/"+point.serverId;

        Log.d(TAG, "Url: " + url);

        Log.d(TAG, "Json post: " + strJsonInventoryArr);

        Log.d(TAG, "Admin ID: " + admin.serverId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_JSON_INVENTORY_ARR, strJsonInventoryArr);
        params.put(PARAM_ADMIN_ID, String.valueOf(admin.serverId) );

        return new ShiftRequest(Request.Method.POST, url, params, new Response.Listener<String>() {
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

    public static ShiftRequest requestDeliverShift(Shift shift, final PostResponseListener listener) {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final String strJsonShift = gson.toJson(shift);


        String url = ConstantsBikeRentalApp.URL_SERVER+"/shift/"+shift.serverId;

        Log.d(TAG, "Url: " + url);

        Log.d(TAG, "Json PUT: " + strJsonShift);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_JSON_SHIFT, strJsonShift);

        return new ShiftRequest(Request.Method.PUT, url, params, new Response.Listener<String>() {
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

    public static ShiftRequest requestAllAdminShift(Admin admin, final PostResponseListener listener) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/admin/shifts/"+admin.serverId;

        Log.d(TAG, "Url: " + url);

        Log.d(TAG, "Admin ID: " + admin.serverId);

        return new ShiftRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
    }

}
