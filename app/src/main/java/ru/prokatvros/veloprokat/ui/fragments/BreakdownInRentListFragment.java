package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.BreakdownInRent;
import ru.prokatvros.veloprokat.model.requests.BreakdowndInRentRequest;
import ru.prokatvros.veloprokat.ui.activities.BreakdownInRentActivity;
import ru.prokatvros.veloprokat.ui.adapters.BreakdownInRentAdapter;


public class BreakdownInRentListFragment extends BaseListFragment {

    public BreakdownInRentListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        BreakdowndInRentRequest request = BreakdowndInRentRequest.requestAllBreakdowns(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Error")) {
                    Toast.makeText(getHostActivity(), "Can not load data", Toast.LENGTH_LONG).show();
                    return;
                }

                Gson gson = new Gson();
                List<BreakdownInRent> listBreakdownInRents = gson.fromJson(response, new TypeToken<List<BreakdownInRent>>(){}.getType());
                BreakdownInRentAdapter adapter = new BreakdownInRentAdapter(getHostActivity(), R.layout.item_base, listBreakdownInRents);
                setAdapter(adapter);
                getHostActivity().getProgressDialog().hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getHostActivity(), "BreakdowndInRent error callback", Toast.LENGTH_LONG).show();
                getHostActivity().getProgressDialog().hide();
                error.printStackTrace();
            }
        });
        getHostActivity().getProgressDialog().show();
        Volley.newRequestQueue(getHostActivity()).add(request);

        return view ;
    }

    @Override
    public void onAddClick() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BreakdownInRent BreakdownInRent  = (BreakdownInRent) getAdapter().getItem(position);
        BreakdownInRentActivity.startClientActivity(getHostActivity(), BreakdownInRent);
    }

}
