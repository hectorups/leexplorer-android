package com.leexplorer.app.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.leexplorer.app.R;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.EventReporter;
import javax.inject.Inject;

public class AppPreferencesActivity extends PreferenceActivity {

  @Inject EventReporter eventReporter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preference);

    Preference versionPreference = findPreference("preference_version");
    try {
      PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
      versionPreference.setSummary(pInfo.versionName + " (" + pInfo.versionCode + ")" );
    } catch (PackageManager.NameNotFoundException e) {
      eventReporter.logException(e);
    }

    Preference feedbackPreference = findPreference("preference_feedback");
    feedbackPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + AppConstants.FEEDBACK_EMAIL + "?subject=" +
            Uri.encode((String) getText(R.string.feedback_email_title))));
        startActivity(Intent.createChooser(intent, getString(R.string.feedback_email_chooser)));

        return false;
      }
    });

  }

}
