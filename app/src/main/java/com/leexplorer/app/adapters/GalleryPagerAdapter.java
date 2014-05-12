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
import com.squareup.picasso.Picasso;
import com.squareup.pollexor.Thumbor;
import javax.inject.Inject;

/**
 * Created by hectormonserrate on 11/05/14.
 */
public class GalleryPagerAdapter extends PagerAdapter {
  @Inject Picasso picasso;

  @Inject Thumbor thumbor;

  Gallery gallery;
  GalleryListFragment fragment;
  private LayoutInflater inflater;

  public GalleryPagerAdapter(GalleryListFragment fragment, Gallery gallery) {
    super();
    this.gallery = gallery;
    this.fragment = fragment;
    this.inflater = LayoutInflater.from(fragment.getActivity());

    ((LeexplorerApplication) fragment.getActivity().getApplicationContext()).inject(this);
  }

  @Override
  public int getCount() {
    return this.gallery.getArtworkImageUrls().size();
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    RelativeLayout layout =
        (RelativeLayout) inflater.inflate(R.layout.fragment_gallery_image, null);
    ImageView ivGalleryImage = (ImageView) layout.findViewById(R.id.ivGallery);

    int thumborBucket =
        (int) fragment.getActivity().getResources().getDimension(R.dimen.thumbor_small);
    String url = thumbor.buildImage(this.gallery.getArtworkImageUrls().get(position))
        .resize(thumborBucket, 0)
        .toUrl();

    picasso.load(url).fit().centerCrop().into(ivGalleryImage);

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
