package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Rent;

public class RentAdapter extends ArrayAdapter<Rent> {

    private static int TIME_OF_EXPIRES = 60*60*1000;

    private int resource;
    private List<Rent> list;

    public RentAdapter(Context context, int resource, List<Rent> list) {
        super(context, resource, list);

        this.resource = resource;
        this.list = list;
    }


    /*public static List<Rent> initListForDEBUG() {
        List<Rent> list = new ArrayList<>();

        for (int i=0; i<10; i++) {
            Rent rent = new Rent();
            rent.name = "Rent "+(i+1);
            list.add(rent);
        }

        return list;
    } */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
            holder = initHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        Rent rent = getItem(position);

        String clientName = "no name";
        String inventoryModel = "no model";

        if (rent.client != null && rent.client.name != null)
            clientName = rent.client.name;

        if (rent.inventory != null && rent.inventory.model != null)
            inventoryModel = rent.inventory.model;

        holder.tvName.setText(clientName + " " + inventoryModel);

        long timeToEndOfRent = rent.endTime - Calendar.getInstance().getTimeInMillis();


        if (timeToEndOfRent <= 0 )
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.red));
        else if (timeToEndOfRent <= TIME_OF_EXPIRES) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.yellow));
        }


        return convertView;
    }

    private Holder initHolder(View convertView) {
        Holder holder = new Holder();
        holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
        return holder;
    }

    class Holder {
        TextView tvName;
    }

}
