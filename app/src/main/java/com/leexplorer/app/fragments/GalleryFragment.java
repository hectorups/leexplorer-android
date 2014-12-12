package com.leexplorer.app.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.leexplorer.app.R;
import com.leexplorer.app.events.artwork.LoadArtworksEvent;
import com.leexplorer.app.events.LoadMapEvent;
import com.leexplorer.app.events.LoadingEvent;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.services.GalleryDownloaderService;
import com.leexplorer.app.util.TextUtil;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.otto.Bus;
import com.squareup.picasso.Callback;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import javax.inject.Inject;

import static com.leexplorer.app.core.AppConstants.FACILITIES_IMG_MAP;

public class GalleryFragment extends BaseFragment {
  private static final String TAG = "leexplorer.com.GalleryFragment";
  private static final String GALLERY_KEY = "gallery";
  private static final String DOWNLOADING_KEY = "downloading";
  private static final String DOWNLOADING_PERCENTAGE_KEY = "downloading_percentage";

  @Inject Bus bus;

  @InjectView(R.id.ivGalleryDetail) ImageView ivGalleryDetail;
  @InjectView(R.id.txDetailAddress) TextView txDetailAddress;
  @InjectView(R.id.txDetailGalleryType) TextView txDetailGalleryType;
  @InjectView(R.id.txLanguage) TextView txLanguage;
  @InjectView(R.id.llOverlayInfo) LinearLayout llOverlayInfo;
  @InjectView(R.id.exploreCollectionBtn) Button exploreCollectionBtn;
  @InjectView(R.id.txHours) TextView txHours;
  @InjectView(R.id.txDetailedPrice) TextView txDetailedPrice;
  @InjectView(R.id.txDescription) TextView txDescription;
  @InjectView(R.id.llFacilitiesImg) LinearLayout llFacilitiesImg;
  @InjectView(R.id.pbDownload) NumberProgressBar pbDownload;
  private boolean downloading;
  private int downloadingPercentage;
  ShareActionProvider miShareAction;
  private MenuItem menuDownload;

  @Inject ImageSourcePicker imageSourcePicker;
  private Gallery gallery;

  public static GalleryFragment newInstance(Gallery gallery) {
    Bundle args = new Bundle();
    args.putParcelable(GALLERY_KEY, gallery);
    GalleryFragment galleryFragment = new GalleryFragment();
    galleryFragment.setArguments(args);
    return galleryFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      this.gallery = savedInstanceState.getParcelable(GALLERY_KEY);
      this.downloading = savedInstanceState.getBoolean(DOWNLOADING_KEY);
      this.downloadingPercentage = savedInstanceState.getInt(DOWNLOADING_PERCENTAGE_KEY);
    } else {
      this.gallery = getArguments().getParcelable(GALLERY_KEY);
    }

