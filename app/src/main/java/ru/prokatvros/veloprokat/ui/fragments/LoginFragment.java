package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.ui.activities.LoginActivity;

public class LoginFragment extends BaseFragment<LoginActivity> {

    public interface OnLoginListener {
        void onLogin(String login, String password);
    }

    OnLoginListener onLoginListener;

    public LoginFragment() {}

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_login;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onLoginListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLoginListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        final TextView tvLogin = (TextView) view.findViewById(R.id.tvLogin);
        final TextView tvPassword = (TextView) view.findViewById(R.id.tvPassword);
        Button buttonLogin = (Button) view.findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( tvLogin.getText().toString().isEmpty() ||
                        tvPassword.getText().toString().isEmpty() ) {
                    Toast.makeText(getHostActivity(), getString(R.string.warning_can_not_have_empty_field),Toast.LENGTH_LONG).show();
                    return;
                }

                onLoginListener.onLogin(tvLogin.getText().toString(), tvPassword.getText().toString());

            }
        });

        return view;
    }





}
