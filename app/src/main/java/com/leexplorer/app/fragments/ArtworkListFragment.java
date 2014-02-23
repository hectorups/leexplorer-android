package com.leexplorer.app.fragments;

/**
 * Created by hectormonserrate on 10/02/14.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.leexplorer.app.LeexplorerApplication;
import com.leexplorer.app.R;
import com.leexplorer.app.adapters.ArtworkAdapter;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.services.BeaconScanService;
import com.leexplorer.app.util.Beacon;
import com.leexplorer.app.util.BeaconArtworkUpdater;
import com.leexplorer.app.util.BeaconsManager;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;


public class ArtworkListFragment extends Fragment {

    private static final String ARTWORK_LIST = "arwork_list";
    private static final String TAG = "com.leexplorer.artworklistfragement";

    @InjectView(R.id.sgvArtworks) StaggeredGridView sgvArtworks;

    protected ArtworkAdapter artworkAdapter;

    private ArrayList<Artwork> artworks = new ArrayList<>();
    private BeaconsManager beaconsManager;

    public interface Callbacks {
        public void onLoading(boolean loading);
        public void onArtworkClicked(Artwork aw);
    }

    public Callbacks callbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            callbacks = (Callbacks)activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ArtworkListFragment.Callbacks");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        beaconsManager = BeaconsManager.getInstance();
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(BeaconScanService.ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(beaconsReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            getActivity().unregisterReceiver(beaconsReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artwork_list_responsive, container, false);

        ButterKnife.inject(this, rootView);

        if (savedInstanceState != null) {
            artworks = savedInstanceState.getParcelableArrayList(ARTWORK_LIST);
        } else {
            refreshArtworkList();
        }

        ///// testing...
        Intent i = new Intent(getActivity(), BeaconScanService.class);
        getActivity().startService(i);
        /////

        artworkAdapter = new ArtworkAdapter(this, artworks);
        sgvArtworks.setAdapter(artworkAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(ARTWORK_LIST, artworks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artwork_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuRefresh:
                refreshArtworkList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshArtworkList(){
        if( LeexplorerApplication.isOnline() ){
            loadArtworkListFromApi();
        } else {
            loadArtworkListFromDB();
        }
    }

    private void loadArtworkListFromApi(){
        if(callbacks != null) callbacks.onLoading(true);
        Client.getArtworksData()
                .subscribeOn(Schedulers.threadPoolForIO())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<ArrayList<Artwork>>() {
                            @Override public void onCompleted() {callbacks.onLoading(false);}

                            @Override public void onError(Throwable throwable) {
                                if(callbacks != null) callbacks.onLoading(false);
                                loadArtworkListFromDB();
                            }

                            @Override public void onNext(ArrayList<Artwork> aws) {
                                refreshArtworkAdapter(aws);
                            }
                        }
                );

    }

    private void loadArtworkListFromDB(){
        if(callbacks != null) callbacks.onLoading(true);

        Observable.create(new Observable.OnSubscribeFunc<ArrayList<Artwork>>() {
            @Override
            public Subscription onSubscribe(Observer<? super ArrayList<Artwork>> observer) {
                observer.onNext(Artwork.galleryArtworks());
                observer.onCompleted();
                return Subscriptions.empty();
            }
        }).subscribeOn(Schedulers.threadPoolForIO())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    new Observer<ArrayList<Artwork>>() {
                        @Override public void onCompleted() {callbacks.onLoading(false);}
                        @Override public void onError(Throwable throwable) {}
                        @Override public void onNext(ArrayList<Artwork> aws) {
                            refreshArtworkAdapter(aws);
                        }
                    }
            );

        if(callbacks != null) callbacks.onLoading(false);
    }

    private void refreshArtworkAdapter(ArrayList<Artwork> aws){
        BeaconArtworkUpdater.updateDistances(aws, beaconsManager.getAll());

        Collections.sort(aws);

        artworks.clear();
        for(Artwork aw: aws){
            artworks.add(aw);
        }
        artworkAdapter.notifyDataSetChanged();
    }

    /*
     * Called by the host activity to get the fragment artworks
     */
    public ArrayList<Artwork> getArtworks(){
        return artworks;
    }

    private BroadcastReceiver beaconsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            ArrayList<Beacon> beacons = intent.getParcelableArrayListExtra(BeaconScanService.BEACONS);
            if (resultCode == Activity.RESULT_OK){
                Log.d(TAG, "Beacons detected: " + beacons.size());
                refreshArtworkList();
            }
        }
    };

}



