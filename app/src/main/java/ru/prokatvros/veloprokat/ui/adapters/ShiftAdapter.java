package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Shift;

public class ShiftAdapter extends ArrayAdapter<Shift> {

    private int resource;
    private List<Shift> list;

    public ShiftAdapter (Context context, int resource, List<Shift> list) {
        super(context, resource, list);

        this.resource = resource;
        this.list = list;
    }


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

        Shift shift = getItem(position);

        holder.tvPoint.setText(shift.point.title);

        return convertView;
    }


    private Holder initHolder(View convertView) {
        Holder holder = new Holder();
        holder.tvPoint = (TextView) convertView.findViewById(R.id.tvPoint);
        return holder;
    }

    class Holder {
        TextView tvPoint;
    }


}
