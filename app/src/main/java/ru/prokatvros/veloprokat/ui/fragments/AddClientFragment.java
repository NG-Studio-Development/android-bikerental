package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;

public class AddClientFragment extends BaseFragment {

    public AddClientFragment() {}

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_add_client;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_client, container, false);
        Button button = (Button) view.findViewById(R.id.buttonAdd);

        final EditText etName = (EditText) view.findViewById(R.id.etName);
        final EditText etSurname = (EditText) view.findViewById(R.id.etSurname);
        final EditText etPhone = (EditText) view.findViewById(R.id.etSearch);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClient(etName.getText().toString(),
                        etSurname.getText().toString(),
                        etPhone.getText().toString());

                getHostActivity().onBackPressed();
            }
        } );

        return view;
    }

    private void addClient( String name, String surname, String phone ) {

        if ( name.isEmpty() || surname.isEmpty() || phone.isEmpty() ) {
            Toast.makeText( getHostActivity(), getString(R.string.warning_can_not_have_empty_field), Toast.LENGTH_LONG ).show();
            return;
        }

        Client client = new Client();
        client.name = name;
        client.surname = surname;
        client.phone = phone;
        client.save();
    }
}
