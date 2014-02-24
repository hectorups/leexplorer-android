package com.leexplorer.app.fragments;

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
        args.putParcelable("gallery", gallery);
        GalleryFragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(args);
        return galleryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gallery = getArguments().getParcelable("gallery");
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
}
