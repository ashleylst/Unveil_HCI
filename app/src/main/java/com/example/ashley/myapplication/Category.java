package com.example.ashley.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by M5510 on 2017/11/11.
 */

public class Category extends AppCompatActivity {

    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter adapter;
    private ImageButton btnclose;
    private int[] icons = {R.drawable.logo, R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo };
    private String[] text = {"111", "222", "333", "444", "555", "666"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StatusBarCompat.setStatusBarColor(this, 0xff1a2644);

        gridView = (GridView) findViewById(R.id.gridView);
        data_list = new ArrayList<Map<String, Object>>();
        getData();
        //加载适配器
        String[] form = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        adapter = new SimpleAdapter(this, data_list, R.layout.item, form, to);
        gridView.setAdapter(adapter);
        //监听item每一项
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(Category.this, "你点击了第" + i, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("text",true);
                setResult(3,intent);
                //finish();
            }
        });

        btnclose = (ImageButton) findViewById(R.id.btnclosec);
        btnclose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    public List<Map<String, Object>> getData() {

        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icons[i]);
            map.put("text", text[i]);
            data_list.add(map);
        }
        return data_list;
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
