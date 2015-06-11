package com.leexplorer.app.adapters;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.RippleClick;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import javax.inject.Inject;

public class GalleryPagerAdapter extends CircularPagerAdapter<String> {
  @Inject ImageSourcePicker imageSourcePicker;

  Gallery gallery;
  private LayoutInflater inflater;

  public GalleryPagerAdapter(GalleryListFragment fragment, final ViewPager pager, Gallery gallery) {
    super();

    setupCircularPagerAdapter(pager, gallery.getArtworkImageIds());

    this.gallery = gallery;
    this.inflater = LayoutInflater.from(fragment.getActivity());

    ((LeexplorerApplication) fragment.getActivity().getApplicationContext()).getComponent().inject(this);
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    RelativeLayout layout =
        (RelativeLayout) inflater.inflate(R.layout.fragment_gallery_image, null);
    ImageView ivGalleryImage = (ImageView) layout.findViewById(R.id.ivGallery);

    imageSourcePicker.getRequestCreator(gallery.getGalleryId(), getItems().get(position),
        R.dimen.thumbor_medium)
        .fit()
        .centerCrop()
        .placeholder(R.drawable.image_place_holder)
        .into(ivGalleryImage);

    container.addView(layout);
    return layout;
  }
}
