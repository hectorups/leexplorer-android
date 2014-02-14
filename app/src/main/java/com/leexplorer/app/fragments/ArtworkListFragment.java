package com.leexplorer.app.fragments;

/**
 * Created by hectormonserrate on 10/02/14.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.ArtworkAdapter;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.util.FakeData;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ArtworkListFragment extends Fragment {
    private static final String ARTWORK_LIST = "arwork_list";
    private static final String TAG = "com.leexplorer.artworklistfragement";

    @InjectView(R.id.sgvArtworks) StaggeredGridView sgvArtworks;

    protected ArtworkAdapter artworkAdapter;

    private ArrayList<Artwork> artworks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_arwork_list, container, false);

        ButterKnife.inject(this, rootView);

        if (savedInstanceState != null) {
            artworks = savedInstanceState.getParcelableArrayList(ARTWORK_LIST);

        } else {
            artworks = FakeData.getArtworks();
        }

        artworkAdapter = new ArtworkAdapter(this, artworks);
        sgvArtworks.setAdapter(artworkAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(ARTWORK_LIST, artworks);
    }
}
