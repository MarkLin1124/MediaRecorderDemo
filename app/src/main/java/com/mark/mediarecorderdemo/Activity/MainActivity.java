package com.mark.mediarecorderdemo.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.mark.mediarecorderdemo.R;

/**
 * Created by marklin on 2017/12/8.
 */

public class MainActivity extends FragmentActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
