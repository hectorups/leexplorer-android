package com.leexplorer.app.adapters;

/**
 * Created by hectormonserrate on 13/02/14.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leexplorer.app.R;
import com.leexplorer.app.models.Artwork;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ArtworkAdapter extends ArrayAdapter<Artwork> {
    protected Fragment fragment;

    public ArtworkAdapter(Fragment fragment, Context context, List<Artwork> objects) {
        super(context, 0, objects);
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.artwork_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tvAuthorAndDate) TextView name;
        @InjectView(R.id.tvAuthorAndDate) TextView tvAuthorAndDate;
        @InjectView(R.id.ivArtworkThumb) TextView ivArtworkThumb;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
