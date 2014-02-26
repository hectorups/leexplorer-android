package com.leexplorer.app.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.leexplorer.app.R;
import com.leexplorer.app.api.Client;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.services.MediaPlayerService;
import com.leexplorer.app.util.ArtDate;
import com.leexplorer.app.util.AudioTime;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.transform.InvertTransformer;

/**
 * Created by hectormonserrate on 11/02/14.
 */
public class ArtworkFragment extends Fragment implements  SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "com.leexplorer.artworkfragment";
    private static final String EXTRA_ARTWORK = "extra_artwork";
    private static final String SAVED_CURRENT_DURAITON = "saved_current_duration";
    private static final String SAVED_TOTAL_DURAITON = "saved_total_duration";
    private static final String SAVED_NOW_PLAYING = "saved_now_playing";
    private static final String SAVED_ON_PAUSE = "saved_on_pause";

    private final int LIKED_IMG_SIZE = 52;

    @InjectView(R.id.tvAuthorAndDate)
    TextView tvAuthorAndDate;

    @InjectView(R.id.tvDescription)
    TextView tvDescription;

    @InjectView(R.id.ivArtwork)
    ImageView ivArtwork;

    @InjectView(R.id.ivLiked)
    ImageView ivLiked;

    @InjectView(R.id.ivLike)
    ImageView ivLike;

    @InjectView(R.id.svDescription)
    FrameLayout svDescription;

    @InjectView(R.id.flHeaderOverlay) FrameLayout flHeaderOverlay;
    @InjectView(R.id.flLike) FrameLayout flLike;
    @InjectView(R.id.tvLikesCount) TextView tvLikesCount;
    @InjectView(R.id.ivLikesCount) ImageView ivLikesCount;

    @InjectView(R.id.flPlayAudio) FrameLayout flPlayAudio;
    @InjectView(R.id.btnPlay) ImageButton btnPlay;
    @InjectView(R.id.btnPause) ImageButton btnPause;
    @InjectView(R.id.sbAudio) SeekBar sbAudio;
    @InjectView(R.id.tvDuration) TextView tvDuration;
    @InjectView(R.id.tvTotalDuration) TextView tvTotalDuration;

    private MenuItem menuPlay;


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
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(MediaPlayerService.ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(audioProgressReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            getActivity().unregisterReceiver(audioProgressReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(SAVED_CURRENT_DURAITON, audioCurrentDuration);
        savedInstanceState.putLong(SAVED_TOTAL_DURAITON, audioTotalDuration);
        savedInstanceState.putBoolean(SAVED_NOW_PLAYING, nowPlaying);
        savedInstanceState.putBoolean(SAVED_ON_PAUSE, onPause);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artwork, container, false);

        ButterKnife.inject(this, rootView);

        if (savedInstanceState != null) {
            audioCurrentDuration = savedInstanceState.getLong(SAVED_CURRENT_DURAITON);
            audioTotalDuration = savedInstanceState.getLong(SAVED_TOTAL_DURAITON);
            nowPlaying = savedInstanceState.getBoolean(SAVED_NOW_PLAYING);
            onPause = savedInstanceState.getBoolean(SAVED_ON_PAUSE);

            showAudio();
        }

        tvAuthorAndDate.setText(artwork.getAuthor() + " - " + ArtDate.shortDate(artwork.getPublishedAt()));
        tvDescription.setText(artwork.getDescription());

        if (svDescription instanceof Parallaxor) {
            ((Parallaxor) svDescription).parallaxViewBy(ivArtwork, new InvertTransformer(), 0.35f);
        }


        flHeaderOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int offset[] = new int[2];
                flLike.getLocationOnScreen( offset );

                if( Math.abs(motionEvent.getRawX() - (offset[0] + LIKED_IMG_SIZE/2 ) ) < LIKED_IMG_SIZE * 2
                    && Math.abs(motionEvent.getRawY() - (offset[1] + LIKED_IMG_SIZE/2 ) ) < LIKED_IMG_SIZE * 2
                        ){
                    onClickLike();
                }
                return false;
            }
        });
        if( artwork.isiLiked() ){
            ivLiked.setVisibility(View.VISIBLE);
        }

        tvLikesCount.setText(String.valueOf(artwork.getLikesCount()));
         Picasso.with(getActivity())
                .load(artwork.getImageUrl())
                .fit()
                .centerCrop()
                .into(ivArtwork);

        sbAudio.setOnSeekBarChangeListener(this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artwork, menu);
        MenuItem item = menu.findItem(R.id.menuShare);

        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        menuPlay = menu.findItem(R.id.menuPlay);

        if(artwork.getAudioUrl() != null ){
            menuPlay.setVisible(true);
        } else {
            menuPlay.setVisible(false);
        }

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
            case R.id.menuPlay:
                menuPlay.setVisible(false);
                playAudio(btnPlay);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickLike() {
        Client.getService().likeArtwork(artwork.isiLiked() ? 0:1, new Callback<Void>(){
            public void failure( RetrofitError re){}
            public void success( Void v, Response r){}
        });


        if(artwork.isiLiked()){
            artwork.unlike();
            ivLiked.setVisibility(View.INVISIBLE);

        } else {
            artwork.like();
            ivLiked.setVisibility(View.VISIBLE);
            Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.anim.enlarge);
            anim.setTarget(ivLiked);
            anim.start();
        }

        tvLikesCount.setText( String.valueOf(artwork.getLikesCount()) );
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

    /*
     * AUDIO @todo: port it to a service
     *
     */

    private long audioTotalDuration = 0;
    private long audioCurrentDuration = 0;
    private boolean nowPlaying = false;
    private boolean onPause = false;

    @OnClick(R.id.btnPlay)
    public void playAudio(View view){

        Intent i = new Intent(getActivity(), MediaPlayerService.class);
        i.putExtra(MediaPlayerService.ARTWORK, artwork);
        //i.putExtra(MediaPlayerService.ARTWORKS, );
        i.putExtra(MediaPlayerService.ACTION, MediaPlayerService.ACTION_PLAY );
        getActivity().startService(i);

        updateSeekbar();
        nowPlaying = true;
        onPause = false;
        showAudio();
    }

    private void updateSeekbar(){
        sbAudio.setMax(100);

        if( audioCurrentDuration > 0 && audioTotalDuration > 0){
            // Displaying Total Duration time
            tvTotalDuration.setText(String.valueOf(AudioTime.milliSecondsToTimer(audioTotalDuration)));
            // Displaying time completed playing
            tvDuration.setText(String.valueOf(AudioTime.milliSecondsToTimer(audioCurrentDuration)));
        }

        // Updating progress bar
        int progress = AudioTime.getProgressPercentage(audioCurrentDuration, audioTotalDuration);
        sbAudio.setProgress(progress);

    }

    private void showAudio(){

        if(menuPlay != null){
            if(audioCurrentDuration == 0 && artwork.getAudioUrl() != null ){
                menuPlay.setVisible(true);
            } else {
                menuPlay.setVisible(false);
            }
        }

        if(!nowPlaying && !onPause){
            flPlayAudio.setVisibility(View.GONE);
            return;
        }

        flPlayAudio.setVisibility(View.VISIBLE);
        if(nowPlaying){
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        } else {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnPause)
    public void pauseAudio(View view){
        pause();
        onPause = true;
        nowPlaying = false;
        showAudio();
    }


    public void pause(){
        Intent i = new Intent(getActivity(), MediaPlayerService.class);
        i.putExtra(MediaPlayerService.ACTION, MediaPlayerService.ACTION_PAUSE );
        getActivity().startService(i);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // handler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(  !nowPlaying ) return;
        int currentPosition = AudioTime.progressToTimer(seekBar.getProgress(), (int) audioTotalDuration);

        Intent i = new Intent(getActivity(), MediaPlayerService.class);
        i.putExtra(MediaPlayerService.ACTION, MediaPlayerService.ACTION_SEEK_TO );
        i.putExtra(MediaPlayerService.SEEK_TO_VALUE, currentPosition);
        getActivity().startService(i);

    }

    private BroadcastReceiver audioProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            Artwork playingArtwork = intent.getParcelableExtra(MediaPlayerService.ARTWORK);
            if (resultCode == Activity.RESULT_OK){
                if(artwork.equals(playingArtwork)) {
                    audioTotalDuration = intent.getLongExtra(MediaPlayerService.TOTAL_DURATION, 0);
                    audioCurrentDuration = intent.getLongExtra(MediaPlayerService.CURRENT_DURATION, 0);
                    if(!onPause) nowPlaying = true;
                } else {
                    audioCurrentDuration = 0;
                    audioTotalDuration = 0;
                    nowPlaying = false;
                    onPause = false;
                }

                showAudio();
                updateSeekbar();
            }
        }
    };



}
