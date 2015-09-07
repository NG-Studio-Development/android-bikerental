package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;
import ru.prokatvros.veloprokat.ui.adapters.RentAdapter;

public class RentListFragment extends BaseListFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = super.onCreateView(inflater, container, savedInstanceState);
        Log.d("RENT_LIST_FRAGMENT", "onCreateView()");


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setAdapter(new RentAdapter(getHostActivity(), R.layout.item_base, Rent.getAll()));
    }

    @Override
    public void onAddClick() {
        RentActivity.startRentActivity(getHostActivity(), null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Rent rent = (Rent) getAdapter().getItem( position );
        RentActivity.startRentActivity(getHostActivity(), rent);
    }

}
