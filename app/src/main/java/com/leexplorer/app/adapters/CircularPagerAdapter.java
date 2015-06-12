package com.leexplorer.app.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class CircularPagerAdapter<T> extends PagerAdapter {

  private List<T> items;
  private int currentPage = 0;

  public CircularPagerAdapter() {
    this.items = new ArrayList<>();
  }

  public void setupCircularPagerAdapter(final ViewPager pager, List<T> items) {
    int actualNoOfIDs = items.size();

    for (T item : items) {
      this.items.add(item);
    }

    if (actualNoOfIDs > 1) {

      this.items.add(0, items.get(actualNoOfIDs - 1));
      this.items.add(items.get(0));

      pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

        @Override public void onPageSelected(int position) {
          currentPage = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override public void onPageScrollStateChanged(int state) {
          if (state == ViewPager.SCROLL_STATE_IDLE) {
            int pageCount = getCount();
            if (currentPage == 0) {
              pager.setCurrentItem(pageCount - 2, false);
            } else if (currentPage == pageCount - 1) {
              pager.setCurrentItem(1, false);
            }
          }
        }
      });
    }
  }

  public List<T> getItems() {
    return items;
  }

  @Override public int getCount() {
    return items.size();
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override public boolean isViewFromObject(View view, Object obj) {
    return view.equals(obj);
  }
}
