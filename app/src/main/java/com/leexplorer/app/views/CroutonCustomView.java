package com.leexplorer.app.views;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.leexplorer.app.R;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class CroutonCustomView {

  @InjectView(R.id.message_text) TextView messageText;
  @InjectView(R.id.message_image) ImageView messageImage;

  private Activity activity;
  private int duration;
  private int messageId;
  private int backgroundColorResourceId;
  private Integer resourceImageId;


  public static CroutonCustomView make(Activity activity, int messageId) {
    return new CroutonCustomView(activity, messageId);
  }

  public CroutonCustomView(Activity activity, int messageId) {
    this.activity = activity;
    this.messageId = messageId;
    this.backgroundColorResourceId = R.color.le_blue;
    this.duration = 3000;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public void setResourceImageId(Integer resourceImageId) {
    this.resourceImageId = resourceImageId;
  }

  public void setBackgroundColorResourceId(int backgroundColorResourceId) {
    this.backgroundColorResourceId = backgroundColorResourceId;
  }

  public static void cancelAllCroutons() {
    Crouton.cancelAllCroutons();
  }

  public void show() {
    LayoutInflater inflater = activity.getLayoutInflater();

    View rootView = inflater.inflate(R.layout.crouton_message, null);
    ButterKnife.inject(this, rootView);

    Resources resources = activity.getApplicationContext().getResources();
    rootView.setBackgroundColor(resources.getColor(backgroundColorResourceId));
    messageText.setText(resources.getString(messageId));

    if (resourceImageId != null) {
      messageImage.setImageDrawable(resources.getDrawable(resourceImageId));
    } else {
      messageImage.setVisibility(View.GONE);
    }

    Configuration croutonConfiguration = new Configuration.Builder().setDuration(duration).build();

    Crouton.make(activity, rootView, 0, croutonConfiguration).show();
  }
}
