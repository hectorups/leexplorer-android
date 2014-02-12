package com.leexplorer.app.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.leexplorer.app.R;
import com.leexplorer.app.fragments.ArtworkFragment;

public class ArtworkActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ArtworkFragment())
                    .commit();
        }
    }




}
