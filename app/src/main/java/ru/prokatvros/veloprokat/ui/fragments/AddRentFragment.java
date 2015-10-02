package ru.prokatvros.veloprokat.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;

public class AddRentFragment extends BaseFragment<RentActivity> {

    private static String TAG = "ADD_RENT_FRAGMENT";

    static TextView tvTime;
    static TextView tvDate;
    Button buttonAddClient;
    Button buttonAddInventory;
    Button buttonAdditionInventory;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_add_rent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_add_rent, container, false);

        setHasOptionsMenu(true);

        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvDate = (TextView) view.findViewById(R.id.tvDate);

        buttonAdditionInventory = (Button) view.findViewById(R.id.buttonAdditionInventory);

        buttonAddClient = (Button) view.findViewById(R.id.buttonAddClient);
        buttonAddInventory = (Button) view.findViewById(R.id.buttonAddInventory);
        Button buttonAdd = (Button) view.findViewById(R.id.buttonAdd);
        ImageButton buttonAddDate = (ImageButton) view.findViewById(R.id.ibAddDate);
        ImageButton ibAddTime = (ImageButton) view.findViewById(R.id.ibAddTime);

        final TimePickerFragment timePickerFragment = new TimePickerFragment();
        final DatePickerFragment datePickerFragment = new DatePickerFragment();

        ibAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });

        buttonAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        //final Rent rent = Rent.getRentFromPool(RentActivity.CREATE_RENT);

        buttonAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(new SearchClientFragment(), true);
            }
        });

        buttonAddInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(SearchInventoryFragment.newSearchMain(), true);
            }
        });


        buttonAdditionInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(SearchInventoryFragment.newSearchAdditional(), true);
            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!datePickerFragment.isSet() || !timePickerFragment.isSet()) {
                    Toast.makeText(getHostActivity(), getString(R.string.warning_must_enter_time_and_date),Toast.LENGTH_LONG).show();
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, datePickerFragment.getYear());
                calendar.set(Calendar.MONTH, datePickerFragment.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePickerFragment.getDay());

                calendar.set(Calendar.HOUR_OF_DAY, timePickerFragment.getHourOfDay());
                calendar.set(Calendar.MINUTE, timePickerFragment.getMinute());

                Rent rent = Rent.getRentFromPool(RentActivity.CREATE_RENT);

                if ( rent != null ) {
                    Log.d(TAG, "Time in millis: "+calendar.getTimeInMillis());
                    rent.endTime = calendar.getTimeInMillis();
                    rent.save();
                    getHostActivity().sendToServer(rent);
                } else {
                    throw new Error("Rent is null, please, check set new rent to pool in RentActivity");
                }

                getHostActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getHostActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
        getHostActivity().getSupportActionBar().setTitle(getString(R.string.title_new_rent));

        Rent rent = Rent.getRentFromPool(RentActivity.CREATE_RENT);

        if(rent.client != null)
            buttonAddClient.setText(rent.client.name);

        if(rent.inventory != null)
            buttonAddInventory.setText(rent.inventory.model);

        if(rent.inventoryAddition != null)
            buttonAdditionInventory.setText(rent.inventoryAddition.model);
    }

    /* protected void sendToServer(final Rent rent) {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final String strJsonRent = gson.toJson(rent);

        Log.d(TAG, "Json rent:"+strJsonRent);
        long idAdmin = BikerentalApplication.getInstance().getAdmin().getId();
        final RentRequest rentRequest = RentRequest.requestPostRent(idAdmin, strJsonRent, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getHostActivity(), "Success", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response: " + response);
                rent.inventory.countRents +=1;
                rent.inventory.save();
                //getHostActivity().onBackPressed();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getHostActivity(), "Error: "+error.toString(), Toast.LENGTH_LONG).show();

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

    } */

    /* */

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        int hourOfDay;
        int minute;
        boolean isSet;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            this.hourOfDay = hourOfDay;
            this.minute = minute;
            this.isSet = true;

            String strHourOfDay;
            String strMinute;

            strHourOfDay = (strHourOfDay = String.valueOf(hourOfDay)).length() > 1 ? strHourOfDay : "0"+strHourOfDay;
            strMinute = (strMinute = String.valueOf(minute)).length() > 1 ? strMinute : "0"+strMinute;

            if (tvTime != null)
                tvTime.setText(strHourOfDay+":"+strMinute);

        }

        public int getHourOfDay() {
            return this.hourOfDay;
        }

        public int getMinute() {
            return minute;
        }

        public boolean isSet() {
            return isSet;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        int year;
        int month;
        int day;
        boolean isSet;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
            isSet = true;

            if (tvDate != null)
                tvDate.setText(day+"/"+month+"/"+year);
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public boolean isSet() {
            return isSet;
        }
    }



}
