package com.leexplorer.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leexplorer.app.R;
import com.leexplorer.app.fragments.GalleryImageFragment;
import com.leexplorer.app.fragments.GalleryListFragment;
import com.leexplorer.app.models.Gallery;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by deepakdhiman on 2/18/14.
 */
public class GalleryAdapter extends ArrayAdapter<Gallery> {

    GalleryListFragment fragment;

    public GalleryAdapter(GalleryListFragment fragment, List<Gallery> galleries) {
        super(fragment.getActivity(), 0, galleries);
        this.fragment = fragment;
    }

    @Override public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_item, parent, false);
            holder = new ViewHolder(view, fragment);
            view.setTag(holder);
        }

        Gallery gallery = getItem(position);

        holder.txGalleryName.setText(gallery.getName());
        holder.txAddress.setText(gallery.getAddress());
        holder.txGalleryType.setText(gallery.getType());
        holder.txPrice.setText(gallery.getPrice());

        MyAdapter mAdapter = new MyAdapter(fragment.getChildFragmentManager(), gallery.getImageUrl());
        holder.pager.setAdapter(mAdapter);
        holder.pager.setCurrentItem(0);

        return view;
    }

    static class ViewHolder {
        private GalleryListFragment fragment;

        @InjectView(R.id.txGalleryName) TextView txGalleryName;
        @InjectView(R.id.txAddress) TextView txAddress;
        @InjectView(R.id.txGalleryType) TextView txGalleryType;
        @InjectView(R.id.txPrice) TextView txPrice;
        @InjectView(R.id.pager) ViewPager pager;

        public ViewHolder(View view, GalleryListFragment fragment) {
            ButterKnife.inject(this, view);
            this.fragment = fragment;
        }

        @OnClick(R.id.pager)
        public void onClickArtwork(View view) {
            Toast.makeText(fragment.getActivity(),"Hello World", Toast.LENGTH_SHORT).show();
            System.out.println("&&&&&&%%%%%%%%%HELLO...................................)))))))))))))))))))))))))))))))");
            System.out.println("&&&&&&%%%%%%%%%HELLO...................................)))))))))))))))))))))))))))))))");
            System.out.println("&&&&&&%%%%%%%%%HELLO...................................)))))))))))))))))))))))))))))))");
            System.out.println("&&&&&&%%%%%%%%%HELLO...................................)))))))))))))))))))))))))))))))");
            System.out.println("&&&&&&%%%%%%%%%HELLO...................................)))))))))))))))))))))))))))))))");
            System.out.println("&&&&&&%%%%%%%%%HELLO...................................)))))))))))))))))))))))))))))))");
            System.out.println("&&&&&&%%%%%%%%%HELLO...................................)))))))))))))))))))))))))))))))");
        }
    }


    public static class MyAdapter extends FragmentPagerAdapter {
        String url;
        public MyAdapter(FragmentManager fragmentManager, String url) {
            super(fragmentManager);
            this.url = url;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            return GalleryImageFragment.newInstance(url);
        }
    }
}
