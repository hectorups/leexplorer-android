package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.leexplorer.app.LeexplorerApplication;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hectormonserrate on 10/05/14.
 */
public class BaseFragment extends Fragment {
  private final CompositeSubscription compositeSubscription = new CompositeSubscription();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((LeexplorerApplication) getActivity().getApplication()).inject(this);
  }

  @Override public void onDestroy() {
    compositeSubscription.unsubscribe();
    super.onDestroy();
  }

  public void addSubscription(final Subscription subscription) {
    compositeSubscription.add(subscription);
  }

  public CompositeSubscription getCompositeSubscription() {
    return compositeSubscription;
  }
}
