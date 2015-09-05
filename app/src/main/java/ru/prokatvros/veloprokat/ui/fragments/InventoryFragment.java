package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;

public class InventoryFragment extends Fragment {

    private static final String ARG_INVENTORY = "inventory";

    private Inventory inventory;

    public static InventoryFragment newInstance(Inventory inventory) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INVENTORY, inventory);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inventory = (Inventory) getArguments().getSerializable(ARG_INVENTORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);

        if (inventory != null ) {
            tvName.setText(inventory.model);
        }


        return view;
    }

}
