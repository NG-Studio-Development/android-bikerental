package ru.prokatvros.veloprokat.model.requests;

import com.android.volley.VolleyError;

public interface PostResponseListener {
        void onResponse(String response);
        void onErrorResponse(VolleyError error);
    }