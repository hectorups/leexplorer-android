package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.leexplorer.app.LeexplorerApplication;
import com.leexplorer.app.util.EventReporter;
import javax.inject.Inject;

abstract public class BaseDialogFragment extends DialogFragment {
  @Inject EventReporter eventTracker;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((LeexplorerApplication) getActivity().getApplication()).inject(this);
  }

  abstract public String getScreenName();

}
