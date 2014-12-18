package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import butterknife.ButterKnife;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import java.lang.reflect.Field;
import javax.inject.Inject;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

abstract public class BaseFragment extends Fragment {
  private static final String TAG = "com.leexplorer.app.fragments.BaseFragment";
  private final CompositeSubscription compositeSubscription = new CompositeSubscription();
  @Inject EventReporter eventReporter;

  private static final Field sChildFragmentManagerField;

  static {
    Field f = null;
    try {
      f = Fragment.class.getDeclaredField("mChildFragmentManager");
      f.setAccessible(true);
    } catch (NoSuchFieldException e) {
      Log.e(TAG, "Error getting mChildFragmentManager field", e);
    }
    sChildFragmentManagerField = f;
  }

  @Override
  public void onDetach() {
    super.onDetach();

    if (sChildFragmentManagerField != null) {
      try {
        sChildFragmentManagerField.set(this, null);
      } catch (Exception e) {
        Log.e(TAG, "Error setting mChildFragmentManager field", e);
      }
    }
  }

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

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.reset(this);
  }

  abstract public String getScreenName();

  public void addSubscription(final Subscription subscription) {
    compositeSubscription.add(subscription);
  }

  public CompositeSubscription getCompositeSubscription() {
    return compositeSubscription;
  }
}
