package com.leexplorer.app.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.models.Gallery;

public class GalleryActivity extends BaseActivity implements GalleryFragment.Callbacks {
  public static final String GALLERY_KEY = "gallery";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery);

    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.flGalleryDetailView);

    if (fragment == null) {
      Gallery gallery = getIntent().getParcelableExtra(GALLERY_KEY);
      getSupportActionBar().setTitle(gallery.getName());
      fragment = GalleryFragment.newInstance(gallery);
      fm.beginTransaction().add(R.id.flGalleryDetailView, fragment).commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.gallery, menu);
    return true;
  }

  //    @Override
  //    public boolean onOptionsItemSelected(MenuItem item) {
  //        // Handle action bar item clicks here. The action bar will
  //        // automatically handle clicks on the Home/Up button, so long
  //        // as you specify a parent activity in AndroidManifest.xml.
  //        int id = item.getItemId();
  //        if (id == R.id.action_settings) {
  //            return true;
  //        }
  //        return super.onOptionsItemSelected(item);
  //    }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
  }
}
