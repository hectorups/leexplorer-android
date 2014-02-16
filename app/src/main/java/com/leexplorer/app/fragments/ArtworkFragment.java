package com.leexplorer.app.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.leexplorer.app.R;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.util.ArtDate;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.transform.InvertTransformer;

/**
 * Created by hectormonserrate on 11/02/14.
 */
public class ArtworkFragment extends Fragment {
    private static final String TAG = "com.leexplorer.artworkfragment";
    private static final String EXTRA_ARTWORK = "extra_artwork";

    @InjectView(R.id.tvAuthorAndDate)
    TextView tvAuthorAndDate;

    @InjectView(R.id.tvDescription)
    TextView tvDescription;

    @InjectView(R.id.ivArtwork)
    ImageView ivArtwork;

    Artwork artwork;
    ShareActionProvider miShareAction;

    public static ArtworkFragment newInstance(Artwork aw){
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ARTWORK, aw);

        ArtworkFragment fragment = new ArtworkFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artwork = getArguments().getParcelable(EXTRA_ARTWORK);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artwork, container, false);

        ButterKnife.inject(this, rootView);

        tvAuthorAndDate.setText(artwork.getAuthor() + " - " + ArtDate.shortDate(artwork.getPublishedAt()));
        tvDescription.setText(artwork.getDescription());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.ivArtwork);
        ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        if (scrollView instanceof Parallaxor) {
            ((Parallaxor) scrollView).parallaxViewBy(imageView, new InvertTransformer(), 0.35f);
        }


        Picasso.with(getActivity())
                .load(artwork.getImageUrl())
                .fit()
                .centerCrop()
                .into(ivArtwork);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artwork, menu);
        MenuItem item = menu.findItem(R.id.menuShare);

        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Picasso.with(getActivity())
                .load(artwork.getImageUrl())
                .into(targetForShare);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                navigateBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateBack(){
        if(NavUtils.getParentActivityName(getActivity()) != null){
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    private Target targetForShare = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            // Do it in the bg so the ui feels fast
            Observable.create(new Observable.OnSubscribeFunc<Uri>() {
                @Override
                public Subscription onSubscribe(Observer<? super Uri> bitmapObserver) {
                    Uri bmpUri = null;
                    try {
                        File file =  new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), "share_image.png");
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.close();
                        bmpUri = Uri.fromFile(file);
                        if(bmpUri != null) bitmapObserver.onNext(bmpUri);
                        bitmapObserver.onCompleted();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Subscriptions.empty();
                }
            }).subscribeOn(Schedulers.newThread())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Action1<Uri>() {
                  @Override
                  public void call(Uri bmpUri) {
                      // Construct a ShareIntent with link to image
                      Intent shareIntent = new Intent();
                      shareIntent.setAction(Intent.ACTION_SEND);
                      shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                      shareIntent.setType("image/*");

                      miShareAction.setShareIntent(shareIntent);
                  }
              });
        }

        @Override
        public void onBitmapFailed(Drawable d) {
        }

        @Override
        public void onPrepareLoad(android.graphics.drawable.Drawable drawable){}
    };

}
