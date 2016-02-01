package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.ui.activities.ClientActivity;
import ru.prokatvros.veloprokat.ui.adapters.ContactAdapter;

public class ClientFragment extends BaseFragment<ClientActivity> {

    private final static String TAG = "CLIENT_FRAGMENT";
    private static final String ARG_CLIENT = "client";

    private Client client;

    public static ClientFragment newInstance(Client client) {
        ClientFragment fragment = new ClientFragment ();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CLIENT, client);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            client = getArguments().getParcelable(ARG_CLIENT);
            if (client == null)
                throw new Error("Not found client model !!!");
        }

        if (getHostActivity().getSupportActionBar() != null) {
            // getHostActivity().getSupportActionBar().setTitle(client.name);
            getHostActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
            getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        setHasOptionsMenu(true);

        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        //TextView tvSurname = (TextView) view.findViewById(R.id.tvSurname);
        //TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        TextView tvRentsCount = (TextView) view.findViewById(R.id.tvRentsCount);
        TextView tvSumm = (TextView) view.findViewById(R.id.tvSumm);
        RecyclerView rvDrawerContainer = (RecyclerView) view.findViewById(R.id.rwClientListData);

        // TextView tvBlackList = (TextView) view.findViewById(R.id.tvBlackList);
        //TextView tvVipNumber = (TextView) view.findViewById(R.id.tvVipNumber);
        //RelativeLayout rlVip = (RelativeLayout) view.findViewById(R.id.rlVip);

        ImageView ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);


        String urlToImage = ConstantsBikeRentalApp.URL_SERVER+"/"+client.avatar;
        Log.d(TAG, "Avatar: " + urlToImage);
        ImageLoader.getInstance().displayImage(urlToImage, ivAvatar);

        tvName.setText(client.name);
        tvRentsCount.setText(String.valueOf(client.countRents));
        tvSumm.setText(String.valueOf(client.summ));

        rvDrawerContainer.setVerticalScrollBarEnabled(false);
        rvDrawerContainer.setHorizontalScrollBarEnabled(false);

        rvDrawerContainer.setAdapter(new ContactAdapter(getHostActivity(), client));

        rvDrawerContainer.setLayoutManager(new LinearLayoutManager(getHostActivity()));

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_client, menu);
        menu.findItem(R.id.action_edit).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                getHostActivity().replaceFragment(AddClientFragment.newInstance(client), true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}