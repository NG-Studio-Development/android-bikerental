package ru.prokatvros.veloprokat.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Message;
import ru.prokatvros.veloprokat.model.requests.MessageRequest;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.ui.adapters.ChatAdapter;


public class ChatFragment extends BaseListFragment {

    private final static String TAG = "CHAT_FRAGMENT";

    protected ChatAdapter adapter = null;

    protected EditText etMessage;

    protected MessageRequest request;

    protected BroadcastReceiver broadcastReceiver;

    public ChatFragment() {  }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_chat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        etMessage = ( EditText ) view.findViewById(R.id.etMessage);
        ImageButton ibSendMessage = (ImageButton) view.findViewById(R.id.ibSendMessage);
        ibSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage != null && etMessage.getText().toString().isEmpty()) {
                    Toast.makeText(getHostActivity(),
                            getString(R.string.warning_can_not_have_empty_message),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Message message = new Message();

                if (etMessage != null)
                    message.message = etMessage.getText().toString();

                message.admin = BikerentalApplication.getInstance().getAdmin();
                message.save();
                addMessageToList(message);
                sendMessage(message);
            }
        });

        if (getHostActivity().getSupportActionBar() != null )
            getHostActivity().getSupportActionBar().setTitle(getString(R.string.chat));

        getMassages();


        IntentFilter intFilt = new IntentFilter(ACTION_LOADED_DATA);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String response = intent.getStringExtra(PARAM_RESPONSE);
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                List<Message> listMessage = gson.fromJson(response, new TypeToken<List<Message>>(){}.getType());
                adapter = new ChatAdapter(context, R.layout.item_chat, listMessage);
                setAdapter(adapter);
                postList(adapter.getCount() -1 );

            }
        };

        getHostActivity().registerReceiver(broadcastReceiver, intFilt);

        return view;
    }




    protected void getMassages() {
       request =  MessageRequest.requestAllMessages(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response: " + response);
                Intent intentBroadcast = new Intent(ACTION_LOADED_DATA);
                intentBroadcast.putExtra(PARAM_RESPONSE, response);

                BikerentalApplication.getInstance().sendBroadcast(intentBroadcast);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getHostActivity(), "Server return error", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        BikerentalApplication.getInstance().isNetworkAvailable(new BikerentalApplication.NetworkAvailableListener() {
            @Override
            public void onResponse(boolean isAvailable) {

                if (!isAvailable) {
                    Toast.makeText(getHostActivity(), getString(R.string.warning_check_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }

                Volley.newRequestQueue(BikerentalApplication.getInstance().getApplicationContext()).add(request);
            }
        });
    }


    protected void addMessageToList(Message message) {

        adapter.add(message);
        synchronized (adapter) {
            adapter.notify();
        }

        postList(adapter.getCount() - 1);
    }

    public void postList(final int selectPosition) {
        getLvList().post(new Runnable() {
            @Override
            public void run() {
                getLvList().setSelection(selectPosition);
            }
        });
    }

    protected void sendMessage(Message message) {

        final MessageRequest request = MessageRequest.requestPostMessage(message, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                if (etMessage!=null)
                    etMessage.getText().clear();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getHostActivity(), "Error in post mess: "+error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        BikerentalApplication.getInstance().isNetworkAvailable(new BikerentalApplication.NetworkAvailableListener() {
            @Override
            public void onResponse(boolean isAvailable) {

                if (!isAvailable) {
                    Toast.makeText(getHostActivity(), getString(R.string.warning_check_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }

                Volley.newRequestQueue(getHostActivity()).add(request);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getHostActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onAddClick() { }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }

}
