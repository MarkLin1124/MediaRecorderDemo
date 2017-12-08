package com.mark.mediarecorderdemo.Manager;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by marklin on 2017/12/8.
 */

public class MediaRecorderManager {
    public static final String TAG = MediaRecorderManager.class.getSimpleName();

    private MediaRecorder mediaRecorder;
    private Context context;

    private String recorderPath = "";
    private boolean isRecording = false;

    public MediaRecorderManager(Context context) {
        this.context = context;
    }

    public void startRecord() {
        if (mediaRecorder != null) {
            resetFile();
        }

        mediaRecorder = new MediaRecorder();
        File file = new File(context.getCacheDir(), Long.toString(System.currentTimeMillis()) + ".mp3");
        recorderPath = file.getAbsolutePath();

        Log.d(TAG, "file path: " + file.getAbsolutePath());

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(recorderPath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Log.d(TAG, "startRecord");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (mediaRecorder == null) {
            return;
        }

        Log.d(TAG, "stopRecord");
        mediaRecorder.stop();
        mediaRecorder.release();
        isRecording = false;
    }

    public void resetFile() {
        Log.d(TAG, "reset file");
        mediaRecorder = null;
        recorderPath = "";
    }

    public String getRecorderPath() {
        return recorderPath;
    }

    public boolean isRecording() {
        return isRecording;
    }
}
