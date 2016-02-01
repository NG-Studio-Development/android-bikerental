package ru.prokatvros.veloprokat.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.model.db.Shift;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.model.requests.ShiftRequest;
import ru.prokatvros.veloprokat.ui.adapters.ItemsAdapter;
import ru.prokatvros.veloprokat.ui.fragments.BreakdownInRentListFragment;
import ru.prokatvros.veloprokat.ui.fragments.ChatFragment;
import ru.prokatvros.veloprokat.ui.fragments.ClientListFragment;
import ru.prokatvros.veloprokat.ui.fragments.InventoryListFragment;
import ru.prokatvros.veloprokat.ui.fragments.ProfileFragment;
import ru.prokatvros.veloprokat.ui.fragments.RentListFragment;


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
        //drawerLayout.setDrawerLockMode();

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Toast.makeText(MainActivity.this, "onOpenDrawer", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };


        boolean isIntercede = BikerentalApplication.getInstance().isIntercede();
        mDrawerToggle.setDrawerIndicatorEnabled(isIntercede);

        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            selectItem(0);
        }


        exportDatabse("BikeRents2.db");
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
                fragment = new BreakdownInRentListFragment();
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

    public void actionIntercedeShift(final MenuItem item) {

        Point point = BikerentalApplication.getInstance().getPoint();
        Admin admin = BikerentalApplication.getInstance().getAdmin();

        //ShiftRequest request = ShiftRequest.intercedeShift(Inventory.getByPoint(point, true), point, admin, new PostResponseListener() {
        ShiftRequest request = ShiftRequest.intercedeShift(Inventory.getAll(), point, admin, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                item.setVisible(false);
                BikerentalApplication.getInstance().setIntercede(true);
                Log.d(TAG, "Response: " + response);

                mDrawerToggle.setDrawerIndicatorEnabled(true);

                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

                Shift shift = gson.fromJson(response, Shift.class);
                shift.save();

                BikerentalApplication.getInstance().setShift(shift);

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                StringWriter errors = new StringWriter();
                error.printStackTrace(new PrintWriter(errors));


                /*
                *  File file = new File("test.log");

                try {
                    // something
                    PrintStream ps = new PrintStream(file);
                    error.printStackTrace(ps);
                    ps.close();

                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                */

                Toast.makeText(MainActivity.this, "Action error: "+errors.toString(), Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        boolean isIntercede = BikerentalApplication.getInstance().isIntercede();
        menu.findItem(R.id.action_commit).setVisible(!isIntercede);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_commit:
                actionIntercedeShift(item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }


    public static void exportDatabse(String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            Log.d("LOAD_DB", sd.getAbsolutePath());

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+BikerentalApplication.getInstance().getPackageName()+"//databases//"+databaseName+"";
                String backupDBPath = "backupname.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            Log.d("LOAD_DB", "Was load");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}




