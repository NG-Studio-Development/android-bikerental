package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.ui.activities.InventoryActivity;
import ru.prokatvros.veloprokat.ui.adapters.InventoryAdapter;


public class InventoryListFragment extends BaseListFragment {

    private ArrayAdapter<Inventory> adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        adapter = new InventoryAdapter(getHostActivity(), R.layout.item_inventory, Inventory.getAll());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAddClick() { }

    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
        InventoryActivity.startInventoryActivity( getHostActivity(), adapter.getItem( position ) );
    }

    @Override
    public ArrayAdapter getAdapter() {
        return adapter;
    }

}
