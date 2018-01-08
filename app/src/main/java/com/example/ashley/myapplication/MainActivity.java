package com.example.ashley.myapplication;

/**
 * Created by M5510 on 2017/11/3.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import Modules.Constants;
import Modules.PlacesAutoCompleteAdapter;
import Modules.RecyclerItemClickListener;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    protected GoogleApiClient mGoogleApiClient;

    private EditText act1;
    private EditText act2;

    private String startp;
    private String endp;

    private Button btnsearch;

    ImageView delete1;
    ImageView delete2;

    public void FocusElsewhere()
    {
        final RelativeLayout rlytTimerName = (RelativeLayout) findViewById(R.id.RelativeLayout1);
        rlytTimerName.setFocusable(true);
        rlytTimerName.setFocusableInTouchMode(true);
        rlytTimerName.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StatusBarCompat.setStatusBarColor(this, 0xff1a2644);
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        FocusElsewhere();

        act1 = (EditText) findViewById(R.id.act1);
        act2 = (EditText) findViewById(R.id.act2);

        delete1 = (ImageView)findViewById(R.id.cross);

        act1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    act1.setCursorVisible(true);
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("from",1);
                    intent.putExtras(bundle);

                    startActivityForResult(intent, 1);
                } else {
                    act1.setCursorVisible(false);
                }
            }
        });
        etNameLostFocus(act1);

        act2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    act2.setCursorVisible(true);
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("from",2);
                    intent.putExtras(bundle);

                    startActivityForResult(intent, 1);
                } else {
                    act1.setCursorVisible(false);
                }
            }
        });


        //setAutoComplete(act2, RecyclerView2, delete2);
        //ArrayAdapter<String> ab=new ArrayAdapter<String>(this,
               //ndroid.R.layout.simple_dropdown_item_1line,words);
       // act2.setAdapter(ab);

        act2.setOnKeyListener(onKeyListener);

        //act1.addTextChangedListener(textWatcher);
        //act2.addTextChangedListener(textWatcher);

        btnsearch = (Button) findViewById(R.id.btnSearch);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            //重写点击事件的处理方法onClick()
            @Override
            public void onClick(View v) {
                //显示Toast信息
                startp = act1.getText().toString();
                endp = act2.getText().toString();

                if( startp.length() == 0 )
                {
                    Toast.makeText(getApplicationContext(), "Please enter your location", Toast.LENGTH_SHORT).show();
                }
                else if( endp.length() == 0 )
                {
                    Toast.makeText(getApplicationContext(), "Please enter your destination", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);

                    Bundle bundle=new Bundle();
                    bundle.putString("result", startp);
                    bundle.putString("content",endp);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private OnKeyListener onKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                //隐藏软键盘
                InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isActive()){
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                act2.requestFocus();
                // 对应逻辑操作
                return true;
            }
            return false;
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            Log.d("TAG","beforeTextChanged--------------->");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            Log.d("TAG","onTextChanged--------------->");
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            Log.d("TAG", "afterTextChanged--------------->");
            int lines = act1.getLineCount();
            int lines1 = act2.getLineCount();
            // 限制最大输入行数
            if (lines > 1) {
                String str = s.toString();
                int cursorStart = act1.getSelectionStart();
                int cursorEnd = act1.getSelectionEnd();
                if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                    str = str.substring(0, cursorStart - 1) + str.substring(cursorStart);
                } else {
                    str = str.substring(0, s.length() - 1);
                }
                // setText会触发afterTextChanged的递归
                act1.setText(str);
                // setSelection用的索引不能使用str.length()否则会越界
                act1.setSelection(act1.getText().length());
                act2.requestFocus();
            }
            if (lines1 > 1) {
                String str = s.toString();
                int cursorStart = act2.getSelectionStart();
                int cursorEnd = act2.getSelectionEnd();
                if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                    str = str.substring(0, cursorStart - 1) + str.substring(cursorStart);
                } else {
                    str = str.substring(0, s.length() - 1);
                }
                // setText会触发afterTextChanged的递归
                act2.setText(str);
                // setSelection用的索引不能使用str.length()否则会越界
                act2.setSelection(act2.getText().length());
            }
        }
    };

    @Override
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
            Intent intent = new Intent(MainActivity.this, Introduction.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 监控返回键
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("Google API Callback","Connection Failed");
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, Constants.API_NOT_CONNECTED,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v == delete1){
            act1.setText("");
        }
        else if(v == delete2) {
            act2.setText("");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void etNameLostFocus(EditText etName) {
        etName.clearFocus();
        InputMethodManager manager = (InputMethodManager) etName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(etName.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //System.out.println("!!!IN");
        if (resultCode == 4) {
            act1.setText(data.getStringExtra("result"));
            //btncategory.setImageResource(R.drawable.icon_category_changed);
        }

        if (resultCode == 5) {
            act2.setText(data.getStringExtra("result"));
        }

        FocusElsewhere();
    }
}