    setHasOptionsMenu(true);
  }

  private BroadcastReceiver downloadProgressReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (!downloading) {
        return;
      }

      int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
      Gallery downloadingGallery = intent.getParcelableExtra(GalleryDownloaderService.GALLERY);
      if (resultCode == Activity.RESULT_OK && gallery.equals(downloadingGallery)) {
        int percentage = intent.getIntExtra(GalleryDownloaderService.CURRENT_PERCENTAGE, 0);
        if (percentage == 100) {
          stopDownload();
          Crouton.makeText(getActivity(), R.string.gallery_downloaded, Style.CONFIRM).show();
        } else {
          setupDownload(percentage);
        }
      }
    }
  };

  @Override
  public void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter(GalleryDownloaderService.ACTION);
    LocalBroadcastManager.getInstance(getActivity())
        .registerReceiver(downloadProgressReceiver, filter);
  }

  @Override
  public void onPause() {
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(downloadProgressReceiver);
    super.onPause();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(GALLERY_KEY, gallery);
    outState.putInt(DOWNLOADING_PERCENTAGE_KEY, downloadingPercentage);
    outState.putBoolean(DOWNLOADING_KEY, downloading);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_gallery_content, container, false);
    ButterKnife.inject(this, view);

    imageSourcePicker.getRequestCreator(gallery.getGalleryId(),
        gallery.getMainImage(), R.dimen.thumbor_medium)
        .fit()
        .centerCrop()
        .placeholder(R.drawable.image_place_holder)
        .into(ivGalleryDetail, new Callback() {
          @Override public void onSuccess() {
            if(llOverlayInfo != null) {
              llOverlayInfo.setVisibility(View.VISIBLE);
            }
          }

          @Override public void onError() {
            if(llOverlayInfo != null) {
              llOverlayInfo.setVisibility(View.VISIBLE);
            }
          }
        });

    txDetailAddress.setText(gallery.getAddress());
    txDetailGalleryType.setText(TextUtil.capitalizeFirstLetter(gallery.getType()));

    StringBuffer languages = new StringBuffer();
    for (String language : gallery.getLanguages()) {
      languages.append(languages.length() == 0 ? "" : ", ")
          .append(TextUtil.capitalizeFirstLetter(language));
    }
    txLanguage.setText(languages.toString());

    txHours.setText(gallery.getHours());
    txDetailedPrice.setText(gallery.getDetailedPrice());

    txDescription.setText(gallery.getDescription());

    if (downloading) {
      setupDownload(downloadingPercentage);
    }

    setFacilities();

    return view;
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    MenuItem item = menu.findItem(R.id.menuShare);

    miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

    menuDownload = menu.findItem(R.id.menuDownloadGallery);
    if (gallery.isGalleryDownloaded()) {
      menuDownload.setVisible(false);
    } else {
      menuDownload.setVisible(true);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.gallery, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuDownloadGallery:
        setupDownload(0);
        bus.post(new LoadingEvent(true));
        GalleryDownloaderService.callService(getActivity(), gallery);
        menuDownload.setVisible(false);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void setFacilities() {
    for (String facility : gallery.getFacilities()) {
      if (FACILITIES_IMG_MAP.get(facility.trim()) != null) {
        ImageView ivFacility = new ImageView(getActivity());
        Bitmap bm =
            BitmapFactory.decodeResource(getResources(), FACILITIES_IMG_MAP.get(facility.trim()));
        ivFacility.setImageBitmap(bm);
        ivFacility.setPadding(0, 0, 20, 0);
        llFacilitiesImg.addView(ivFacility);
      }
    }
  }

  @OnClick({R.id.ivGalleryDetail, R.id.exploreCollectionBtn})
  public void loadArtworks(View view) {
    bus.post(new LoadArtworksEvent(gallery));
  }

  @OnClick(R.id.llGalleryDetailLocation)
  public void onClickAddress(View view) {
    bus.post(new LoadMapEvent(String.valueOf(txDetailAddress.getText())));
  }

  @OnClick(R.id.llFacilitiesImg)
  public void showFacilitiesList(View v) {
    FragmentManager fm = getChildFragmentManager();
    FacilitiesDialogFragment dialog = FacilitiesDialogFragment.newInstance(gallery.getFacilities());

    dialog.show(fm, "fragment_facilities_desc");
  }

  public void setupDownload(int download) {
    downloadingPercentage = download;
    downloading = true;
    if(pbDownload != null) {
      pbDownload.setProgress(download);
      pbDownload.setVisibility(View.VISIBLE);
      exploreCollectionBtn.setVisibility(View.INVISIBLE);
    }
  }

  public void stopDownload() {
    bus.post(new LoadingEvent(false));
    downloading = false;
    downloadingPercentage = 0;
    pbDownload.setProgress(0);
    pbDownload.setVisibility(View.GONE);
    exploreCollectionBtn.setVisibility(View.VISIBLE);
  }

  @Override public String getScreenName() {
    return TAG;
  }

  public Gallery getGallery() {
    return gallery;
  }
}
