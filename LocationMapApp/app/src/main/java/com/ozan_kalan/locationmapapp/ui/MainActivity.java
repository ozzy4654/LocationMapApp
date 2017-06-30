package com.ozan_kalan.locationmapapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozan_kalan.locationmapapp.R;
import com.ozan_kalan.locationmapapp.adapters.LocationAdapter;
import com.ozan_kalan.locationmapapp.models.LocationModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ozan_kalan.locationmapapp.ui.LocationDetailedActivity.LOCATION_ADDRESS;
import static com.ozan_kalan.locationmapapp.ui.LocationDetailedActivity.LOCATION_ETA;
import static com.ozan_kalan.locationmapapp.ui.LocationDetailedActivity.LOCATION_ID;
import static com.ozan_kalan.locationmapapp.ui.LocationDetailedActivity.LOCATION_LAT;
import static com.ozan_kalan.locationmapapp.ui.LocationDetailedActivity.LOCATION_LONG;
import static com.ozan_kalan.locationmapapp.ui.LocationDetailedActivity.LOCATION_NAME;

public class MainActivity extends AppCompatActivity implements LocationAdapter.LocationAdapterOnClickHandler {

    private LocationAdapter mLocationAdapter;
    private Gson mGson;
    private LinearLayoutManager mLinearLayoutManager;

    private SharedPreferences.Editor mEditor;

    private String BASE_URL;

    private List<LocationModel> mm;

    @BindView(R.id.location_list_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.network_error_txt_view) TextView mNetworkError;
    @BindView(R.id.no_locations_txt_view) TextView mNoLocations;

    OkHttpClient mClient = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGson = new GsonBuilder().create();
        BASE_URL = getString(R.string.base_url);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLocationAdapter = new LocationAdapter(this);
        mRecyclerView.setAdapter(mLocationAdapter);

        if (savedInstanceState != null) {
            ArrayList<LocationModel> mSavedList = savedInstanceState.getParcelableArrayList("LIST");
            mLocationAdapter.setData(mSavedList);
        }
        else
            queryLocationAPI(getResources().getString(R.string.locations_endpoint));

    }


    private void queryLocationAPI(String query) {
        try {
            if (isOnline())
                apiCall(query);
            else
                showError();
        } catch (Exception e) {
            e.printStackTrace();
            showError();
        }
    }

    private void apiCall(final String query) {
        final Request request  = new Request.Builder()
                .url(BASE_URL + query)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showError();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String json = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter(json);
                    }
                });
            }
        });
    }

    /**
     * Now that we have the data from API
     * we can set our adapter and show the posters
     * to the user
     */
    private void setAdapter(String json) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mNetworkError.setVisibility(View.INVISIBLE);

        mm = new ArrayList<>(Arrays.asList(mGson.fromJson(json, LocationModel[].class)));
        mLocationAdapter.setData(mm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_name) {
            mm = mLocationAdapter.getData();
            Collections.sort(mm, new Comparator<LocationModel>() {
                @Override
                public int compare(LocationModel o1, LocationModel o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            mLocationAdapter.notifyDataSetChanged();
            return true;
        }
        if (item.getItemId() == R.id.sort_distance) {
            mm = mLocationAdapter.getData();
            Collections.sort(mm, new Comparator<LocationModel>() {
                @Override
                public int compare(LocationModel o1, LocationModel o2) {
                    System.out.println("OBJ1  "+o1.getArrivalTime());
                    System.out.println("OBJ2  "+o2.getArrivalTime());

                    return o1.getArrivalTime().compareToIgnoreCase(o2.getArrivalTime());
                }
            });
            mLocationAdapter.notifyDataSetChanged();
            return true;
        }

        if (item.getItemId() == R.id.sort_eta) {
            mm = mLocationAdapter.getData();
            Collections.sort(mm, new Comparator<LocationModel>() {
                @Override
                public int compare(LocationModel o1, LocationModel o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            mLocationAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle output) {
        super.onSaveInstanceState(output);

        output.putParcelableArrayList("LIST",  new ArrayList<Parcelable>(mLocationAdapter.getData()));
    }

    /**
     * This method allow the app to check for network changes
     * so in the event of Network/wifi is down or in airplane mode
     * the app will not crash
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * In the event a network error occurred
     * this methond will tell notify the user
     */
    private void showError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoLocations.setVisibility(View.INVISIBLE);
        mNetworkError.setVisibility(View.VISIBLE);
        mNetworkError.setText(getString(R.string.network_error));
    }

    /**
     * Handles clicking a Location and sending
     * the user to the details activity
     */
    @Override
    public void onClick(LocationModel locationModel) {

        Intent intent = new Intent(getApplicationContext(), LocationDetailedActivity.class);

        Bundle bundle = new Bundle();

        bundle.putString(LOCATION_ADDRESS, locationModel.getAddress());
        bundle.putString(LOCATION_NAME, locationModel.getName());
        bundle.putString(LOCATION_ETA, locationModel.getArrivalTime());
        bundle.putDouble(LOCATION_LAT, locationModel.getLatitude());
        bundle.putDouble(LOCATION_LONG, locationModel.getLongitude());
        bundle.putInt(LOCATION_ID, locationModel.getID());

        intent.putExtras(bundle);

        startActivity(intent);

    }
}
