package com.leexplorer.app.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.leexplorer.app.LeexplorerApplication;
import com.leexplorer.app.R;
import com.leexplorer.app.models.Gallery;
import com.squareup.picasso.Picasso;
import com.squareup.pollexor.Thumbor;
import java.util.HashMap;
import javax.inject.Inject;

/**
 * Created by hectormonserrate on 01/03/14.
 */
public class GalleryInfoAdapter implements GoogleMap.InfoWindowAdapter {
  public static final int THUMNAIL_SIZE = R.dimen.thumbor_small;
  @Inject Picasso picasso;
  @Inject Thumbor thumbor;
  private Context mContext;
  private LayoutInflater mInflater;
  private HashMap<String, Gallery> mGalleries;

  public GalleryInfoAdapter(Context c, LayoutInflater i, HashMap<String, Gallery> images) {
    mInflater = i;
    mGalleries = images;
    mContext = c;

    ((LeexplorerApplication) mContext.getApplicationContext()).inject(this);
  }

  @Override
  public View getInfoContents(Marker marker) {
    return null;
  }

  @Override
  public View getInfoWindow(Marker marker) {
    // Getting view from the layout file
    View v = mInflater.inflate(R.layout.gallery_info_window, null);
    ViewHolder holder = new ViewHolder(v);

    holder.tvName.setText(marker.getTitle());
    holder.tvName.setMaxLines(1);
    holder.tvName.setEllipsize(TextUtils.TruncateAt.END);

    holder.tvDescription.setText(marker.getSnippet());
    holder.tvDescription.setMaxLines(3);
    holder.tvDescription.setEllipsize(TextUtils.TruncateAt.END);

    Gallery gallery = mGalleries.get(marker.getId());
    if (gallery != null) {
      int thumborBucket = (int) mContext.getResources().getDimension(THUMNAIL_SIZE);
      String url =
          thumbor.buildImage(gallery.getArtworkImageUrls().get(0)).resize(thumborBucket, 0).toUrl();
      picasso.load(url).placeholder(R.drawable.ic_museum_black).into(holder.ivImage);
    } else {
      holder.ivImage.setVisibility(View.GONE);
    }

    return v;
  }

  static class ViewHolder {
    @InjectView(R.id.tvName) TextView tvName;
    @InjectView(R.id.tvDescription) TextView tvDescription;
    @InjectView(R.id.ivImage) ImageView ivImage;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
