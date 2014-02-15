package com.leexplorer.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leexplorer.app.R;
import com.leexplorer.app.models.Artwork;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hectormonserrate on 11/02/14.
 */
public class ArtworkFragment extends Fragment {
    private static final String ARTWORK_SAVED = "tweet_results";
    private static final String TAG = "com.leexplorer.artworkfragment";
    private static final String EXTRA_ARTWORK = "extra_artwork";

    @InjectView(R.id.tvAuthorAndDate)
    TextView tvAuthorAndDate;

    @InjectView(R.id.tvDescription)
    TextView tvDescription;

    @InjectView(R.id.ivArtwork)
    ImageView ivArtwork;

    Artwork artwork;

    public static ArtworkFragment newInstance(Artwork aw){
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ARTWORK, aw);

        ArtworkFragment fragment = new ArtworkFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artwork = getArguments().getParcelable(EXTRA_ARTWORK);

        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artwork, container, false);

        ButterKnife.inject(this, rootView);

        tvAuthorAndDate.setText(artwork.getName());
        tvDescription.setText(artwork.getDescription());


        Picasso.with(getActivity())
                .load(artwork.getImageUrl())
                .fit()
                .centerCrop()
                .into(ivArtwork);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                navigateBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateBack(){
        if(NavUtils.getParentActivityName(getActivity()) != null){
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

}
