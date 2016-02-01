package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.activeandroid.query.From;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.database.ModelLoader;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.ui.activities.ClientActivity;
import ru.prokatvros.veloprokat.ui.adapters.ClientAdapter;


public class ClientListFragment extends BaseListFragment implements CompoundButton.OnCheckedChangeListener,
                                                            LoaderManager.LoaderCallbacks<List<Client>> {

    protected static final String SELECTED_RADIO_KEY = "selected_radio";

    RadioButton rbMain;
    RadioButton rbVip;
    RadioButton rbBlackList;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_client_list;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (getHostActivity().getSupportActionBar() != null)
            getHostActivity().getSupportActionBar().setTitle(getString(R.string.clients));
    }

    @Override
    public void onStart() {
        super.onStart();
        rbMain.setChecked(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = super.onCreateView(inflater, container, savedInstanceState);
        rbMain = (RadioButton) view.findViewById(R.id.rbMain);
        rbVip = (RadioButton) view.findViewById(R.id.rbVip);
        rbBlackList = (RadioButton) view.findViewById(R.id.rbBlackList);

        rbMain.setOnCheckedChangeListener(this);
        rbVip.setOnCheckedChangeListener(this);
        rbBlackList.setOnCheckedChangeListener(this);

        getLoaderManager().initLoader(0, null, ClientListFragment.this);
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

    @Override
    public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {

        if ( !isChecked ) return;

        Bundle args = new Bundle();
        args.putInt(SELECTED_RADIO_KEY, buttonView.getId());

        getLoaderManager().restartLoader(0, args, ClientListFragment.this);

    }

    protected From getRequestFrom(int idSelected) {
        From from;

        switch (idSelected) {
            default:
            case R.id.rbMain:
                from = Client.getAllMain();
                break;

            case R.id.rbVip:
                from = Client.getAllVip();
                break;

            case R.id.rbBlackList:
                from = Client.getAllBlackList();
                break;
        }

        return from;
    }

    @Override
    public Loader<List<Client>> onCreateLoader(int id, Bundle args) {

        setVisibilityProgressBar(true);

        if (args!=null) {
            int idSelect = args.getInt(SELECTED_RADIO_KEY, -1);

            if (idSelect != -1)
                return new ModelLoader<Client>(getHostActivity(), Client.class, getRequestFrom(idSelect), false);
        }

        return new ModelLoader<Client>(getHostActivity(), Client.class, Client.getAllMain(), true);
    }

    @Override
    public void onLoadFinished(Loader<List<Client>> loader, List<Client> data) {

        setVisibilityProgressBar(false);

        if (getAdapter() == null ) {
            setAdapter(new ClientAdapter(getHostActivity(), R.layout.item_client, data));
            return;
        }

        getAdapter().clear();
        getAdapter().addAll(data);
        getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Client>> loader) {
        getAdapter().clear();
        getAdapter().notifyDataSetChanged();
        setVisibilityProgressBar(true);
    }
}
