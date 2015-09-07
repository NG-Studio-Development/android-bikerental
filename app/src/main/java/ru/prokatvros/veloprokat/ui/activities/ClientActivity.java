package ru.prokatvros.veloprokat.ui.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.ui.fragments.AddClientFragment;
import ru.prokatvros.veloprokat.ui.fragments.ClientFragment;

public class ClientActivity extends BaseActivity {

    private static String CLIENT_KEY = "client_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Client client = null;
        Intent intent = getIntent();
        Fragment fragment = new AddClientFragment();

        if ( intent != null ) {
            client = (Client) intent.getSerializableExtra(CLIENT_KEY);

            if ( client != null )
                fragment = ClientFragment.newInstance(client);
        }

        replaceFragment(fragment, false);

    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    public static void  startClientActivity(/*@NotNull*/ Context context, /*@Nullable*/ Client client) {
        Intent intent = new Intent(context, ClientActivity.class);
        intent.putExtra(CLIENT_KEY, client);
        context.startActivity(intent);
    }

}
