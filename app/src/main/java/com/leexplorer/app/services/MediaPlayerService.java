package com.leexplorer.app.services;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.leexplorer.app.R;
import com.leexplorer.app.activities.ArtworkActivity;
import com.leexplorer.app.models.Artwork;

import java.util.ArrayList;

public class MediaPlayerService extends IntentService {

    private static final int STATUS_INTERVAL = 500;

    public static final String TOTAL_DURATION = "com.leexplorer.mediaplayerservice.total_duration";
    public static final String CURRENT_DURATION = "com.leexplorer.mediaplayerservice.current_duration";
    public static final String ARTWORK = "com.leexplorer.mediaplayerservice.artworks";
    public static final String ARTWORKS = "com.leexplorer.mediaplayerservice.artwork";
    public static final String SEEK_TO_VALUE = "com.leexplorer.mediaplayerservice.seek_to_value";
    public static final String ACTION = "com.leexplorer.mediaplayerservice.action";
    public static final int ACTION_PLAY = 1;
    public static final int ACTION_STOP = 2;
    public static final int ACTION_PAUSE = 3;
    public static final int ACTION_SEEK_TO = 4;

    private static final int NOTIFICATION_ID = 11;
    private static MediaPlayer mediaPlayer;
    private static int currentPosition = 0;
    private static Artwork artwork;
    private static ArrayList<Artwork> artworks;

    private Handler handler = new Handler();

    public MediaPlayerService() {
        super("test-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Service", "Intent received");
        switch (intent.getIntExtra(ACTION, 0)){
            case ACTION_PLAY:
                //artworks = intent.getParcelableArrayListExtra(ARTWORKS);
                play((Artwork) intent.getParcelableExtra(ARTWORK));
                break;
            case ACTION_STOP:
                stop();
                break;
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_SEEK_TO:
                seek_to(intent.getIntExtra(SEEK_TO_VALUE, 0));
                break;
        }
    }

    private void prepareNotification() {
        Resources r = getResources();
        Intent intent = new Intent(this, ArtworkActivity.class);
        intent.putExtra(ArtworkActivity.EXTRA_ARTWORK, artwork);
        intent.putExtra(ArtworkActivity.EXTRA_ARTWORKS, artworks);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_action_play)
                .setContentTitle(r.getString(R.string.audio_notification_title))
                .setContentText(r.getString(R.string.audio_notification_text))
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }


    private void stop(){
        handler.removeCallbacks(updateTimeTask);

        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
    }

    private void play(Artwork artwork){

        if(mediaPlayer != null && this.artwork != null && this.artwork.equals(artwork)){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
            updateProgress();
            return;
        }

        this.artwork = artwork;
        stop();

        Uri audioUri = Uri.parse("http://podcasts.ricksteves.com/walkingtours/Pantheon.mp3");
        mediaPlayer = MediaPlayer.create(getApplicationContext(), audioUri);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
            }
        });
        mediaPlayer.start();
        prepareNotification();
        updateProgress();
    }

    private void pause(){
        if(mediaPlayer == null){
            return;
        }

        mediaPlayer.pause();
    }

    private void seek_to(int position){
        if(mediaPlayer == null){
            return;
        }

        mediaPlayer.seekTo(position);
    }

    private void updateProgress() {
        handler.postDelayed(updateTimeTask, STATUS_INTERVAL);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            if( mediaPlayer == null){
                handler.removeCallbacks(updateTimeTask);
                return;
            }

            try{
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();
                broadcastProgress(totalDuration, currentDuration);
                handler.postDelayed(this, STATUS_INTERVAL);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void broadcastProgress(long totalDuration, long currentDuration){
        Intent in = new Intent(ACTION);
        in.putExtra("resultCode", Activity.RESULT_OK);
        in.putExtra(ARTWORK, artwork);
        in.putExtra(TOTAL_DURATION, totalDuration);
        in.putExtra(CURRENT_DURATION, currentDuration);

        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }


}
