package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.ui.activities.InventoryActivity;
import ru.prokatvros.veloprokat.ui.adapters.InventoryAdapter;


public class BreakdownInRentListFragment extends BaseListFragment {

    public BreakdownInRentListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (getHostActivity().getSupportActionBar() != null)
            getHostActivity().getSupportActionBar().setTitle(getString(R.string.breakdowns));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();

        InventoryAdapter adapter = new InventoryAdapter(getHostActivity(), R.layout.item_inventory, Inventory.getByState(Inventory.REFIT_STATE));
        setAdapter(adapter);
    }

    @Override
    public void onAddClick() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //BreakdownInRent BreakdownInRent  = (BreakdownInRent) getAdapter().getItem(position);
        //BreakdownInRentActivity.startClientActivity(getHostActivity(), BreakdownInRent);

        Inventory inventory = (Inventory) getAdapter().getItem(position);
        InventoryActivity.startInventoryActivity(getHostActivity(), inventory);
    }

}
