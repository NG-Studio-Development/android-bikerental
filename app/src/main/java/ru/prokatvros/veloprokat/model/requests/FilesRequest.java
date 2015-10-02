package ru.prokatvros.veloprokat.model.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;

public class FilesRequest extends StringRequest {

    private final static String TAG = "FILES_REQUEST";

    private final static String PARAM_BASE_64 = "base_64";

    private Map<String, String> params;

    public FilesRequest (int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    public static FilesRequest requestPostImage(String base64,  final PostResponseListener listener) {



        String url = ConstantsBikeRentalApp.URL_SERVER+"/"+"add_image";

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_BASE_64, base64);

        return new FilesRequest(Request.Method.POST, url, params, new Response.Listener<String>() {
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
