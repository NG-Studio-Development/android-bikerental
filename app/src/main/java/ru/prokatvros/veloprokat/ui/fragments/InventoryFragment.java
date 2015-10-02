package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.ui.activities.InventoryActivity;

public class InventoryFragment extends BaseFragment<InventoryActivity> {

    private static final String ARG_INVENTORY = "inventory";

    private Inventory inventory;

    public static InventoryFragment newInstance(Inventory inventory) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_INVENTORY, inventory);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inventory = (Inventory) getArguments().getParcelable(ARG_INVENTORY);
        }

        getHostActivity().getSupportActionBar().setTitle(inventory.model);
        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_inventory;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvNumber = (TextView) view.findViewById(R.id.tvNumber);
        TextView tvCount = (TextView) view.findViewById(R.id.tvRentsCount);
        TextView tvCost = (TextView) view.findViewById(R.id.tvCost);


        setHasOptionsMenu(true);

        if ( inventory != null ) {
            tvName.setText(inventory.model);
            tvNumber.setText(inventory.number);
            tvCost.setText(String.valueOf(getString(R.string.hour)+": "+inventory.tarif.sumPerHour+"\n"+
                                            getString(R.string.day)+": "+inventory.tarif.sumDay+"\n"+
                                            getString(R.string.th_hour)+": "+inventory.tarif.sumTsDay ) );
            tvCount.setText(String.valueOf(inventory.countRents));
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* switch (item.getItemId()) {
            case android.R.id.home:
                getHostActivity().onBackPressed();
                break;
        } */

        return super.onOptionsItemSelected(item);
    }

}
