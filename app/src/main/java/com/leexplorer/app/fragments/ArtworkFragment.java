package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leexplorer.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hectormonserrate on 11/02/14.
 */
public class ArtworkFragment extends Fragment {

    @InjectView(R.id.tvAuthorAndDate)
    TextView tvAuthorAndDate;

    @InjectView(R.id.tvDescriptionHeader)
    TextView tvDescriptionHeader;

    @InjectView(R.id.tvDescription)
    TextView tvDescription;

    @InjectView(R.id.ivArtwork)
    ImageView ivArtwork;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artwork, container, false);

        ButterKnife.inject(this, rootView);

        return rootView;
    }
}
