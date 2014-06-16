package com.leexplorer.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.leexplorer.app.activities.ArtworkActivity;
import com.leexplorer.app.fragments.ArtworkFragment;
import com.leexplorer.app.models.Artwork;
import java.util.List;

/**
 * Created by hectormonserrate on 15/06/14.
 */
public class ArtworkViewPagerAdapter extends FragmentStatePagerAdapter implements
    ViewPager.OnPageChangeListener {

  private List<Artwork> artworks;

  private ArtworkActivity context;

  public ArtworkViewPagerAdapter(FragmentManager fm, ArtworkActivity context, List<Artwork> artworks){
    super(fm);
    this.artworks = artworks;
    this.context = context;
  }

  @Override
  public Fragment getItem(int position) {
    Artwork artwork = artworks.get(position);
    return ArtworkFragment.newInstance(artwork);
  }

  @Override
  public int getCount() {
    return artworks.size();
  }

  @Override
  public void onPageScrolled(int i, float v, int i2) {
  }

  @Override
  public void onPageSelected(int i) {
    Artwork artwork = artworks.get(i);
    if (artwork != null) {
      context.setTitle(artwork.getName());
    }
  }

  @Override
  public void onPageScrollStateChanged(int i) {
  }

}
