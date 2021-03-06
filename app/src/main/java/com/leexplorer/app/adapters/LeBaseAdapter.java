package com.leexplorer.app.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.leexplorer.app.core.ApplicationComponent;
import com.leexplorer.app.core.LeexplorerApplication;
import java.util.List;

public abstract class LeBaseAdapter<T> extends ArrayAdapter<T> {

  public LeBaseAdapter(Context context, List<T> objects) {
    super(context, 0, objects);

    injectComponent(((LeexplorerApplication) context.getApplicationContext()).getComponent());
  }

  abstract protected void injectComponent(ApplicationComponent component);
}
