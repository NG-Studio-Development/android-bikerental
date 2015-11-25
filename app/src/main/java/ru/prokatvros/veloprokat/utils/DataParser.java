package ru.prokatvros.veloprokat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.activeandroid.query.Delete;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.model.db.Breakdown;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.model.db.Tarif;
import ru.prokatvros.veloprokat.model.requests.BreakdowndInRentRequest;
import ru.prokatvros.veloprokat.model.requests.ClientRequest;
import ru.prokatvros.veloprokat.model.requests.InventoryRequest;
import ru.prokatvros.veloprokat.model.requests.PointRequest;
import ru.prokatvros.veloprokat.model.requests.RentRequest;

public class DataParser {

    private final static String TAG = "DATA_PARSER";

    private final static String PARAM_JSON_DATA = "json_data";

    static final int ON_START = 0;
    static final int ON_SUCCESS = 1;
    static final int ON_ERROR = 2;

    Context context;

    public static DataParser instance = null;

    public interface OnParseListener {
        void onStart();
        void onSuccess();
        void onError();
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (onParseListener == null)
                return;

            if (msg.what == ON_SUCCESS)
                onParseListener.onSuccess();
            else if(msg.what == ON_ERROR)
                onParseListener.onError();
        }
    };

    public static OnParseListener onParseListener;

    private DataParser() {}

    private DataParser(Context context) { this.context = context; }

    public static DataParser getInstance(Context context) {

        if (instance == null)
            instance = new DataParser(context);

        return instance;
    }

    public BreakdowndInRentRequest breakdowndRequest = BreakdowndInRentRequest.requestAllBreakdowns(new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            List<Breakdown> breakdownList = null;
            Log.d("REQ_BREAK_D", "Breakdown json: "+response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonResults = jsonObject.getJSONArray("results");
                breakdownList = gson.fromJson(jsonResults.toString(), new TypeToken<List<Breakdown>>() {
                }.getType());

            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            if (breakdownList != null)
                Breakdown.parse(breakdownList);

            handler.sendMessage(handler.obtainMessage(ON_SUCCESS));
            //Volley.newRequestQueue(context).add(requestGetRent);

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    });

    public PointRequest pointRequest = PointRequest.reqGetPoint(new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            List<Point> pointList = null;

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonResults = jsonObject.getJSONArray("results");
                pointList = gson.fromJson(jsonResults.toString(), new TypeToken<List<Point>>() {
                }.getType());

            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            if (pointList != null)
                Point.parse(pointList);
            Volley.newRequestQueue(context).add(requestGetRent);

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    });

    public ClientRequest clientRequest = ClientRequest.requestGetClient(new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            List<Client> clientList = null;

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonResults = jsonObject.getJSONArray("results");
                clientList = gson.fromJson(jsonResults.toString(), new TypeToken<List<Client>>() {
                }.getType());

            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            if (clientList != null)
                Client.parse(clientList);

            Volley.newRequestQueue(context).add(pointRequest);


        }

    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    });

    InventoryRequest requestGetInventory
            = InventoryRequest.requestGetInventory(new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            Gson gson = new Gson();
            List<Inventory> inventoryList = null;

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonResults = jsonObject.getJSONArray("results");
                inventoryList = gson.fromJson(jsonResults.toString(), new TypeToken<List<Inventory>>() {
                }.getType());

            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            if (inventoryList != null)
                Inventory.parse(inventoryList);


            Volley.newRequestQueue(context).add(clientRequest);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    });

    RentRequest requestGetRent = RentRequest.requestGetRent(new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        getRentResponse(response);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                });



    protected void getRentResponse(String response) {
        Gson gson = new Gson();
        List<Rent> rentListInventory = null;

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonResults = jsonObject.getJSONArray("results");
            rentListInventory = gson.fromJson(jsonResults.toString(), new TypeToken<List<Rent>>() {
            }.getType());

            rentListInventory.size();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        if (rentListInventory != null)
            Rent.parse(rentListInventory);


        Volley.newRequestQueue(context).add(breakdowndRequest);
    }

    public void parsing(OnParseListener onParseListener) {

        this.onParseListener = onParseListener;
        this.onParseListener.onStart();
        Volley.newRequestQueue(context).add(requestGetInventory);

    }


    public void loadDumpDB(OnParseListener onParseListener) {

        this.onParseListener = onParseListener;
        this.onParseListener.onStart();
        Volley.newRequestQueue(context).add(requestGetInventory);

    }

    public void clear() {
        SharedPreferences.Editor editorPref = BikerentalApplication.getInstance().getApplicationPreferencesEditor();
        editorPref.putString(PARAM_JSON_DATA, null);
        editorPref.commit();
    }

    public void clearDB() {
        new Delete().from(Inventory.class).execute();
        new Delete().from(Breakdown.class).execute();
        new Delete().from(Client.class).execute();
        new Delete().from(Tarif.class).execute();
        new Delete().from(Point.class).execute();
    }

    public String loadDataFromPool() {
        return BikerentalApplication.getInstance().getApplicationPreferences().getString(PARAM_JSON_DATA, "");
    }

    public void saveToPoolRentData(String strJsonRent) {
        String jsonDataStr = BikerentalApplication.getInstance().getApplicationPreferences().getString(PARAM_JSON_DATA, "");
        JSONObject data = null;

        try {

            if ( jsonDataStr.isEmpty() ) {

                data = new JSONObject();

                JSONObject rentJSONObject = new JSONObject(strJsonRent);

                JSONArray rentJSONArray = new JSONArray();
                rentJSONArray.put(rentJSONObject);

                data.put("rents", rentJSONArray );

            } else {

                data = new JSONObject(jsonDataStr);
                data = data.getJSONObject("data");

                JSONArray rentJSONArray = data.getJSONArray("rents");

                if (rentJSONArray == null)
                    rentJSONArray = new JSONArray();

                JSONObject rentJSONObject = new JSONObject(strJsonRent);
                rentJSONArray.put(rentJSONObject);
                data.put("rents", rentJSONArray );

            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", data);

            SharedPreferences.Editor editorPref = BikerentalApplication.getInstance().getApplicationPreferencesEditor();
            editorPref.putString(PARAM_JSON_DATA, jsonObject.toString());
            editorPref.commit();

            String save = BikerentalApplication.getInstance().getApplicationPreferences().getString(PARAM_JSON_DATA, "");

            Log.d(TAG, "Rent in data: "+save);

        } catch (JSONException ex) {
            ex.printStackTrace();
            Log.d(TAG, "Cath in add rent");
        }
    }

    public void saveToPoolClientData(String strJsonClient) {

        String jsonDataStr = BikerentalApplication.getInstance().getApplicationPreferences().getString(PARAM_JSON_DATA,"");
        JSONObject data = null;

        try {

            if (jsonDataStr.isEmpty()) {

                data = new JSONObject();

                JSONObject clientJSONObject = new JSONObject(strJsonClient);

                JSONArray clientJSONArray = new JSONArray();
                clientJSONArray.put(clientJSONObject);

                data.put("clients", clientJSONArray );

            } else {

                data = new JSONObject(jsonDataStr);

                JSONArray clientsJSONArray = data.getJSONArray("clients");

                if (clientsJSONArray == null)
                    clientsJSONArray = new JSONArray();

                JSONObject clientsJSONObject = new JSONObject(strJsonClient);

                clientsJSONArray.put(clientsJSONObject);

                data.put("clients", clientsJSONArray );
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", data);

            SharedPreferences.Editor editorPref = BikerentalApplication.getInstance().getApplicationPreferencesEditor();
            editorPref.putString(PARAM_JSON_DATA, jsonObject.toString());
            editorPref.commit();



        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

}