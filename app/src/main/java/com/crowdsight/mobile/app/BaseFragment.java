package com.crowdsight.mobile.app;

import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by pankaj on 23/07/14.
 */
public class BaseFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

    /**
     * Default empty constructor
     */
    public BaseFragment(){
        //
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
        public void onFragmentInteraction(String id);
        public void onFragmentInteraction(int actionId);
    }
}
