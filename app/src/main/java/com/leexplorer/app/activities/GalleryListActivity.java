package com.leexplorer.app.activities;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.fragments.GalleryMapFragment;
import com.leexplorer.app.models.Gallery;

public class GalleryListActivity extends BaseActivity implements GalleryListFragment.Callbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_list);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if(fragment == null){
            fragment = new GalleryListFragment();
            fm.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuMap) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            FragmentManager fm = getSupportFragmentManager();
            GalleryListFragment fragment = (GalleryListFragment) fm.findFragmentById(R.id.container);
            GalleryMapFragment mapFragment = GalleryMapFragment.newInstance(fragment.getGalleries());
            ft.replace(R.id.container, mapFragment, "NewFragmentTag");
            ft.commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadGalleryDetails(Gallery gallery) {
        FragmentManager fm = getSupportFragmentManager();
        GalleryListFragment fragment = (GalleryListFragment) fm.findFragmentById(R.id.container);

        if(fragment == null) {
            return;
        }

        Intent i = new Intent(this, GalleryActivity.class);
        i.putExtra(GalleryActivity.GALLERY_KEY, gallery);
        startActivity(i);
    }
}
