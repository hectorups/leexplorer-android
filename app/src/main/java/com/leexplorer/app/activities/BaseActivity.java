package com.leexplorer.app.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

/**
 * Created by hectormonserrate on 20/02/14.
 */
public class BaseActivity extends ActionBarActivity {
    private int processesLoading = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    public void onLoading(boolean loading){
        processesLoading += loading ? 1 : -1;
        if( processesLoading < 1 ){
            processesLoading = 0;
            setProgressBarIndeterminateVisibility(false);
        } else {
            setProgressBarIndeterminateVisibility(true);
        }
    }
}
