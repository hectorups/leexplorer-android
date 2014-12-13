package com.leexplorer.app.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import com.leexplorer.app.R;
import com.leexplorer.app.events.ShareEvent;
import java.util.ArrayList;
import java.util.List;

public class ShareManager {

  private Context context;

  private static final String[] ALLOWED_SHARED_PACKAGE_NAMES = {
      "facebook", "twitter", "mail", "com.google.android.gm", "instagram", "pinterest", "yahoo",
      "whatsapp"
  };

  public ShareManager(Context context) {
    this.context = context;
  }

  public Intent shareIntent(ShareEvent event) {

    List<Intent> targets = new ArrayList<>();
    Intent template = new Intent(Intent.ACTION_SEND);
    template.setType("image/*");
    List<ResolveInfo> candidates = context.getPackageManager().queryIntentActivities(template, 0);

    for (ResolveInfo candidate : candidates) {
      String packageName = candidate.activityInfo.packageName;
      boolean allowed = false;
      for (String allowedPackage : ALLOWED_SHARED_PACKAGE_NAMES) {
        if (packageName.contains(allowedPackage)) {
          allowed = true;
          break;
        }
      }

      if (!allowed) {
        continue;
      }

      Intent target = new Intent(android.content.Intent.ACTION_SEND);
      target.setType("*/*");
      target.putExtra(Intent.EXTRA_STREAM, event.getBmpUri());
      target.putExtra(Intent.EXTRA_SUBJECT, event.getTitle());
      target.putExtra(Intent.EXTRA_TEXT, event.getDescription());
      target.setPackage(packageName);

      if (packageName.contains("com.facebook") || packageName.contains("com.instagram")) {
        target.setType("image/*");
      }

      targets.add(target);
    }

    Intent chooser =
        Intent.createChooser(targets.remove(0), context.getString(R.string.share_chooser));
    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[] {
    }));

    return chooser;
  }
}
