package com.example.ashley.myapplication;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

public class Introduction extends AppCompatActivity {

    private TextView titletop;
    private ImageButton btnclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StatusBarCompat.setStatusBarColor(this, 0xff1a2644);

        titletop = (TextView) findViewById(R.id.title_top);
        titletop.setText("Introduction");

        btnclose = (ImageButton) findViewById(R.id.btnclosec) ;
        btnclose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

}
