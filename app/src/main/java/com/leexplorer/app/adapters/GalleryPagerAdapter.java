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
import com.leexplorer.app.util.offline.ImageSourcePicker;
import javax.inject.Inject;

/**
 * Created by hectormonserrate on 11/05/14.
 */
public class GalleryPagerAdapter extends CircularPagerAdapter<String> {
  @Inject ImageSourcePicker imageSourcePicker;

  Gallery gallery;
  GalleryListFragment fragment;
  private LayoutInflater inflater;

  public GalleryPagerAdapter(GalleryListFragment fragment, final ViewPager pager, Gallery gallery) {
    super();

    setupCircularPagerAdapter(pager, gallery.getArtworkImageUrls());

    this.gallery = gallery;
    this.fragment = fragment;
    this.inflater = LayoutInflater.from(fragment.getActivity());

    ((LeexplorerApplication) fragment.getActivity().getApplicationContext()).inject(this);
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    RelativeLayout layout =
        (RelativeLayout) inflater.inflate(R.layout.fragment_gallery_image, null);
    ImageView ivGalleryImage = (ImageView) layout.findViewById(R.id.ivGallery);

    imageSourcePicker.getRequestCreator(gallery.getGalleryId(), getItems().get(position),
        R.dimen.thumbor_small).placeholder(R.drawable.image_place_holder).into(ivGalleryImage);

    ivGalleryImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fragment.callbacks.loadGalleryDetails(gallery);
      }
    });

    container.addView(layout);
    return layout;
  }
}
