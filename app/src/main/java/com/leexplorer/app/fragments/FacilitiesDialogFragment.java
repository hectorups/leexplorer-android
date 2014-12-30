package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.FacilitiesAdapter;
import com.leexplorer.app.models.Facility;
import java.util.ArrayList;
import java.util.List;

import static com.leexplorer.app.core.AppConstants.FACILITIES_IMG_MAP;

public class FacilitiesDialogFragment extends BaseDialogFragment {
  private final static String TAG = "leexplorer.com.facilitiesDialogFragment";

  @InjectView(R.id.lvFacilitiesDetails) ListView lvFacilitiesDetails;
  private FacilitiesAdapter facilitiesAdapter;

  public FacilitiesDialogFragment() {
  }

  public static FacilitiesDialogFragment newInstance(ArrayList<String> facilities) {
    FacilitiesDialogFragment frag = new FacilitiesDialogFragment();
    Bundle args = new Bundle();
    args.putStringArrayList("facilities", facilities);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    List<Facility> facilities = new ArrayList<Facility>();
    List<String> facilitiesStr = getArguments().getStringArrayList("facilities");
    for (String facilityStr : facilitiesStr) {
      facilities.add(new Facility(facilityStr, FACILITIES_IMG_MAP.get(facilityStr)));
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

  @Override public String getScreenName() {
    return TAG;
  }
}
