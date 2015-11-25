package ru.prokatvros.veloprokat.ui.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.model.requests.RentRequest;
import ru.prokatvros.veloprokat.services.receivers.SampleAlarmReceiver;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;

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

        String title = rent.client != null && rent.client.name != null ? rent.client.name : getString(R.string.without_name);

        if (getHostActivity().getSupportActionBar() != null) {
            getHostActivity().getSupportActionBar().setTitle(title);
            getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        TextView tvClientName = (TextView) view.findViewById(R.id.tvClientName);
        TextView tvClientPhone = (TextView) view.findViewById(R.id.tvClientPhone);
        TextView tvInventoryName = (TextView) view.findViewById(R.id.tvInventoryName);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvTimeTitle = (TextView) view.findViewById(R.id.tvTimeTitle);
        RelativeLayout rlBreakdown = (RelativeLayout) view.findViewById(R.id.rlBreakdown);
        TextView tvBreakdown = (TextView) view.findViewById(R.id.tvBreakdown);
        TextView tvBreakdownSpinnerTitle = (TextView) view.findViewById(R.id.tvBreakdownSpinnerTitle);
        TextView tvCost = (TextView) view.findViewById(R.id.tvCost);
        TextView tvSurcharge = (TextView) view.findViewById(R.id.tvSurcharge);
        RelativeLayout rlSurcharge = (RelativeLayout) view.findViewById(R.id.rlSurcharge);
        final TextView tvSummFine = (TextView) view.findViewById(R.id.tvSummFine);
        TextView tvPrepaid = (TextView) view.findViewById(R.id.tvPrepaid);

        TextView tvInventoryAdditionalName = (TextView) view.findViewById(R.id.tvInventoryAdditionalName);

        final EditText etSurcharge = (EditText) view.findViewById(R.id.etSurcharge);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerBreakdown);
        Button buttonClose = (Button) view.findViewById(R.id.buttonClose);

        final List<Breakdown> breakdownList = new ArrayList<>();
        breakdownList.add(Breakdown.createWithoutBreakdown(getString(R.string.without_breakdown)));
        breakdownList.addAll(Breakdown.getAll());


        final ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, createSinnerTitle(breakdownList) );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (rent.client != null) {
            String clientName = new String();
            if (rent.client.name != null)
                clientName = clientName+rent.client.name;

            if (rent.client.surname != null)
                clientName = clientName+rent.client.surname;

            tvClientName.setText(!clientName.isEmpty() ? clientName : getString(R.string.not_specified));

            if( rent.client.phone != null )
                tvClientPhone.setText(rent.client.phone);
        }



        if ( rent.inventory!=null && rent.inventory.model!=null )
            tvInventoryName.setText(rent.inventory.model);

        tvPrepaid.setText(String.valueOf(rent.surcharge));

        if (!rent.isCompleted()) {
            tvCost.setText(String.valueOf( rent.getCost() ) );
            tvTime.setText(getDate(rent.endTime) );
        }

        if ( rent.inventoryAddition != null && rent.inventoryAddition.model != null )
            tvInventoryAdditionalName.setText(rent.inventoryAddition.model);



        if ( rent != null && rent.breakdown != null )
            spinner.setSelection( breakdownList.indexOf(rent.breakdown) );

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (rent == null)
                    return;

                Breakdown breakdown = breakdownList.get(position);

                if (!breakdown.code.equals(Breakdown.CODE_WITHOUT_BREAKDOWN)) {
                    rent.breakdown = breakdownList.get(position);
                    tvSummFine.setText(String.valueOf(rent.breakdown.summ));
                }

                rent.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if ( rent.isCompleted() ) {
            rlBreakdown.setVisibility(View.VISIBLE);

            if ( rent.breakdown != null )
                tvBreakdown.setText(rent.breakdown.description);

            buttonClose.setVisibility(View.INVISIBLE);
            tvTime.setVisibility(View.INVISIBLE);
            tvTimeTitle.setText(getString(R.string.was_closed));
            tvTimeTitle.setTextColor(getResources().getColor(R.color.red));
            spinner.setVisibility(View.GONE);
            tvBreakdownSpinnerTitle.setVisibility(View.GONE);
            etSurcharge.setVisibility(View.GONE);
            rlSurcharge.setVisibility(View.VISIBLE);
            tvSurcharge.setText(rent.surcharge == 0
                    ? getString(R.string.without_surcharge)
                    : String.valueOf(rent.surcharge));

            if (rent.breakdown != null)
                tvSummFine.setText( String.valueOf(rent.breakdown.summ) );
        }



        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rent.setCompleteds(true);
                rent.client.countRents += 1;
                rent.client.summ += Integer.valueOf(etSurcharge.getText().toString());
                rent.client.save();

                rent.inventory.state = Inventory.FREE_STATE;
                rent.inventory.setCountRent( rent.inventory.getCountRent()+1 ); ;
                rent.inventory.save();

                if (!etSurcharge.getText().toString().isEmpty())
                    rent.surcharge = Integer.valueOf(etSurcharge.getText().toString());

                rent.save();
                sendToServer(rent);

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
                Toast.makeText(BikerentalApplication.getInstance(), "Success", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response: " + response);
                //rent.inventory.countRents +=1;
                //rent.inventory.save();

                getHostActivity().onBackPressed();
            }

            @Override
            public void onErrorResponse(VolleyError error) {

                //DataParser.getInstance(getHostActivity()).saveToPoolRentData(strJsonRent);

                SampleAlarmReceiver alarmReceiver = new SampleAlarmReceiver();
                alarmReceiver.setAlarm(BikerentalApplication.getInstance());

                error.printStackTrace();
            }
        });

        BikerentalApplication.getInstance().isNetworkAvailable(new BikerentalApplication.NetworkAvailableListener() {
            @Override
            public void onResponse(boolean isAvailable) {
                if (isAvailable) {
                    Volley.newRequestQueue(getHostActivity()).add(rentRequest);
                } else {

                    //DataParser.getInstance(getHostActivity()).saveToPoolRentData(strJsonRent);

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
        } catch(Exception ex){
            ex.printStackTrace();
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
