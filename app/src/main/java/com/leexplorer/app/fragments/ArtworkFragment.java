package com.leexplorer.app.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.leexplorer.app.R;
import com.leexplorer.app.events.AudioCompleteEvent;
import com.leexplorer.app.events.AudioProgressEvent;
import com.leexplorer.app.events.FullScreenImageEvent;
import com.leexplorer.app.events.LoadingEvent;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.services.MediaPlayerService;
import com.leexplorer.app.util.ArtDate;
import com.leexplorer.app.util.AudioTime;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.transform.InvertTransformer;

public class ArtworkFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {
  private static final String TAG = "com.leexplorer.artworkfragment";
  private static final String EXTRA_ARTWORK = "extra_artwork";
  private static final String SAVED_CURRENT_DURAITON = "saved_current_duration";
  private static final String SAVED_TOTAL_DURAITON = "saved_total_duration";
  private static final String SAVED_NOW_PLAYING = "saved_now_playing";
  private static final String SAVED_ON_PAUSE = "saved_on_pause";

  @Inject Picasso picasso;
  @Inject ImageSourcePicker imageSourcePicker;
  @Inject Bus bus;

  @InjectView(R.id.tvAuthorAndDate) TextView tvAuthorAndDate;
  @InjectView(R.id.tvDescription) TextView tvDescription;
  @InjectView(R.id.ivArtwork) ImageView ivArtwork;
  @InjectView(R.id.svDescription) FrameLayout svDescription;
  @InjectView(R.id.flHeaderOverlay) FrameLayout flHeaderOverlay;
  @InjectView(R.id.flPlayAudio) FrameLayout flPlayAudio;
  @InjectView(R.id.btnPlay) ImageButton btnPlay;
  @InjectView(R.id.btnPause) ImageButton btnPause;
  @InjectView(R.id.sbAudio) SeekBar sbAudio;
  @InjectView(R.id.tvDuration) TextView tvDuration;
  @InjectView(R.id.tvTotalDuration) TextView tvTotalDuration;
  private Artwork artwork;
  private ShareActionProvider miShareAction;

  private MenuItem menuPlay;
  private Target targetForShare = new Target() {
    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
      // Do it in the bg so the ui feels fast
      addSubscription(Observable.create(new Observable.OnSubscribe<Uri>() {
        @Override
        public void call(Subscriber<? super Uri> subscriber) {
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

            @Override
            public void onNext(Uri bmpUri) {
              // Construct a ShareIntent with link to image
              Intent shareIntent = new Intent();
              shareIntent.setAction(Intent.ACTION_SEND);
              shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
              shareIntent.setType("image/*");

              miShareAction.setShareIntent(shareIntent);
            }
          }));
    }

    @Override
    public void onBitmapFailed(Drawable d) {
    }

    @Override
    public void onPrepareLoad(android.graphics.drawable.Drawable drawable) {
    }
  };

  private long audioTotalDuration = 0;
  private long audioCurrentDuration = 0;
  private boolean nowPlaying = false;
  private boolean onPause = false;

  @Override public String getScreenName() {
    return TAG;
  }

  @Subscribe public void audioProgressReceiver(AudioProgressEvent event) {
    Artwork playingArtwork = event.getArtwork();
    if (artwork.equals(playingArtwork)) {
      audioTotalDuration = event.getTotalDuration();
      audioCurrentDuration = event.getCurrentDuration();
      Log.d(TAG, "audio: " + audioCurrentDuration + " of " + audioTotalDuration);

      if (!onPause) {
        nowPlaying = true;
      }
    } else {
      setAudioClosed();
    }

    showAudio();
    updateSeekbar();
  }

  @Subscribe public void audioComplete(AudioCompleteEvent event) {
    Log.d(TAG, "audio: completed");
    setAudioClosed();
    showAudio();
  }

  private void setAudioClosed() {
    audioCurrentDuration = 0;
    audioTotalDuration = 0;
    nowPlaying = false;
    onPause = false;
  }

