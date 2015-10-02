package ru.prokatvros.veloprokat.model.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;

public class RentRequest extends StringRequest {

    private static String PARAM_JSON_RENT = "json_rent";
    private static String PARAM_ID_ADMIN = "id_admin";

    private Map<String, String> params;

    public RentRequest (int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    public static RentRequest requestPostRent( long adminId, String strJsonRent, final PostResponseListener listener ) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/"+"add_rent";

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_ID_ADMIN, String.valueOf(adminId));
        params.put(PARAM_JSON_RENT, strJsonRent);

        return new RentRequest(Request.Method.POST, url, params, new Response.Listener<String>() {
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


    public static RentRequest requestPutRent( long adminId, String strJsonRent, final PostResponseListener listener ) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/"+"rent";

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_ID_ADMIN, String.valueOf(adminId));
        params.put(PARAM_JSON_RENT, strJsonRent);

        return new RentRequest(Request.Method.PUT, url, params, new Response.Listener<String>() {
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
