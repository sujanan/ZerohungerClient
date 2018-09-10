package com.zerohunger.zerohungerclient.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zerohunger.zerohungerclient.R;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener {

    private static final int CAMERA_ZOOM_LEVEL = 20;
    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final String TAG = "MainActivity";

    private TextView textNavHeaderPhoneNumber;
    private TextView textNavHeaderName;
    private Button buttonLocation;
    private FloatingActionButton fabCurrentLocation;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FusedLocationProviderClient mFusedLocationClient;
    private BroadcastReceiver mBroadcastReceiver;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private boolean mFirstTimeZoomDone;
    private boolean mUserInteracting;
    private InventoryStateUpdater mInventoryStateUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createBaseUI();

        initFirebase();

        initLocationClient();

        initLocationRequest();

        initLocationCallbacks();

        if (!isLocationEnabled()) {
            showLocationEnableButton();
        }

        registerMapOnMapReadyCallbacks();

        registerFabCurrentLocationClickListener();

        registerFabCurrentLocationLongClickListener();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, PhoneNumberActivity.class);
            startActivityForResult(intent, 1);
        } else {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavHeaderWithPreference();
        grantLocationPermission();
        addGpsBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeGpsBroadcastReceiver();
    }

    private void createBaseUI() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerInNavigationView = navigationView.getHeaderView(0);
        textNavHeaderPhoneNumber =
                headerInNavigationView.findViewById(R.id.textMainNavHeaderPhoneNumber);
        textNavHeaderName =
                headerInNavigationView.findViewById(R.id.textMainNavHeaderName);
        buttonLocation =
                findViewById(R.id.buttonMainLocation);
        hideLocationEnableButton();
        fabCurrentLocation = findViewById(R.id.fabMainCurrentLocation);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    private void initLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void registerMapOnMapReadyCallbacks() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initLocationCallbacks() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, location.getLongitude() + ":" + location.getLatitude());
                    mCurrentLocation = location;
                    if (!mFirstTimeZoomDone) {
                        moveCameraToDefaultZoomLevel(location);
                        mFirstTimeZoomDone = true;
                        changeFabToBlue();
                    } else {
                        if (!mUserInteracting) {
                            moveCamera(location);
                            changeFabToBlue();
                        }
                    }
                }
            }
        };
    }

    private void changeFabToBlue() {
        fabCurrentLocation.setImageResource(R.drawable.ic_my_location_blue_24dp);
    }

    private void changeFabToBlack() {
        fabCurrentLocation.setImageResource(R.drawable.ic_my_location_black_24dp);
    }

    private void registerFabCurrentLocationClickListener() {
        fabCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLocation != null) {
                    moveCamera(mCurrentLocation);
                    mUserInteracting = false;
                }
            }
        });
    }

    private void registerFabCurrentLocationLongClickListener() {
        fabCurrentLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                moveCameraToDefaultZoomLevel(mCurrentLocation);
                return true;
            }
        });
    }

    /* ========================================================================================= */
    /* =================================== LOCATION SETTINGS =================================== */
    /* ========================================================================================= */

    private boolean isLocationEnabled() {
        LocationManager locationManager;

        locationManager = (LocationManager)
                getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {}

        return false;
    }

    private void showLocationEnableButton() {
        buttonLocation.setVisibility(View.VISIBLE);
    }

    private void hideLocationEnableButton() {
        buttonLocation.setVisibility(View.GONE);
    }

    private void requestLocationSettings() {
        LocationSettingsRequest.Builder builder;
        Task<LocationSettingsResponse> task;
        SettingsClient client;

        builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        client = LocationServices.getSettingsClient(this);
        task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {}
                }
            }
        });
    }

    public void enableLocation(View view) {
        if (isLocationPermissionGranted()) {
            requestLocationSettings();
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Location permissions are not granted";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);

            toast.show();
        }
    }

    private void addGpsBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                    if (isLocationEnabled()) {
                        hideLocationEnableButton();
                        addCurrentLocationMarker();
                    } else {
                        showLocationEnableButton();
                        changeFabToBlack();
                    }
                }
            }
        };
        registerReceiver(mBroadcastReceiver,
                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    private void removeGpsBroadcastReceiver() {
        mBroadcastReceiver = null;
    }

    /* =================================================================================== */
    /* =================================== PERMISSIONS =================================== */
    /* =================================================================================== */

    private boolean isLocationPermissionGranted() {
        int i1 = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int i2 = PackageManager.PERMISSION_GRANTED;
        return i1 == i2;
    }

    private void grantLocationPermission() {
        if (isLocationPermissionGranted()) {
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            boolean b = grantResults.length > 0;
            b = b && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (b && mMap != null) {
                setLastKnownLocation();
                addCurrentLocationMarker();
            }
        }
    }
    /* ================================================================================== */
    /* ==================================== LOCATION ==================================== */
    /* ================================================================================== */
    @SuppressLint("MissingPermission")
    private void setLastKnownLocation () {
        if (!isLocationPermissionGranted()) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            moveCameraToDefaultZoomLevel(location);
                        } else {
                            Log.d(TAG, "location null");
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (!isLocationEnabled()) {
            return;
        }
        if (!isLocationPermissionGranted()) {
            return;
        }
        mFirstTimeZoomDone = false;
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /* ============================================================================== */
    /* ==================================== MAPS ==================================== */
    /* ============================================================================== */

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnCameraMoveStartedListener(this);
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(CAMERA_ZOOM_LEVEL);
        mInventoryStateUpdater = new InventoryStateUpdater(mMap);
        mInventoryStateUpdater.startReading();
        addCurrentLocationMarker();
        setLastKnownLocation();
    }

    @SuppressLint("MissingPermission")
    private void addCurrentLocationMarker() {
        if (isLocationPermissionGranted() && isLocationEnabled() && mMap != null) {
           mMap.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    private void removeCurrentLocationMarker() {
        if (!isLocationEnabled() && mMap != null) {
            mMap.setMyLocationEnabled(false);
        }
    }

    private void moveCamera(Location location) {
        if (mMap == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void moveCameraToDefaultZoomLevel(Location location) {
        if (mMap == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL));
    }

    @Override
    public void onCameraIdle() {
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            mUserInteracting = true;
            changeFabToBlack();
        }
    }

    private void updateNavHeaderWithPreference() {
        SharedPreferences userPref = MainActivity.this.getSharedPreferences(
                getString(R.string.preference_user_file_key),
                Context.MODE_PRIVATE);
        String phoneNumber = userPref.getString(
                getString(R.string.saved_profile_phone_number),
                null);
        String name = userPref.getString(
                getString(R.string.saved_profile_name),
                null);
        if (phoneNumber != null) {
            textNavHeaderPhoneNumber.setText(phoneNumber);
        }
        if (name != null) {
            textNavHeaderName.setText(name);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                }
        }
    }
}
