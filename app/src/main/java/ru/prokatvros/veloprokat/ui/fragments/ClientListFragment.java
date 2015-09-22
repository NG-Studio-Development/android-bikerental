package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.ui.activities.ClientActivity;
import ru.prokatvros.veloprokat.ui.adapters.ClientAdapter;


public class ClientListFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<Client>{



    private List<Client> clientList = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SET_ADAPTER) {
                setAdapter(new ClientAdapter(BikerentalApplication.getInstance(), R.layout.item_base, clientList));
                setVisibilityProgressBar(false);
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getHostActivity().getSupportActionBar().setTitle(getString(R.string.clients));
    }

    @Override
    public void onStart() {
        super.onStart();

        setVisibilityProgressBar(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                clientList = Client.getAll();
                handler.sendMessage( handler.obtainMessage( SET_ADAPTER) );
            }
        }).start();
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

    @Override
    public Loader<Client> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Client> loader, Client data) {

    }

    @Override
    public void onLoaderReset(Loader<Client> loader) {

    }

}
