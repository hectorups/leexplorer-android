package com.leexplorer.app.fragments;

/**
 * Created by hectormonserrate on 10/02/14.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.util.functions.Action1;


public class ArtworkListFragment extends Fragment {
    private static final String ARTWORK_LIST = "arwork_list";
    private static final String TAG = "com.leexplorer.artworklistfragement";

    @InjectView(R.id.sgvArtworks) StaggeredGridView sgvArtworks;

    protected ArtworkAdapter artworkAdapter;

    private ArrayList<Artwork> artworks = new ArrayList<>();

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
        callbacks.onLoading(true);
        if( LeexplorerApplication.isOnline() ){
            loadArtworkListFromApi();
        } else {
//            populateProfileView(user);
        }
    }

    private void loadArtworkListFromApi(){
        Client.getArtworksData()
                .subscribeOn(Schedulers.threadPoolForIO())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Artwork>>() {
                    @Override
                    public void call(ArrayList<Artwork> aws) {
                        artworks.clear();
                        for(Artwork aw: aws){
                            artworks.add(aw);
                        }
                        artworkAdapter.notifyDataSetChanged();
                        callbacks.onLoading(false);
                    }
                });
    }

    /*
     * Called by the host activity to get the fragment artworks
     */
    public ArrayList<Artwork> getArtworks(){
        return artworks;
    }

}
