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
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.events.LoadMapEvent;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.models.Gallery;
import com.squareup.otto.Bus;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by deepakdhiman on 2/18/14.
 */
public class GalleryAdapter extends LeBaseAdapter<Gallery> {

  GalleryListFragment fragment;
  @Inject Bus bus;

  public GalleryAdapter(GalleryListFragment fragment, List<Gallery> galleries) {
    super(fragment.getActivity(), galleries);
    this.fragment = fragment;
    ((LeexplorerApplication) fragment.getActivity().getApplicationContext()).inject(this);
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
      holder = new ViewHolder(view, bus);
      view.setTag(holder);
    }

    Gallery gallery = getItem(position);

    holder.txGalleryName.setText(gallery.getName());
    holder.txAddress.setText(gallery.getAddress());
    holder.txGalleryType.setText(gallery.getType());
    holder.txPrice.setText(String.valueOf(gallery.getPrice()));

    GalleryPagerAdapter pagerAdapter = new GalleryPagerAdapter(fragment, holder.pager, gallery);
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
    private Bus bus;

    public ViewHolder(View view, Bus bus) {
      ButterKnife.inject(this, view);
      this.bus = bus;
    }

    @OnClick(R.id.llGalleryLocation)
    public void onClickAddress(View view) {
      bus.post(new LoadMapEvent(String.valueOf(txAddress.getText())));
    }
  }
}
