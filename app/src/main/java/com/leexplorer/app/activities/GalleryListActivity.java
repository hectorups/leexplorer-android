package com.leexplorer.app.activities;

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

    private final String LIST_FRAGMENT_TAG = "list_fragment_tag";
    private final String MAP_FRAGMENT_TAG = "map_fragment_tag";

    private MenuItem menuList;
    private MenuItem menuMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_list);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(LIST_FRAGMENT_TAG);

        if(fragment == null){
            fragment = new GalleryListFragment();
            fm.beginTransaction()
                    .add(R.id.container, fragment, LIST_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery_list, menu);

        menuList = menu.findItem(R.id.menuList);
        menuMap = menu.findItem(R.id.menuMap);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        GalleryListFragment listFragment = (GalleryListFragment) fm.findFragmentByTag(LIST_FRAGMENT_TAG);
        GalleryMapFragment mapFragment = (GalleryMapFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG);

        switch (id){
            case R.id.menuMap:
                if(mapFragment == null){
                    mapFragment = GalleryMapFragment.newInstance(listFragment.getGalleries());
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mapFragment, MAP_FRAGMENT_TAG)
                        .commit();

                menuList.setVisible(true);
                menuMap.setVisible(false);

                return true;
            case R.id.menuList:
                if(listFragment == null){
                    listFragment = new GalleryListFragment();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, listFragment, LIST_FRAGMENT_TAG)
                        .commit();

                menuList.setVisible(false);
                menuMap.setVisible(true);

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
