package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.ui.activities.InventoryActivity;
import ru.prokatvros.veloprokat.ui.adapters.InventoryHeaderAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class InventoryListFragment extends BaseFragment {

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
        getHostActivity().getSupportActionBar().setTitle(getString(R.string.inventory));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory_list, container, false);

        Point point = BikerentalApplication.getInstance().getPoint();

        StickyListHeadersListView stickyList = (StickyListHeadersListView) view.findViewById(R.id.lvInventory);

        final InventoryHeaderAdapter adapter = new InventoryHeaderAdapter(getHostActivity(), Inventory.getByPoint(point, true));

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

    /*@Override
    public void onAddClick() { throw new UnsupportedOperationException(); }

    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
        Inventory inventory = (Inventory) getAdapter().getItem( position );
        InventoryActivity.startInventoryActivity( getHostActivity(), inventory );
    } */

}
