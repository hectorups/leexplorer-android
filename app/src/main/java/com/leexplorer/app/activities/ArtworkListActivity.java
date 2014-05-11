package com.leexplorer.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.leexplorer.app.R;
import com.leexplorer.app.fragments.ArtworkListFragment;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.Gallery;

public class ArtworkListActivity extends BaseActivity implements ArtworkListFragment.Callbacks {

    public static final String EXTRA_GALLERY = "extra_gallery";
    public static final String EXTRA_FROM_NOTIFICATION = "extra_from_notification";

    private Gallery gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork_list);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        gallery = getIntent().getParcelableExtra(EXTRA_GALLERY);

//        boolean from_notification = getIntent().getBooleanExtra(EXTRA_FROM_NOTIFICATION, false);
//        if (from_notification) {
//            gallery.setWasSeen(true);
//            gallery.save();
//        }

        setTitle(gallery.getName());

        if (fragment == null) {
            fragment = ArtworkListFragment.newInstance(gallery);
            fm.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, GalleryActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(GalleryActivity.GALLERY_KEY, gallery);
                startActivity(i);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
     * Implement ArtworkListFragment.Callbacks
     */
    public void onArtworkClicked(Artwork aw) {
        FragmentManager fm = getSupportFragmentManager();
        ArtworkListFragment fragment = (ArtworkListFragment) fm.findFragmentById(R.id.container);

        if (fragment == null) {
            return;
        }

        Intent i = new Intent(this, ArtworkActivity.class);
        i.putExtra(ArtworkActivity.EXTRA_ARTWORK, aw);
        i.putExtra(ArtworkActivity.EXTRA_ARTWORKS, fragment.getArtworks());
        startActivity(i);
    }

}
