package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;


public class BlankFragment extends Fragment {

    EditText etText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        etText = (EditText) view.findViewById(R.id.etText);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new Listener());

        return view;
    }


    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            List<Inventory> list = Inventory.getByName(etText.getText().toString());
            list.size();
            int i;
            i = 1+1;

        }
    }

}
