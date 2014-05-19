package com.leexplorer.app.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.leexplorer.app.LeexplorerApplication;
import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by hectormonserrate on 11/05/14.
 */
public class GalleryPagerAdapter extends PagerAdapter {
  @Inject ImageSourcePicker imageSourcePicker;

  List<String> images;
  Gallery gallery;
  GalleryListFragment fragment;
  private LayoutInflater inflater;

  public GalleryPagerAdapter(GalleryListFragment fragment, Gallery gallery) {
    super();

    images = new ArrayList<>();
    for(String url: gallery.getArtworkImageUrls()){
      images.add(url);
    }

    this.gallery = gallery;
    this.fragment = fragment;
    this.inflater = LayoutInflater.from(fragment.getActivity());

    ((LeexplorerApplication) fragment.getActivity().getApplicationContext()).inject(this);
  }

  @Override
  public int getCount() {
    return images.size();
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    RelativeLayout layout =
        (RelativeLayout) inflater.inflate(R.layout.fragment_gallery_image, null);
    ImageView ivGalleryImage = (ImageView) layout.findViewById(R.id.ivGallery);

    imageSourcePicker.getRequestCreator(gallery.getGalleryId(),
        images.get(position), R.dimen.thumbor_small)
        .placeholder(R.drawable.ic_museum_black)
        .into(ivGalleryImage);

    ivGalleryImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fragment.callbacks.loadGalleryDetails(gallery);
      }
    });

    container.addView(layout);
    return layout;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override
  public boolean isViewFromObject(View view, Object obj) {
    return view.equals(obj);
  }
}
