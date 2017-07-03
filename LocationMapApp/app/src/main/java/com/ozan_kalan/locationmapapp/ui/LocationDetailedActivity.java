package com.ozan_kalan.locationmapapp.ui;

import android.content.Context;
import android.content.Intent;
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
 * <p>
 * This Activity displays the all the detail about
 * the location selected from our api.
 * <p>
 * The relevant data is provided to this activity through the intent
 */

public class LocationDetailedActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * Keys for the intent extras
     */
    private static final String LOCATION_ADDRESS = "location_address";
    private static final String LOCATION_NAME = "location_name";
    private static final String LOCATION_LAT = "location_lat";
    private static final String LOCATION_LONG = "location_long";
    private static final String LOCATION_ID = "location_id";
    private static final String LOCATION_ETA = "location_eta";

    @BindView(R.id.eta_txt)
    TextView mEta;
    @BindView(R.id.location_name_txt)
    TextView mName;
    @BindView(R.id.location_address_txt)
    TextView mAddress;
    @BindView(R.id.latitdue_txt)
    TextView mLatitude;
    @BindView(R.id.longitude_txt)
    TextView mLongitude;

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

    /**
     * Setting up the views and the map fragment.
     * The data for the text views are obtained
     * from the bundle using the static keys.
     */
    private void setupViews() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        mEta.setText(bundle.getString(LOCATION_ETA));
        mName.setText(bundle.getString(LOCATION_NAME));
        mAddress.setText(bundle.getString(LOCATION_ADDRESS));
        mLatitude.setText(String.valueOf(bundle.getDouble(LOCATION_LAT)));
        mLongitude.setText(String.valueOf(bundle.getDouble(LOCATION_LONG)));
    }

    /**
     * This static function purpose is to make an intent extras for this class
     * an added benefit of this is that if params are added/removed,
     * we will know at compile time rather than run-time :)
     *
     * @param context
     * @param locationName
     * @param locationAddress
     * @param lat
     * @param longititdue
     * @param eta
     * @param locationId
     * @return intent
     */
    public static Intent setIntent(Context context, String locationName, String locationAddress, Double lat,
                                   Double longititdue, String eta, int locationId) {

        Bundle bundle = new Bundle();

        bundle.putString(LOCATION_ADDRESS, locationAddress);
        bundle.putString(LOCATION_NAME, locationName);
        bundle.putString(LOCATION_ETA, eta);
        bundle.putDouble(LOCATION_LAT, lat);
        bundle.putDouble(LOCATION_LONG, longititdue);
        bundle.putInt(LOCATION_ID, locationId);

        Intent intent = new Intent(context, LocationDetailedActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    /**
     * Override the Map method to set up the marker
     * for the location. Then we move the camera over
     * the marker.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(bundle.getDouble(LOCATION_LAT), bundle.getDouble(LOCATION_LONG));
        googleMap.addMarker(new MarkerOptions().position(location)
                .title(mName.getText().toString()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
    }
}
