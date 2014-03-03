package com.leexplorer.app.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.leexplorer.app.R;

/**
 * Created by deepakdhiman on 3/3/14.
 */
public class FacilitiesDialogFragment extends DialogFragment {



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facilities_dialog, container);
        getDialog().setTitle("Facilities");
        return view;
    }

}
