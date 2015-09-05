package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import ru.prokatvros.veloprokat.R;

public abstract class BaseListFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_base_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);

        ImageButton ibAdd = (ImageButton) view.findViewById(R.id.ibAdd);

        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick();
            }
        });

        ListView lvList = (ListView) view.findViewById(R.id.lvList);
        ArrayAdapter adapter = getAdapter();
        adapter.getCount();
        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseListFragment.this.onItemClick(parent, view, position, id);
            }
        });

        return view;
    }


    public abstract void onAddClick ();
    public abstract void onItemClick (AdapterView<?> parent, View view, int position, long id);
    public abstract ArrayAdapter getAdapter ();


}
