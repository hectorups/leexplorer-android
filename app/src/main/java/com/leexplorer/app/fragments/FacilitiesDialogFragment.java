package com.leexplorer.app.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.leexplorer.app.R;
import com.leexplorer.app.adapters.FacilitiesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by deepakdhiman on 3/3/14.
 */
public class FacilitiesDialogFragment extends DialogFragment {

    @InjectView(R.id.lvFacilitiesDetails)
    private ListView lvFacilitiesDetails;
    private FacilitiesAdapter facilitiesAdapter;

    public FacilitiesDialogFragment() {
    }

    public static FacilitiesDialogFragment newInstance(List<String> facilities) {
        FacilitiesDialogFragment frag = new FacilitiesDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("facilities", (ArrayList<String>) facilities);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facilitiesAdapter = new FacilitiesAdapter(savedInstanceState.getStringArrayList("facilities"));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facilities_dialog, container);
        ButterKnife.inject(this, view);
        getDialog().setTitle("Facilities");
        return view;
    }

}
