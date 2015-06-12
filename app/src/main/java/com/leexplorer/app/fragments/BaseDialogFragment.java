package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.leexplorer.app.core.ApplicationComponent;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import javax.inject.Inject;

abstract public class BaseDialogFragment extends DialogFragment {
  @Inject EventReporter eventTracker;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectComponent(((LeexplorerApplication) getActivity().getApplicationContext()).getComponent());
  }

  abstract protected void injectComponent(ApplicationComponent component);

  abstract public String getScreenName();
}
