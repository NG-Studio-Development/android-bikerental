package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.prokatvros.veloprokat.BuildConfig;
import ru.prokatvros.veloprokat.ui.activities.BaseActivity;


public abstract class BaseFragment<ActivityClass extends BaseActivity> extends Fragment {

    public final static String PARAM_ACTION = "param_action";
    public final static String PARAM_ID_ORDER = "param_id_order";
    public final static String PARAM_TIME_IN_MILLIS = "param_time_in_millis";

    public static int sd;
    protected BaseFragment() {
        /* Nothing to do */
	}


	public abstract int getLayoutResID();

    public void findChildViews(/*@NotNull*/ View view) {
        /* Optional */
    }


    @Override
    public void onAttach(Activity activity) {
        if (BuildConfig.DEBUG) {
            //noinspection unchecked
            ActivityClass dummyActivity = (ActivityClass) activity;
            Log.d(((Object) this).getClass().getSimpleName(), String.format("Attached to activity: %s", dummyActivity));
        }
        super.onAttach(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResID(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert view != null;
        findChildViews(view);
    }

    public ActivityClass getHostActivity() {
        //noinspection unchecked
        return (ActivityClass) getActivity();
    }

    /*public ObjectPool<Fragment> getFragmentPool() {
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getHostActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}