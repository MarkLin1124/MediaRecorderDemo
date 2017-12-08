package com.mark.mediarecorderdemo.Activity;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mark.mediarecorderdemo.Manager.MediaPlayerManager;
import com.mark.mediarecorderdemo.Manager.MediaRecorderManager;
import com.mark.mediarecorderdemo.R;

import java.util.Calendar;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by marklin on 2017/12/8.
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int PERMISSION_STORAGE = 1000;
    public static final int PERMISSION_AUDIO_RECORD = 1001;

    public static final int START_RECORD = 0;
    public static final int STOP_RECORD = 1;
    public static final int RESET_RECORD = 2;

    private MediaRecorderManager mediaRecorderManager;
    private MediaPlayerManager mediaPlayerManager;

    private TextView tvRecordPath, tvRecordTime, tvMediaTime;
    private Button btnStart, btnStop, btnReset, btnPlay, btnPause, btnRelay;

    private ProgressDialog dialog;

    private int continueTime = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            switch (msg.what) {
                case START_RECORD:
                    tvRecordPath.setText(mediaRecorderManager.getRecorderPath());
                    mHandler.postDelayed(countDownRunnable, 1000);
                    break;
                case STOP_RECORD:
                    mHandler.removeCallbacks(countDownRunnable);
                    continueTime = 0;
                    break;
                case RESET_RECORD:
                    tvRecordPath.setText("");
                    tvRecordTime.setText("");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaRecorderManager == null) {
            mediaRecorderManager = new MediaRecorderManager(MainActivity.this);
        }
        if (mediaPlayerManager == null) {
            mediaPlayerManager = new MediaPlayerManager(MainActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        if (!checkPermission()) {
            return;
        }

        switch (v.getId()) {
            case R.id.btn_start:
                if (!mediaRecorderManager.isRecording()) {
                    dialog.show();
                    mHandler.post(startRecord);
                }
                break;
            case R.id.btn_stop:
                if (mediaRecorderManager.isRecording()) {
                    dialog.show();
                    mHandler.post(stopRecord);
                }
                break;
            case R.id.btn_reset:
                if (!mediaRecorderManager.isRecording()) {
                    mHandler.post(resetRecord);
                }
                break;
            case R.id.btn_play:
                if (!TextUtils.isEmpty(mediaRecorderManager.getRecorderPath()) && !mediaPlayerManager.getMediaPlayer().isPlaying()) {
                    mediaPlayerManager.setPath(mediaRecorderManager.getRecorderPath());
                    mediaPlayerManager.play();
                }
                break;
            case R.id.btn_pause:
                if (mediaPlayerManager.getMediaPlayer().isPlaying()) {
                    mediaPlayerManager.pause();
                }
                break;
            case R.id.btn_replay:
                if (!mediaPlayerManager.getMediaPlayer().isPlaying()) {
                    mediaPlayerManager.reset();
                }
                break;
        }
    }

    private void initView() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Waiting...");

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(this);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnPause.setOnClickListener(this);
        btnRelay = (Button) findViewById(R.id.btn_replay);
        btnRelay.setOnClickListener(this);

        tvRecordPath = (TextView) findViewById(R.id.tv_record_path);
        tvRecordTime = (TextView) findViewById(R.id.tv_continue_time);
        tvMediaTime = (TextView) findViewById(R.id.tv_media_time);
    }

    private boolean checkPermission() {
        int[] permission = {ActivityCompat.checkSelfPermission(MainActivity.this, RECORD_AUDIO), ActivityCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE)};

        if (permission[0] != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO}, PERMISSION_AUDIO_RECORD);
            return false;
        }

        if (permission[1] != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_AUDIO_RECORD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog.show();
                    mHandler.post(startRecord);
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            continueTime = continueTime + 1000;

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(continueTime);
            tvRecordTime.setText(String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", calendar.get(Calendar.SECOND)));

            mHandler.postDelayed(countDownRunnable, 1000);
        }
    };

    private Runnable startRecord = new Runnable() {
        @Override
        public void run() {
            //record in background and use handler message to control UI
            mediaRecorderManager.startRecord();
            mHandler.sendMessage(mHandler.obtainMessage(START_RECORD));
        }
    };

    private Runnable stopRecord = new Runnable() {
        @Override
        public void run() {
            mediaRecorderManager.stopRecord();
            mHandler.sendMessage(mHandler.obtainMessage(STOP_RECORD));
        }
    };

    private Runnable resetRecord = new Runnable() {
        @Override
        public void run() {
            mediaRecorderManager.resetFile();
            mHandler.sendMessage(mHandler.obtainMessage(RESET_RECORD));
        }
    };
}
