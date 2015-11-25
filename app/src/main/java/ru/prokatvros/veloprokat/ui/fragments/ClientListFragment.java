package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.ui.activities.ClientActivity;
import ru.prokatvros.veloprokat.ui.adapters.ClientAdapter;


public class ClientListFragment extends BaseListFragment implements CompoundButton.OnCheckedChangeListener {

    private BroadcastReceiver broadcastReceiver;

    RadioButton rbMain;
    RadioButton rbVip;
    RadioButton rbBlackList;

    private List<Client> clientList = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SET_ADAPTER) {
                setAdapter(new ClientAdapter(BikerentalApplication.getInstance(), R.layout.item_client, clientList));
                setVisibilityProgressBar(false);
            }
        }
    };

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
        setVisibilityProgressBar(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //clientList = Client.getAllMain();
                clientList = Client.getAll();
                handler.sendMessage( handler.obtainMessage( SET_ADAPTER) );
            }
        }).start();
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

        IntentFilter intFilter = new IntentFilter(ACTION_LOADED_DATA);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (clientList != null)
                    setAdapter(new ClientAdapter(context, R.layout.item_client, clientList));
            }
        };

        getHostActivity().registerReceiver(broadcastReceiver, intFilter);

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

        switch (buttonView.getId()) {
            default:
            case R.id.rbMain:
                //clientList = Client.getAllMain();
                clientList = Client.getAll();
                break;

            case R.id.rbVip:
                clientList = Client.getAllVip();
                break;

            case R.id.rbBlackList:
                clientList = Client.getAllBlackList();
                break;
        }

        setAdapter( new ClientAdapter( getHostActivity(), R.layout.item_client, clientList ) );

    }

}
