package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private static final String ARTWORK_SAVED = "tweet_results";
    private static final String TAG = "com.leexplorer.artworkfragment";

    @InjectView(R.id.tvAuthorAndDate)
    TextView tvAuthorAndDate;

    @InjectView(R.id.tvDescription)
    TextView tvDescription;

    @InjectView(R.id.ivArtwork)
    ImageView ivArtwork;

    Artwork artwork;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artwork, container, false);

        ButterKnife.inject(this, rootView);

        if (savedInstanceState != null) {
            artwork = savedInstanceState.getParcelable(ARTWORK_SAVED);

        } else {
            artwork = FakeData.getArtworks().get(0);
        }

        tvAuthorAndDate.setText(artwork.getName());
        tvDescription.setText(artwork.getDescription());

        Picasso.with(getActivity())
                .load(artwork.getImageUrl())
                .into(ivArtwork);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putParcelable(ARTWORK_SAVED, artwork);
    }

}
