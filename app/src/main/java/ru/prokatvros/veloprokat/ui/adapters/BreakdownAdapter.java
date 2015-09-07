package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Breakdown;

public class BreakdownAdapter extends ArrayAdapter<Breakdown> {

    private int resource;
    private List<Breakdown> list;

    public BreakdownAdapter(Context context, int resource, List<Breakdown> list) {
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

        Breakdown breakdown = getItem(position);

        holder.tvName.setText(breakdown.description);

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