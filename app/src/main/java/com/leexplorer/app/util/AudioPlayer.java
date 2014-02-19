package com.leexplorer.app.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by hectormonserrate on 18/02/14.
 */
public class AudioPlayer {

    private MediaPlayer mediaPlayer;
    private int currentPosition;

    public boolean isOn(){
        return mediaPlayer != null;
    }

    public void stop(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
            currentPosition = 0;
        }
    }

    public void play(Context c, String audio){

        if(mediaPlayer != null && currentPosition != 0){
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
            return;
        }

        stop();
        mediaPlayer = new MediaPlayer();
        Uri myUri = Uri.parse(audio);
        try {
            mediaPlayer.setDataSource(c, myUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            public void onCompletion(MediaPlayer mp){
                mp.stop();
            }
        });
        mediaPlayer.start();

    }

    public void pause(){
        if(mediaPlayer == null){
            return;
        }

        mediaPlayer.pause();
        currentPosition = mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public long getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int posiiton){
        mediaPlayer.seekTo(posiiton);
    }

}
