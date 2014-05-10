package com.leexplorer.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leexplorer.app.R;
import com.leexplorer.app.fragments.FacilitiesDialogFragment;
import com.leexplorer.app.models.Facility;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.leexplorer.app.util.AppConstants.FACILITIES_LABEL_MAP;

/**
 * Created by deepakdhiman on 3/3/14.
 */
public class FacilitiesAdapter extends ArrayAdapter<Facility> {

    FacilitiesDialogFragment fragment;

    public FacilitiesAdapter(FacilitiesDialogFragment fragment, List<Facility> facilities) {
        super(fragment.getActivity(), 0, facilities);
        this.fragment = fragment;
    }


    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.facilities_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Facility facility = getItem(position);
        Bitmap bm = BitmapFactory.decodeResource(fragment.getResources(),
                facility.getFacilityBitMapId());
        holder.ivFacilitiesDetailImg.setImageBitmap(bm);
        holder.txFacilitiesDetailLabel.setText(FACILITIES_LABEL_MAP.get(facility.getFacilityName()));
        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.ivFacilitiesDetailImg)
        ImageView ivFacilitiesDetailImg;
        @InjectView(R.id.txFacilitiesDetailLabel)
        TextView txFacilitiesDetailLabel;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
