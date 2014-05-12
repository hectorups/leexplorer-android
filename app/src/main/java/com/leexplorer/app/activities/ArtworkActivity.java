package com.leexplorer.app.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import com.leexplorer.app.R;
import com.leexplorer.app.fragments.ArtworkFragment;
import com.leexplorer.app.models.Artwork;
import java.util.ArrayList;

public class ArtworkActivity extends BaseActivity implements ArtworkFragment.Callbacks {
  public static final String EXTRA_ARTWORK = "extra_artwork";
  public static final String EXTRA_ARTWORKS = "extra_artworks";

  private ArrayList<Artwork> artworks;
  private ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_artwork);

    Artwork currentAw = getIntent().getParcelableExtra(EXTRA_ARTWORK);
    artworks = getIntent().getParcelableArrayListExtra(EXTRA_ARTWORKS);

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
}
