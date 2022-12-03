package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Activity for adding a city to the city list
 */
public class LocationActivity extends AppCompatActivity {
    String username;
    ArrayList<String> cities;

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
     * Runs when LocationActivity is rendered
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setUsersTheme(prefs);
        setContentView(R.layout.activity_location);

        username = prefs.getString("name","");

        // Process the Intent payload that has opened this Activity and show the information accordingly
        Intent currentIntent = getIntent();
        cities = currentIntent.getStringArrayListExtra("cities") == null ? new ArrayList<>(): currentIntent.getStringArrayListExtra("cities");

        // Initializing the GUI elements
        String welcome = "Please add a city name in the input filed.";
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        welcomeMessage.setText(welcome);

        // Button functionality
        Button buttonMap = findViewById(R.id.buttonAddLocation);

        buttonMap.setOnClickListener(v -> {
            TextInputEditText textInput = findViewById(R.id.edit_city);
            String text = textInput == null ? "" : textInput.getText().toString();

            cities.add(text);

            FirebaseDatabase.getInstance().getReference().child("users").child(username).child("cities").setValue(cities);

            Intent addLocationIntent = new Intent(LocationActivity.this, MainActivity.class);
            addLocationIntent.putExtra("cities", cities);
            startActivity(addLocationIntent);
        });
    }
}