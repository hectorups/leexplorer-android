package com.leexplorer.app.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.leexplorer.app.core.LeexplorerApplication;
import java.util.List;

/**
 * Created by hectormonserrate on 11/05/14.
 */
public abstract class LeBaseAdapter<T> extends ArrayAdapter<T> {

  public LeBaseAdapter(Context context, List<T> objects) {
    super(context, 0, objects);

    ((LeexplorerApplication) context.getApplicationContext()).inject(this);
  }
}
