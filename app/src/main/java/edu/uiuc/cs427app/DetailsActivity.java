package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Activity for the detailed view for each city
 */
public class DetailsActivity extends AppCompatActivity {
    String username;
    ArrayList<String> cities;
    String cityName;

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
     * Runs when DetailsActivity is rendered
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setUsersTheme(prefs);
        setContentView(R.layout.activity_details);

        username = prefs.getString("name","");

        // Process the Intent payload that has opened this Activity and show the information accordingly
        Intent currentIntent = getIntent();
        cities = currentIntent.getStringArrayListExtra("cities");
        cityName = currentIntent.getStringExtra("city");

        String welcome = "Welcome to the " + cityName;
        String cityWeatherInfo = "Detailed information about the weather of " + cityName;

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);

        // TODO: Get the weather information from a Service that connects to a weather server and show the results

        // Button functionality
        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(v -> {});

        Button buttonRemove = findViewById(R.id.removeButton);
        buttonRemove.setOnClickListener(v -> {
            cities.remove(cityName);

            FirebaseDatabase.getInstance().getReference().child("users").child(username).child("cities").setValue(cities);

            Intent removeLocationIntent = new Intent(DetailsActivity.this, MainActivity.class);
            removeLocationIntent.putExtra("cities", cities);
            startActivity(removeLocationIntent);
        });
    }
}