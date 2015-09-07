package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;

public class ClientFragment extends BaseFragment {

    private static final String ARG_CLIENT = "client";

    private Client client;

    public static ClientFragment newInstance(Client client) {
        ClientFragment fragment = new ClientFragment ();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CLIENT, client);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            client = (Client) getArguments().getSerializable(ARG_CLIENT);
        }
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_client;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_client, container, false);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);

        if (client != null ) {
            tvName.setText(client.name);
        }


        return view;
    }

}