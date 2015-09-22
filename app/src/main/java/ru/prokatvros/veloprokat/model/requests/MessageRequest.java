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

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;
import ru.prokatvros.veloprokat.model.db.Message;

public class MessageRequest extends StringRequest {
    private static final String TAG = "MESSAGE_REQUEST";

    private static String PARAM_JSON_DATA = "data";

    private Map<String, String> params;

    public MessageRequest (int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public MessageRequest (int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    public static MessageRequest requestAllMessages(Response.Listener<String> listener, Response.ErrorListener errorListener) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/messages";
        Log.d(TAG, "Request all mess = " + url);
        return  new MessageRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static MessageRequest requestPostMessage(  Message message, final PostResponseListener listener) {

        String url = ConstantsBikeRentalApp.URL_SERVER+"/message";

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonData = gson.toJson(message);

        Log.d(TAG, "Json data post mess = " + jsonData);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_JSON_DATA, jsonData);

        Log.d(TAG, "Request post mess = " + url);
        return  new MessageRequest(Method.POST, url, params, new Response.Listener<String>() {
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
