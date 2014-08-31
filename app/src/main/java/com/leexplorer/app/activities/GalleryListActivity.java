package com.leexplorer.app.activities;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.leexplorer.app.R;
import com.leexplorer.app.events.ConfirmDialogResultEvent;
import com.leexplorer.app.fragments.ConfirmDialogFragment;
import com.leexplorer.app.fragments.GalleryFragment;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.fragments.GalleryMapFragment;
import com.leexplorer.app.models.Gallery;
import com.squareup.otto.Subscribe;

public class GalleryListActivity extends BaseActivity
    implements GalleryListFragment.Callbacks, GalleryMapFragment.Callbacks,
    GalleryFragment.Callbacks {

  static final String MAP_FRAGMENT_ON = "map_fragment_on";
  private static final String LIST_FRAGMENT_TAG = "list_fragment_tag";
  private static final String MAP_FRAGMENT_TAG = "map_fragment_tag";
  private MenuItem menuList;
  private MenuItem menuMap;
  private MenuItem bluetoothWarning;
  private boolean menuFragmentOn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      menuFragmentOn = savedInstanceState.getBoolean(MAP_FRAGMENT_ON, false);
    } else {
      menuFragmentOn = false;
    }

    setContentView(R.layout.fragment_gallery_list_responsive);
    if (isTabletMode()) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    if (savedInstanceState == null) {
      FragmentManager fm = getSupportFragmentManager();
      Fragment fragment = new GalleryListFragment();
      fm.beginTransaction().add(R.id.flGalleryListView, fragment, LIST_FRAGMENT_TAG).commit();
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
    bluetoothWarning = menu.findItem(R.id.bluetoothWarning);

    updateMenuIcon();
    checkBluetoothConfiguration();

    return true;
  }

  @TargetApi(18)
  private void checkBluetoothConfiguration() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      showBluetoothWarningIcon(false);
      return;
    }

    BluetoothManager bluetoothManager =
        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

    if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
      showBluetoothWarningIcon(true);
    } else {
      showBluetoothWarningIcon(false);
    }
  }

  private void showBluetoothWarningIcon(boolean show) {
    if (bluetoothWarning != null) {
      bluetoothWarning.setVisible(show);
    }
  }

  private void updateMenuIcon() {
    if (menuFragmentOn) {
      menuList.setVisible(true);
      menuMap.setVisible(false);
    } else {
      menuList.setVisible(false);
      menuMap.setVisible(true);
    }
  }

  @Subscribe public void onConfirmOk(ConfirmDialogResultEvent event) {
    if (event.getCaller().contentEquals(TAG)) {
      Toast.makeText(this, "Confirm OK", Toast.LENGTH_SHORT).show();
      enableBluetooth();
    }
  }

  @TargetApi(18)
  private void enableBluetooth() {
    BluetoothManager bluetoothManager =
        (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    bluetoothAdapter.enable();

    showBluetoothWarningIcon(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    FragmentManager fm = getSupportFragmentManager();
    GalleryListFragment listFragment =
        (GalleryListFragment) fm.findFragmentByTag(LIST_FRAGMENT_TAG);
    GalleryMapFragment mapFragment = (GalleryMapFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG);

    switch (id) {
      case R.id.bluetoothWarning:
        ConfirmDialogFragment.newInstance(TAG,
            this.getResources().getString(R.string.bluetooth_warning_title),
            this.getResources().getString(R.string.bluetooth_warning_message))
            .show(getSupportFragmentManager(), ConfirmDialogFragment.TAG);
        break;
      case R.id.menuMap:
        if (mapFragment == null) {
          mapFragment = GalleryMapFragment.newInstance(listFragment.getGalleries());
        }

        getSupportFragmentManager().beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.flGalleryListView, mapFragment, MAP_FRAGMENT_TAG)
            .commit();

        menuFragmentOn = true;
        updateMenuIcon();

        return true;
      case R.id.menuList:
        if (listFragment == null) {
          listFragment = new GalleryListFragment();
        }

        getSupportFragmentManager().beginTransaction()
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
          .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
          .replace(R.id.flGalleryDetailView, fragmentGallery)
          .commit();
    } else {
      Intent i = new Intent(this, GalleryActivity.class);
      i.putExtra(GalleryActivity.GALLERY_KEY, gallery);
      startActivity(i);
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
  }

  @Override
  public void onGalleryMapClicked(Gallery gallery) {
    loadGalleryDetails(gallery);
  }
}
