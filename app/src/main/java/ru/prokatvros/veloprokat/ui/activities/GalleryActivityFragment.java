package ru.prokatvros.veloprokat.ui.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.prokatvros.veloprokat.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class GalleryActivityFragment extends Fragment {

    public GalleryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }
}
