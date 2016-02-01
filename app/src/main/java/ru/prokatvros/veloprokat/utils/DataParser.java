package ru.prokatvros.veloprokat.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.prokatvros.veloprokat.net.InputStreamVolleyRequest;

public class DataParser {

    private final static String TAG = "DATA_PARSER";

    private final static String PARAM_JSON_DATA = "json_data";

    static final int ON_START = 0;
    static final int ON_SUCCESS = 1;
    static final int ON_ERROR = 2;

    Context context;

    public static DataParser instance = null;

    public interface OnLoadDBListener {
        void onLoad();
        void onFinish();
        void onError();
    }

    public OnLoadDBListener loadDBListener = null;

    private DataParser(Context context) { this.context = context; }

    public static DataParser getInstance(Context context) {

        if (instance == null)
            instance = new DataParser(context);

        return instance;
    }

    private static String DB_PATH = "/data/data/ru.prokatvros.veloprokat/databases/";
    private static String DB_NAME = "backupname2.db";

    private void copyDataBase(byte[] response) throws IOException {

        InputStream myInput = new ByteArrayInputStream(response);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

        loadDBListener.onFinish();

    }

    private void copyDataBase() throws IOException{
        //Открываем локальную БД как входящий поток
        InputStream myInput = context.getAssets().open(DB_NAME);

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

        loadDBListener.onFinish();
    }

    public void loadDumpDB(String url, OnLoadDBListener listener) {
        this.loadDBListener = listener;

        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url, null, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {

                try {
                    loadDBListener.onLoad();
                    //copyDataBase();
                    copyDataBase(response);

                } catch (IOException ex) {
                    loadDBListener.onError();
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadDBListener.onError();
                error.printStackTrace();
            }
        });


        RequestQueue mRequestQueue = Volley.newRequestQueue(context,
                new HurlStack());

        mRequestQueue.add(request);

    }


    /*

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (loadDBListener == null)
                return;

            if (msg.what == ON_SUCCESS) {}

            else if(msg.what == ON_ERROR){}

        }
    };

    public BreakdowndInRentRequest breakdowndRequest = BreakdowndInRentRequest.requestAllBreakdowns(new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            List<Breakdown> breakdownList = null;
            Log.d("REQ_BREAK_D", "Breakdown json: " + response);
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

    } */

}