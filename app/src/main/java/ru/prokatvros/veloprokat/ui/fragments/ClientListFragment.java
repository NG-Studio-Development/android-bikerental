package ru.prokatvros.veloprokat.ui.fragments;

import android.view.View;
import android.widget.AdapterView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.ui.activities.ClientActivity;
import ru.prokatvros.veloprokat.ui.adapters.ClientAdapter;


public class ClientListFragment extends BaseListFragment {


    @Override
    public void onStart() {
        super.onStart();
        setAdapter(new ClientAdapter(getHostActivity(), R.layout.item_base, Client.getAll()));
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
