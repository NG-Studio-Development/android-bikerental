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
import ru.prokatvros.veloprokat.model.db.Inventory;

public class InventoryAdapter extends ArrayAdapter<Inventory> {

    private int resource;
    private List<Inventory> list;

    public InventoryAdapter(Context context, int resource, List<Inventory> list) {
        super(context, resource, list);

        this.resource = resource;
        this.list = list;
    }


    public static List<Inventory> initListForDEBUG() {
        List<Inventory> list = new ArrayList<>();

        for (int i=0; i<10; i++) {
            Inventory inventory = new Inventory();
            inventory.model = "Inventory "+(i+1);
            list.add(inventory);
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

        Inventory inventory = getItem(position);

        holder.tvName.setText(inventory.model);

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
