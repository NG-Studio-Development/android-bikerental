package ru.prokatvros.veloprokat;

import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.db.Breakdown;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.model.db.Tarif;
import ru.prokatvros.veloprokat.model.requests.InventoryRequest;
import ru.prokatvros.veloprokat.model.requests.LoadAllDataRequest;

public class BikerentalApplication extends android.app.Application {

    private volatile static BikerentalApplication instance;

    private volatile static DataParser dataParser;

    private SharedPreferences applicationPreferences;

    public SharedPreferences getApplicationPreferences() {
        return applicationPreferences;
    }

    private SharedPreferences.Editor applicationPreferencesEditor;

    public SharedPreferences.Editor getApplicationPreferencesEditor() {
        return applicationPreferencesEditor;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);

        synchronized (BikerentalApplication.class) {
            if (BuildConfig.DEBUG) {
                if (instance != null)
                    throw new RuntimeException("Something strange: there is another application instance.");
            }
            instance = this;

            BikerentalApplication.class.notifyAll();
        }

        //loadDataFromWeb();


        applicationPreferences = getSharedPreferences(ConstantsBikeRentalApp.SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        applicationPreferencesEditor = applicationPreferences.edit();

        Rent.removeEmpty();
    }


    @SuppressWarnings( { "ConstantConditions", "unchecked" } )
    public static BikerentalApplication getInstance() {
        BikerentalApplication application = instance;
        if (application == null) {
            synchronized (BikerentalApplication.class) {
                if (instance == null) {
                    if (BuildConfig.DEBUG) {
                        if (Thread.currentThread() == Looper.getMainLooper().getThread())
                            throw new UnsupportedOperationException(
                                    "Current application's instance has not been initialized yet (wait for onCreate, please).");
                    }
                    try {
                        do {
                            BikerentalApplication.class.wait();
                        } while ((application = instance) == null);
                    } catch (InterruptedException e) {
                        /* Nothing to do */
                    }
                }
            }
        }
        return application;
    }

    @Deprecated
    public void loadDataFromWeb() {

        Breakdown.initListForDEBUG();

        InventoryRequest request = InventoryRequest.requestAllInventory("point", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REQUEST_ALL_INVENTORY", "Response: " + response);


                try {

                    byte bytes[] = response.getBytes("WINDOWS-1251");
                    response = new String(bytes, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("inventories");

                    List<Inventory> inventoryList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<Inventory>>() {
                    }.getType());


                    ActiveAndroid.beginTransaction();
                    try {
                        for (Inventory inventory : inventoryList) {
                            inventory.save();
                        }

                        ActiveAndroid.setTransactionSuccessful();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                    }

                } catch (JSONException | UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("REQUEST_ALL_INVENTORY", "onErrorResponse");
                Toast.makeText(BikerentalApplication.this, "ERROR IN requestAllInventory", Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this).add(request);
    }

    public Admin getAdmin() {
        long adminId = getApplicationPreferences().getLong(ConstantsBikeRentalApp.PREFERENCE_ID_ADMIN, -1);

        if (adminId == -1)
            throw new Error("Not found admin, please check registration of admin");

        return Admin.load(Admin.class, adminId);
    }

    public void setAdmin(Admin admin) {
        SharedPreferences.Editor  editor = getApplicationPreferencesEditor();
        editor.putLong(ConstantsBikeRentalApp.PREFERENCE_ID_ADMIN, admin.getId());
        editor.commit();
    }


    public DataParser getDataParser() {
        //if ( dataParser == null )
            //dataParser = new DataParser();
        return dataParser = new DataParser();
    }


    public interface OnParseListener {
        void onStart();
        void onSuccess();
        void onError();
    }

    public class DataParser {

        OnParseListener onParseListener;


        private LoadAllDataRequest requestCollectData
                = LoadAllDataRequest.requestCollectData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(BikerentalApplication.this, "IN RESPONSE", Toast.LENGTH_LONG).show();

                Volley.newRequestQueue(BikerentalApplication.this).add(jsonRequestAllData);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //getProgressDialog().hide();
                onParseListener.onError();
                Toast.makeText(BikerentalApplication.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
                onParseListener.onError();
                Toast.makeText(BikerentalApplication.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });



        public void parsing(OnParseListener onParseListener) {
            this.onParseListener = onParseListener;
            this.onParseListener.onStart();
            Volley.newRequestQueue(BikerentalApplication.this).add(requestCollectData);
        }

        protected void saveToDataBase(List<? extends Model>... lists) {
            ActiveAndroid.beginTransaction();
            try {

                for (List<? extends Model> list : lists) {
                    for (Model model : list) {
                        model.save();
                    }
                }
                ActiveAndroid.setTransactionSuccessful();
                onParseListener.onSuccess();
            }
            finally {
                ActiveAndroid.endTransaction();
            }

        }

        protected void result(final JSONObject response) {



            //new Thread(new Runnable() {
                //@Override
                //public void run() {
                    Gson gson = new Gson();
                    try {
                        JSONArray jsonClientsArray = response.getJSONObject("data").getJSONArray("clients");
                        JSONArray jsonTarifsArray = response.getJSONObject("data").getJSONArray("tarifs");
                        JSONArray jsonInventoriesArray = response.getJSONObject("data").getJSONArray("inventories");
                        JSONArray jsonPointsArray = response.getJSONObject("data").getJSONArray("points");

                        List<Client> clientList = gson.fromJson(jsonClientsArray.toString(), new TypeToken<List<Client>>() {
                        }.getType());

                        List<Tarif> tarifsList = gson.fromJson(jsonTarifsArray.toString(), new TypeToken<List<Tarif>>() {
                        }.getType());

                        List<Inventory> inventoriesList = gson.fromJson(jsonInventoriesArray.toString(), new TypeToken<List<Inventory>>() {
                        }.getType());

                        List<Point> pointsList = gson.fromJson(jsonPointsArray.toString(), new TypeToken<List<Point>>() {
                        }.getType());

                        saveToDataBase(clientList, tarifsList, inventoriesList, pointsList);

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        onParseListener.onError();
                    }
                //}
            //}).start();

        }
    }
}
