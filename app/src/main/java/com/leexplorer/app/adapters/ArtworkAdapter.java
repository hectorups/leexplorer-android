package com.leexplorer.app.adapters;

/**
 * Created by hectormonserrate on 13/02/14.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.leexplorer.app.R;
import com.leexplorer.app.fragments.ArtworkListFragment;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.util.ArtDate;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ArtworkAdapter extends ArrayAdapter<Artwork> {
    protected ArtworkListFragment fragment;

    public ArtworkAdapter(ArtworkListFragment fragment, List<Artwork> objects) {
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
            holder = new ViewHolder(view, fragment);
            view.setTag(holder);
        }

        Artwork aw = getItem(position);

        holder.tvName.setText(aw.getName());

        holder.tvAuthorAndDate.setText(aw.getAuthor() + " - " + ArtDate.shortDate(aw.getPublishedAt()) );

        holder.ivArtworkThumb.setTag(aw);
        holder.ivArtworkThumb.setHeightRatio(getHeightRatioFromPopularity(aw));
        Picasso.with(getContext())
                .load(aw.getImageUrl())
                .into(holder.ivArtworkThumb);

        setSignalIndicator(holder, aw);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.tvName) TextView tvName;
        @InjectView(R.id.tvAuthorAndDate) TextView tvAuthorAndDate;
        @InjectView(R.id.ivArtworkThumb) DynamicHeightImageView ivArtworkThumb;
        @InjectView(R.id.flSignalIndicator) FrameLayout flSignalIndicator;
        @InjectView(R.id.ivSignalIcon) ImageView ivSignalIcon;

        private ArtworkListFragment fragment;

        public ViewHolder(View view, ArtworkListFragment fragment ) {
            ButterKnife.inject(this, view);
            this.fragment = fragment;
        }

        @OnClick(R.id.ivArtworkThumb)
        public void onClickArtwork(View view) {
            Artwork aw = (Artwork) view.getTag();
            fragment.callbacks.onArtworkClicked(aw);
        }
    }

    private void setSignalIndicator(ViewHolder holder, Artwork aw){
        if( aw.getDistance() == Artwork.Distance.OUT_OF_RANGE ){
            holder.flSignalIndicator.setVisibility(View.INVISIBLE);
            return;
        }

        int drawable = R.drawable.ic_signal_close;

        if(aw.getDistance() == Artwork.Distance.CLOSE) {
            drawable = R.drawable.ic_signal_medium;
        } else if(aw.getDistance() == Artwork.Distance.FAR) {
            drawable = R.drawable.ic_signal_far;
        }

        holder.ivSignalIcon.setImageResource(drawable);
        holder.flSignalIndicator.setVisibility(View.VISIBLE);
    }


    // @todo: this is for testing, needs to be implemted depending on
    // the overal gallery score
    private double getHeightRatioFromPopularity(Artwork aw) {
        double factor = 0;
        if(aw.getLikesCount() > 100){
            factor = 1;
        } else if( (aw.getLikesCount() > 50)) {
            factor = 0.5;
        }

        return (( factor / 2.0) + 1.0); // height will be 1.0 - 1.5 the width
    }

}
