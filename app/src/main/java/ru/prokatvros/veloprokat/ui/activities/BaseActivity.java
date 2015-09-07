package ru.prokatvros.veloprokat.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.ui.fragments.ProfileFragment;

public abstract class BaseActivity extends AppCompatActivity {

    private final static String SWITCH_FRAGMENT_ADD = "switch_fragment_add";
    private final static String SWITCH_FRAGMENT_REPLACE = "switch_fragment_replace";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public ProgressDialog getProgressDialog() {

        if( progressDialog == null ) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.please_wait));
        }

        return progressDialog;
    }

    public void replaceFragment(Fragment fragment, boolean saveInBackStack) {
        updateFragment(fragment,SWITCH_FRAGMENT_REPLACE,saveInBackStack);
    }

    public void addFragment(ProfileFragment fragment, boolean saveInBackStack) {
        updateFragment(fragment, SWITCH_FRAGMENT_ADD, saveInBackStack);
    }

    private void updateFragment(Fragment fragment, String keySwitchFragment, boolean saveInBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        switch (keySwitchFragment) {
            case SWITCH_FRAGMENT_ADD:
                transaction.add(getContainer(), fragment);
                break;
            case SWITCH_FRAGMENT_REPLACE:
                transaction.replace(getContainer(), fragment);
                break;
        }

        if (saveInBackStack)
            transaction.addToBackStack(fragment.getClass().getName());

        transaction.commit();
    }

    protected abstract int getContainer();



    /*public void onBackPressed() {
        super.onBackPressed();
    } */

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
