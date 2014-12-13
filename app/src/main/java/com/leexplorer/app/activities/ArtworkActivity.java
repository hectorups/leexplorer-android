package com.leexplorer.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.ArtworkViewPagerAdapter;
import com.leexplorer.app.events.artworks.ArtworkUpdatedEvent;
import com.leexplorer.app.events.artworks.FullScreenImageEvent;
import com.leexplorer.app.models.Artwork;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;

public class ArtworkActivity extends BaseActivity {
  public static final String EXTRA_ARTWORK = "extra_artwork";
  public static final String EXTRA_ARTWORKS = "extra_artworks";

  private ArrayList<Artwork> artworks;
  private ViewPager viewPager;

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(EXTRA_ARTWORKS, artworks);
    outState.putParcelable(EXTRA_ARTWORK, artworks.get(viewPager.getCurrentItem()));
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    bus.unregister(this);
    super.onPause();
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.d(TAG, "onNewIntent");
    Artwork artwork = intent.getParcelableExtra(EXTRA_ARTWORK);
    if(artwork != null) {
      setViewPagerPage(artwork);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_artwork);
    setupActionBar();

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
    FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);
    frameLayout.addView(viewPager);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    FragmentManager fm = getSupportFragmentManager();

    ArtworkViewPagerAdapter adapter = new ArtworkViewPagerAdapter(fm, this, artworks);
    viewPager.setAdapter(adapter);
    viewPager.setOnPageChangeListener(adapter);

    // Set page in ViewPager
    setViewPagerPage(currentArtwork);
  }

  @Override public boolean showHomeButton() {
    return true;
  }

  public void setViewPagerPage(Artwork artwork) {
    // Set page in ViewPager
    setTitle(artwork.getName());
    for (int i = 0; i < artworks.size(); i++) {
      Artwork aw = artworks.get(i);
      if (artwork.equals(aw)) {
        viewPager.setCurrentItem(i);
        break;
      }
    }
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

  @Subscribe public void onArtworkChanged(ArtworkUpdatedEvent event) {
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

  @Subscribe public void onFullScreenImage(FullScreenImageEvent event) {
    Log.d(TAG, "onfullscreenimage");
    FullScreenImageActivity.launchActivity(event.getArtwork(), this);
  }
}
