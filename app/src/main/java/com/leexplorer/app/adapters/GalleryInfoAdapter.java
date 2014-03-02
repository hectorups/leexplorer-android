package com.leexplorer.app.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hectormonserrate on 01/03/14.
 */
public class GalleryInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private HashMap<String, Uri> mImages;

    public GalleryInfoAdapter(Context c, LayoutInflater i, HashMap<String, Uri> images ){
        mInflater = i;
        mImages = images;
        mContext = c;
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

        Uri image = mImages.get(marker.getId());
        if (image != null) {
            Picasso.with(mContext)
                    .load(image)
                    .placeholder(R.drawable.ic_museum_black)
                    .into(holder.ivImage);
        }


        return v;
    }


    static class ViewHolder {
        private GalleryListFragment fragment;

        @InjectView(R.id.tvName) TextView tvName;
        @InjectView(R.id.tvDescription) TextView tvDescription;
        @InjectView(R.id.ivImage) ImageView ivImage;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

}
