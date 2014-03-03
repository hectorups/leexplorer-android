package com.leexplorer.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.leexplorer.app.R;

import java.util.List;

/**
 * Created by deepakdhiman on 3/3/14.
 */
public class FacilitiesAdapter {

    List<String> facilities;

    public FacilitiesAdapter(List<String> facilities){
        this.facilities = facilities;
    }


    public View getView(int position, View view, ViewGroup parent) {
//        ViewHolder holder;
//        if (view != null) {
//            holder = (ViewHolder) view.getTag();
//        } else {
//            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.gallery_item, parent, false);
//            holder = new ViewHolder(view, fragment);
//            view.setTag(holder);
//        }



        return view;
    }
}
