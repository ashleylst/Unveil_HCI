package com.example.ashley.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import static android.R.id.progress;

public class Range extends AppCompatActivity {

    private SeekBar seekbar2;
    private SeekBar seekbar1;

    private TextView showSeekText1;
    private TextView showSeekText2;

    private TextView titletop;
    private ImageButton btnclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_range);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StatusBarCompat.setStatusBarColor(this, 0xff1a2644);

        titletop = (TextView) findViewById(R.id.title_top);
        titletop.setText("Range");

        seekbar1 = (SeekBar)findViewById(R.id.seekBar1);
        showSeekText1 = (TextView)findViewById(R.id.showSeekText1);
        //设置进度条改变事件
        seekbar1.setOnSeekBarChangeListener(new SeekBarListeners());

        seekbar2 = (SeekBar)findViewById(R.id.seekBar2);
        showSeekText2 = (TextView)findViewById(R.id.showSeekText2);
        //设置进度条改变事件
        seekbar2.setOnSeekBarChangeListener(new SeekBarListeners());

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

    final class SeekBarListeners implements SeekBar.OnSeekBarChangeListener {

        //正在拖动时触发
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            //showSeekText.append("正在拖动:"+seekBar.getProgress()+":"+fromUser+":"+progress+"\r\n");
        }
        //开始拖动时触发
        public void onStartTrackingTouch(SeekBar seekBar) {
            //showSeekText.append("开始拖动:"+seekBar.getProgress()+"\r\n");
        }
        //拖动停止时触发
        public void onStopTrackingTouch(SeekBar seekBar) {

            Intent intent = new Intent();
            intent.putExtra("text",true);
            setResult(1,intent);

            switch (seekBar.getId()) {

                case R.id.seekBar1:
                    showSeekText1.setText("Maximum distance to hotspots: " + seekBar.getProgress() + "m");
                    break;

                case R.id.seekBar2:
                    showSeekText2.setText("Maximum number of hotspots: " + seekBar.getProgress());
                    break;
            }
            //showSeekText.append("停止拖动:"+seekBar.getProgress()+"\r\n");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 监控返回键
           finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
