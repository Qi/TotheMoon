package com.vortex.qi.tothemoon;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


//    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
//    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
//    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
//    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
//    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
//    private static final LatLng DARWIN = new LatLng(-12.459501, 130.839915);

    private LatLng home;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 123;
    public TextView nextDate;
    public TextView prizePool;
    public FloatingActionButton fab;
    public Spinner regionSpinner;
    public NavigationView navigationView;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public String[] infoDate, infoPool;
    public MenuItem lastPickLottery;
    public int lastPickRegion = -1;
    public CameraUpdate upDate;
    public LocationManager locationManager;
    public String locationProvider;
    public Location location;
//    public View mapView;
    public NestedScrollView scroll;
    public MapFragment mMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    final String GOOGLE_KEY = "AIzaSyC1Kfq1p84heXOomAq7PD9VbKkEjWC4MIs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Log.d("QiWu", GOOGLE_KEY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        infoDate = new String[2];
        infoPool = new String[2];

        View includedView = findViewById(R.id.included_layout);
        nextDate = (TextView) includedView.findViewById(R.id.tv_next_date);
        prizePool = (TextView) includedView.findViewById(R.id.tv_prize_pool);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        regionSpinner = (Spinner) headerView.findViewById(R.id.region_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(adapter);
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences restoreSharedPref = getSharedPreferences("Settings", 0);
                SharedPreferences.Editor prefEditor = restoreSharedPref.edit();
                prefEditor.putInt("lastPickRegion", (int) id);
                prefEditor.apply();
//                Log.d("QiWu", id + "was picked");

                if (id == 0) {
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.activity_main_drawer_cn_ml);

                } else if (id == 1) {
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.activity_main_drawer_ca_on);
                    infoDate[1] = "p[class*=marginClearBottom]";
                    infoPool[1] = "p[class*=marginClearAll]";
                }
                setLottery(navigationView);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Snackbar.make(navigationView, "Need your location!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
            return;
        } else {
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            } else {
                Toast.makeText(this, "no location provider", Toast.LENGTH_SHORT).show();
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        SharedPreferences sharedPref = getSharedPreferences("Settings", MODE_PRIVATE);
        lastPickRegion = sharedPref.getInt("lastPickRegion", -1);

        if (lastPickRegion != -1) {
            regionSpinner.setSelection(lastPickRegion);
//            Log.d("QiWu", "start"+lastPickRegion+ "was picked");
        } else {
            regionSpinner.setSelection(0);
//            Log.d("QiWu", "no region picked");
        }

        GoogleMapOptions options = new GoogleMapOptions();
//        options.liteMode(true);
        mMapFragment = MapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_container, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);

        scroll = (NestedScrollView) findViewById(R.id.scroll);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scroll.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scroll.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scroll.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(navigationView, "Got your location!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                } else {
                    Snackbar.make(navigationView, "Can't get your location!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setLottery(NavigationView navigationView) {
        SharedPreferences sharedPref = getSharedPreferences("Settings", MODE_PRIVATE);
        lastPickLottery = navigationView.getMenu().findItem(sharedPref.getInt("lastPickLotteryID", -1));

        if (lastPickLottery != null) {
            onNavigationItemSelected(lastPickLottery);
//            Log.d("QiWu", "lastPickLottery not null");
        } else {
//            Log.d("QiWu", "didn't find");
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;
        FragmentTransaction ft;
        int id = item.getItemId();
        SharedPreferences sharedPref = getSharedPreferences("Settings", 0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt("lastPickLotteryID", id);
        prefEditor.apply();
//        Log.d("QiWu", "Lottery "+id+"picked");

        if (id == R.id.nav_dcb) {
            fragment = new dcb_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            collapsingToolbarLayout.setTitle("Double Color Balls");
            infoDate[0] = "http://caipiao.163.com/order/ssq/";
            infoDate[1] = "span:contains(投注截止时间)";
            infoPool[0] = infoDate[0];
            infoPool[1] = "p[class*=totalPool]";
            new WebInfoDate().execute(infoDate);
            new WebInfoPool().execute(infoPool);

        } else if (id == R.id.nav_bl) {
            fragment = new sbl_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            collapsingToolbarLayout.setTitle("Big Lottery");
            infoDate[0] = "http://caipiao.163.com/order/dlt/";
            infoDate[1] = "span:contains(代购截止)";
            infoPool[0] = infoDate[0];
            infoPool[1] = "p[class*=totalPool]";
            new WebInfoDate().execute(infoDate);
            new WebInfoPool().execute(infoPool);

        } else if (id == R.id.nav_ss) {
            fragment = new ss_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            collapsingToolbarLayout.setTitle("Seven Stars");
            infoDate[0] = "http://caipiao.163.com/order/dlt/";
            infoDate[1] = "span:contains(代购截止)";
            infoPool[0] = infoDate[0];
            infoPool[1] = "p[class*=totalPool]";
            new WebInfoDate().execute(infoDate);
            new WebInfoPool().execute(infoPool);

        } else if (id == R.id.nav_sh) {
            fragment = new sh_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            collapsingToolbarLayout.setTitle("Seven Happiness");
            infoDate[0] = "http://caipiao.163.com/order/dlt/";
            infoDate[1] = "span:contains(代购截止)";
            infoPool[0] = infoDate[0];
            infoPool[1] = "p[class*=totalPool]";
            new WebInfoDate().execute(infoDate);
            new WebInfoPool().execute(infoPool);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_lotto_max) {
            fragment = new lm_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            collapsingToolbarLayout.setTitle("Lotto Max");
            infoDate[0] = "http://www.olg.ca/lotteries/games/howtoplay.do?game=lottomax";
            infoPool[0] = infoDate[0];
            new WebInfoDate().execute(infoDate);
            new WebInfoPool().execute(infoPool);

        } else if (id == R.id.nav_lotto_649) {
            fragment = new l6_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            collapsingToolbarLayout.setTitle("Lotto 6/49");
            infoDate[0] = "http://www.olg.ca/lotteries/games/howtoplay.do?game=lotto649";
            infoPool[0] = infoDate[0];
            new WebInfoDate().execute(infoDate);
            new WebInfoPool().execute(infoPool);

        } else if (id == R.id.nav_ontario_49) {
            fragment = new o4_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            collapsingToolbarLayout.setTitle("Ontario 49");
            infoDate[0] = "http://www.olg.ca/lotteries/games/howtoplay.do?game=ontario49";
            infoPool[0] = infoDate[0];
            new WebInfoDate().execute(infoDate);
            new WebInfoPool().execute(infoPool);

        } else if (id == R.id.nav_lottario) {
            fragment = new lo_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            collapsingToolbarLayout.setTitle("Lottario");
            infoDate[0] = "http://www.olg.ca/lotteries/games/howtoplay.do?game=lottario";
            infoPool[0] = infoDate[0];
            new WebInfoDate().execute(infoDate);
            new WebInfoPool().execute(infoPool);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String generateNum(int lower, int upper, int number, boolean unique) {
        String selectedNums = "";
        Random rand = new Random();
        int[] num = new int[number];
        int temp;
        int count = 0;
        NumberFormat formatter = new DecimalFormat("00");
        if (unique) {
            Boolean notExist = true;
            while (count < num.length) {
                temp = rand.nextInt(upper - lower + 1) + lower;
                for (int j = 0; j < count; j++) {
                    if (num[j] == temp) {
                        notExist = false;
                        break;
                    }
                    notExist = true;
                }
                if (notExist) {
                    num[count] = temp;
                    count++;
                }
            }
            Arrays.sort(num);
            for (int aNum : num) {
                selectedNums = selectedNums + formatter.format(aNum) + " ";
            }
        } else {
            for (int i = 0; i < number; i++) {
                num[i] = rand.nextInt(upper - lower + 1) + lower;
                selectedNums = selectedNums + formatter.format(num[i]) + " ";
            }
        }
        return selectedNums;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        home = new LatLng(location.getLatitude(), location.getLongitude());
        upDate = CameraUpdateFactory.newLatLngZoom(home, 16);
        mMap.animateCamera(upDate);
        addMarkers();
    }

    private void addMarkers(){
        mMap.addMarker(new MarkerOptions().position(home).title("You"));
        PlacesService mPlacesService = new PlacesService(GOOGLE_KEY);
        ArrayList<Place> retailers = new ArrayList<>();
        retailers = mPlacesService.findPlaces(home.latitude, home.longitude, "grocery_or_supermarket");
//        Log.d("QiWu", String.valueOf(retailers.size()));
//        for (int i = 0; i<retailers.size(); i++) {
//            mMap.addMarker(new MarkerOptions()
//                    .position(retailers.get(i).getLatLng())
//                    .title("Brisbane"));
//        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class WebInfoDate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Document doc = null;
            Element date = null;
            try {
                doc = Jsoup.connect(params[0]).get();
                date = doc.select(params[1]).first();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return date.text();
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("-"))
                nextDate.setText("The next jackpot is " + result.split(":")[1].split(" ")[1]);
            else
                nextDate.setText(result);
        }
    }

    private class WebInfoPool extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Document doc = null;
            Element pool = null;
            try {
                doc = Jsoup.connect(params[0]).get();
                pool = doc.select(params[1]).first();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pool.text();
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            prizePool.setText(result.split("e")[0] + " ");
        }
    }
}
