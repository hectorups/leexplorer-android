package com.leexplorer.app.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ImageShareTarget implements Target {

  private CompositeSubscription compositeSubscription;
  private Callbacks callbacks;

  public interface Callbacks {
    void readyToShare(Uri bmpUri);
  }

  public ImageShareTarget(CompositeSubscription subscription) {
    compositeSubscription = subscription;
  }

  public void setCallbacks(Callbacks callbacks) {
    this.callbacks = callbacks;
  }

  @Override public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
    // Do it in the bg so the ui feels fast
    compositeSubscription.add(Observable.create(new Observable.OnSubscribe<Uri>() {
      @Override public void call(Subscriber<? super Uri> subscriber) {
        Uri bmpUri;
        try {
          File file = new File(
              Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
              "share_image.png");
          FileOutputStream out = new FileOutputStream(file);
          bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
          out.close();
          bmpUri = Uri.fromFile(file);
          if (bmpUri != null) {
            subscriber.onNext(bmpUri);
          }
          subscriber.onCompleted();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    })
        .subscribeOn(Schedulers.newThread())
        .observeOn(Schedulers.io())
        .subscribe(new Observer<Uri>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
          }

          @Override public void onNext(Uri bmpUri) {
            // Construct a ShareIntent with link to image
            callbacks.readyToShare(bmpUri);
          }
        }));
  }

  @Override public void onBitmapFailed(Drawable d) {
  }

  @Override public void onPrepareLoad(android.graphics.drawable.Drawable drawable) {
  }
}
