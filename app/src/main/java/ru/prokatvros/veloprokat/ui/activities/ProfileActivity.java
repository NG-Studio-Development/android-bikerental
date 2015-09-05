package ru.prokatvros.veloprokat.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.ui.fragments.ProfileFragment;

//import android.widget.Toolbar;

//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;

public class ProfileActivity extends BaseActivity {



    public static void  startProfileActivity(/*@NotNull*/ Context context /*@Nullable*/ /*ContactStep contact*/) {
        Intent intent = new Intent(context, ProfileActivity.class);
        //intent.putExtra(WhereAreYouAppConstants.KEY_CONTACT,contact);
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            //addFragment(ProfileFragment.class,getIntent().getExtras(),false);
            addFragment(new ProfileFragment(), false);
        }
    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }

    /*public void initActionBar(boolean isMyProfile) {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setBackgroundDrawable(new ColorDrawable());
        actionBarHolder = new ActionBarHolder();
        actionBar.setCustomView(actionBarHolder.initHolder(this));
        actionBarHolder.setActionBarIcon(R.drawable.drawable_ic_back);
        actionBarHolder.setActionBarIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        actionBarHolder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        actionBarHolder.enterState(isMyProfile ? 0 : ActionBarHolder.STATE_CONTACT_PROFILE);
    }*/

    /*public ActionBarHolder getActionBarHolder() {
        return actionBarHolder;
    }*/

    /*@Override
    public void onBackPressed() {
        if(actionBarHolder.collapseSearchField(actionBarHolder.findViewById(R.id.ivEdit))) {
            return;
        }

        super.onBackPressed();
    }*/
}
