package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leexplorer.app.R;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.util.FakeData;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hectormonserrate on 11/02/14.
 */
public class ArtworkFragment extends Fragment {

    @InjectView(R.id.tvAuthorAndDate)
    TextView tvAuthorAndDate;

    @InjectView(R.id.tvDescription)
    TextView tvDescription;

    @InjectView(R.id.ivArtwork)
    ImageView ivArtwork;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artwork, container, false);

        ButterKnife.inject(this, rootView);

        Artwork artwork = FakeData.getArtworks().get(0);

        tvAuthorAndDate.setText(artwork.getName());
        tvDescription.setText(artwork.getDescription());

        Picasso.with(getActivity())
                .load(artwork.getImageUrl())
                .fit()
                .into(ivArtwork);

        return rootView;
    }

}
