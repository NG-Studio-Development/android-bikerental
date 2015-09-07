package ru.prokatvros.veloprokat.ui.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.ui.fragments.AddRentFragment;
import ru.prokatvros.veloprokat.ui.fragments.RentFragment;

public class RentActivity extends BaseActivity {

    private final static String RENT_KEY = "rent_key";

    public final static String CREATE_RENT = "create_rent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Rent rent = null;
        Intent intent = getIntent();
        Fragment fragment = new AddRentFragment();

        if ( intent != null ) {
            rent = intent.getParcelableExtra(RENT_KEY);

            if ( rent != null )
                fragment = RentFragment.newInstance(rent);
            else
                Rent.createRentInPool(RentActivity.CREATE_RENT);
        } else {
            Rent.createRentInPool(RentActivity.CREATE_RENT);
        }

        if (savedInstanceState == null)
            replaceFragment(fragment, false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Rent.removeRentInPool(RentActivity.CREATE_RENT);
    }

    public static void  startRentActivity(/*@NotNull*/ Context context, /*@Nullable*/ Rent rent) {
        Intent intent = new Intent(context, RentActivity.class);
        if ( rent != null )
            intent.putExtra (RENT_KEY, rent);
        context.startActivity(intent);
    }


    @Override
    protected int getContainer() {
        return R.id.container;
    }
}
