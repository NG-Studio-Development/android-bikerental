package ru.prokatvros.veloprokat.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.database.ModelLoader;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.activities.ClientActivity;
import ru.prokatvros.veloprokat.ui.activities.RentActivity;
import ru.prokatvros.veloprokat.ui.adapters.ClientAdapter;

public class SearchClientFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Client>> {

    private static final int REQUEST_ADD_NEW_CLIENT = 1;
    protected static final String SUBNUMBER_KEY = "subnumber";

    protected ListView lvClient;
    protected LinearLayout llEmptyList;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_search_base;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_base, container, false);

        if (getHostActivity().getSupportActionBar()!=null) {
            getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getHostActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setHasOptionsMenu(true);

        lvClient = (ListView) view.findViewById(R.id.lvList);
        final EditText etPhone = (EditText) view.findViewById(R.id.etSearch);
        llEmptyList = (LinearLayout) view.findViewById(R.id.llEmptyList);;
        ImageButton ibAdd = (ImageButton) view.findViewById(R.id.ibAdd);
        TextView tvCode = (TextView) view.findViewById(R.id.tvCode);
        tvCode.setVisibility(View.VISIBLE);
        etPhone.setHint(getString(R.string.hint_enter_client_number));

        //final List<Client> clientList = new ArrayList<>();
        //final ClientAdapter adapter = new ClientAdapter(getHostActivity(), R.layout.item_client, clientList);
        //lvClient.setAdapter(adapter);

        lvClient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientAdapter adapter = (ClientAdapter) lvClient.getAdapter();
                Rent.getRentFromPool(RentActivity.CREATE_RENT).client = adapter.getItem(position);
                getHostActivity().onBackPressed();
            }
        });


        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getHostActivity(), ClientActivity.class);
                intent.putExtra(ClientActivity.KEY_SEARCHING_NUMBER, etPhone.getText().toString());
                startActivityForResult(intent, REQUEST_ADD_NEW_CLIENT);
            }
        });

        getLoaderManager().initLoader(0, null, SearchClientFragment.this);

        etPhone.setInputType(InputType.TYPE_CLASS_PHONE);




        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            private Timer timer=new Timer();
            private final long DELAY = 500; // milliseconds

            @Override
            public void afterTextChanged(final Editable s) {

                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {

                                Bundle args = new Bundle();
                                args.putString(SUBNUMBER_KEY, s.toString());
                                getLoaderManager().restartLoader(0, args, SearchClientFragment.this);
                            }
                        },
                        DELAY
                );

            }
        });


        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Client client = null;

        if (data != null)
            client = data.getParcelableExtra(ClientActivity.KEY_ADD_CLIENT);

        if (client != null) {
            Rent.getRentFromPool(RentActivity.CREATE_RENT).client = client;
            getHostActivity().onBackPressed();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rent, menu);
        menu.findItem(R.id.actionClose).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionClose:
                getHostActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Client>> onCreateLoader(int id, Bundle args) {

        String subNumber = null;
        if (args != null)
            subNumber = args.getString(SUBNUMBER_KEY);

        if (subNumber != null)
            return new ModelLoader<Client>(getHostActivity(), Client.class, Client.getFromBySubNumber(subNumber), false);
        else
            return new ModelLoader<Client>(getHostActivity(), Client.class, true);

    }

    @Override
    public void onLoadFinished(Loader<List<Client>> loader, List<Client> data) {

        //setVisibilityProgressBar(false);

        if (data.size() == 0) {
            llEmptyList.setVisibility(View.VISIBLE);
        } else {
            llEmptyList.setVisibility(View.GONE);
        }

        ClientAdapter adapter = (ClientAdapter) lvClient.getAdapter();
        if (adapter == null ) {
            lvClient.setAdapter(new ClientAdapter(getHostActivity(), R.layout.item_client, data));
            return;
        }

        adapter.clear();
        adapter.addAll(data);
        adapter.notifyDataSetChanged();



    }

    @Override
    public void onLoaderReset(Loader<List<Client>> loader) {

        ClientAdapter adapter = (ClientAdapter) lvClient.getAdapter();

        if (adapter == null ) return;

        adapter.clear();
        adapter.notifyDataSetChanged();

        //setVisibilityProgressBar(true);
    }
}
