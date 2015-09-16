package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.ui.activities.MainActivity;
import ru.prokatvros.veloprokat.ui.adapters.PointAdapter;

public class SelectPointFragment extends BaseFragment {

    public SelectPointFragment() { }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_select_point;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_point, container, false);
        ListView lvPoint = (ListView) view.findViewById(R.id.lvPoints);
        final PointAdapter adapter = new PointAdapter(getHostActivity(), R.layout.item_base, PointAdapter.initListForDEBUG());
        lvPoint.setAdapter(adapter);

        lvPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Point point = adapter.getItem(position);
                Admin admin = BikerentalApplication.getInstance().getAdmin();
                admin.point = point;
                admin.save();

                MainActivity.startMainActivity(getHostActivity());
                getHostActivity().finish();

            }
        });

        return view;
    }

}
