package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.leexplorer.app.LeexplorerApplication;

/**
 * Created by hectormonserrate on 10/05/14.
 */
public class BaseFragment extends Fragment {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((LeexplorerApplication) getActivity().getApplication()).inject(this);
    }
}
