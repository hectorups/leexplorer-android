package com.leexplorer.app.fragments;

import static com.leexplorer.app.util.AppConstants.*;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leexplorer.app.R;
import com.leexplorer.app.models.Gallery;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by deepakdhiman on 2/23/14.
 */
public class GalleryFragment extends Fragment {

    private Gallery gallery;

    @InjectView(R.id.ivGalleryDetail)
    ImageView ivGalleryDetail;
    @InjectView(R.id.txDetailAddress)
    TextView txDetailAddress;
    @InjectView(R.id.txDetailGalleryType)
    TextView txDetailGalleryType;
    @InjectView(R.id.txLanguage)
    TextView txLanguage;

    @InjectView(R.id.txHours)
    TextView txHours;
    @InjectView(R.id.txDetailedPrice)
    TextView txDetailedPrice;
    @InjectView(R.id.txFacilities)
    TextView txFacilities;
    @InjectView(R.id.txDescription)
    TextView txDescription;

    public static GalleryFragment newInstance(Gallery gallery){
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_content, container, false);
        ButterKnife.inject(this, view);

        Picasso.with(getActivity())
                .load(gallery.getImageUrl())
                .fit()
                .centerCrop()
                .into(ivGalleryDetail);

        txDetailAddress.setText(gallery.getAddress());
        txDetailGalleryType.setText(gallery.getType());
        txLanguage.setText(gallery.getLanguage());
        txHours.setText(gallery.getHours());
        txDetailedPrice.setText(gallery.getDetailedPrice());
        txFacilities.setText(gallery.getFacilities());
        txDescription.setText(gallery.getDescription());
        return view;
    }

    @OnClick(R.id.ivGalleryDetail)
    public void loadArtworks(View view){
        callbacks.loadArtworks(gallery);
    }

    @OnClick(R.id.llGalleryDetailLocation)
    public void onClickAddress(View view) {
        callbacks.loadMap(String.valueOf(txDetailAddress.getText()));
    }

    public interface Callbacks {
        public void onLoading(boolean loading);
        public void loadArtworks(Gallery gallery);
        public void loadMap(String address);
    }

    public Callbacks callbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            callbacks = (Callbacks)activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement GalleryFragment.Callbacks");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }
}
