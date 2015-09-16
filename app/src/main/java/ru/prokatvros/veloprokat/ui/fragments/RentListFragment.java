package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
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

    RadioButton rbCompleted;
    RadioButton rbUncompleted;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_rents_list;
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
        //setAdapter(new RentAdapter(getHostActivity(), R.layout.item_base, Rent.getAll()));
        //setAdapter(new RentAdapter(getHostActivity(), R.layout.item_base, Rent.getAllByCompleted(false)));
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
