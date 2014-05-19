package com.leexplorer.app.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.models.Gallery;
import java.util.List;

/**
 * Created by deepakdhiman on 2/18/14.
 */
public class GalleryAdapter extends LeBaseAdapter<Gallery> {

  GalleryListFragment fragment;

  public GalleryAdapter(GalleryListFragment fragment, List<Gallery> galleries) {
    super(fragment.getActivity(), galleries);
    this.fragment = fragment;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;
    if (view != null) {
      holder = (ViewHolder) view.getTag();
    } else {
      LayoutInflater inflater =
          (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.gallery_item, parent, false);
      holder = new ViewHolder(view, fragment);
      view.setTag(holder);
    }

    Gallery gallery = getItem(position);

    holder.txGalleryName.setText(gallery.getName());
    holder.txAddress.setText(gallery.getAddress());
    holder.txGalleryType.setText(gallery.getType());
    holder.txPrice.setText(String.valueOf(gallery.getPrice()));

    GalleryPagerAdapter pagerAdapter = new GalleryPagerAdapter(fragment, gallery);
    holder.pager.setAdapter(pagerAdapter);
    holder.pager.setCurrentItem(0);

    return view;
  }

  static class ViewHolder {
    @InjectView(R.id.txGalleryName) TextView txGalleryName;
    @InjectView(R.id.txAddress) TextView txAddress;
    @InjectView(R.id.txGalleryType) TextView txGalleryType;
    @InjectView(R.id.txPrice) TextView txPrice;
    @InjectView(R.id.pager) ViewPager pager;
    private GalleryListFragment fragment;

    public ViewHolder(View view, GalleryListFragment fragment) {
      ButterKnife.inject(this, view);
      this.fragment = fragment;
    }

    @OnClick(R.id.llGalleryLocation)
    public void onClickAddress(View view) {
      fragment.callbacks.loadMap(String.valueOf(txAddress.getText()));
    }
  }
}
