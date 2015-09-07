package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.ui.activities.ClientActivity;
import ru.prokatvros.veloprokat.ui.adapters.ClientAdapter;


public class ClientListFragment extends BaseListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // setAdapter(new ClientAdapter(getHostActivity(), R.layout.item_base, ClientAdapter.initListForDEBUG()));
        setAdapter(new ClientAdapter(getHostActivity(), R.layout.item_base, Client.getAll()));
        return view;
    }


    @Override
    public void onAddClick() {
        ClientActivity.startClientActivity(getHostActivity(), null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Client client = (Client) getAdapter().getItem(position);
        ClientActivity.startClientActivity(getHostActivity(), client);
    }



    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_list, container, false);


        return view;
    } */

}
