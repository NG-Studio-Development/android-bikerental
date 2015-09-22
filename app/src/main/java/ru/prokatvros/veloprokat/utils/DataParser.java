package ru.prokatvros.veloprokat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import ru.prokatvros.veloprokat.model.requests.LoadAllDataRequest;

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

    private LoadAllDataRequest requestCollectData
            = LoadAllDataRequest.requestCollectData(new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Toast.makeText(context, "IN RESPONSE", Toast.LENGTH_LONG).show();

            Volley.newRequestQueue(context).add(jsonRequestAllData);

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            handler.sendMessage( handler.obtainMessage(ON_ERROR) );
            Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            error.printStackTrace();
        }
    });

    JsonObjectRequest jsonRequestAllData = LoadAllDataRequest.jsonRequestAllData(new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            result(response);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            handler.sendMessage( handler.obtainMessage(ON_ERROR) );
            Toast.makeText(context , "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            error.printStackTrace();
        }
    });



    public void parsing(OnParseListener onParseListener) {
        this.onParseListener = onParseListener;
        this.onParseListener.onStart();
        Volley.newRequestQueue(context).add(requestCollectData);
    }

    /*@Deprecated
    protected void saveToDataBase(List<? extends Model>... lists) {
        ActiveAndroid.beginTransaction();
        try {

            for (List<? extends Model> list : lists) {
                for (Model model : list) {
                    model.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
            handler.sendMessage(handler.obtainMessage(ON_SUCCESS) );
        }
        finally {
            ActiveAndroid.endTransaction();
        }

    } */

    protected void saveToDataBase( List<Client> clientList,
                                  List<Tarif> tarifsList,
                                  List<Inventory> inventoriesList,
                                  List<Point> pointsList,
                                  List<Breakdown> breakdownList,
                                  List<Rent> rentList ) {






        ActiveAndroid.beginTransaction();
        try {

            for(Client client : clientList ) {
                client.save();
            }

            for(Tarif tarif : tarifsList) {
                tarif.save();
            }

            for (Inventory inventory : inventoriesList) {
                inventory.save();
            }

            for (Point point : pointsList) {
                point.save();
            }

            for (Breakdown breakdown : breakdownList) {
                breakdown.save();
            }



            ActiveAndroid.setTransactionSuccessful();

        }
        finally {
            ActiveAndroid.endTransaction();
        }

        ActiveAndroid.beginTransaction();
        try {
            for (Rent rent : rentList) {
                //rent.inventory.save();
                //rent.client.save();
                rent.puckFields();
                rent.save();
            }
            ActiveAndroid.setTransactionSuccessful();
            handler.sendMessage(handler.obtainMessage(ON_SUCCESS) );
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    protected void result(final JSONObject response) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendMessage( handler.obtainMessage(ON_START) );
                Gson gson = new Gson();
                try {
                    JSONArray jsonClientsArray = response.getJSONObject("data").getJSONArray("clients");
                    JSONArray jsonTarifsArray = response.getJSONObject("data").getJSONArray("tarifs");
                    JSONArray jsonInventoriesArray = response.getJSONObject("data").getJSONArray("inventories");
                    JSONArray jsonPointsArray = response.getJSONObject("data").getJSONArray("points");
                    JSONArray jsonBreakdownArray = response.getJSONObject("data").getJSONArray("breakdown");
                    JSONArray jsonRentArray = response.getJSONObject("data").getJSONArray("rents");

                    Log.d(TAG,jsonInventoriesArray.toString());

                    List<Client> clientList = gson.fromJson(jsonClientsArray.toString(), new TypeToken<List<Client>>() {
                    }.getType());

                    List<Tarif> tarifsList = gson.fromJson(jsonTarifsArray.toString(), new TypeToken<List<Tarif>>() {
                    }.getType());

                    List<Inventory> inventoriesList = gson.fromJson(jsonInventoriesArray.toString(), new TypeToken<List<Inventory>>() {
                    }.getType());

                    List<Point> pointsList = gson.fromJson(jsonPointsArray.toString(), new TypeToken<List<Point>>() {
                    }.getType());

                    List<Breakdown> breakdownList = gson.fromJson(jsonBreakdownArray.toString(), new TypeToken<List<Breakdown>>() {
                    }.getType());

                    List<Rent> rentList = gson.fromJson(jsonRentArray.toString(), new TypeToken<List<Rent>>() {
                    }.getType());


                    saveToDataBase(clientList, tarifsList, inventoriesList, pointsList, breakdownList, rentList);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                    handler.sendMessage( handler.obtainMessage(ON_ERROR) );
                }
            }
        }).start();

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
        return BikerentalApplication.getInstance().getApplicationPreferences().getString(PARAM_JSON_DATA,"");
    }

    public void saveToPoolRentData(String strJsonRent) {
        String jsonDataStr = BikerentalApplication.getInstance().getApplicationPreferences().getString(PARAM_JSON_DATA,"");
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

            String save = BikerentalApplication.getInstance().getApplicationPreferences().getString(PARAM_JSON_DATA,"");

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