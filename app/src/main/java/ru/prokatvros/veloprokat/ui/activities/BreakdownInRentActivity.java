package ru.prokatvros.veloprokat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.BreakdownInRent;
import ru.prokatvros.veloprokat.ui.fragments.BreakdownInRentFragment;

public class BreakdownInRentActivity extends BaseActivity {

    private static BreakdownInRent breakdownInRent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        replaceFragment(BreakdownInRentFragment.newInstance(breakdownInRent), false);
    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_breakdown_in_rent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static void  startClientActivity(/*@NotNull*/ Context context, /*@Nullable*/ BreakdownInRent breakdownInRent) {
        Intent intent = new Intent(context, BreakdownInRentActivity.class);
        BreakdownInRentActivity.breakdownInRent = breakdownInRent;
        //intent.putExtra(CLIENT_KEY, client);
        context.startActivity(intent);
    }

}
