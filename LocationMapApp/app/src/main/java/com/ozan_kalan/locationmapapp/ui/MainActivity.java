package com.ozan_kalan.locationmapapp.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements LocationAdapter.LocationAdapterOnClickHandler {

    public static final String LIST = "list";

    private LocationAdapter mLocationAdapter;
    private Gson mGson;
    private String mBaseUrl;
    private List<LocationModel> mLocations;

    @BindView(R.id.location_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.network_error_txt_view)
    TextView mNetworkError;
    @BindView(R.id.no_locations_txt_view)
    TextView mNoLocations;

    OkHttpClient mClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGson = new GsonBuilder().create();
        mBaseUrl = getString(R.string.base_url);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLocationAdapter = new LocationAdapter(this);
        mRecyclerView.setAdapter(mLocationAdapter);

        if (savedInstanceState != null) {
            ArrayList<LocationModel> mSavedList = savedInstanceState.getParcelableArrayList(LIST);
            mLocationAdapter.setData(mSavedList);
        } else
            queryLocationAPI(getResources().getString(R.string.locations_endpoint));

    }

    /**
     * This method checks to see if the device is online,
     * if so it will call our apiCall method
     * <p>
     * if the device is offline then the app will show its not
     * connected to inform the user.
     */
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

    /**
     * This method will build our query and request
     * to call the Api to retrieve the data.
     * it will also handle a request failure
     */
    private void apiCall(final String query) {
        final Request request = new Request.Builder()
                .url(mBaseUrl + query)
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

        mLocations = new ArrayList<>(Arrays.asList(mGson.fromJson(json, LocationModel[].class)));
        sortLocations(mLocations, 0);
        mLocationAdapter.setData(mLocations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_name) {
            sortLocations(mLocationAdapter.getData(), 0);
            return true;
        }
        if (item.getItemId() == R.id.sort_distance) {

            sortLocations(mLocationAdapter.getData(), 1);
            return true;
        }

        if (item.getItemId() == R.id.sort_eta) {
            sortLocations(mLocationAdapter.getData(), 2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used to sort our List of locations.
     * depending on the int passed in,it will sort by name, location, eta, and defaults to name
     */
    private void sortLocations(List<LocationModel> locationsList, final int caseNum) {


        Collections.sort(locationsList, new Comparator<LocationModel>() {
            @Override
            public int compare(LocationModel o1, LocationModel o2) {

                switch (caseNum) {
                    case 0:
                        return o1.getName().compareToIgnoreCase(o2.getName());

                    case 1:
                        return o1.getAddress().compareToIgnoreCase(o2.getAddress());

                    case 2:
                        return o1.getArrivalTime().compareToIgnoreCase(o2.getArrivalTime());

                    default:
                        return o1.getName().compareToIgnoreCase(o2.getName());
                }
            }
        });
        mLocationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle output) {
        super.onSaveInstanceState(output);
        output.putParcelableArrayList(LIST, new ArrayList<Parcelable>(mLocationAdapter.getData()));
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
     * this method will tell notify the user
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

        Intent intent = LocationDetailedActivity.setIntent(getApplicationContext(), locationModel.getName(),
                locationModel.getAddress(), locationModel.getLatitude(),
                locationModel.getLongitude(), locationModel.getArrivalTime(), locationModel.getID());

        startActivity(intent);

    }
}
