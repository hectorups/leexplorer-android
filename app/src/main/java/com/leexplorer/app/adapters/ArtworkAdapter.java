package com.leexplorer.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.leexplorer.app.R;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.core.ApplicationComponent;
import com.leexplorer.app.events.artworks.LoadArtworksEvent;import com.leexplorer.app.fragments.ArtworkListFragment;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.services.MediaPlayerService.Status;
import com.leexplorer.app.util.ArtDate;
import com.leexplorer.app.util.RippleClick;import com.leexplorer.app.util.offline.ImageSourcePicker;
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
  protected void injectComponent(ApplicationComponent component) {
    component.inject(this);
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

    setupView(holder, artwork);
    setupPlayingIndicator(holder, artwork);
    setupSignalIndicator(holder, artwork);

    return view;
  }

  private void setupView(ViewHolder holder, Artwork artwork) {
    double aspectRatio = getHeightRatioFromPopularity(artwork);
    holder.tvName.setText(artwork.getName());
    holder.tvName.setMaxLines(1);
    holder.tvName.setEllipsize(TextUtils.TruncateAt.END);
    holder.tvAuthor.setText(artwork.getAuthor());

    String dateText = artwork.getPublishedDescription();
    if(TextUtils.isEmpty(dateText)) {
      dateText = ArtDate.shortDate(artwork.getPublishedAt());
    }

    if (!TextUtils.isEmpty(dateText)) {
      holder.tvDate.setText(" - " + dateText);
    }

    holder.ivArtworkThumb.setTag(artwork);
    holder.ivArtworkThumb.setHeightRatio(aspectRatio);

    imageSourcePicker.getRequestCreator(artwork, R.dimen.thumbor_large)
        .fit()
        .placeholder(R.drawable.image_place_holder)
        .centerCrop()
        .placeholder(R.drawable.image_place_holder)
        .transform(new AspectRationDummyTransformation(aspectRatio))
        .into(holder.ivArtworkThumb);
  }

  private void setupSignalIndicator(ViewHolder holder, Artwork artwork) {
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

  private double getHeightRatioFromPopularity(Artwork artwork) {
    double factor = 0;
    if (artwork.getLikesCount() > 100) {
      factor = 1;
    } else if (artwork.getLikesCount() > 50) {
      factor = 0.5;
    }

    return factor / 2.0 + 1.0;
  }

  private void setupPlayingIndicator(ViewHolder holder, Artwork artwork) {
    FrameLayout indicator = holder.flPlayingIndicator;

    int imageResource = 0;
    Status status = artwork.getStatus();

    if (status == null || status == Status.Idle) {
      indicator.setVisibility(View.GONE);
      return;
    } else if (status == Status.Playing) {
      imageResource = R.drawable.ic_play;
    } else if (status == Status.Paused) {
      imageResource = R.drawable.ic_pause;
    }

    indicator.setVisibility(View.VISIBLE);

    Drawable d = getContext().getResources().getDrawable(imageResource);
    holder.ivPlayingIndicator.setImageDrawable(d);

    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), imageResource);
    d = new BitmapDrawable(getContext().getResources(), bitmap);
    int color = getContext().getResources().getColor(R.color.le_black_more_transparent);
    d.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
    holder.ivPlayingIndicatorShadow1.setImageDrawable(d);
    holder.ivPlayingIndicatorShadow2.setImageDrawable(d);
  }

  static class ViewHolder {
    @InjectView(R.id.tvName) TextView tvName;
    @InjectView(R.id.tvAuthor) TextView tvAuthor;
    @InjectView(R.id.tvDate) TextView tvDate;
    @InjectView(R.id.ivArtworkThumb) DynamicHeightImageView ivArtworkThumb;
    @InjectView(R.id.llSignalIndicator) LinearLayout llSignalIndicator;
    @InjectView(R.id.tvSignalDistance) TextView tvSignalDistance;
    @InjectView(R.id.ivSignal) ImageView ivSignal;
    @InjectView(R.id.ivPlayingIndicator) ImageView ivPlayingIndicator;
    @InjectView(R.id.ivPlayingIndicatorShadow1) ImageView ivPlayingIndicatorShadow1;
    @InjectView(R.id.ivPlayingIndicatorShadow2) ImageView ivPlayingIndicatorShadow2;
    @InjectView(R.id.flPlayingIndicator) FrameLayout flPlayingIndicator;

    private ArtworkListFragment fragment;

    public ViewHolder(View view, ArtworkListFragment fragment) {
      ButterKnife.inject(this, view);
      this.fragment = fragment;
    }

    @OnClick(R.id.ivArtworkThumb)
    public void onClickArtwork(View view) {
      final Artwork artwork = (Artwork) view.getTag();
      RippleClick.run(fragment.getActivity(), new Runnable() {
        @Override public void run() {
          fragment.onArtworkClicked(artwork);
        }
      });
    }
  }
}
