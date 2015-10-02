package ru.prokatvros.veloprokat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.ui.fragments.InventoryFragment;

public class InventoryActivity extends BaseActivity {

    private final static String INVENTORY_KEY = "inventory_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Inventory inventory = null;
        Intent intent = getIntent();

        if ( intent != null )
            inventory = intent.getParcelableExtra(INVENTORY_KEY);

        if (inventory != null)
            replaceFragment(InventoryFragment.newInstance(inventory), false);

    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    public static void  startInventoryActivity(/*@NotNull*/ Context context, /*@Nullable*/ Inventory inventory) {
        Intent intent = new Intent(context, InventoryActivity.class);
        intent.putExtra(INVENTORY_KEY, inventory);
        context.startActivity(intent);
    }
}
