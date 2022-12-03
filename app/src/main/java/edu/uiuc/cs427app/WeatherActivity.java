package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Activity for the weather information for each city
 */
public class WeatherActivity extends AppCompatActivity {
    private String cityName;
    private String username;

    /**
     * Sets the app theme based on the User's preferences
     * @param prefs - pointer to User's profile preferences
     */
    private void setUsersTheme(SharedPreferences prefs) {
        int color = prefs.getInt("color",1);
        if (color == 1) {
            setTheme(R.style.Theme_Red);
        } else {
            setTheme(R.style.Theme_Blue);
        }
    }

    /**
     * Runs when WeatherActivity is rendered
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        setUsersTheme(prefs);
        setContentView(R.layout.activity_weather);

        username = prefs.getString("name","");
        setTitle("Team 25 " + username);

        Intent currentIntent = getIntent();
        cityName = currentIntent.getStringExtra("cityName");

        String cityWeatherInfo = "Detailed information about the weather of " + cityName;

        // Initializing the GUI elements
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        cityInfoMessage.setText(cityWeatherInfo);

        String apiURL = "https://api.weatherbit.io/v2.0/current?city=" + cityName + "&country=US&key=04d75a67876a481d8d4c94a0e9e06e44&include=minutely";
        new WeatherActivity.WeatherApi().execute(apiURL);
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
