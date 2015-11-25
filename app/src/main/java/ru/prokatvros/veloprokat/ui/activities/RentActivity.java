package ru.prokatvros.veloprokat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.Model;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.model.requests.RentRequest;
import ru.prokatvros.veloprokat.services.receivers.SampleAlarmReceiver;
import ru.prokatvros.veloprokat.ui.fragments.AddRentFragment;
import ru.prokatvros.veloprokat.ui.fragments.RentFragment;

public class RentActivity extends BaseActivity {

    private final static String TAG = "RENT_ACTIVITY";

    private final static String RENT_KEY = "rent_key";

    public final static String CREATE_RENT = "create_rent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Rent rent = null;
        Intent intent = getIntent();
        Fragment fragment = new AddRentFragment();

        if ( intent != null ) {
            Long idRent = intent.getLongExtra(RENT_KEY, -1);
            rent = Model.load(Rent.class, idRent);

            if ( rent != null )
                fragment = RentFragment.newInstance(rent);
            else
                Rent.createRentInPool(RentActivity.CREATE_RENT);
        } else {
            Rent.createRentInPool(RentActivity.CREATE_RENT);
        }

        if (savedInstanceState == null)
            replaceFragment(fragment, false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Rent.removeRentInPool(RentActivity.CREATE_RENT);
    }

    public static void  startRentActivity(/*@NotNull*/ Context context, /*@Nullable*/ Rent rent) {
        Intent intent = new Intent(context, RentActivity.class);
        if ( rent != null )
            intent.putExtra (RENT_KEY, rent.getId());
        context.startActivity(intent);
    }

    public void sendToServer(final Rent rent) {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final String strJsonRent = gson.toJson(rent);

        Log.d(TAG, "Json rent:" + strJsonRent);
        long idAdmin = BikerentalApplication.getInstance().getAdmin().getId();
        final RentRequest rentRequest = RentRequest.requestPostRent(idAdmin, strJsonRent, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(RentActivity.this, "Success", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response: " + response);
                //rent.inventory.countRents +=1;
                //rent.inventory.save();
                //getHostActivity().onBackPressed();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(RentActivity.this, "Error: "+error.toString(), Toast.LENGTH_LONG).show();

                //DataParser.getInstance(RentActivity.this).saveToPoolRentData(strJsonRent);

                SampleAlarmReceiver alarmReceiver = new SampleAlarmReceiver();
                alarmReceiver.setAlarm(RentActivity.this);

                error.printStackTrace();
            }
        });

        BikerentalApplication.getInstance().isNetworkAvailable(new BikerentalApplication.NetworkAvailableListener() {
            @Override
            public void onResponse(boolean isAvailable) {
                if (isAvailable) {
                    Volley.newRequestQueue(RentActivity.this).add(rentRequest);
                } else {

                    //DataParser.getInstance(RentActivity.this).saveToPoolRentData(strJsonRent);


                    SampleAlarmReceiver alarmReceiver = new SampleAlarmReceiver();
                    alarmReceiver.setAlarm(RentActivity.this);
                    Toast.makeText(RentActivity.this, "Server not avialable", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    protected int getContainer() {
        return R.id.container;
    }
}
