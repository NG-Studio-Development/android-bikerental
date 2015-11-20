package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Shift;
import ru.prokatvros.veloprokat.ui.adapters.ShiftAdapter;

public class ListShiftFragment extends BaseFragment {

    protected static List<Shift> shiftList = null;

    public static ListShiftFragment newInstance(List<Shift> shiftList) {
        ListShiftFragment fragment = new ListShiftFragment();
        ListShiftFragment.shiftList = shiftList;

        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args); */

        return fragment;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_list_shift;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_shift, container, false);

        ListView lvShift = (ListView) view.findViewById(R.id.lvShift);

        lvShift.setAdapter(new ShiftAdapter(getHostActivity(),R.layout.item_shift, shiftList));

        return view;
    }
}
