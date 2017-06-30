package com.ozan_kalan.locationmapapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ozan_kalan.locationmapapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ozan.kalan on 6/28/17.
 */

public class LocationDetailedActivity extends AppCompatActivity implements OnMapReadyCallback{

    public static final String LOCATION_ADDRESS = "location_address";
    public static final String LOCATION_NAME = "location_name";
    public static final String LOCATION_LAT = "location_lat";
    public static final String LOCATION_LONG = "location_long";
    public static final String LOCATION_ID = "location_id";
    public static final String LOCATION_ETA = "location_eta";


    @BindView(R.id.eta_txt) TextView mEta;
    @BindView(R.id.location_name_txt) TextView mName;
    @BindView(R.id.location_address_txt) TextView mAddress;
    @BindView(R.id.latitdue_txt) TextView mLatitdue;
    @BindView(R.id.longitude_txt) TextView mLongitude;


    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_location);
        bundle = getIntent().getExtras();
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(bundle.getString(LOCATION_NAME));
        setupViews();

    }

    private void setupViews() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);


        mEta.setText(bundle.getString(LOCATION_ETA));
        mName.setText(bundle.getString(LOCATION_NAME));
        mAddress.setText(bundle.getString(LOCATION_ADDRESS));
        mLatitdue.setText( String.valueOf(bundle.getDouble(LOCATION_LAT)));
        mLongitude.setText(String.valueOf(bundle.getDouble(LOCATION_LONG)));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(bundle.getDouble(LOCATION_LAT), bundle.getDouble(LOCATION_LONG));
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title(mName.getText().toString()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
    }
}