  public static ArtworkFragment newInstance(Artwork aw) {
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_ARTWORK, aw);
    ArtworkFragment fragment = new ArtworkFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      artwork = savedInstanceState.getParcelable(EXTRA_ARTWORK);
      audioCurrentDuration = savedInstanceState.getLong(SAVED_CURRENT_DURAITON);
      audioTotalDuration = savedInstanceState.getLong(SAVED_TOTAL_DURAITON);
      nowPlaying = savedInstanceState.getBoolean(SAVED_NOW_PLAYING);
      onPause = savedInstanceState.getBoolean(SAVED_ON_PAUSE);
    } else {
      artwork = getArguments().getParcelable(EXTRA_ARTWORK);
    }
    setHasOptionsMenu(true);
  }

  @Override
  public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override
  public void onPause() {
    bus.unregister(this);
    super.onPause();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putLong(SAVED_CURRENT_DURAITON, audioCurrentDuration);
    savedInstanceState.putLong(SAVED_TOTAL_DURAITON, audioTotalDuration);
    savedInstanceState.putBoolean(SAVED_NOW_PLAYING, nowPlaying);
    savedInstanceState.putBoolean(SAVED_ON_PAUSE, onPause);
    savedInstanceState.putParcelable(EXTRA_ARTWORK, artwork);
  }

  @SuppressWarnings("PMD") @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_artwork, container, false);

    ButterKnife.inject(this, rootView);

    showAudio();

    tvAuthorAndDate.setText(artwork.getAuthor());
    String dateText = ArtDate.shortDate(artwork.getPublishedAt());
    if (!TextUtils.isEmpty(dateText)) {
      tvAuthorAndDate.setText(tvAuthorAndDate.getText() + " - " + dateText);
    }

    tvDescription.setText(artwork.getDescription());

    if (svDescription instanceof Parallaxor) {
      ((Parallaxor) svDescription).parallaxViewBy(ivArtwork, new InvertTransformer(), 0.35f);
    }

    flHeaderOverlay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        bus.post(new FullScreenImageEvent(artwork));
      }
    });

    imageSourcePicker.getRequestCreator(artwork, R.dimen.thumbor_large).into(ivArtwork);

    sbAudio.setOnSeekBarChangeListener(this);

    return rootView;
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    MenuItem item = menu.findItem(R.id.menuShare);

    miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

    menuPlay = menu.findItem(R.id.menuPlay);

    if (artwork.getAudioUrl() != null) {
      menuPlay.setVisible(true);
    } else {
      menuPlay.setVisible(false);
    }

    picasso.load(artwork.getImageUrl()).into(targetForShare);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.artwork, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        navigateBack();
        break;
      case R.id.menuPlay:
        setAudioClosed();
        menuPlay.setVisible(false);
        playAudio(btnPlay);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }

  private void navigateBack() {
    if (NavUtils.getParentActivityName(getActivity()) != null) {
      NavUtils.navigateUpFromSameTask(getActivity());
    }
  }

  @OnClick(R.id.btnPlay)
  public void playAudio(View view) {
    Intent i = new Intent(getActivity(), MediaPlayerService.class);
    i.putExtra(MediaPlayerService.ARTWORK, artwork);
    //i.putExtra(MediaPlayerService.ARTWORKS, );
    i.putExtra(MediaPlayerService.ACTION, MediaPlayerService.ACTION_PLAY);
    getActivity().startService(i);

    updateSeekbar();
    if (onPause == false) {
      bus.post(new LoadingEvent(true));
    }
    nowPlaying = true;
    onPause = false;
    showAudio();
  }

  private void updateSeekbar() {
    sbAudio.setMax(100);

    if (audioCurrentDuration > 0 && audioTotalDuration > 0) {
      // Displaying Total Duration time
      tvTotalDuration.setText(String.valueOf(AudioTime.milliSecondsToTimer(audioTotalDuration)));
      // Displaying time completed playing
      tvDuration.setText(String.valueOf(AudioTime.milliSecondsToTimer(audioCurrentDuration)));
    }

    // Updating progress bar
    int progress = AudioTime.getProgressPercentage(audioCurrentDuration, audioTotalDuration);
    sbAudio.setProgress(progress);
  }

  private void showAudio() {
    if (menuPlay != null) {
      if (audioCurrentDuration == 0 && artwork.getAudioUrl() != null) {
        menuPlay.setVisible(true);
      } else {
        menuPlay.setVisible(false);
      }
    }

    if (!nowPlaying && !onPause) {
      flPlayAudio.setVisibility(View.GONE);

      return;
    }

    flPlayAudio.setVisibility(View.VISIBLE);
    if (nowPlaying) {
      btnPause.setVisibility(View.VISIBLE);
      btnPlay.setVisibility(View.GONE);
    } else {
      btnPause.setVisibility(View.GONE);
      btnPlay.setVisibility(View.VISIBLE);
    }
  }

  @OnClick(R.id.btnPause)
  public void pauseAudio(View view) {
    pause();
    onPause = true;
    nowPlaying = false;
    showAudio();
  }

  public void pause() {
    Intent i = new Intent(getActivity(), MediaPlayerService.class);
    i.putExtra(MediaPlayerService.ACTION, MediaPlayerService.ACTION_PAUSE);
    getActivity().startService(i);
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    if (!nowPlaying) {
      return;
    }
    int currentPosition =
        AudioTime.progressToTimer(seekBar.getProgress(), (int) audioTotalDuration);

    Intent i = new Intent(getActivity(), MediaPlayerService.class);
    i.putExtra(MediaPlayerService.ACTION, MediaPlayerService.ACTION_SEEK_TO);
    i.putExtra(MediaPlayerService.SEEK_TO_VALUE, currentPosition);
    getActivity().startService(i);
  }
}
