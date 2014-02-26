package com.leexplorer.app.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leexplorer.app.R;
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
        holder.txPrice.setText(String.valueOf(gallery.getPrice()));

        MyAdapter mAdapter = new MyAdapter(fragment, gallery);
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

        @OnClick(R.id.llGalleryLocation)
        public void onClickAddress(View view) {
            fragment.callbacks.loadMap(String.valueOf(txAddress.getText()));
        }
    }

    public static class MyAdapter extends PagerAdapter {
        Gallery gallery;
        private LayoutInflater inflater;
        GalleryListFragment fragment;
        public MyAdapter(GalleryListFragment fragment, Gallery gallery) {
            super();
            this.gallery = gallery;
            this.fragment = fragment;
            this.inflater = LayoutInflater.from(fragment.getActivity());
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_gallery_image, null);
            ImageView ivGalleryImage = (ImageView)layout.findViewById(R.id.ivGallery);
            Picasso.with(fragment.getActivity())
                    .load("http://pablo-ruiz-picasso.com/images/works/143_s.jpg") // @todo ...
                    .fit()
                    .centerCrop()
                    .into(ivGalleryImage);

            ivGalleryImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.callbacks.loadGalleryDetails(gallery);
                }
            });

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }
}
