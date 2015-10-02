package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.Model;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Breakdown;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.model.requests.RentRequest;
import ru.prokatvros.veloprokat.services.receivers.SampleAlarmReceiver;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;
import ru.prokatvros.veloprokat.utils.DataParser;

public class RentFragment extends BaseFragment<RentActivity> {

    private static final String TAG = "RENT_FRAGMENT";
    private static final String ARG_RENT = "rent";

    private Rent rent;

    public static RentFragment newInstance(Rent rent) {
        RentFragment fragment = new RentFragment ();

        Bundle args = new Bundle();
        args.putLong(ARG_RENT, rent.getId());
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Long idRent = getArguments().getLong(ARG_RENT);
            rent = Model.load(Rent.class, idRent);
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

        getHostActivity().getSupportActionBar().setTitle(rent.client.name);
        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        TextView tvClientName = (TextView) view.findViewById(R.id.tvClientName);
        TextView tvInventoryName = (TextView) view.findViewById(R.id.tvInventoryName);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvTimeTitle = (TextView) view.findViewById(R.id.tvTimeTitle);
        RelativeLayout rlBreakdown = (RelativeLayout) view.findViewById(R.id.rlBreakdown);
        TextView tvBreakdown = (TextView) view.findViewById(R.id.tvBreakdown);
        TextView tvBreakdownSpinnerTitle = (TextView) view.findViewById(R.id.tvBreakdownSpinnerTitle);

        TextView tvInventoryAdditionalName = (TextView) view.findViewById(R.id.tvInventoryAdditionalName);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerBreakdown);
        Button buttonClose = (Button) view.findViewById(R.id.buttonClose);

        final List<Breakdown> breakdownList = Breakdown.getAll();

        final ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, createSinnerTitle(breakdownList) );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        tvClientName.setText(rent.client.name + " " + rent.client.surname);
        tvInventoryName.setText(rent.inventory.model);

        tvInventoryAdditionalName.setText(rent.inventoryAddition.model);

        tvTime.setText(getDate(rent.endTime));

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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (rent.isCompleted()) {
            rlBreakdown.setVisibility(View.VISIBLE);
            if (rent.breakdown != null )
                tvBreakdown.setText(rent.breakdown.description);
            buttonClose.setVisibility(View.INVISIBLE);
            tvTime.setVisibility(View.INVISIBLE);
            tvTimeTitle.setText(getString(R.string.was_closed));
            tvTimeTitle.setTextColor(getResources().getColor(R.color.red));
            spinner.setVisibility(View.INVISIBLE);
            tvBreakdownSpinnerTitle.setVisibility(View.INVISIBLE);
        }

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rent.setCompleteds(true);
                rent.save();
                sendToServer(rent);
                getHostActivity().onBackPressed();
            }
        });

        return view;
    }



    public void sendToServer(final Rent rent) {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final String strJsonRent = gson.toJson(rent);

        Log.d(TAG, "Json rent:" + strJsonRent);
        long idAdmin = BikerentalApplication.getInstance().getAdmin().getId();
        final RentRequest rentRequest = RentRequest.requestPutRent(idAdmin, strJsonRent, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getHostActivity(), "Success", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response: " + response);
                rent.inventory.countRents +=1;
                rent.inventory.save();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(RentActivity.this, "Error: "+error.toString(), Toast.LENGTH_LONG).show();

                DataParser.getInstance(getHostActivity()).saveToPoolRentData(strJsonRent);

                SampleAlarmReceiver alarmReceiver = new SampleAlarmReceiver();
                alarmReceiver.setAlarm(getHostActivity());

                error.printStackTrace();
            }
        });

        BikerentalApplication.getInstance().isNetworkAvailable(new BikerentalApplication.NetworkAvailableListener() {
            @Override
            public void onResponse(boolean isAvailable) {
                if (isAvailable) {
                    Volley.newRequestQueue(getHostActivity()).add(rentRequest);
                } else {

                    DataParser.getInstance(getHostActivity()).saveToPoolRentData(strJsonRent);


                    SampleAlarmReceiver alarmReceiver = new SampleAlarmReceiver();
                    alarmReceiver.setAlarm(getHostActivity());
                    Toast.makeText(getHostActivity(), "Server not avialable", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private String getDate(long timeStamp){

        try{
            DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    List<String> createSinnerTitle(List<Breakdown> breakdownList) {
        List<String> list = new ArrayList<>();

        for (Breakdown breakdown : breakdownList) {
            list.add(breakdown.description);
        }
        return list;
    }

}
