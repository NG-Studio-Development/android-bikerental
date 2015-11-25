package ru.prokatvros.veloprokat.model.requests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;

public class PointRequest extends StringRequest {

    public PointRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static PointRequest reqGetPoint(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ConstantsBikeRentalApp.URL_SERVER+"/points";


        return new PointRequest(Request.Method.GET, url, listener, errorListener);
    }
}
