package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.ui.activities.InventoryActivity;
import ru.prokatvros.veloprokat.ui.activities.MainActivity;
import ru.prokatvros.veloprokat.ui.adapters.InventoryHeaderAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;



public class InventoryListFragment extends BaseFragment<MainActivity> {

    protected StickyListHeadersListView stickyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_inventory_list;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (getHostActivity().getSupportActionBar() != null)
            getHostActivity().getSupportActionBar().setTitle(getString(R.string.inventory));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory_list, container, false);

        Point point = BikerentalApplication.getInstance().getPoint();

        stickyList = (StickyListHeadersListView) view.findViewById(R.id.lvInventory);

        //List<Inventory> inventoryList =  Inventory.getByPoint(point, true);
        //inventoryList.size();


        //final InventoryHeaderAdapter adapter = new InventoryHeaderAdapter( getHostActivity(), Inventory.getByPoint(point, true) );

        List<Inventory> inventoryList = Inventory.getAll();
        final InventoryHeaderAdapter adapter = new InventoryHeaderAdapter( getHostActivity(), inventoryList );

        stickyList.setAdapter(adapter);
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Inventory inventory = (Inventory) adapter.getItem(position);
                InventoryActivity.startInventoryActivity(getHostActivity(), inventory);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ( (InventoryHeaderAdapter) stickyList.getAdapter()).notifyDataSetChanged();
    }

    /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_commit).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_commit:
                Toast.makeText(getHostActivity(), "Action commit", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    } */


    /*@Override
    public void onAddClick() { throw new UnsupportedOperationException(); }

    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
        Inventory inventory = (Inventory) getAdapter().getItem( position );
        InventoryActivity.startInventoryActivity( getHostActivity(), inventory );
    } */

}
