package com.leexplorer.app.adapters;

/**
 * Created by hectormonserrate on 13/02/14.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.leexplorer.app.R;
import com.leexplorer.app.models.Artwork;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ArtworkAdapter extends ArrayAdapter<Artwork> {
    protected Fragment fragment;

    public ArtworkAdapter(Fragment fragment, List<Artwork> objects) {
        super(fragment.getActivity(), 0, objects);
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.artwork_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        Artwork aw = getItem(position);

        holder.tvName.setText(aw.getName());
        holder.tvAuthorAndDate.setText(aw.getAuthor());

        holder.ivArtworkThumb.setHeightRatio(getRandomHeightRatio());
        Picasso.with(getContext())
                .load(aw.getImageUrl())
                .into(holder.ivArtworkThumb);


        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tvName) TextView tvName;
        @InjectView(R.id.tvAuthorAndDate) TextView tvAuthorAndDate;
        @InjectView(R.id.ivArtworkThumb) DynamicHeightImageView ivArtworkThumb;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private double getRandomHeightRatio() {
        return ((new Random()).nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }

}
