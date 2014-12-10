package com.leexplorer.app.adapters;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.leexplorer.app.R;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.fragments.ArtworkListFragment;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.util.ArtDate;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.leexplorer.app.util.transformations.AspectRationDummyTransformation;
import java.util.List;
import javax.inject.Inject;

public class ArtworkAdapter extends LeBaseAdapter<Artwork> {
  protected ArtworkListFragment fragment;

  @Inject ImageSourcePicker imageSourcePicker;

  public ArtworkAdapter(ArtworkListFragment fragment, List<Artwork> objects) {
    super(fragment.getActivity(), objects);
    this.fragment = fragment;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    ViewHolder holder;

    if (view != null) {
      holder = (ViewHolder) view.getTag();
    } else {
      LayoutInflater inflater =
          (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.artwork_item, parent, false);
      holder = new ViewHolder(view, fragment);
      view.setTag(holder);
    }

    Artwork artwork = getItem(position);

    double aspectRatio = getHeightRatioFromPopularity(artwork);

    holder.tvName.setText(artwork.getName());
    holder.tvName.setMaxLines(1);
    holder.tvName.setEllipsize(TextUtils.TruncateAt.END);
    holder.tvAuthor.setText(artwork.getAuthor());

    String dateText = ArtDate.shortDate(artwork.getPublishedAt());
    if (!TextUtils.isEmpty(dateText)) {
      holder.tvDate.setText(" - " + dateText);
    }

    holder.ivArtworkThumb.setTag(artwork);
    holder.ivArtworkThumb.setHeightRatio(aspectRatio);

    imageSourcePicker.getRequestCreator(artwork, R.dimen.thumbor_large)
        .fit()
        .centerCrop()
        .placeholder(R.drawable.image_place_holder)
        .transform(new AspectRationDummyTransformation(aspectRatio))
        .into(holder.ivArtworkThumb);

    setSignalIndicator(holder, artwork);

    return view;
  }

  private void setSignalIndicator(ViewHolder holder, Artwork artwork) {
    if (artwork.getNormalizedDistance() == Artwork.Distance.OUT_OF_RANGE) {
      holder.llSignalIndicator.setVisibility(View.INVISIBLE);
      return;
    }

    if (AppConstants.isDebug()) {
      holder.tvSignalDistance.setText(String.format("%.2f m", artwork.getDistance()));
    } else {
      holder.tvSignalDistance.setVisibility(View.GONE);
    }

    holder.ivSignal.setImageResource(R.drawable.ble_detected);
    AnimationDrawable animationDrawable = (AnimationDrawable) holder.ivSignal.getDrawable();
    animationDrawable.start();

    holder.llSignalIndicator.setVisibility(View.VISIBLE);
  }

  // @todo: this is for testing, needs to be implemted depending on
  // the overal gallery score
  private double getHeightRatioFromPopularity(Artwork artwork) {
    double factor = 0;
    if (artwork.getLikesCount() > 100) {
      factor = 1;
    } else if (artwork.getLikesCount() > 50) {
      factor = 0.5;
    }

    return factor / 2.0 + 1.0; // height will be 1.0 - 1.5 the width
  }

  static class ViewHolder {
    @InjectView(R.id.tvName) TextView tvName;
    @InjectView(R.id.tvAuthor) TextView tvAuthor;
    @InjectView(R.id.tvDate) TextView tvDate;
    @InjectView(R.id.ivArtworkThumb) DynamicHeightImageView ivArtworkThumb;
    @InjectView(R.id.llSignalIndicator) LinearLayout llSignalIndicator;
    @InjectView(R.id.tvSignalDistance) TextView tvSignalDistance;
    @InjectView(R.id.ivSignal) ImageView ivSignal;

    private ArtworkListFragment fragment;

    public ViewHolder(View view, ArtworkListFragment fragment) {
      ButterKnife.inject(this, view);
      this.fragment = fragment;
    }

    @OnClick(R.id.ivArtworkThumb)
    public void onClickArtwork(View view) {
      Artwork artwork = (Artwork) view.getTag();
      fragment.onArtworkClicked(artwork);
    }
  }
}
