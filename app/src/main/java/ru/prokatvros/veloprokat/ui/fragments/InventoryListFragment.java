package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.ui.activities.InventoryActivity;
import ru.prokatvros.veloprokat.ui.adapters.InventoryAdapter;


public class InventoryListFragment extends BaseListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getHostActivity().getSupportActionBar().setTitle(getString(R.string.inventory));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        ImageButton ibAdd = (ImageButton) view.findViewById(R.id.ibAdd);
        ibAdd.setVisibility(View.INVISIBLE);
        Point point = BikerentalApplication.getInstance().getPoint();

        setAdapter(new InventoryAdapter(getHostActivity(), R.layout.item_base, Inventory.getByPoint(point)));

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
