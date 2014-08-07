package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.core.EventReporter;
import javax.inject.Inject;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

abstract public class BaseFragment extends Fragment {
  private final CompositeSubscription compositeSubscription = new CompositeSubscription();
  @Inject EventReporter eventReporter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((LeexplorerApplication) getActivity().getApplication()).inject(this);
  }

  @Override public void onDestroy() {
    compositeSubscription.unsubscribe();
    super.onDestroy();
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    eventReporter.screenViewed(getScreenName());
  }

  abstract public String getScreenName();

  public void addSubscription(final Subscription subscription) {
    compositeSubscription.add(subscription);
  }

  public CompositeSubscription getCompositeSubscription() {
    return compositeSubscription;
  }
}
