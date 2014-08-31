package com.leexplorer.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.leexplorer.app.R;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.events.ConfirmDialogResultEvent;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public class ConfirmDialogFragment extends DialogFragment {
  public static final String TAG = "ConfirmDialogFragment";
  private static final String TITLE_EXTRA = "title";
  private static final String TEXT_EXTRA = "text";
  private static final String CALLER_EXTRA = "caller";

  @Inject Bus bus;

  private String title;
  private String text;
  private String caller;

  public static ConfirmDialogFragment newInstance(String caller, String title, String text) {
    ConfirmDialogFragment f = new ConfirmDialogFragment();

    // Supply num input as an argument.
    Bundle args = new Bundle();
    args.putString(TITLE_EXTRA, title);
    args.putString(TEXT_EXTRA, text);
    args.putString(CALLER_EXTRA, caller);
    f.setArguments(args);

    return f;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    ((LeexplorerApplication) getActivity().getApplication()).inject(this);
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      title = savedInstanceState.getString(TITLE_EXTRA);
      text = savedInstanceState.getString(TEXT_EXTRA);
      caller = savedInstanceState.getString(CALLER_EXTRA);
    } else {
      title = getArguments().getString(TITLE_EXTRA);
      text = getArguments().getString(TEXT_EXTRA);
      caller = getArguments().getString(CALLER_EXTRA);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(TITLE_EXTRA, title);
    outState.putString(TEXT_EXTRA, text);
    outState.putString(CALLER_EXTRA, caller);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
    alertDialogBuilder.setTitle(title);
    alertDialogBuilder.setMessage(text);

    Resources resources = getActivity().getResources();
    alertDialogBuilder.setPositiveButton(resources.getString(R.string.confirm_dialog_ok),
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            bus.post(new ConfirmDialogResultEvent(caller));
          }
        });

    alertDialogBuilder.setNegativeButton(resources.getString(R.string.confirm_dialog_cancel),
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    return alertDialogBuilder.create();
  }
}