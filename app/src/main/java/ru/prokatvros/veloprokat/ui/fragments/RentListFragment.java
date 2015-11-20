package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;
import ru.prokatvros.veloprokat.ui.adapters.RentAdapter;

public class RentListFragment extends BaseListFragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "RENT_LIST_F";

    private BroadcastReceiver broadcastReceiver;

    RadioButton rbCompleted;
    RadioButton rbUncompleted;
    List<Rent> rentList;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_rents_list;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ( getHostActivity().getSupportActionBar() != null )
            getHostActivity().getSupportActionBar().setTitle(getString(R.string.rents));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = super.onCreateView(inflater, container, savedInstanceState);
        rbCompleted = (RadioButton) view.findViewById(R.id.rbCompleted);
        rbUncompleted = (RadioButton) view.findViewById(R.id.rbUncompleted);

        rbUncompleted.setOnCheckedChangeListener(this);
        rbCompleted.setOnCheckedChangeListener(this);


        IntentFilter intFilter = new IntentFilter(ACTION_LOADED_DATA);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (rentList != null)
                    setAdapter(new RentAdapter(context, R.layout.item_base, rentList));
            }
        };
        getHostActivity().registerReceiver(broadcastReceiver, intFilter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        rbUncompleted.setChecked(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                rentList = Rent.getAllByCompleted(false);
                Intent intentBroadcast = new Intent(ACTION_LOADED_DATA);
                BikerentalApplication.getInstance().sendBroadcast(intentBroadcast);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getHostActivity().unregisterReceiver(broadcastReceiver);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!isChecked) return;

        boolean completed;

        switch (buttonView.getId()) {
            default:
            case R.id.rbUncompleted:
                completed = false;
                break;

            case R.id.rbCompleted:
                completed = true;
                break;
        }

        List<Rent> rentList = Rent.getAllByCompleted(completed);
        //List<Rent> rentList = Rent.getAll();
        setAdapter(new RentAdapter(getHostActivity(), R.layout.item_base, rentList));
    }
}
