package com.leexplorer.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import com.leexplorer.app.R;
import com.leexplorer.app.events.ArtworkClickedEvent;
import com.leexplorer.app.fragments.ArtworkListFragment;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.Gallery;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ArtworkListActivity extends BaseActivity implements ArtworkListFragment.Callbacks {

  public static final String EXTRA_GALLERY = "extra_gallery";
  public static final String EXTRA_FROM_NOTIFICATION = "extra_from_notification";
  public static final int ARTWORK_DETAIL_REQUEST = 0;

  private Gallery gallery;
  private ArtworkListFragment fragment;

  @Inject Bus bus;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_artwork_list);

    FragmentManager fm = getSupportFragmentManager();
    fragment = (ArtworkListFragment) fm.findFragmentById(R.id.container);

    if (savedInstanceState == null) {
      gallery = getIntent().getParcelableExtra(EXTRA_GALLERY);
    } else {
      gallery = savedInstanceState.getParcelable(EXTRA_GALLERY);
    }

    setTitle(gallery.getName());

    if (fragment == null) {
      fragment = ArtworkListFragment.newInstance(gallery);
      fm.beginTransaction().add(R.id.container, fragment).commit();
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    bus.unregister(this);
    super.onPause();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_GALLERY, gallery);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:

        if( isTabletMode() ) {
          Intent i = new Intent(this, GalleryListActivity.class);
          i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          i.putExtra(GalleryListActivity.EXTRA_INITIAL_GALLERY, gallery);
          startActivity(i);
        } else {
          Intent i = new Intent(this, GalleryActivity.class);
          i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          i.putExtra(GalleryActivity.GALLERY_KEY, gallery);
          startActivity(i);
        }

        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Subscribe public void onArtworkClicked(ArtworkClickedEvent event) {
    Intent i = new Intent(this, ArtworkActivity.class);
    i.putExtra(ArtworkActivity.EXTRA_ARTWORK, event.getArtwork());
    i.putExtra(ArtworkActivity.EXTRA_ARTWORKS, new ArrayList<>(event.getArtworks()));
    startActivityForResult(i, ARTWORK_DETAIL_REQUEST);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == ARTWORK_DETAIL_REQUEST && resultCode == Activity.RESULT_OK) {
      List<Artwork> newArtworks = data.getParcelableArrayListExtra(ArtworkActivity.EXTRA_ARTWORKS);
      if (newArtworks != null && fragment != null) {
        fragment.updateAdapterDataset(newArtworks);
      }
    }
  }
}
