package com.example.ashley.myapplication;

/**
 * Created by M5510 on 2017/11/19.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class SlidingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (enableSliding()) {
            SlidingLayout rootView = new SlidingLayout(this);
            rootView.bindActivity(this);
        }
    }

    protected boolean enableSliding() {
        return true;
    }
}
