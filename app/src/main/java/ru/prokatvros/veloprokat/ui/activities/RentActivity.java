package ru.prokatvros.veloprokat.ui.activities;

import android.os.Bundle;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.ui.fragments.RentFragment;

public class RentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (savedInstanceState == null)
            replaceFragment(new RentFragment(), false);
    }



    @Override
    protected int getContainer() {
        return R.id.container;
    }

}
