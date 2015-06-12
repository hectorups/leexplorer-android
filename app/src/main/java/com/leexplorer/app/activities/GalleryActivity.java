package com.leexplorer.app.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.leexplorer.app.R;
import com.leexplorer.app.core.ApplicationComponent;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.models.Gallery;

public class GalleryActivity extends BaseActivity {
  public static final String GALLERY_KEY = "gallery";
  private Gallery gallery;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery);
    setupActionBar();

    if (savedInstanceState == null) {
      gallery = getIntent().getParcelableExtra(GALLERY_KEY);
    } else {
      gallery = savedInstanceState.getParcelable(GALLERY_KEY);
    }

    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.flGalleryDetailView);

    if (fragment == null) {
      getSupportActionBar().setTitle(gallery.getName());
      fragment = GalleryFragment.newInstance(gallery);
      fm.beginTransaction().add(R.id.flGalleryDetailView, fragment).commit();
    }
  }

  @Override protected void injectComponent(ApplicationComponent component) {
    component.inject(this);
  }

  @Override public boolean showHomeButton() {
    return true;
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(GALLERY_KEY, gallery);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    finish();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
  }
}
