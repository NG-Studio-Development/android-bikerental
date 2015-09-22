package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;
import ru.prokatvros.veloprokat.ui.adapters.RentAdapter;

public class RentListFragment extends BaseListFragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "RENT_LIST_F";

    RadioButton rbCompleted;
    RadioButton rbUncompleted;
    List<Rent> rentList;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SET_ADAPTER) {
                if (rentList != null)
                    setAdapter(new RentAdapter(getHostActivity(), R.layout.item_base, rentList));
                //setVisibilityProgressBar(false);
            }
        }
    };

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_rents_list;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
                handler.sendMessage(handler.obtainMessage(SET_ADAPTER));

            }
        }).start();
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
        setAdapter(new RentAdapter(getHostActivity(), R.layout.item_base, rentList));
    }
}
