package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.ui.activities.MainActivity;
import ru.prokatvros.veloprokat.ui.adapters.PointAdapter;

public class SelectPointFragment extends BaseFragment {

    private PointAdapter adapter;

    public SelectPointFragment() { }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_select_point;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_point, container, false);
        final ListView lvPoint = (ListView) view.findViewById(R.id.lvPoints);

        //MainActivity.exportDatabse("BikeRents3.db");
        MainActivity.exportDatabse("backupname2.db");

        lvPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (adapter == null)
                    return;

                Point point = adapter.getItem(position);
                Admin admin = BikerentalApplication.getInstance().getAdmin();
                admin.save();

                BikerentalApplication.getInstance().setPoint(point);

                MainActivity.startMainActivity(getHostActivity());


                getHostActivity().finish();

            }
        });

        List<Point> pointList = Point.getAll();

        adapter = new PointAdapter(getHostActivity(), R.layout.item_base, pointList);
        lvPoint.setAdapter(adapter);
        /*PointRequest pointRequest = PointRequest.reqGetPoint(new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    int errorCode = jsonResponse.getInt("error_code");
                    if ( errorCode != 0 ) {
                        Toast.makeText(getHostActivity(), getString(R.string.can_not_load_data_from_server), Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<Point> listPoint = new Gson().fromJson(jsonResponse.getJSONObject("data").getJSONArray("points").toString(),
                            new TypeToken<List<Point>>() {
                            }.getType());

                    adapter = new PointAdapter(getHostActivity(), R.layout.item_base, listPoint);
                    lvPoint.setAdapter(adapter);

                    getHostActivity().getProgressDialog().hide();

                } catch(JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                getHostActivity().getProgressDialog().hide();
                Toast.makeText(getHostActivity(), getString(R.string.can_not_load_data_from_server), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });*/

        //getHostActivity().getProgressDialog().show();

        //Volley.newRequestQueue(getHostActivity()).add(pointRequest);

        return view;
    }

}
