package com.mark.mediarecorderdemo.Manager;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mark.mediarecorderdemo.Activity.MainActivity;

import java.io.IOException;

/**
 * Created by marklin on 2017/12/8.
 */

public class MediaPlayerManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public static final String TAG = MediaPlayerManager.class.getSimpleName();

    private MediaPlayer mediaPlayer;
    private Context context;

    private String recorderPath = "";
    private boolean isPause = false;

    public MediaPlayerManager(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }

    public void setPath(String path) {
        recorderPath = path;
    }

    public void play() {
        if (isPause) {
            isPause = false;
            mediaPlayer.start();
        } else {
            try {
                mediaPlayer.setDataSource(recorderPath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Media not found, IO Exception");
            }
        }
    }

    public void pause() {
        mediaPlayer.pause();
        isPause = true;
    }

    public void reset() {
        isPause = false;
        mediaPlayer.reset();
        play();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
