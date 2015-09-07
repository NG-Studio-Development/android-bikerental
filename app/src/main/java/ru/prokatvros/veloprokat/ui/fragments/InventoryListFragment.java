package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.ui.activities.InventoryActivity;
import ru.prokatvros.veloprokat.ui.adapters.InventoryAdapter;


public class InventoryListFragment extends BaseListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        setAdapter(new InventoryAdapter(getHostActivity(), R.layout.item_base, Inventory.getAll()));

        return view;
    }

    @Override
    public void onAddClick() { throw new UnsupportedOperationException(); }

    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
        Inventory inventory = (Inventory) getAdapter().getItem( position );
        InventoryActivity.startInventoryActivity( getHostActivity(), inventory );
    }

}
