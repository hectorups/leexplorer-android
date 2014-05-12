package com.leexplorer.app.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.leexplorer.app.R;
import com.leexplorer.app.models.Gallery;
import com.squareup.picasso.Picasso;
import com.squareup.pollexor.Thumbor;
import javax.inject.Inject;

import static com.leexplorer.app.util.AppConstants.FACILITIES_IMG_MAP;

/**
 * Created by deepakdhiman on 2/23/14.
 */
public class GalleryFragment extends BaseFragment {

  private static String GALLERY_KEY = "gallery";
  public Callbacks callbacks;
  @InjectView(R.id.ivGalleryDetail) ImageView ivGalleryDetail;
  @InjectView(R.id.txDetailAddress) TextView txDetailAddress;
  @InjectView(R.id.txDetailGalleryType) TextView txDetailGalleryType;
  @InjectView(R.id.txLanguage) TextView txLanguage;

  @InjectView(R.id.txHours) TextView txHours;
  @InjectView(R.id.txDetailedPrice) TextView txDetailedPrice;
  @InjectView(R.id.txDescription) TextView txDescription;
  @InjectView(R.id.llFacilitiesImg) LinearLayout llFacilitiesImg;
  @Inject Picasso picasso;
  @Inject Thumbor thumbor;
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
    this.gallery = getArguments().getParcelable(GALLERY_KEY);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_gallery_content, container, false);
    ButterKnife.inject(this, view);

    Log.d("adsf", gallery.getArtworkImageUrls().get(0));

    int thumborBucket = (int) getActivity().getResources().getDimension(R.dimen.thumbor_medium);
    String url =
        thumbor.buildImage(gallery.getArtworkImageUrls().get(0)).resize(thumborBucket, 0).toUrl();

    picasso.load(url).fit().centerCrop().into(ivGalleryDetail);

    txDetailAddress.setText(gallery.getAddress());
    txDetailGalleryType.setText(gallery.getType());

    StringBuffer languages = new StringBuffer();
    for (String language : gallery.getLanguages()) {
      languages.append(languages.length() == 0 ? "" : ", ");
      languages.append(language);
    }
    txLanguage.setText(languages.toString());

    txHours.setText(gallery.getHours());
    txDetailedPrice.setText(gallery.getDetailedPrice());

    txDescription.setText(gallery.getDescription());

    setFacilities();

    return view;
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

  @OnClick(R.id.ivGalleryDetail)
  public void loadArtworks(View view) {
    callbacks.loadArtworks(gallery);
  }

  @OnClick(R.id.llGalleryDetailLocation)
  public void onClickAddress(View view) {
    callbacks.loadMap(String.valueOf(txDetailAddress.getText()));
  }

  @OnClick(R.id.llFacilitiesImg)
  public void showFacilitiesList(View v) {
    FragmentManager fm = getChildFragmentManager();
    FacilitiesDialogFragment dialog = FacilitiesDialogFragment.newInstance(gallery.getFacilities());

    dialog.show(fm, "fragment_facilities_desc");
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    super.onAttach(activity);
    if (activity instanceof Callbacks) {
      callbacks = (Callbacks) activity;
    } else {
      throw new ClassCastException(
          activity.toString() + " must implement GalleryFragment.Callbacks");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    callbacks = null;
  }

  public interface Callbacks {
    void onLoading(boolean loading);

    void loadArtworks(Gallery gallery);

    void loadMap(String address);
  }
}
