package ru.prokatvros.veloprokat.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.requests.AuthorizationRequest;
import ru.prokatvros.veloprokat.model.requests.LoadAllDataRequest;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.ui.fragments.LoginFragment;
import ru.prokatvros.veloprokat.ui.fragments.SelectPointFragment;
import ru.prokatvros.veloprokat.utils.DataParser;

public class LoginActivity extends  BaseActivity implements LoginFragment.OnLoginListener {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "Your-Sender-ID";

    static final String TAG = "GCM Demo";

    GoogleCloudMessaging gcm;

    AtomicInteger msgId = new AtomicInteger();

    Context context;

    String regid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        context = getApplicationContext();

        if (true/*checkPlayServices()*/) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                replaceFragment(new LoginFragment(), false);
            } else {
                if ( BikerentalApplication.getInstance().isSelectedPoint() ) {
                    MainActivity.startMainActivity(this);
                    finish();
                } else {
                    replaceFragment(new SelectPointFragment(), false);
                }
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion(context);

        Log.i(TAG, "Saving regId on app version " + appVersion);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground(final String login, final String password) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                sendRegistrationIdToBackend(login, password);
            }
        }.execute(null, null, null);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences() {
        return BikerentalApplication.getInstance().getApplicationPreferences();
    }

    private void sendRegistrationIdToBackend(String login, String password) {

        AuthorizationRequest request = AuthorizationRequest.requestLogin(login, password, regid, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                Log.d(TAG, "Response: " + response);
                Admin admin = gson.fromJson(response, Admin.class);
                loadDB(admin);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                getProgressDialog().hide();
                Toast.makeText(LoginActivity.this, "Id to back "+error.toString(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        getProgressDialog().setMessage(getString(R.string.authorisation));
        getProgressDialog().show();

        Volley.newRequestQueue(this).add(request);
    }

    protected void loadDB(final Admin admin) {
        getProgressDialog().setMessage(getString(R.string.update_data));
        LoadAllDataRequest requestCollectData = LoadAllDataRequest.requestCollectData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonData = new JSONObject(response);

                    if ( jsonData.getInt("error_encode") == 0 ) {
                        String url = ConstantsBikeRentalApp.URL_SERVER
                                +jsonData.getJSONObject("data").getString("url");
                        loadDumpDB(url, admin);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    getProgressDialog().hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                getProgressDialog().hide();
            }
        });

        Volley.newRequestQueue(this).add(requestCollectData);
    }

    protected void loadDumpDB(String url, final Admin admin) {
        getProgressDialog().setMessage(getString(R.string.load_data));
        BikerentalApplication.getInstance().getDataParser().loadDumpDB(url, new DataParser.OnLoadDBListener() {
            @Override
            public void onLoad() {}

            @Override
            public void onFinish() {
                admin.save();
                BikerentalApplication.getInstance().setAdmin(admin);
                replaceFragment(new SelectPointFragment(), false);
                getProgressDialog().hide();
            }

            @Override
            public void onError() {
                getProgressDialog().hide();
            }
        });
    }


    @Override
    protected int getContainer() {
        return R.id.container;
    }

    @Override
    public void onLogin(String login, String password) {
        registerInBackground(login, password);
    }
}