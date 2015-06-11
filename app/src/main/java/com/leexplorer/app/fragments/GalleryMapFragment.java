package com.leexplorer.app.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.GalleryInfoAdapter;
import com.leexplorer.app.core.EventReporter;
import com.leexplorer.app.core.LeexplorerApplication;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class GalleryMapFragment extends SupportMapFragment {
  private static final String TAG = "GalleryMapFragment";
  private static final String EXTRA_GALLERIES = "extra_galleries";
  private static final String EXTRA_BUNDLE = "bundle";
  private static final double MIN_LAT_BOUNDS = 0.2;
  private static final int ANIMATE_LOCATION_DURATION = 600;
  private static final int ANIMATE_INFOBOX_DURATION = 300;
  private static final int ANIMATE_DELAY = 200;
  public Callbacks callbacks;
  @Inject ImageSourcePicker imageSourcePicker;
  @Inject EventReporter eventReporter;
  private CameraPosition savedCameraPosition;
  private GoogleMap map;
  private boolean created;
  private List<Gallery> galleries;
  private HashMap<Marker, List<Gallery>> markerGalleryHashMap =
      new HashMap<Marker, List<Gallery>>();
  private int markerHeight;
  private int markerWidth;
  private float lastZoom;

  public static GalleryMapFragment newInstance(List<Gallery> galleries) {
    Bundle args = new Bundle();
    args.putParcelableArrayList(EXTRA_GALLERIES, new ArrayList<>(galleries));

    GalleryMapFragment fragment = new GalleryMapFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    super.onAttach(activity);
    if (activity instanceof Callbacks) {
      callbacks = (Callbacks) activity;
    } else {
      throw new ClassCastException(
          activity.toString() + " must implement GalleryListFragment.Callbacks");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    callbacks = null;
  }

  @Override
  public void onCreate(final Bundle state) {
    super.onCreate(state);
    ((LeexplorerApplication) getActivity().getApplicationContext()).getComponent().inject(this);

    Bitmap marker =
        BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_map_pin);
    markerHeight = marker.getHeight();
    markerWidth = marker.getWidth();

    if (state == null) {
      galleries = getArguments().getParcelableArrayList(EXTRA_GALLERIES);
    } else {
      Bundle b = state.getBundle(EXTRA_BUNDLE);
      if (b != null) {
        galleries = b.getParcelableArrayList(EXTRA_GALLERIES);
      }
    }

    if (galleries == null) {
      galleries = new ArrayList<>();
    }

  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Bundle b = new Bundle();
    b.putParcelableArrayList(EXTRA_GALLERIES, new ArrayList<>(galleries));
    outState.putBundle(EXTRA_BUNDLE, b);
  }

  @Override
  public void onStart() {
    super.onStart();

    showGalleries();
  }

  public void showGalleries() {
    if (initMap()) {

      //If the map has no size listen for a camera event until setting it up
      //This always occurs just after it is drawn
      if (getView().getMeasuredHeight() != 0) {
        setCameraListeningMode(true);
        buildMap();
      } else {
        setCameraListeningMode(false);
      }
    }
  }

  private boolean initMap() {
    map = getMap();
    if (map == null) {

      return false;
    }
    if (created) {
      return true;
    }

    map.clear();
    //map.getUiSettings().setZoomControlsEnabled(false);
    map.getUiSettings().setZoomControlsEnabled(false);
    map.getUiSettings().setCompassEnabled(false);
    map.getUiSettings().setRotateGesturesEnabled(false);
    map.getUiSettings().setTiltGesturesEnabled(false);
    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        List<Gallery> galleries = markerGalleryHashMap.get(marker);

        if (galleries != null && galleries.size() == 1) {
          Gallery gallery = galleries.get(0);

          imageSourcePicker.getRequestCreator(gallery.getGalleryId(),
              gallery.getArtworkImageIds().get(0), GalleryInfoAdapter.THUMNAIL_SIZE)
              .placeholder(R.drawable.image_place_holder)
              .into(new InfoWindowImage(marker));
        } else {
          animateToOpenInfoWindow(marker);
        }

        return true;
      }
    });

    map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
      @Override
      public void onInfoWindowClick(Marker marker) {
        List<Gallery> galleries = markerGalleryHashMap.get(marker);
        if (galleries.size() > 1) {
          LatLngBounds bounds = buildBounds(galleries);
          showBounds(bounds, true);
        } else {
          if (callbacks != null) {
            callbacks.onGalleryMapClicked(galleries.get(0));
          }
        }
      }
    });

    created = true;
    return true;
  }

  private void setCameraListeningMode(boolean mapBuilt) {
    if (mapBuilt) {
      map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

          if (cameraPosition.zoom != lastZoom) {
            lastZoom = map.getCameraPosition().zoom;
            showMarkers();
          }
        }
      });
    } else {
      map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
          // we were waiting for the map to be drawn before we could build the map
          buildMap();
          setCameraListeningMode(true);
        }
      });
    }
  }

  private void buildMap() {

    LatLngBounds bounds = buildBounds(galleries);

    if (savedCameraPosition != null) {
      map.moveCamera(CameraUpdateFactory.newCameraPosition(savedCameraPosition));
      savedCameraPosition = null;
    } else {
      //move map to new bounds
      showBounds(bounds, false);
      int padding = galleries.size() > 0 ? getMarkerHeight() : 0;
      map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }
    showMarkers();
  }

  private void showBounds(LatLngBounds bounds, boolean animate) {
    int padding = markerHeight;
    if (animate) {
      map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding),
          ANIMATE_LOCATION_DURATION, null);
    } else {
      map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }
  }

  /**
   * Generates the bounds of the maps bases on the list of galleries provided
   */
  private LatLngBounds buildBounds(List<Gallery> galleries) {

    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

    for (Gallery gallery : galleries) {
      LatLng latLng = new LatLng(gallery.getLatitude(), gallery.getLongitude());
      boundsBuilder.include(latLng);
    }

    LatLngBounds bounds = boundsBuilder.build();

    //set minimum bounds
    if (bounds.northeast.latitude - bounds.southwest.latitude < MIN_LAT_BOUNDS) {
      double newNorthEastLat = bounds.getCenter().latitude + MIN_LAT_BOUNDS / 2;
      double newSouthWestLat = bounds.getCenter().latitude - MIN_LAT_BOUNDS / 2;
      bounds = new LatLngBounds(new LatLng(newSouthWestLat, bounds.southwest.longitude),
          new LatLng(newNorthEastLat, bounds.northeast.longitude));
    }

    return bounds;
  }

  private void showMarkers() {

    //create new mapping of marker delegates - will become markers
    HashMap<MarkerDelegate, List<Gallery>> markerDelegateHashMap = new HashMap<>();

    //Add markers - checking for overlap
    Projection projection = map.getProjection();
    for (Gallery gallery : galleries) {
      if (gallery.getLongitude() == 0 && gallery.getLatitude() == 0) {
        continue;
      }
      LatLng newPosition = new LatLng(gallery.getLatitude(), gallery.getLongitude());

      boolean isNewMarker = true;

      for (Map.Entry<MarkerDelegate, List<Gallery>> entry : markerDelegateHashMap.entrySet()) {
        MarkerDelegate marker = entry.getKey();
        //check for overlap, consolidate if it does
        if (isMarkerOverlapping(marker.getPosition(), newPosition, projection)) {

          //combine markers - find new position and set new text
          int consolidatedCount = entry.getValue().size();
          double newLat =
              (marker.getPosition().latitude * consolidatedCount + newPosition.latitude) / (
                  consolidatedCount
                      + 1);
          double newLng =
              (marker.getPosition().longitude * consolidatedCount + newPosition.longitude) / (
                  consolidatedCount
                      + 1);

          markerDelegateHashMap.get(marker).add(gallery);
          marker.setPosition(new LatLng(newLat, newLng));
          setMarkerText(marker, markerDelegateHashMap.get(marker));
          isNewMarker = false;
          break;
        }
      }

      //No consolidation occured, so add a new marker delegate
      if (isNewMarker) {
        MarkerDelegate marker = new MarkerDelegate();
        marker.setPosition(newPosition);
        List<Gallery> markerGallerys = new ArrayList<Gallery>();
        markerGallerys.add(gallery);
        markerDelegateHashMap.put(marker, markerGallerys);
        setMarkerText(marker, markerGallerys);
      }
    }

    //clear markers
    map.clear();
    //If Honeycomb or later, create and add animate de-consolidated markers

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
      markerGalleryHashMap = addAndAnimateMarkers(markerDelegateHashMap, markerGalleryHashMap);
    } else {
      // if not add the new markers as is
      final HashMap<Marker, List<Gallery>> newMarkerHashMap = new HashMap<Marker, List<Gallery>>();

      for (Map.Entry<MarkerDelegate, List<Gallery>> entry : markerDelegateHashMap.entrySet()) {
        newMarkerHashMap.put(entry.getKey().createMarker(map), entry.getValue());
      }
      markerGalleryHashMap = newMarkerHashMap;
    }
  }

  private boolean isMarkerOverlapping(LatLng existingLatLng, LatLng newLatLng,
      Projection projection) {

    Point existingPoint = projection.toScreenLocation(existingLatLng);
    Point newPoint = projection.toScreenLocation(newLatLng);

    return existingPoint.x + markerWidth >= newPoint.x
        && existingPoint.x - markerWidth <= newPoint.x
        && existingPoint.y + markerHeight >= newPoint.y
        && existingPoint.y - markerHeight <= newPoint.y;
  }

  /**
   * Sets marker title and snippet
   */
  private void setMarkerText(MarkerDelegate marker, List<Gallery> galleries) {
    if (galleries.size() == 1) {
      Gallery g = galleries.get(0);
      marker.setTitle(g.getName());
      marker.setSnippet(g.getDescription());
      marker.setGallery(g);
    } else {
      marker.setTitle(
          getResources().getString(R.string.consolidated_marker_title, galleries.size()));
      StringBuffer consolidatedDescription = new StringBuffer();
      for (Gallery g : galleries) {
        consolidatedDescription.append(consolidatedDescription.length() == 0 ? "" : ", ")
            .append(g.getName());
      }
      marker.setSnippet(consolidatedDescription.toString());
      marker.setGallery(null);
    }
  }

  /**
   * Find galleries whose markers have been deconsolidated and animate from the last position
   * This method also builds the new Marker HashMap from the MarkerDelegate HashMap
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private HashMap<Marker, List<Gallery>> addAndAnimateMarkers(
      final HashMap<MarkerDelegate, List<Gallery>> markerDelagateHashMap,
      final HashMap<Marker, List<Gallery>> oldMarkerHashMap) {

    HashMap<Marker, List<Gallery>> newMarkerHashMap = new HashMap<>();
    HashMap<String, Gallery> markerInfo = new HashMap<>();

    List<Animator> animatorList = new ArrayList<>();

    for (Map.Entry<MarkerDelegate, List<Gallery>> newEntry : markerDelagateHashMap.entrySet()) {
      LatLng finalPosition = null;
      outerloop:
      for (Map.Entry<Marker, List<Gallery>> oldEntry : oldMarkerHashMap.entrySet()) {
        for (Gallery newGallery : newEntry.getValue()) {
          if (isGalleryContained(oldEntry.getValue(), newGallery)
              && oldEntry.getValue().size() > newEntry.getValue().size()) {
            finalPosition = newEntry.getKey().getPosition();
            newEntry.getKey().setPosition(oldEntry.getKey().getPosition());

            break outerloop;
          }
        }
      }

      //create actual marker and add to new hashmap
      Marker marker = newEntry.getKey().createMarker(map);
      newMarkerHashMap.put(marker, newEntry.getValue());
      if (finalPosition != null) {
        animatorList.add(createMarkerAnimation(marker, finalPosition));
      }

      markerInfo.put(marker.getId(), newEntry.getKey().getGallery());
    }

    map.setInfoWindowAdapter(new GalleryInfoAdapter(getActivity().getApplicationContext(),
        getActivity().getLayoutInflater(), markerInfo));

    if (animatorList.size() > 0) {
      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.setStartDelay(ANIMATE_DELAY);
      animatorSet.setDuration(ANIMATE_LOCATION_DURATION);
      animatorSet.playTogether(animatorList);
      animatorSet.start();
    }
    return newMarkerHashMap;
  }

  private boolean isGalleryContained(List<Gallery> galleries, Gallery g) {
    for (Gallery testGallery : galleries) {
      if (g.equals(testGallery)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates a value animator for a given marker
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
  private ValueAnimator createMarkerAnimation(final Marker marker, final LatLng finalPosition) {

    final LatLng startPosition = marker.getPosition();
    ValueAnimator valueAnimator = new ValueAnimator();
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float v = animation.getAnimatedFraction();
        LatLng newPosition = interpolateMarkerPosition(v, startPosition, finalPosition);
        marker.setPosition(newPosition);
      }
    });
    valueAnimator.setFloatValues(0, 1);
    valueAnimator.addListener(new MarkerAnimatorListener(marker, finalPosition));
    return valueAnimator;
  }

  /**
   * Provided interoplation math for marker tanslation
   */
  public LatLng interpolateMarkerPosition(float fraction, LatLng a, LatLng b) {
    double lat = (b.latitude - a.latitude) * fraction + a.latitude;
    double lngDelta = b.longitude - a.longitude;

    // Take the shortest path across the 180th meridian.
    if (Math.abs(lngDelta) > 180) {
      lngDelta -= Math.signum(lngDelta) * 360;
    }
    double lng = lngDelta * fraction + a.longitude;
    return new LatLng(lat, lng);
  }

  /**
   * Show an infobox and move the camera so the camera is centered on the top of the marker
   */
  public void animateToOpenInfoWindow(Marker marker) {
    marker.showInfoWindow();
    Projection projection = map.getProjection();

    //get marker point on screen
    Point markerPoint = projection.toScreenLocation(marker.getPosition());

    //translate it down
    markerPoint.y -= getMarkerHeight();

    //create LatLng Offset from Point
    LatLng offset = projection.fromScreenLocation(markerPoint);
    map.animateCamera(CameraUpdateFactory.newLatLng(offset), ANIMATE_INFOBOX_DURATION, null);
  }

  private int getMarkerHeight() {
    return markerHeight;
  }

  public void setSavedCameraPosition(CameraPosition savedCameraPosition) {
    this.savedCameraPosition = savedCameraPosition;
  }

  public CameraPosition getCameraPosition() {
    if (map != null) {
      return map.getCameraPosition();
    }
    return null;
  }

  public interface Callbacks {
    void onGalleryMapClicked(Gallery gallery);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private static class MarkerAnimatorListener implements Animator.AnimatorListener {
    private Marker marker;
    private LatLng finalPosition;

    public MarkerAnimatorListener(Marker marker, final LatLng finalPosition) {

      this.marker = marker;
      this.finalPosition = finalPosition;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
      marker.setPosition(finalPosition);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
      marker.setPosition(finalPosition);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
  }

  private static class MarkerDelegate {
    private LatLng position;
    private String title;
    private String snippet;
    private Gallery gallery;

    public LatLng getPosition() {
      return position;
    }

    public void setPosition(LatLng latlng) {
      this.position = latlng;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getSnippet() {
      return snippet;
    }

    public void setSnippet(String snippet) {
      this.snippet = snippet;
    }

    public Gallery getGallery() {
      return gallery;
    }

    public void setGallery(Gallery gallery) {
      this.gallery = gallery;
    }

    public Marker createMarker(GoogleMap map) {
      return map.addMarker(
          new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin))
              .position(position)
              .title(title)
              .snippet(snippet));
    }
  }

  /**
   * What is this class doing here ? infowindow can't be modified once rendered. So images from
   * urls dont work. We use Picasso  async and cache capabilities. We load the image and
   * only call show infowindow once this is done, then from the infowindow adapter it will be
   * still in memory and will be done in the same thread.
   */
  private class InfoWindowImage implements Target {
    private Marker marker;

    public InfoWindowImage(Marker marker) {
      this.marker = marker;
    }

    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
      animateToOpenInfoWindow(marker);
    }

    @Override
    public void onBitmapFailed(Drawable d) {
      Log.e(TAG, "Failed to open infowindow");
      eventReporter.logException("Failed to open infowindow");
    }

    @Override
    public void onPrepareLoad(android.graphics.drawable.Drawable drawable) {
    }
  }
}

