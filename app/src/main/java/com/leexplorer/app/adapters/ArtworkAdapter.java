package com.leexplorer.app.adapters;

/**
 * Created by hectormonserrate on 13/02/14.
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.artwork_item, parent, false);
            holder = new ViewHolder(view, fragment);
            view.setTag(holder);
        }

        Artwork aw = getItem(position);

        holder.tvName.setText(aw.getName());
        holder.tvName.setMaxLines(1);
        holder.tvName.setEllipsize(TextUtils.TruncateAt.END);

        holder.tvAuthorAndDate.setText(aw.getAuthor() + " - " + ArtDate.shortDate(aw.getPublishedAt()));

        holder.ivArtworkThumb.setTag(aw);
        holder.ivArtworkThumb.setHeightRatio(getHeightRatioFromPopularity(aw));
        Picasso.with(getContext())
                .load(aw.getImageUrl())
                .into(holder.ivArtworkThumb);

        setSignalIndicator(holder, aw);

        return view;
    }

    private void setSignalIndicator(ViewHolder holder, Artwork aw) {
        if (aw.getDistance() == Artwork.Distance.OUT_OF_RANGE) {
            holder.flSignalIndicator.setVisibility(View.INVISIBLE);
            return;
        }

        int color = R.color.le_green;
        int siganl_text = R.string.signal_immediate;
        int bg_drawable = R.drawable.immediate_rounded_rectanble;

        if (aw.getDistance() == Artwork.Distance.CLOSE) {
            color = R.color.le_blue;
            siganl_text = R.string.signal_close;
            bg_drawable = R.drawable.close_rounded_rectangle;
        } else if (aw.getDistance() == Artwork.Distance.FAR) {
            color = R.color.le_yellow;
            siganl_text = R.string.signal_far;
            bg_drawable = R.drawable.far_rounded_rectangle;
        }

        holder.tvSignalIcon.setText(fragment.getResources().getString(siganl_text));
        holder.tvSignalIcon.setTextColor(fragment.getResources().getColor(color));
        holder.tvSignalIcon.setBackgroundResource(bg_drawable);
        holder.flSignalIndicator.setVisibility(View.VISIBLE);
    }

    // @todo: this is for testing, needs to be implemted depending on
    // the overal gallery score
    private double getHeightRatioFromPopularity(Artwork aw) {
        double factor = 0;
        if (aw.getLikesCount() > 100) {
            factor = 1;
        } else if (aw.getLikesCount() > 50) {
            factor = 0.5;
        }

        return factor / 2.0 + 1.0; // height will be 1.0 - 1.5 the width
    }

    static class ViewHolder {
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.tvAuthorAndDate)
        TextView tvAuthorAndDate;
        @InjectView(R.id.ivArtworkThumb)
        DynamicHeightImageView ivArtworkThumb;
        @InjectView(R.id.flSignalIndicator)
        FrameLayout flSignalIndicator;
        @InjectView(R.id.tvSignalIcon)
        TextView tvSignalIcon;

        private ArtworkListFragment fragment;

        public ViewHolder(View view, ArtworkListFragment fragment) {
            ButterKnife.inject(this, view);
            this.fragment = fragment;
        }

        @OnClick(R.id.ivArtworkThumb)
        public void onClickArtwork(View view) {
            Artwork aw = (Artwork) view.getTag();
            fragment.callbacks.onArtworkClicked(aw);
        }
    }

}
