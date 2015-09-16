package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Breakdown;
import ru.prokatvros.veloprokat.model.db.Rent;

public class RentFragment extends BaseFragment {

    private static final String ARG_RENT = "rent";

    private Rent rent;

    public static RentFragment newInstance(Rent rent) {
        RentFragment fragment = new RentFragment ();

        Bundle args = new Bundle();
        args.putParcelable(ARG_RENT, rent);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //rent = Rent.getRentFromPool(RentActivity.DEBUG_RENT);

        if (getArguments() != null) {
            rent = getArguments().getParcelable(ARG_RENT);
            // Log.d("DEBUG_SEND_RENT", "Rent id in RentFragment: " + rent.getId());
        }

    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_rent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rent, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerBreakdown);
        Button buttonClose = (Button) view.findViewById(R.id.buttonClose);

        final List<Breakdown> breakdownList = Breakdown.getAll();

        final ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, breakdownList );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (rent != null && rent.breakdown != null)
            spinner.setSelection( breakdownList.indexOf(rent.breakdown) );

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (rent == null)
                    return;

                rent.breakdown = breakdownList.get(position);
                rent.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rent.setCompleted(true);
                rent.save();
            }
        });

        return view;
    }
}
