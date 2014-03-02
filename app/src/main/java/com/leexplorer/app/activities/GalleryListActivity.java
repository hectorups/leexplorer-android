package com.leexplorer.app.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.fragments.GalleryMapFragment;
import com.leexplorer.app.models.Gallery;


public class GalleryListActivity extends BaseActivity
        implements GalleryListFragment.Callbacks,
                   GalleryMapFragment.Callbacks,
                   GalleryFragment.Callbacks{

    private static final String LIST_FRAGMENT_TAG = "list_fragment_tag";
    private static final String MAP_FRAGMENT_TAG = "map_fragment_tag";

    static final String MAP_FRAGMENT_ON = "map_fragment_on";

    private MenuItem menuList;
    private MenuItem menuMap;
    private boolean menuFragmentOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            menuFragmentOn = savedInstanceState.getBoolean(MAP_FRAGMENT_ON, false);
        } else {
            menuFragmentOn = false;
        }

        setContentView(R.layout.fragment_gallery_list_responsive);
        if(isTabletMode()){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(LIST_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new GalleryListFragment();
            fm.beginTransaction()
                    .add(R.id.flGalleryListView, fragment, LIST_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(MAP_FRAGMENT_ON, menuFragmentOn);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery_list, menu);

        menuList = menu.findItem(R.id.menuList);
        menuMap = menu.findItem(R.id.menuMap);

        updateMenuIcon();

        return true;
    }

    private void updateMenuIcon(){
        if(menuFragmentOn){
            menuList.setVisible(true);
            menuMap.setVisible(false);
        } else {
            menuList.setVisible(false);
            menuMap.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        GalleryListFragment listFragment = (GalleryListFragment) fm.findFragmentByTag(LIST_FRAGMENT_TAG);
        GalleryMapFragment mapFragment = (GalleryMapFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG);

        switch (id) {
            case R.id.menuMap:
                if (mapFragment == null) {
                    mapFragment = GalleryMapFragment.newInstance(listFragment.getGalleries());
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.flGalleryListView, mapFragment, MAP_FRAGMENT_TAG)
                        .addToBackStack(null)
                        .commit();

                menuFragmentOn = true;
                updateMenuIcon();

                return true;
            case R.id.menuList:
                if (listFragment == null) {
                    listFragment = new GalleryListFragment();
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.flGalleryListView, listFragment, LIST_FRAGMENT_TAG)
                        .commit();

                menuFragmentOn = false;
                updateMenuIcon();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadGalleryDetails(Gallery gallery) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.flGalleryListView);
        if (fragment == null) {
            return;
        }
//
//        int screenSize = getResources().getConfiguration().screenLayout &
//                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (isTabletMode()) {
            Fragment fragmentGallery = GalleryFragment.newInstance(gallery);
            fm.beginTransaction()
                    .replace(R.id.flGalleryDetailView, fragmentGallery)
                    .commit();
        } else {
            Intent i = new Intent(this, GalleryActivity.class);
            i.putExtra(GalleryActivity.GALLERY_KEY, gallery);
            startActivity(i);
        }
    }

    @Override
    public void onGalleryMapClicked(Gallery gallery){
        loadGalleryDetails(gallery);
    }
}
