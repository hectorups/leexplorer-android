package com.leexplorer.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.leexplorer.app.R;
import com.leexplorer.app.events.LoadingEvent;
import com.leexplorer.app.events.ShareEvent;
import com.leexplorer.app.events.artworks.FullScreenImageEvent;
import com.leexplorer.app.events.audio.AudioCompleteEvent;
import com.leexplorer.app.events.audio.AudioProgressEvent;
import com.leexplorer.app.events.audio.AudioResumingEvent;
import com.leexplorer.app.events.audio.AudioStartedEvent;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.services.MediaPlayerService;
import com.leexplorer.app.services.MediaPlayerService.Status;
import com.leexplorer.app.util.ArtDate;
import com.leexplorer.app.util.AudioTime;
import com.leexplorer.app.util.ImageShareTarget;
import com.leexplorer.app.util.offline.ImageSourcePicker;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import javax.inject.Inject;
import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.transform.InvertTransformer;

public class ArtworkFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {
  private static final String TAG = "com.leexplorer.artworkfragment";
  private static final String EXTRA_ARTWORK = "extra_artwork";
  private static final String SAVED_CURRENT_DURAITON = "saved_current_duration";
  private static final String SAVED_TOTAL_DURAITON = "saved_total_duration";
  private static final String SAVED_STATUS = "saved_now_playing";

  @Inject ImageSourcePicker imageSourcePicker;
  @Inject Bus bus;

  @InjectView(R.id.tvAuthorAndDate) TextView tvAuthorAndDate;
  @InjectView(R.id.tvDescription) TextView tvDescription;
  @InjectView(R.id.ivArtwork) ImageView ivArtwork;
  @InjectView(R.id.svDescription) FrameLayout svDescription;
  @Optional @InjectView(R.id.flHeaderOverlay) FrameLayout flHeaderOverlay;
  @InjectView(R.id.flPlayAudio) FrameLayout flPlayAudio;
  @InjectView(R.id.btnPlay) ImageButton btnPlay;
  @InjectView(R.id.btnPause) ImageButton btnPause;
  @InjectView(R.id.sbAudio) SeekBar sbAudio;
  @InjectView(R.id.tvDuration) TextView tvDuration;
  @InjectView(R.id.tvTotalDuration) TextView tvTotalDuration;
  @InjectView(R.id.llArtwoContent) LinearLayout llArtworkContent;

  private Artwork artwork;
  private int originalContentPadding;

  private MenuItem menuPlay;
  private ImageShareTarget targetForShare;

  private long audioTotalDuration = 0;
  private long audioCurrentDuration = 0;
  private Status audioStatus;
  private boolean waitingForShareImage = false;
  private Uri shareImage;

  public static ArtworkFragment newInstance(Artwork artwork) {
    Bundle args = new Bundle();
    args.putParcelable(EXTRA_ARTWORK, artwork);
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
      audioStatus = MediaPlayerService.Status.values()[savedInstanceState.getInt(SAVED_STATUS, 0)];
    } else {
      artwork = getArguments().getParcelable(EXTRA_ARTWORK);
      audioStatus = Status.Idle;
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
    savedInstanceState.putParcelable(EXTRA_ARTWORK, artwork);
    savedInstanceState.putInt(SAVED_STATUS, audioStatus.ordinal());
  }

