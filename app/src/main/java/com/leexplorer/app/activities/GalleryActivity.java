package com.leexplorer.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.models.Gallery;

import static com.leexplorer.app.util.AppConstants.GALLERY_KEY;

public class GalleryActivity extends BaseActivity implements GalleryFragment.Callbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if(fragment == null){
            Gallery gallery = getIntent().getParcelableExtra(GALLERY_KEY);
            getSupportActionBar().setTitle(gallery.getName());
            fragment = GalleryFragment.newInstance(gallery);
            fm.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadArtworks(Gallery gallery) {

        FragmentManager fm = getSupportFragmentManager();
        GalleryFragment fragment = (GalleryFragment) fm.findFragmentById(R.id.container);

        if(fragment == null) {
            return;
        }
        Intent i = new Intent(this, ArtworkListActivity.class);
        i.putExtra(ArtworkListActivity.EXTRA_GALLERY, gallery);
        startActivity(i);
    }
}
