package edu.uiuc.cs427app;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uiuc.cs427app.databinding.ActivityMapsBinding;

/**
 * Activity for the map view of a city
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    // track the city passed in from the details page
    private String currentCity = "";

    // Key: city name
    // Value: pair of location defined by (latitude, longitude)
    private HashMap<String, Pair<Double, Double>> cities = new HashMap<>();

    /**
     * Add the following cities to the cities hashmap:
     *      Champaign
     *      Chicago
     *      New York
     *      Los Angeles
     *      San Fransisco
     */
    private void createLocationOfCities()
    {
        cities.put("Champaign", new Pair<>(40.11, -88.24));
        cities.put("Chicago", new Pair<>(41.88, -87.63));
        cities.put("New York", new Pair<>(40.71, -74.0));
        cities.put("Los Angeles", new Pair<>(34.05, -118.24));
        cities.put("San Fransisco", new Pair<>(37.77, -122.42));
    }

    /**
     * Sets the app theme based on the User's preferences
     * @param prefs - pointer to User's profile preferences
     */
    private void setUsersTheme(SharedPreferences prefs) {
        int color = prefs.getInt("color",1);
        if (color == 1) {
            setTheme(R.style.Theme_Red);
        } else {
            System.out.println("hi");
            setTheme(R.style.Theme_Blue);
        }
    }

    /**
     * Runs when MapsActivity is rendered
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // create dictionary of cities and location
        createLocationOfCities();

        // get the passed in city name from the previous page
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentCity = extras.getString("cityName");
        }

        // get user's preferences
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setUsersTheme(prefs);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * @brief Manipulates the map, of a city, once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we add markers and zoom the map's camera.
     *
     * @warning If Google Play services is not installed on the device, the user will be prompted
     * to install it inside the SupportMapFragment. This method will only be triggered once
     * the user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // get the city's location (lat, long)
        Pair<Double, Double> location = cities.get(currentCity);
        double latitude = location.first;
        double longitude = location.second;

        // Add a marker that display's the city name and location
        LatLng city = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(city).title(currentCity +
                " (" + latitude + ", " + longitude + ")")).showInfoWindow();

        // set camera to lat. and long. with respect to zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 10.0f));
    }
}