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
import ru.prokatvros.veloprokat.model.db.Client;

public class ClientAdapter extends ArrayAdapter<Client> {

    private int resource;
    private List<Client> list;

    public ClientAdapter(Context context, int resource, List<Client> list) {
        super(context, resource, list);

        this.resource = resource;
        this.list = list;
    }


    public static List<Client> initListForDEBUG() {
        List<Client> list = new ArrayList<>();

        for (int i=0; i<10; i++) {
            Client client = new Client();
            client.name = "Client "+(i+1);
            list.add(client);
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

        Client client = getItem(position);

        holder.tvName.setText(client.name);
        holder.tvPhone.setText(client.phone);

        return convertView;
    }

    private Holder initHolder(View convertView) {
        Holder holder = new Holder();
        holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
        holder.tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
        return holder;
    }

    class Holder {
        TextView tvName;
        TextView tvPhone;
    }

}
