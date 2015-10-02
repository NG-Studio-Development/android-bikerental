package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.BreakdownInRent;

public class BreakdownInRentAdapter extends ArrayAdapter<BreakdownInRent> {

    private int resource;

    public BreakdownInRentAdapter (Context context, int resource, List<BreakdownInRent> list) {
        super(context, resource, list);
        this.resource = resource;
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

        BreakdownInRent breakdown = getItem(position);

        holder.tvDescription.setText(breakdown.name);

        return convertView;
    }

    private Holder initHolder(View convertView) {
        Holder holder = new Holder();
        holder.tvDescription = (TextView) convertView.findViewById(R.id.tvName);
        return holder;
    }

    class Holder {
        TextView tvDescription;
    }


}
