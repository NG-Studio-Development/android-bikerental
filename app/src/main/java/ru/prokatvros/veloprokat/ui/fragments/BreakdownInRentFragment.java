package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.BreakdownInRent;

public class BreakdownInRentFragment extends Fragment {

    static BreakdownInRent breakdownInRent;

    public static BreakdownInRentFragment newInstance(BreakdownInRent breakdownInRent) {
        BreakdownInRentFragment fragment = new BreakdownInRentFragment();
        BreakdownInRentFragment.breakdownInRent = breakdownInRent;
        return fragment;
    }

    public BreakdownInRentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_breakdown_in_rent, container, false);
        TextView tvName = (TextView) view.findViewById(R.id.tvPhone);
        TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        TextView tvModel = (TextView) view.findViewById(R.id.tvModel);
        TextView tvNumber = (TextView) view.findViewById(R.id.tvNumber);

        tvName.setText(breakdownInRent.name);
        tvPhone.setText(breakdownInRent.phone);
        tvModel.setText(breakdownInRent.model);
        tvNumber.setText(String.valueOf(breakdownInRent.number));

        return view;
    }
}
