package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.leexplorer.app.R;
import com.leexplorer.app.adapters.FacilitiesAdapter;
import com.leexplorer.app.models.Facility;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.leexplorer.app.util.AppConstants.FACILITIES_IMG_MAP;

/**
 * Created by deepakdhiman on 3/3/14.
 */
public class FacilitiesDialogFragment extends DialogFragment {

    @InjectView(R.id.lvFacilitiesDetails)
    ListView lvFacilitiesDetails;
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
        List<Facility> facilities = new ArrayList<Facility>();
        List<String> facilitiesStr = getArguments().getStringArrayList("facilities");
        for(String facilityStr:facilitiesStr){
            facilities.add(new Facility(facilityStr,FACILITIES_IMG_MAP.get(facilityStr)));
        }
        facilitiesAdapter = new FacilitiesAdapter(this, facilities);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facilities_dialog, container);
        ButterKnife.inject(this, view);
        getDialog().setTitle("Facilities");
        lvFacilitiesDetails.setAdapter(facilitiesAdapter);
        return view;
    }

}
