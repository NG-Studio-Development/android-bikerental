package ru.prokatvros.veloprokat.ui.activities;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.requests.LoadAllDataRequest;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.ui.adapters.ItemsAdapter;
import ru.prokatvros.veloprokat.ui.fragments.ChatFragment;
import ru.prokatvros.veloprokat.ui.fragments.ClientListFragment;
import ru.prokatvros.veloprokat.ui.fragments.InventoryListFragment;
import ru.prokatvros.veloprokat.ui.fragments.ProfileFragment;
import ru.prokatvros.veloprokat.ui.fragments.RentListFragment;
import ru.prokatvros.veloprokat.utils.DataParser;


public class MainActivity extends BaseActivity {

    private final String TAG = "MAIN_ACTIVITY";

    RecyclerView rvDrawerContainer;
    ItemsAdapter menuAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ProgressDialog pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Admin admin = BikerentalApplication.getInstance().getAdmin();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvDrawerContainer = (RecyclerView) findViewById(R.id.rwDrawerContainer);
        rvDrawerContainer.setHasFixedSize(true);
        menuAdapter = new ItemsAdapter(this,
                admin.name, new ItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectItem(position);
            }
        });

        rvDrawerContainer.setAdapter(menuAdapter);

        mLayoutManager = new LinearLayoutManager(this);

        rvDrawerContainer.setLayoutManager(mLayoutManager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    private void selectItem(int position) {

        ItemsAdapter.MenuItem item = menuAdapter.getItem(position);

        Fragment fragment;

        switch(position) {
            case 0:
                fragment = new InventoryListFragment();
                break;

            case 1:
                fragment = new RentListFragment();
                break;

            case 2:
                fragment = new ChatFragment();
                break;

            case 3:
                fragment = new ProfileFragment();
                break;

            case 4:
                fragment = new ClientListFragment();
                break;
            default:
                fragment = new ProfileFragment();
                return;
        }

        replaceFragment(fragment, false);
        drawerLayout.closeDrawers();
    }


    @Override
    protected void onResume() {
        super.onResume();

        /*
        Client client = new Client();

        client.name="vasa";
        client.phone="9449";
        client.surname="sidorov";
        client.serverId=9999;
        //client.save();

        Rent rent = new Rent();
        rent.client = client;

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final String strJsonRent = gson.toJson(rent);

        Log.d("CLIENT_TO_JSON", strJsonRent); */

        //List<Inventory> list = Inventory.getAll();
        //list.size();

    }

    private void getPoolDataDEBUG() {
        String dataFromPool = DataParser.getInstance(this).loadDataFromPool();
        Log.d(TAG, "Data from pool: " + dataFromPool);

        LoadAllDataRequest request = LoadAllDataRequest.saveRequestAllData(dataFromPool, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Save request all data SUCCESS");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Save request all data ERROR");
            }
        });

        Volley.newRequestQueue(this).add(request);
    }




    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}




