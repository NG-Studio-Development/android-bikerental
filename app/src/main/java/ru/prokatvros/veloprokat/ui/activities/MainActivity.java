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
import android.view.View;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.ui.adapters.ItemsAdapter;
import ru.prokatvros.veloprokat.ui.fragments.ClientListFragment;
import ru.prokatvros.veloprokat.ui.fragments.InventoryListFragment;
import ru.prokatvros.veloprokat.ui.fragments.ProfileFragment;
import ru.prokatvros.veloprokat.ui.fragments.RentListFragment;


public class MainActivity extends BaseActivity {

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvDrawerContainer = (RecyclerView) findViewById(R.id.rwDrawerContainer);
        rvDrawerContainer.setHasFixedSize(true);
        menuAdapter = new ItemsAdapter(this,
                "D_Name"/*WhereAreYouApplication.getInstance().getUserName()*/,
                "D_Email"/*WhereAreYouApplication.getInstance().getUserEmail()*/ , new ItemsAdapter.OnItemClickListener() {
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
            replaceFragment(new ProfileFragment(), false);
        }

        // BikerentalApplication.getInstance().loadDataFromWeb();




    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* WhereAreYouAppLog.i("onActivityResult " + requestCode);
        if(requestCode == REQUEST_CODE_ENABLE_GPS) {
            startService(new Intent(MainActivity.this, GeoService.class));
        } */
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
                fragment = new ProfileFragment();
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
    }







    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}




