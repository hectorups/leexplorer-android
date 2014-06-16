package com.leexplorer.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import butterknife.ButterKnife;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.ArtworkViewPagerAdapter;
import com.leexplorer.app.events.ArtworkUpdated;
import com.leexplorer.app.fragments.ArtworkFragment;
import com.leexplorer.app.models.Artwork;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;

public class ArtworkActivity extends BaseActivity implements ArtworkFragment.Callbacks {
  public static final String EXTRA_ARTWORK = "extra_artwork";
  public static final String EXTRA_ARTWORKS = "extra_artworks";

  public final static float BIG_SCALE = 1.0f;
  public final static float SMALL_SCALE = 0.7f;
  public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

  private ArrayList<Artwork> artworks;
  private ViewPager viewPager;

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(EXTRA_ARTWORKS, artworks);
    outState.putParcelable(EXTRA_ARTWORK, artworks.get(viewPager.getCurrentItem()));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_artwork);

    ButterKnife.inject(this);

    Artwork currentArtwork;
    if (savedInstanceState == null) {
      currentArtwork = getIntent().getParcelableExtra(EXTRA_ARTWORK);
      artworks = getIntent().getParcelableArrayListExtra(EXTRA_ARTWORKS);
    } else {
      currentArtwork = savedInstanceState.getParcelable(EXTRA_ARTWORK);
      artworks = savedInstanceState.getParcelableArrayList(EXTRA_ARTWORKS);
    }

    viewPager = new ViewPager(this);
    viewPager.setId(R.id.viewPager);
    setContentView(viewPager);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    FragmentManager fm = getSupportFragmentManager();

    ArtworkViewPagerAdapter adapter = new ArtworkViewPagerAdapter(fm, this, artworks);
    viewPager.setAdapter(adapter);
    viewPager.setOnPageChangeListener(adapter);

    // Set page in ViewPager
    setTitle(currentArtwork.getName());
    for (int i = 0; i < artworks.size(); i++) {
      Artwork aw = artworks.get(i);
      if (currentArtwork.equals(aw)) {
        viewPager.setCurrentItem(i);
        break;
      }
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Subscribe public void onArtworkChanged(ArtworkUpdated event) {
    for (int i = 0; i < artworks.size(); i++) {
      if (artworks.get(i).equals(event.getArtwork())) {
        artworks.add(i, event.getArtwork());
        break;
      }
    }
  }

  @Override public void onBackPressed() {
    Intent data = new Intent();
    data.putExtra(EXTRA_ARTWORKS, artworks);
    setResult(Activity.RESULT_OK, data);
    finish();
  }
}
