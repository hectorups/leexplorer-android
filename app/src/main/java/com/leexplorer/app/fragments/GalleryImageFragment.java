package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leexplorer.app.R;
import com.squareup.picasso.Picasso;

/**
 * Created by deepakdhiman on 2/21/14.
 */
public class GalleryImageFragment extends Fragment {

    private String url;

    static public GalleryImageFragment newInstance(String url){
        GalleryImageFragment galleryImageFragment = new GalleryImageFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        galleryImageFragment.setArguments(args);
        return galleryImageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_gallery_image, container,
                false);

        ImageView ivGalleryImage = (ImageView)layoutView.findViewById(R.id.ivGallery);
        Picasso.with(getActivity())
                .load(url)
                .fit()
                .centerCrop()
                .into(ivGalleryImage);

        return layoutView;
    }
}
