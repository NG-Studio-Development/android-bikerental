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
import java.util.Map;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;

public class ClientRequest extends StringRequest {

    private static final String TAG ="CLIENT_REQUEST";

    private static String PARAM_JSON_CLIENT = "json_client";

    public static int CODE_ERROR_DUPLICATE_POST_CLIENT = 1;
    public static int CODE_NOT_ERROR_POST_CLIENT = 0;


    private Map<String, String> params;

    public ClientRequest (int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(method, url, listener, errorListener);
        this.params = params;
    }


    private static int[] text_error_post_client = new int[2];

    public static String errorMessagePostClient(int code) {
        return BikerentalApplication.getInstance().getString(text_error_post_client[code]);
    }

    public static ClientRequest requestPostClient(Client client,  final PostResponseListener listener) {


        text_error_post_client[CODE_ERROR_DUPLICATE_POST_CLIENT] = R.string.warning_duplicate_client;
        text_error_post_client[CODE_NOT_ERROR_POST_CLIENT] = R.string.warning_duplicate_client;

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final String strJsonClient = gson.toJson(client);


        Log.d(TAG, "Json: " + strJsonClient);

        String url = ConstantsBikeRentalApp.URL_SERVER+"/"+"add_client";

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_JSON_CLIENT, strJsonClient);

        return new ClientRequest(Request.Method.POST, url, params, new Response.Listener<String>() {
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
