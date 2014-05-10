package com.leexplorer.app.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.leexplorer.app.R;
import com.leexplorer.app.adapters.GalleryAdapter;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.services.LocationService;
import com.leexplorer.app.util.GalleryComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by deepakdhiman on 2/17/14.
 */
public class GalleryListFragment extends Fragment {

    public Callbacks callbacks;
    @InjectView(R.id.lvGalleries)
    ListView lvGalleries;
    private List<Gallery> galleries;
    private GalleryAdapter galleryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleries = new ArrayList<Gallery>();
        galleryAdapter = new GalleryAdapter(this, galleries);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_list, container, false);
        ButterKnife.inject(this, view);
        lvGalleries.setAdapter(galleryAdapter);
        loadArtworkList();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            callbacks = (Callbacks) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement GalleryListFragment.Callbacks");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    private void loadArtworkList() {
        loadGalleryListFromApi();
        // Get data from Api or DB
//        if (LeexplorerApplication.isOnline()) {
//
//        } else {
//            //loadGalleryListFromDB();
//        }
    }

    private void loadGalleryListFromApi() {
        if (callbacks != null) {
            callbacks.onLoading(true);
        }
        Client.getGalleriesData()
                .subscribeOn(Schedulers.threadPoolForIO())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<ArrayList<Gallery>>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                throwable.printStackTrace();
                                if (callbacks != null) {
                                    callbacks.onLoading(false);
                                }
//                                if (galleries == null || galleries.size() == 0) {
//                                    loadGalleryListFromDB();
//                                }
                            }

                            @Override
                            public void onNext(ArrayList<Gallery> galleries) {
                                updateAdapterDataset(galleries);
                                if (callbacks != null) {
                                    callbacks.onLoading(false);
                                }
                            }
                        }
                );

    }

    private void updateAdapterDataset(ArrayList<Gallery> galleries) {
        this.galleries.clear();
        LocationService service = new LocationService(getActivity());
        Location currentLocation = null;
        if (service.isLocationAvailable()) {
            currentLocation = service.getLocation();
        }

        for (Gallery gallery : galleries) {
            if (currentLocation != null) {
                float[] results = new float[4];
                Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        gallery.getLatitude(), gallery.getLongitude(), results);
                //float distanceInMiles = /1609.344f;
                gallery.setDistanceFromCurrentLocation(results[0]);
            }
            this.galleries.add(gallery);
        }
        Collections.sort(this.galleries, new GalleryComparator());
        galleryAdapter.notifyDataSetChanged();
        if (callbacks.isTabletMode()) {
            callbacks.loadGalleryDetails(this.galleries.get(0));
        }
    }

//    private void loadGalleryListFromDB(){
//        if(callbacks != null) callbacks.onLoading(true);
//
//        Observable.create(new Observable.OnSubscribeFunc<ArrayList<Gallery>>() {
//            @Override
//            public Subscription onSubscribe(Observer<? super ArrayList<Gallery>> observer) {
//                observer.onNext(Gallery.galleryGallerys());
//                observer.onCompleted();
//                return Subscriptions.empty();
//            }
//        }).subscribeOn(Schedulers.threadPoolForIO())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Observer<ArrayList<Gallery>>() {
//                            @Override public void onCompleted() {callbacks.onLoading(false);}
//                            @Override public void onError(Throwable throwable) {}
//                            @Override public void onNext(ArrayList<Gallery> galleries) {
//                                updateAdapterDataset(galleries);
//                            }
//                        }
//                );
//
//        if(callbacks != null) callbacks.onLoading(false);
//    }

    public ArrayList<Gallery> getGalleries() {
        return new ArrayList<Gallery>(galleries);
    }

    public interface Callbacks {
        void onLoading(boolean loading);

        void loadGalleryDetails(Gallery gallery);

        void loadMap(String address);

        boolean isTabletMode();
    }

}
