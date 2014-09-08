package com.leexplorer.app.views;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.leexplorer.app.R;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class CroutonCustomView {

  @InjectView(R.id.message_text) TextView messageText;

  private Activity activity;
  private int duration;
  private int messageId;
  private int fragmentId;

  public CroutonCustomView(Activity activity, int messageId) {
    this(activity, messageId, 2000, 0);
  }

  public CroutonCustomView(Activity activity, int messageId, int duration) {
    this(activity, messageId, duration, 0);
  }

  public CroutonCustomView(Activity activity, int messageId, int duration, int fragmentId) {
    this.activity = activity;
    this.fragmentId = fragmentId;
    this.duration = duration;
    this.messageId = messageId;
  }

  public static void cancelAllCroutons() {
    Crouton.cancelAllCroutons();
  }

  public void show() {
    LayoutInflater inflater = activity.getLayoutInflater();

    View rootView = inflater.inflate(R.layout.crouton_message, null);
    ButterKnife.inject(this, rootView);

    messageText.setText(getText());

    Configuration croutonConfiguration = new Configuration.Builder().setDuration(duration).build();

    Crouton.make(activity, rootView, fragmentId, croutonConfiguration).show();
  }

  private String getText() {
    return activity.getApplicationContext().getString(messageId);
  }
}
