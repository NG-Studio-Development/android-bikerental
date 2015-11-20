package ru.prokatvros.veloprokat.ui.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Calendar;
import java.util.Date;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;

//import org.joda.time.DateTime;
//import org.joda.time.Period;

public class AddRentFragment extends BaseFragment<RentActivity> {

    private static String TAG = "ADD_RENT_FRAGMENT";


    EditText etPaid;
    static TextView tvTime;
    static TextView tvDate;
    Button buttonAddClient;
    Button buttonAddInventory;
    Button buttonAdditionInventory;
    TextView tvCost;

    TimePickerFragment timePickerFragment; ;
    DatePickerFragment datePickerFragment;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_add_rent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_add_rent, container, false);

        setHasOptionsMenu(true);

        etPaid = (EditText) view.findViewById(R.id.etPaid);

        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvDate = (TextView) view.findViewById(R.id.tvDate);

        buttonAdditionInventory = (Button) view.findViewById(R.id.buttonAdditionInventory);

        buttonAddClient = (Button) view.findViewById(R.id.buttonAddClient);
        buttonAddInventory = (Button) view.findViewById(R.id.buttonAddInventory);
        Button buttonAdd = (Button) view.findViewById(R.id.buttonAdd);
        ImageButton buttonAddDate = (ImageButton) view.findViewById(R.id.ibAddDate);
        ImageButton ibAddTime = (ImageButton) view.findViewById(R.id.ibAddTime);
        tvCost = (TextView) view.findViewById(R.id.tvCost);
        tvCost.setText(getString(R.string.calculate_cost));

        tvCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rent rent = Rent.getRentFromPool(RentActivity.CREATE_RENT);

                if ( !isFullData() ) {
                    Toast.makeText(getHostActivity(), getString(R.string.warning_can_not_have_empty_field), Toast.LENGTH_LONG).show();
                    return;
                }

                Period period = new Period(new DateTime(), new DateTime(getDeadlineInMillis()));


                int years = period.getYears();
                int month = period.getMonths();
                int weeks = period.getWeeks();
                int days = period.getDays();
                int hour = period.getHours();

                if (years >0 || month >0 || weeks>0 || days>3) {
                    Toast.makeText(getHostActivity(), getString(R.string.warning_rent_can_not_be_moere_then_three_days), Toast.LENGTH_LONG).show();
                    return;
                }

                if (days == 0 && hour == 0) {
                    Toast.makeText(getHostActivity(), getString(R.string.warning_rent_can_not_be_less_then_one_hour), Toast.LENGTH_LONG).show();
                    return;
                } 
                rent.startTime = new Date().getTime();
                rent.endTime = getDeadlineInMillis();
                tvCost.setText(String.valueOf(rent.getCost()));
            }
        });

        timePickerFragment = new TimePickerFragment();
        datePickerFragment = new DatePickerFragment();

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



                Rent rent = Rent.getRentFromPool(RentActivity.CREATE_RENT);

                if ( rent != null ) {
                    //rent.endTime = getDeadlineInMillis();
                    //rent.startTime = new Date().getTime();
                    if (rent.startTime == 0 || rent.startTime == 0) {
                        Toast.makeText(getHostActivity(), getString(R.string.warning_you_must_calculate_cost),Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (etPaid.getText().toString().isEmpty()) {
                        Toast.makeText(getHostActivity(), getString(R.string.warning_set_prepay),Toast.LENGTH_LONG).show();
                        return;
                    }

                    rent.inventory.state = Inventory.RENTED_STATE;
                    rent.surcharge = Integer.valueOf(etPaid.getText().toString());
                    rent.inventory.save();
                    rent.save();

                    tvCost.setText( String.valueOf(rent.getCost()) );

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

    protected boolean isFullData() {
        Rent rent = Rent.getRentFromPool(RentActivity.CREATE_RENT);
        return datePickerFragment.isSet() && timePickerFragment.isSet() &&  rent.client != null && rent.inventory != null;
    }

    public long getDeadlineInMillis() {

        if (datePickerFragment == null || timePickerFragment == null
                || !datePickerFragment.isSet() && !timePickerFragment.isSet())
            return 0;

        Calendar calendar = Calendar.getInstance();

        if ( datePickerFragment.isSet() ){
            calendar.set(Calendar.YEAR, datePickerFragment.getYear());
            calendar.set(Calendar.MONTH, datePickerFragment.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, datePickerFragment.getDay());
        }

        if ( timePickerFragment.isSet() ) {
            calendar.set(Calendar.HOUR_OF_DAY, timePickerFragment.getHourOfDay());
            calendar.set(Calendar.MINUTE, timePickerFragment.getMinute());
        }

        return calendar.getTimeInMillis();

    }


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
