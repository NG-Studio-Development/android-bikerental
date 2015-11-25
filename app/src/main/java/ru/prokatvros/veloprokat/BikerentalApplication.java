package ru.prokatvros.veloprokat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import ru.prokatvros.veloprokat.net.InputStreamVolleyRequest;
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


    protected void initActiveAndroid() {
        Configuration.Builder configurationBuilder = new Configuration.Builder(BikerentalApplication.this);

        configurationBuilder.addModelClasses( Admin.class, Breakdown.class, Client.class,
                Inventory.class, Point.class, Rent.class, Tarif.class,
                Shift.class,
                ru.prokatvros.veloprokat.model.db.Message.class );


        //ActiveAndroid.initialize(this);
        ActiveAndroid.initialize(configurationBuilder.create());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        ImageLoader.getInstance().handleSlowNetwork(true);

        /*try {
            copyDataBase();
        } catch (IOException ex) {
            ex.printStackTrace();
        } */


        String url = "http://prokatvros.temp.swtest.ru/api/files/backupname2.db";
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url, null, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {

                try {
                    copyDataBase(response);
                    //copyDataBase();
                    //initActiveAndroid();
                    Log.d("LOAD_BACKUP","Was load");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                /*HashMap<String, Object> map = new HashMap<String, Object>();
                try {
                    if ( response != null ) {


                        //String content = request.responseHeaders.get("Content-Disposition").toString();
                        //StringTokenizer st = new StringTokenizer(content, "=");
                        //String[] arrTag = st.toArray();

                        //String filename = arrTag[1];
                        //filename = filename.replace(":", ".");
                        //Log.d("DEBUG::FILE NAME", filename);

                        try{
                            long lenghtOfFile = response.length;

                            //covert reponse to input stream
                            InputStream input = new ByteArrayInputStream(response);
                            File path = Environment.getExternalStorageDirectory();
                            File file = new File(path, filename);
                            map.put("resume_path", file.toString());
                            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                            byte data[] = new byte[1024];

                            long total = 0;
                            int count;

                            while ((count = input.read(data)) != -1) {
                                total += count;
                                output.write(data, 0, count);
                            }

                            output.flush();

                            output.close();
                            input.close();
                        }catch(IOException e){
                            e.printStackTrace();

                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                    e.printStackTrace();
                }*/




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(),
                new HurlStack());
        
        mRequestQueue.add(request);

        initActiveAndroid();

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

        InventoryRequest request = InventoryRequest.requestGetInventory("point", new Response.Listener<String>() {
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
                Toast.makeText(BikerentalApplication.this, "ERROR IN requestGetInventory", Toast.LENGTH_LONG).show();
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

    private static String PREFERENCE_INTERCEDE = "preference_intercede";
    public void setIntercede(boolean isIntercede) {
        SharedPreferences.Editor  editor = getApplicationPreferencesEditor();
        editor.putBoolean(PREFERENCE_INTERCEDE, isIntercede);
        editor.commit();
    }

    public boolean isIntercede() {
        return getApplicationPreferences().getBoolean(PREFERENCE_INTERCEDE, false);
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

        return Point.load(Point.class, pointId);
    }


    public String getUUID() {
        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }

    /*public DataParser getDataParser() {
        return DataParser.getInstance(this);
    } */

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


    private static String DB_PATH = "/data/data/ru.prokatvros.veloprokat/databases/";
    private static String DB_NAME = "backupname2.db";

    private void copyDataBase(byte[] response) throws IOException {
        //Открываем локальную БД как входящий поток
        //InputStream myInput = this.getAssets().open(DB_NAME);
        InputStream myInput = new ByteArrayInputStream(response);

        //Путь ко вновь созданной БД
        String outFileName = DB_PATH + DB_NAME;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    private void copyDataBase() throws IOException {
        //Открываем локальную БД как входящий поток
        InputStream myInput = this.getAssets().open(DB_NAME);

        //Путь ко вновь созданной БД
        String outFileName = DB_PATH + DB_NAME;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


}
