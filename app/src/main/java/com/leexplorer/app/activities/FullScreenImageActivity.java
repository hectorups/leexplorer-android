package com.leexplorer.app.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.leexplorer.app.R;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import javax.inject.Inject;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FullScreenImageActivity extends BaseActivity {
  public static final String EXTRA_ARTWORK = "artwork";
  @InjectView(R.id.loading_status) View progress;
  @InjectView(R.id.rootView) FrameLayout rootView;
  @Inject ImageSourcePicker imageSourcePicker;
  private Artwork artwork;
  private ImageView ivImage;

  private Target target = new Target() {
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
      ivImage.setImageBitmap(bitmap);
      new PhotoViewAttacher(ivImage);
      showProgress(false);
      rootView.setBackgroundColor(
          FullScreenImageActivity.this.getResources().getColor(R.color.le_black));
    }

    @Override
    public void onBitmapFailed(Drawable d) {
      showProgress(false);
    }

    @Override
    public void onPrepareLoad(android.graphics.drawable.Drawable drawable) {
    }
  };

  public static void launchActivity(Artwork artwork, Activity context) {
    Intent intent = new Intent(context, FullScreenImageActivity.class);
    intent.putExtra(EXTRA_ARTWORK, artwork);
    context.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      artwork = savedInstanceState.getParcelable(EXTRA_ARTWORK);
    } else {
      artwork = getIntent().getParcelableExtra(EXTRA_ARTWORK);
    }

    setContentView(R.layout.activity_fullscreen_image);
    ButterKnife.inject(this);

    ivImage = new PhotoView(this);
    ivImage.setAdjustViewBounds(true);
    rootView.addView(ivImage);

    enableFullScreen(true);

    showProgress(true);

    imageSourcePicker.getRequestCreator(artwork).into(target);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override public void onResume() {
    super.onResume();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      getActionBar().hide();
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override public void onPause() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      getActionBar().show();
    }
    super.onPause();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_ARTWORK, artwork);
  }

  @TargetApi(11)
  private void enableFullScreen(boolean enabled) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    int newVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    if(enabled) {
      newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_IMMERSIVE;
    }

    getDecorView().setSystemUiVisibility(newVisibility);
  }

  private View getDecorView() {
    return getWindow().getDecorView();
  }

  private void showProgress(boolean showProgress) {
    if (showProgress) {
      progress.setVisibility(View.VISIBLE);
      ivImage.setVisibility(View.GONE);
    } else {
      progress.setVisibility(View.GONE);
      ivImage.setVisibility(View.VISIBLE);
    }
  }
}
