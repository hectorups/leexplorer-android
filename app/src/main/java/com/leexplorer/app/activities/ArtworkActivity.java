package com.leexplorer.app.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.leexplorer.app.R;
import com.leexplorer.app.fragments.ArtworkFragment;
import com.leexplorer.app.models.Artwork;

public class ArtworkActivity extends ActionBarActivity {
    public static final String EXTRA_ARTWORK = "extra_artwork";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork);

        Artwork aw = getIntent().getParcelableExtra(EXTRA_ARTWORK);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ArtworkFragment.newInstance(aw))
                    .commit();
        }
    }






}
