package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity for the detailed view for each city
 */
public class DetailsActivity extends AppCompatActivity {
    String username;
    ArrayList<String> cities;
    String cityName;

    // only support map view for these cities
    ArrayList<String> defaultCities = new ArrayList<>(
            Arrays.asList("Champaign", "Chicago", "New York", "Los Angeles", "San Fransisco")
    );

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

        String welcome = "Welcome to " + cityName;
        String cityWeatherInfo = "Detailed information about the weather of " + cityName;

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);

        // TODO: Get the weather information from a Service that connects to a weather server and show the results
        String apiURL = "https://api.weatherbit.io/v2.0/current?city=" + cityName + "&country=US&key=04d75a67876a481d8d4c94a0e9e06e44&include=minutely";
        new WeatherApi().execute(apiURL);

        // move to MapsActivity: show the map of city
        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(v -> {
            // only show the map if it is already in the default maps list
            if (defaultCities.contains(cityName)) {
                Intent addLocationIntent = new Intent(DetailsActivity.this, MapsActivity.class);
                // pass in city name
                addLocationIntent.putExtra("cityName", cityName);
                startActivity(addLocationIntent);
            }
        });

        Button buttonRemove = findViewById(R.id.removeButton);
        buttonRemove.setOnClickListener(v -> {
            cities.remove(cityName);

            FirebaseDatabase.getInstance().getReference().child("users").child(username).child("cities").setValue(cities);

            Intent removeLocationIntent = new Intent(DetailsActivity.this, MainActivity.class);
            removeLocationIntent.putExtra("cities", cities);
            startActivity(removeLocationIntent);
        });
    }

    /**
     * Class that runs in async to call weather API
     */
    private class WeatherApi extends AsyncTask<String, Void, String> {
        /**
         * Handles API connection and gets response
         * @param params apiURL
         * @return String of all the JSON
         */
        @Override
        protected String doInBackground(String... params) {
            String apiURL = params[0];

            URL url = null;
            try {
                url = new URL(apiURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // establishes connection
            HttpURLConnection urlConnection = null;
            try {
                assert url != null;
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String response = "";
            try {
                // reads in response
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();

                response = sb.toString();
                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return response;
        }

        /**
         * Handles the response and displays the information to the user
         * @param result String of the JSON from the weather API
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // parsing result to JSON object
            JSONObject object = null;
            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // parsing the JSON object into data we want
            try {
                JSONObject data = object.getJSONArray("data").getJSONObject(0);
                String city_name = (String) data.get("city_name");
                String observation_time = (String) data.get("ob_time");
                String temperature = String.valueOf(data.get("temp")); // in Celsius

                JSONObject weather = data.getJSONObject("weather");
                String weather_description = (String) weather.get("description");

                int cloudIndex = (int) data.get("clouds");
                String relative_humidity = String.valueOf(data.get("rh")); // in %

                // direction wind is coming from
                String wind_direction = (String) data.get("wind_cdir_full");
                String wind_speed = String.valueOf(data.get("wind_spd")); // in m/s

                TextView cityWeatherInfo = findViewById(R.id.cityInfo);
                String text = (String) cityWeatherInfo.getText();
                text += "\nTime: " + observation_time + "\n";
                text += "Temperature: " + temperature + " C\n";
                text += "Weather: " + weather_description + "\n";
                text += "Humidity: " + relative_humidity + "%\n";
                text += "Cloud: " + getCloudyStatus(cloudIndex) + "\n";
                text += "Wind: " + wind_direction + " at " + wind_speed + " m/s";
                cityWeatherInfo.setText(text);
                cityWeatherInfo.setBackgroundResource(R.drawable.weather1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getCloudyStatus(int cloudIndex) {
            /**
             * 0-10%  Sunny/Clear
             * 10-20% Fair
             * 20-30% Mostly sunny
             * 30-60% Partly cloudy
             * 60-70% Partly sunny
             * 70-90% Mostly cloudy
             * 90-100% Overcast
             *
             */
            if (cloudIndex >= 0 && cloudIndex < 10 ) return "Sunny/Clear";
            else if (cloudIndex >= 10 && cloudIndex < 20 ) return "Fair";
            else if (cloudIndex >= 20 && cloudIndex < 30 ) return "Mostly sunny";
            else if (cloudIndex >= 30 && cloudIndex < 60 ) return "Partly cloudy";
            else if (cloudIndex >= 60 && cloudIndex < 70 ) return "Partly sunny";
            else if (cloudIndex >= 70 && cloudIndex < 90 ) return "Mostly cloudy";
            else if (cloudIndex >= 90 && cloudIndex < 100 ) return "Overcast";
            else return "Unknown";
        }
    }
}