  @SuppressWarnings("PMD") @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_artwork_responsive, container, false);

    ButterKnife.inject(this, rootView);

    setupShare();

    showAudio();

    setupUI();

    return rootView;
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    menuPlay = menu.findItem(R.id.menuPlay);

    if (artwork.getAudioId() != null) {
      menuPlay.setVisible(true);
    } else {
      menuPlay.setVisible(false);
    }

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
      case R.id.menuShare:
        shareArtwork();
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

  @Override public String getScreenName() {
    return TAG;
  }

  private void setupShare() {
    targetForShare = new ImageShareTarget(getCompositeSubscription());
    targetForShare.setCallbacks(new ImageShareTarget.Callbacks() {
      @Override public void readyToShare(Uri bmpUri) {
        shareImage = bmpUri;
        if (waitingForShareImage) {
          waitingForShareImage = false;
          shareArtwork();
        }
      }
    });

    imageSourcePicker.getRequestCreator(artwork, R.dimen.thumbor_medium).into(targetForShare);
  }

  private void setupUI() {
    tvAuthorAndDate.setText(artwork.getAuthor());
    String dateText = ArtDate.shortDate(artwork.getPublishedAt());
    if (!TextUtils.isEmpty(dateText)) {
      tvAuthorAndDate.setText(tvAuthorAndDate.getText() + " - " + dateText);
    }

    tvDescription.setText(artwork.getDescription());

    if (svDescription instanceof Parallaxor) {
      ((Parallaxor) svDescription).parallaxViewBy(ivArtwork, new InvertTransformer(), 0.35f);
    }

    if (flHeaderOverlay != null) {
      flHeaderOverlay.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          bus.post(new FullScreenImageEvent(artwork));
        }
      });
    } else {
      ivArtwork.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          bus.post(new FullScreenImageEvent(artwork));
        }
      });
    }

    imageSourcePicker.getRequestCreator(artwork, R.dimen.thumbor_large)
        .placeholder(R.drawable.image_place_holder)
        .into(ivArtwork);

    sbAudio.setOnSeekBarChangeListener(this);

    originalContentPadding = llArtworkContent.getPaddingBottom();
  }

  private void shareArtwork() {
    if (shareImage == null) {
      waitingForShareImage = true;
      return;
    }

    bus.post(new ShareEvent(artwork.getName(), getString(R.string.share_artwork_description), null,
        "artwork", shareImage));
  }

  @Subscribe public void audioProgressReceiver(AudioProgressEvent event) {
    Artwork playingArtwork = event.getArtwork();
    if (artwork.equals(playingArtwork)) {
      if (audioCurrentDuration == 0) {
        bus.post(new LoadingEvent(false));
      }

      audioStatus = event.getStatus();
      audioTotalDuration = event.getTotalDuration();
      audioCurrentDuration = event.getCurrentDuration();
      Log.d(TAG, "Audio: "
          + audioStatus.name()
          + " "
          + audioCurrentDuration
          + " of "
          + audioTotalDuration);
    } else {
      audioStatus = Status.Idle;
      setAudioClosed();
    }

    showAudio();
    updateSeekbar();
  }

  @Subscribe public void onAudioComplete(AudioCompleteEvent event) {
    Log.d(TAG, "audio: completed");
    setAudioClosed();
    showAudio();
  }

  @Subscribe public void onAudioFailed(AudioStartedEvent event) {
    if (event.getArtwork().equals(artwork)) {
      bus.post(new LoadingEvent(false));
    }
  }

  @Subscribe public void onAudioResuming(AudioResumingEvent event) {
    if (event.getArtwork().equals(artwork)) {
      audioStatus = Status.Paused;
    }
  }

  private void setAudioClosed() {
    audioCurrentDuration = 0;
    audioTotalDuration = 0;
    audioStatus = Status.Idle;
  }

  @OnClick(R.id.btnPlay)
  public void playAudio(View view) {
    updateSeekbar();
    if (audioStatus == Status.Idle) {
      bus.post(new LoadingEvent(true));
    }
    audioStatus = Status.Playing;
    showAudio();

    Intent i = new Intent(getActivity(), MediaPlayerService.class);
    i.putExtra(MediaPlayerService.ARTWORK, artwork);
    i.putExtra(MediaPlayerService.ACTION, MediaPlayerService.ACTION_PLAY);
    getActivity().startService(i);
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
      if (audioCurrentDuration == 0 && artwork.getAudioId() != null) {
        menuPlay.setVisible(true);
      } else {
        menuPlay.setVisible(false);
      }
    }

    if (audioStatus == Status.Idle) {
      if (llArtworkContent.getPaddingBottom() > originalContentPadding) {
        llArtworkContent.setPadding(llArtworkContent.getPaddingLeft(),
            llArtworkContent.getPaddingTop(), llArtworkContent.getPaddingRight(),
            originalContentPadding);
      }
      flPlayAudio.setVisibility(View.GONE);
      return;
    }

    if (llArtworkContent.getPaddingBottom() == originalContentPadding) {
      llArtworkContent.setPadding(llArtworkContent.getPaddingLeft(),
          llArtworkContent.getPaddingTop(), llArtworkContent.getPaddingRight(),
          originalContentPadding + flPlayAudio.getHeight());
    }

    flPlayAudio.setVisibility(View.VISIBLE);
    if (audioStatus == Status.Playing) {
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
    audioStatus = Status.Paused;
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
    if (audioStatus != Status.Playing) {
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
