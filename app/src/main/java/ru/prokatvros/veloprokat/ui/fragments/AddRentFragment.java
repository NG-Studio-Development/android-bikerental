package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;

public class AddRentFragment extends BaseFragment {


    @Override
    public int getLayoutResID() {
        return R.layout.fragment_add_rent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_add_rent, container, false);

        Button buttonAddClient = (Button) view.findViewById(R.id.buttonAddClient);
        Button buttonAddInventory = (Button) view.findViewById(R.id.buttonAddInventory);
        Button buttonAdd = (Button) view.findViewById(R.id.buttonAdd);


        buttonAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(new SearchClientFragment(), true);
            }
        });


        buttonAddInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(new SearchInventoryFragment(), true);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rent.getRentFromPool(RentActivity.CREATE_RENT).save();
                getHostActivity().onBackPressed();
            }
        });

        return view;
    }



}
