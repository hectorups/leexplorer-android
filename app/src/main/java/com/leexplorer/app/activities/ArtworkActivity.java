package com.leexplorer.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import com.leexplorer.app.R;
import com.leexplorer.app.events.ArtworkUpdated;
import com.leexplorer.app.fragments.ArtworkFragment;
import com.leexplorer.app.models.Artwork;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;

public class ArtworkActivity extends BaseActivity implements ArtworkFragment.Callbacks {
  public static final String EXTRA_ARTWORK = "extra_artwork";
  public static final String EXTRA_ARTWORKS = "extra_artworks";

  private ArrayList<Artwork> artworks;
  private ViewPager mViewPager;

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(EXTRA_ARTWORKS, artworks);
    outState.putParcelable(EXTRA_ARTWORK, artworks.get(mViewPager.getCurrentItem()));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_artwork);

    Artwork currentAw;
    if (savedInstanceState == null) {
      currentAw = getIntent().getParcelableExtra(EXTRA_ARTWORK);
      artworks = getIntent().getParcelableArrayListExtra(EXTRA_ARTWORKS);
    } else {
      currentAw = savedInstanceState.getParcelable(EXTRA_ARTWORK);
      artworks = savedInstanceState.getParcelableArrayList(EXTRA_ARTWORKS);
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    FragmentManager fm = getSupportFragmentManager();

    mViewPager = new ViewPager(this);
    mViewPager.setId(R.id.viewPager);
    setContentView(mViewPager);

    mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
      @Override
      public Fragment getItem(int i) {
        Artwork artwork = artworks.get(i);
        return ArtworkFragment.newInstance(artwork);
      }

      @Override
      public int getCount() {
        return artworks.size();
      }
    });

    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int i, float v, int i2) {
      }

      @Override
      public void onPageSelected(int i) {
        Artwork aw = artworks.get(i);
        if (aw != null) {
          setTitle(aw.getName());
        }
      }

      @Override
      public void onPageScrollStateChanged(int i) {
      }
    });

    // Set page in ViewPager
    setTitle(currentAw.getName());
    for (int i = 0; i < artworks.size(); i++) {
      Artwork aw = artworks.get(i);
      if (currentAw.equals(aw)) {
        mViewPager.setCurrentItem(i);
        break;
      }
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
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
