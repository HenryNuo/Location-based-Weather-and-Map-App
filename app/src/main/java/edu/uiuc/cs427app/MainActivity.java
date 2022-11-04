package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Activity that displays the user's city list
 */
public class MainActivity extends AppCompatActivity {
    String username;
    ArrayList<String> cities;

    /**
     * Sets the app theme based on the User's preferences
     * @param prefs - pointer to User's profile preferences
     */
    public void setUsersTheme(SharedPreferences prefs) {
        int color = prefs.getInt("color",1);

        if (color == 1) {
            setTheme(R.style.Theme_Red);

        } else {
            setTheme(R.style.Theme_Blue);
        }
    }

    /**
     * Runs when MainActivity is rendered
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setUsersTheme(prefs);
        setContentView(R.layout.activity_main);

        username = prefs.getString("name","");
        setTitle("Team 25 " + username);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        Intent currentIntent = getIntent();
        cities = currentIntent.getStringArrayListExtra("cities");

        // runs when user opens app when still logged in
        if (cities == null) {

            // gets city data for user
            FirebaseDatabase.getInstance().getReference().child("users").child(username).child("cities").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    System.out.println("error");

                } else {
                    cities = new ArrayList<>();
                    cities.addAll((ArrayList<String>) task.getResult().getValue());

                    renderList();
                }
            });

        } else { // runs when the user registers or logs in
            renderList();
        }
    }

    /**
     * Creates UI components dynamically based on user's city list
     */
    public void renderList() {
        LinearLayout llMain = findViewById(R.id.mainLayout);

        for (String city : cities) {
            LinearLayout parentLayout = new LinearLayout(MainActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 200);
            parentLayout.setLayoutParams(params);
            parentLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView cityName = new TextView(MainActivity.this);
            cityName.setWidth(400);
            cityName.setText(city);

            // Button functionality for DetailsActivity to see weather and map data for city
            Button detailsButton = new Button(MainActivity.this);
            detailsButton.setText("Show Details");
            detailsButton.setOnClickListener(v -> {
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("cities", cities);
                detailsIntent.putExtra("city", city);
                startActivity(detailsIntent);
            });

            parentLayout.addView(cityName);
            parentLayout.addView(detailsButton);

            llMain.addView(parentLayout);
        }

        // Button functionality for LocationActivity to add cities to list
        Button buttonAddLocation = findViewById(R.id.buttonAddLocation);

        buttonAddLocation.setOnClickListener(v -> {
            // Implement this action to add a new location to the list of locations
            Intent addLocationIntent = new Intent(this, LocationActivity.class);
            addLocationIntent.putExtra("cities", cities);
            startActivity(addLocationIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logout) {
            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor.putString("name", "");
            editor.putString("password", "");
            editor.putBoolean("isLogin", false);
            editor.commit();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}