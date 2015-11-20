package ru.prokatvros.veloprokat.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.ui.fragments.AddClientFragment;
import ru.prokatvros.veloprokat.ui.fragments.ClientFragment;

public class ClientActivity extends BaseActivity {

    private final static String CLIENT_KEY = "client_key";

    public final static String KEY_ADD_CLIENT = "key_add_client";

    public final static String KEY_SEARCHING_NUMBER = "param_searching_number";

    public final static String KEY_ACTION = "param_action";

    public final static int ACTION_ADD_CLIENT = 0;

    public final static int ACTION_INFO_CLIENT = 1;

    public final static int ACTION_REDACT_CLIENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Client client = null;
        Intent intent = getIntent();

        Fragment fragment = AddClientFragment.newInstance( intent.getStringExtra( KEY_SEARCHING_NUMBER ) );

        if ( intent != null && intent.getParcelableExtra(CLIENT_KEY) != null) {
            client = intent.getParcelableExtra(CLIENT_KEY);
            if ( client != null )
                fragment = ClientFragment.newInstance(client);
        }

        //Fragment fragment = getFragment( intent.getIntExtra( KEY_ACTION, ACTION_ADD_CLIENT ) );

        replaceFragment(fragment, false);

    }

    /* protected Fragment getFragment(int action) {

        if ( action == ACTION_ADD_CLIENT ) {
            return AddClientFragment.newInstance( getIntent().getStringExtra(KEY_SEARCHING_NUMBER) );

        } else if (action == ACTION_INFO_CLIENT) {

            Client client = getIntent().getParcelableExtra(CLIENT_KEY);

            if ( client != null )
                return ClientFragment.newInstance(client);


        } else if (action == ACTION_REDACT_CLIENT) {
            Client client = getIntent().getParcelableExtra(CLIENT_KEY);
            return AddClientFragment.newInstance( client );
        }

        throw new Error("Unavialable action !!!");
    } */


    @Override
    protected int getContainer() {
        return R.id.container;
    }

    /* public static void startAddClientActivity( Context context ) {
        Intent intent = new Intent(context, ClientActivity.class);
        intent.putExtra(KEY_ACTION, ACTION_ADD_CLIENT);
        context.startActivity(intent);
    } */

    public static void  startClientActivity(/*@NotNull*/ Context context, /*@Nullable*/ Client client) {
        Intent intent = new Intent(context, ClientActivity.class);
        intent.putExtra(CLIENT_KEY, client);
        intent.putExtra(KEY_ACTION, ACTION_INFO_CLIENT);
        context.startActivity(intent);
    }

    //public static void  startRedactActivity(/*@NotNull*/ Context context, /*@Nullable*/ Client client) {
        //Intent intent = new Intent(context, ClientActivity.class);
        //intent.putExtra(CLIENT_KEY, client);
        //intent.putExtra(KEY_ACTION, ACTION_REDACT_CLIENT);
        //context.startActivity(intent);
    //}

}
