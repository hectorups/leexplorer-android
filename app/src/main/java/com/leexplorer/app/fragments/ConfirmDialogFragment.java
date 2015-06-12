package com.leexplorer.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
  private static final String OK_EXTRA = "ok";
  private static final String CANCEL_EXTRA = "cancel";

  @Inject Bus bus;

  private String title;
  private String text;
  private String caller;
  private String ok;
  private String cancel;

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

  public static ConfirmDialogFragment newInstance(String caller, String title, String text,
      String ok, String cancel) {
    ConfirmDialogFragment f = newInstance(caller, title, text);
    Bundle args = f.getArguments();
    args.putString(OK_EXTRA, ok);
    args.putString(CANCEL_EXTRA, cancel);
    f.setArguments(args);

    return f;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    ((LeexplorerApplication) getActivity().getApplication()).getComponent().inject(this);
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      title = savedInstanceState.getString(TITLE_EXTRA);
      text = savedInstanceState.getString(TEXT_EXTRA);
      caller = savedInstanceState.getString(CALLER_EXTRA);
      ok = savedInstanceState.getString(OK_EXTRA);
      cancel = savedInstanceState.getString(CANCEL_EXTRA);
    } else {
      title = getArguments().getString(TITLE_EXTRA);
      text = getArguments().getString(TEXT_EXTRA);
      caller = getArguments().getString(CALLER_EXTRA);

      ok = getArguments().getString(OK_EXTRA);
      if (ok == null) {
        ok = getResources().getString(R.string.confirm_dialog_ok);
      }

      cancel = getArguments().getString(CANCEL_EXTRA);
      if (cancel == null) {
        cancel = getResources().getString(R.string.confirm_dialog_cancel);
      }
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(TITLE_EXTRA, title);
    outState.putString(TEXT_EXTRA, text);
    outState.putString(CALLER_EXTRA, caller);
    outState.putString(OK_EXTRA, ok);
    outState.putString(CANCEL_EXTRA, cancel);
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
    alertDialogBuilder.setTitle(title);
    alertDialogBuilder.setMessage(text);
    alertDialogBuilder.setPositiveButton(ok, onPositiveButtonListener());
    alertDialogBuilder.setNegativeButton(cancel, onNegativeButtonListener());

    return alertDialogBuilder.create();
  }

  public DialogInterface.OnClickListener onPositiveButtonListener() {
    return new DialogInterface.OnClickListener() {

      @Override public void onClick(DialogInterface dialog, int which) {
        bus.post(new ConfirmDialogResultEvent(caller, true));
      }
    };
  }

  public DialogInterface.OnClickListener onNegativeButtonListener() {
    return new DialogInterface.OnClickListener() {

      @Override public void onClick(DialogInterface dialog, int which) {
        bus.post(new ConfirmDialogResultEvent(caller, false));
      }
    };
  }
}