package com.example.ashley.myapplication;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.jar.*;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;


/**
 * Created by M5510 on 2017/11/10.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;

    private ImageButton btncategory;
    private ImageButton btnrange;
    private ImageButton btngoback;
    private ImageButton btnwalk;
    private ImageButton btncycling;

    private EditText startp;
    private EditText endp;

    private ImageButton btnexchange;
    private ImageButton btnsetting;

    private TextView topLineTv;

    private TopMiddlePopup middlePopup;

    public static int screenW, screenH;

    private static final String TAG = "hci";

    private PlaceDetectionClient mPlaceDetectionClient;

    private LocationManager locationManager;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private CameraPosition mCameraPosition;
    private GeoDataClient mGeoDataClient;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private boolean isWalking = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        StatusBarCompat.setStatusBarColor(this, 0xff1a2644);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        final RelativeLayout rlytTimerName = (RelativeLayout) findViewById(R.id.layout_map);
        rlytTimerName.setFocusable(true);
        rlytTimerName.setFocusableInTouchMode(true);
        rlytTimerName.requestFocus();

        initView();
        getScreenPixels();

        startp = (EditText) findViewById(R.id.start_point);
        endp = (EditText) findViewById(R.id.end_point);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String content = intent.getStringExtra("content");

        startp.setText(result.toCharArray(), 0, result.length());
        endp.setText(content.toCharArray(), 0, content.length());

        startp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    startp.setCursorVisible(true);
                    Intent intent = new Intent(MapActivity.this, SearchActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("from",1);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1);
                } else {
                    startp.setCursorVisible(false);
                }
            }
        });

        etNameLostFocus(startp);


        endp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    endp.setCursorVisible(true);
                    Intent intent = new Intent(MapActivity.this, SearchActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("from",2);
                    intent.putExtras(bundle);

                    startActivityForResult(intent, 1);
                } else {
                    startp.setCursorVisible(false);
                }
            }
        });

        etNameLostFocus(endp);

        startp.addTextChangedListener(textWatcher);
        endp.addTextChangedListener(textWatcher);

        getLocationPermission();

        sendRequest();
    }

    private void sendRequest() {
        String origin = startp.getText().toString();
        String destination = endp.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination, isWalking).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_start))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_end))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(0xff4285f4).
                    width(12);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    public void LocatePoints() {
        List<android.location.Address> addressList = null;
        List<android.location.Address> addressList2 = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(startp.getText().toString(), 1);
            addressList2 = geocoder.getFromLocationName(endp.getText().toString(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.location.Address address = addressList.get(0);
        android.location.Address address1 = addressList2.get(0);
        LatLng startlocation = new LatLng(address.getLatitude(), address.getLongitude());
        LatLng endlocation = new LatLng(address1.getLatitude(), address1.getLongitude());
        mMap.addMarker(new MarkerOptions().position(startlocation).title("marker1"));
        mMap.addMarker(new MarkerOptions().position(endlocation).title("marker2"));
    }

    public class Markers {
        public String name_of_hotspot;
        public double longtitude;
        public double latitude;
        public Marker marker;
        private boolean isPlayed = false;

        public Markers(String name, double latitude, double longtitude) {
            this.name_of_hotspot = name;
            this.longtitude = longtitude;
            this.latitude = latitude;
            this.marker = initiate();
        }

        public Marker initiate() {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longtitude))
                    .title(name_of_hotspot)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_2))
            );
            return marker;
        }

        protected void finalize(){
            System.out.println("finalize");
        }
    }

    ArrayList markerslist = new ArrayList();

    public void SetMarkers() {
        markerslist.add(new Markers("Amsterdam Central Station", 52.377524, 4.900757));
        markerslist.add(new Markers("Basilica of St Nicholas", 52.376600, 4.901100));
        markerslist.add(new Markers("Schreiers Tower", 52.376535, 4.902276));
        markerslist.add(new Markers("Grand Hotel Amrath (Prins Hendrikkade 108)", 52.374421, 4.904043));
        markerslist.add(new Markers("Kikkerbilssluis", 52.372834, 4.907869));
        markerslist.add(new Markers("Hogeschool (Prins Hendrikkade 191)", 52.371220, 4.911683));
        markerslist.add(new Markers("Compagnie Pakhuizen (P.Hendrikkade 176)", 52.371568, 4.910036));
        markerslist.add(new Markers("Pakhuizen (s-Gravenhekje 1a)", 52.372240, 4.907915));
        markerslist.add(new Markers("Gebouw Batavia (Prins Hendrikkade 84)", 52.376770, 4.901615));
        markerslist.add(new Markers("De Gooyer", 52.366799, 4.926541));
        markerslist.add(new Markers("Admiraliteitslijnbaan(Werkspoormuseum)", 52.367946, 4.924320));
        markerslist.add(new Markers("PC Hoofthuis", 52.373553, 4.889660));
        markerslist.add(new Markers("Magna Plaza", 52.373612, 4.890465));
        markerslist.add(new Markers("Dam Palace", 52.373206, 4.891431));
        markerslist.add(new Markers("Huis de Pinto", 52.370112, 4.900913));
        markerslist.add(new Markers("Mozes & Aaron Church", 52.368356, 4.903400));
        markerslist.add(new Markers("Desmet Studios", 52.366832, 4.909599));
        markerslist.add(new Markers("Artis: Aquarium", 52.364662, 4.918025));
        markerslist.add(new Markers("Muiderpoort", 52.363692, 4.919591));
        markerslist.add(new Markers("Working-class Housing Berlage", 52.363677, 4.939922));
    }

    private void DestroyMarkers(){
        Iterator<Markers> iter = markerslist.iterator();
        while (iter.hasNext())
        {
            Markers i = iter.next();
            i = null;
            System.gc();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 3) {
            boolean text = data.getExtras().getBoolean("text");
            if (text) {
                btncategory.setImageResource(R.drawable.icon_category_changed);
            }
            //btncategory.setImageResource(R.drawable.icon_category_changed);
        }

        if (resultCode == 1) {
            boolean text = data.getExtras().getBoolean("text");
            if (text) {
                btnrange.setImageResource(R.drawable.icon_range_changed);
            }
        }

        if (resultCode == 4) {
            startp.setText(data.getStringExtra("result"));
            //btncategory.setImageResource(R.drawable.icon_category_changed);
            ClearView();
        }

        if (resultCode == 5) {
            endp.setText(data.getStringExtra("result"));
            ClearView();
        }

    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            Log.d("TAG", "afterTextChanged--------------->");
            int lines = startp.getLineCount();
            int lines1 = endp.getLineCount();
// 限制最大输入行数
            if (lines > 1) {
                String str = s.toString();
                int cursorStart = startp.getSelectionStart();
                int cursorEnd = startp.getSelectionEnd();
                if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                    str = str.substring(0, cursorStart - 1) + str.substring(cursorStart);
                } else {
                    str = str.substring(0, s.length() - 1);
                }
// setText会触发afterTextChanged的递归
                startp.setText(str);
// setSelection用的索引不能使用str.length()否则会越界
                startp.setSelection(startp.getText().length());
            }
            if (lines1 > 1) {
                String str = s.toString();
                int cursorStart = endp.getSelectionStart();
                int cursorEnd = endp.getSelectionEnd();
                if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                    str = str.substring(0, cursorStart - 1) + str.substring(cursorStart);
                } else {
                    str = str.substring(0, s.length() - 1);
                }
// setText会触发afterTextChanged的递归
                endp.setText(str);
// setSelection用的索引不能使用str.length()否则会越界
                endp.setSelection(endp.getText().length());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            Log.d("TAG", "beforeTextChanged--------------->");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            Log.d("TAG", "onTextChanged--------------->");
        }
    };

    private void etNameLostFocus(EditText etName) {
        etName.clearFocus();
        InputMethodManager manager = (InputMethodManager) etName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(etName.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获取当前焦点所在的控件；
            View view = getCurrentFocus();
            if (view != null && view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int) ev.getRawX();
                int rawY = (int) ev.getRawY();

                // 判断点击的点是否落在当前焦点所在的 view 上；
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    private void initView() {

        btncategory = (ImageButton) findViewById(R.id.btnCategory);
        btnrange = (ImageButton) findViewById(R.id.btnRange);
        btngoback = (ImageButton) findViewById(R.id.btngoback);
        btnwalk = (ImageButton) findViewById(R.id.btnwalk);
        btncycling = (ImageButton) findViewById(R.id.btncycle);

        btncategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resetImg();
                //btncategory.setImageResource(R.drawable.tab_weixin_pressed);
                Intent intent2 = new Intent(MapActivity.this, Category.class);
                startActivityForResult(intent2, 1);
                //startActivity(intent2);
            }
        });

        btnrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resetImg();
                //btncategory.setImageResource(R.drawable.tab_weixin_pressed);
                Intent intent = new Intent(MapActivity.this, Range.class);
                startActivityForResult(intent, 1);
            }
        });

        btngoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final FloatingActionButton btnFab = (FloatingActionButton) findViewById(R.id.btnfab);
        final FloatingActionButton btnStop = (FloatingActionButton) findViewById(R.id.btnfab2);
        btnFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(MapActivity.this, "Hello FAB!", Toast.LENGTH_SHORT).show();

                btnFab.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                Iterator<Markers> iter = markerslist.iterator();
                while (iter.hasNext())
                {
                    iter.next().isPlayed = false;
                }
                UpdateLocation();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btnFab.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
                Iterator<Markers> iter = markerslist.iterator();
                while (iter.hasNext())
                {
                    iter.next().isPlayed = true;
                }
            }
        });

        btnexchange = (ImageButton) findViewById(R.id.btnexchange);
        btnexchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp;
                tmp = startp.getText().toString();
                startp.setText(endp.getText().toString().toCharArray(), 0, endp.length());
                endp.setText(tmp.toCharArray(), 0, tmp.length());

                ClearView();
            }
        });

        topLineTv = (TextView) findViewById(R.id.rule_line_tv);

        btnsetting = (ImageButton) findViewById(R.id.btnsetting);
        btnsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(0);
                middlePopup.show(topLineTv);
            }
        });

        btncycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWalking) {
                    btncycling.setImageResource(R.drawable.cycling_changed);
                    btnwalk.setImageResource(R.drawable.walk);
                    mMap.clear();
                    isWalking = false;

                    ClearView();
                }
            }
        });

        btnwalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWalking) {
                    btncycling.setImageResource(R.drawable.cycling);
                    btnwalk.setImageResource(R.drawable.walk_changed);

                    isWalking = true;

                    ClearView();
                }
            }
        });

    }

    private void ClearView() {
        DestroyMarkers();

        for (Polyline line : polylinePaths) {
            line.remove();
        }
        polylinePaths.clear();

        sendRequest();

        SetMarkers();
    }


    /**
     * 设置弹窗
     *
     * @param type
     */
    private void setPopup(int type) {
        middlePopup = new TopMiddlePopup(MapActivity.this, screenW, screenH,
                onItemClickListener, getItemsName(), type);
    }

    /**
     * 设置弹窗内容
     *
     * @return
     */
    private ArrayList<String> getItemsName() {
        ArrayList<String> items = new ArrayList<String>();
        items.add("Share");
        items.add("Suggest hotspots");
        items.add("Settings");
        return items;
    }

    /**
     * 弹窗点击事件
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            System.out.println("--onItemClickListener--:");
            middlePopup.dismiss();
        }
    };

    /**
     * 获取屏幕的宽和高
     */
    public void getScreenPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenW = metrics.widthPixels;
        screenH = metrics.heightPixels;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView infotext = (TextView) v.findViewById(R.id.infotext);

                    infotext.setText(marker.getTitle());
                    return v;
                }
            });
        }
        getLocationPermission();

        SetMarkers();

        // getDeviceLocation();

        //Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // LocatePoints();
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        //updateLocationUI();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            System.out.println("!!!!I am here");
                            mLastKnownLocation = task.getResult();
                            LatLng mpos = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(mpos).title("Marker in mypos"));
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(mpos));
                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mpos, 13));
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(mpos)      // Sets the center of the map to location user
                                    .zoom(17)                   // Sets the zoom
                                    .bearing(90)                // Sets the orientation of the camera to east
                                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            // new LatLng(mLastKnownLocation.getLatitude(),
                            //  mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            System.out.println("!!!!Wrong");
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    public void UpdateLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 从GPS获取最近的定位信息
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // 将location里的位置信息显示在EditText中
        Navigate(location);
        // 设置每2秒获取一次GPS的定位信息
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000, 8, new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        // 当GPS定位信息发生改变时，更新位置
                        Navigate(location);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Navigate(null);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // 当GPS LocationProvider可用时，更新位置

                        Navigate(locationManager
                                .getLastKnownLocation(provider));

                    }

                    @Override
                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {
                    }
                });
    }

    public void Navigate(Location location)
    {
        Iterator<Markers> iterator = markerslist.iterator();
        while (iterator.hasNext())
        {
            Markers i = iterator.next();
            float result_d[] = new float[10];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), i.latitude, i.longtitude, result_d);
            String s = Float.toString(result_d[0]);
            if( result_d[0] != 0f && !i.isPlayed )
            {
                Toast.makeText(MapActivity.this, s, Toast.LENGTH_SHORT).show();
                MediaPlayer player = MediaPlayer.create(MapActivity.this, R.raw.test);
                player.start();
                i.isPlayed = true;
                break;
            }
        }
    }
}
