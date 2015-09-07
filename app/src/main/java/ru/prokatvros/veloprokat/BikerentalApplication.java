package ru.prokatvros.veloprokat;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import ru.prokatvros.veloprokat.model.db.Breakdown;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.model.requests.InventoryRequest;

public class BikerentalApplication extends android.app.Application {

    private volatile static BikerentalApplication instance;

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
                Log.d("REQUEST_ALL_INVENTORY", "onError");
                Toast.makeText(BikerentalApplication.this, "ERROR IN requestAllInventory", Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this).add(request);
    }

}
