package com.leexplorer.app.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.leexplorer.app.R;
import com.leexplorer.app.adapters.GalleryAdapter;
import com.leexplorer.app.models.Gallery;
import com.leexplorer.app.util.FakeData;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by deepakdhiman on 2/17/14.
 */
public class GalleryListFragment extends Fragment {

    @InjectView(R.id.lvGalleries) ListView lvGalleries;
    private List<Gallery> galleries;
    private GalleryAdapter galleryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleries = new ArrayList<Gallery>();
        //galleryAdapter = new GalleryAdapter(this, galleries);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_list, container, false);
        ButterKnife.inject(this,view);
        List<Gallery> galleries = FakeData.getGalleries();
        galleryAdapter = new GalleryAdapter(this, galleries);
        lvGalleries.setAdapter(galleryAdapter);
        galleryAdapter.addAll(galleries);

        lvGalleries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "HELLO WORLD!!!!!", Toast.LENGTH_SHORT ).show();
            }
        });
        return view;
    }


    public interface Callbacks {
        public void onLoading(boolean loading);
        public void loadGalleryDetails(Gallery gallery);
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
                    + " must implement GalleryListFragment.Callbacks");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }
}
