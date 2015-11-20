package ru.prokatvros.veloprokat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.db.Breakdown;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.model.db.Shift;
import ru.prokatvros.veloprokat.model.db.Tarif;
import ru.prokatvros.veloprokat.utils.DataParser;
import ru.prokatvros.veloprokat.utils.FileUtils;

//import net.danlew.android.joda.JodaTimeAndroid;

public class BikerentalApplication extends android.app.Application {

    private volatile static BikerentalApplication instance;

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

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        ImageLoader.getInstance().handleSlowNetwork(true);

        Configuration.Builder configurationBuilder = new Configuration.Builder(this);

        configurationBuilder.addModelClasses( Admin.class, Breakdown.class, Client.class,
                                            Inventory.class, Point.class, Rent.class, Tarif.class,
                                            Shift.class,
                                            ru.prokatvros.veloprokat.model.db.Message.class );


        //ActiveAndroid.initialize(this);
        ActiveAndroid.initialize(configurationBuilder.create());

        FileUtils.init(this);
        JodaTimeAndroid.init(this);

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

        //Rent.removeEmpty();
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

    public void logout() {
        /*getApplicationPreferencesEditor().clear();
        getApplicationPreferencesEditor().commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);*/

    }

    /* @Deprecated
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
    } */

    public Admin getAdmin() {
        long adminId = getApplicationPreferences().getLong(ConstantsBikeRentalApp.PREFERENCE_ID_ADMIN, -1);

        if (adminId == -1)
            throw new Error("Not found admin, please check registration of admin !!!");

        return Admin.load(Admin.class, adminId);
    }

    public void setAdmin(Admin admin) {
        SharedPreferences.Editor  editor = getApplicationPreferencesEditor();
        editor.putLong(ConstantsBikeRentalApp.PREFERENCE_ID_ADMIN, admin.getId());
        editor.commit();
    }

    public void setPoint(Point point) {
        SharedPreferences.Editor  editor = getApplicationPreferencesEditor();
        editor.putLong(ConstantsBikeRentalApp.PREFERENCE_ID_POINT, point.getId());
        editor.commit();
    }

    public void setShift(Shift shift) {
        SharedPreferences.Editor  editor = getApplicationPreferencesEditor();
        editor.putLong(ConstantsBikeRentalApp.PREFERENCE_ID_SHIFT, shift.getId());
        editor.commit();
    }

    public Shift getShift() {
        long shiftId = getApplicationPreferences().getLong(ConstantsBikeRentalApp.PREFERENCE_ID_SHIFT, -1);

        if (shiftId == -1)
            throw new Error("Not found admin, please check registration of admin !!!");

        return Shift.load(Shift.class, shiftId);
    }

    public Point getPoint() {
        long pointId = getApplicationPreferences().getLong(ConstantsBikeRentalApp.PREFERENCE_ID_POINT, -1);

        if (pointId == -1)
            throw new Error("Not found admin, please check registration of admin !!!");

        return Admin.load(Point.class, pointId);
    }


    public String getUUID() {
        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }

    public DataParser getDataParser() {
        return DataParser.getInstance(this);
    }

    public interface NetworkAvailableListener {
        void onResponse(boolean isAvailable);
    }

    NetworkAvailableListener availableListener = null;
    private final int ON_ERROR = 0;
    private final int ON_SUCCESS = 1;

    Handler serverAvialableHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (availableListener == null)
                return;

                availableListener.onResponse( msg.what == ON_SUCCESS );

        }
    };

    public void isNetworkAvailable( final NetworkAvailableListener listener ) {
        availableListener = listener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //boolean isAvailable = false;
                try {
                    URL url = new URL(ConstantsBikeRentalApp.URL_SERVER+"/");
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();

                    if (conn.getResponseCode() == 200)
                        serverAvialableHandler.sendMessage( serverAvialableHandler.obtainMessage(ON_SUCCESS) );
                    else
                        serverAvialableHandler.sendMessage( serverAvialableHandler.obtainMessage(ON_ERROR) );
                    //isAvailable = true;


                } catch (IOException ex) {
                    ex.printStackTrace();
                    serverAvialableHandler.sendMessage( serverAvialableHandler.obtainMessage(ON_ERROR) );
                        //listener.onError();
                }


                //listener.onResponse(isAvailable);

            }
        }).start();
    }



}
