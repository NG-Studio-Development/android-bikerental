package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Point;

public class PointAdapter extends ArrayAdapter<Point> {

    private int resource;
    private List<Point> list;

    public PointAdapter(Context context, int resource, List<Point> list) {
        super(context, resource, list);

        this.resource = resource;
        this.list = list;
    }


    public static List<Point> initListForDEBUG() {
        List<Point> list = new ArrayList<>();

        for (int i=0; i<10; i++) {
            Point point = new Point();
            point.title = "Title "+(i+1);
            list.add(point);
        }

        return list;
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

        Point point = getItem(position);

        holder.tvTitle.setText(point.title);

        return convertView;
    }

    private Holder initHolder(View convertView) {
        Holder holder = new Holder();
        holder.tvTitle = (TextView) convertView.findViewById(R.id.tvPhone);
        return holder;
    }

    class Holder {
        TextView tvTitle;
    }

}
