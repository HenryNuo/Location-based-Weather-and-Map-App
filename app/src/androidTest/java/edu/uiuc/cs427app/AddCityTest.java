package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.SystemClock;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddCityTest {

    int one_second = 1000; // 1000 ms = 1 sec

    String TEST_USERNAME = "testUser_Cities";

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addOneCity() {
        // add city
        String city_name = "Rome";
        addCity(city_name);

        // Check database that city_name was added
        ArrayList<String> userCities = getUserCities();

        // Check that the city was added successfully
        assertTrue(userCities.contains(city_name));
    }

    @Test
    public void addManyCities() {
        // add the cities
        String[] cities_to_add = new String[]
                {"London", "Paris", "Madrid", "Warsaw", "Honolulu"};
        for (String city : cities_to_add)
        {
            addCity(city);
        }

        // Check database that cities were added
        ArrayList<String> userCities = getUserCities();

        // Check that the cities were added successfully
        for (String city : cities_to_add)
        {
            assertTrue(userCities.contains(city));
        }
    }

    @Test
    public void addEmptyCity() {
        ArrayList<String> userCities = getUserCities();
        int before_userCitiesSize = userCities.size();

        // add city
        String city_name = "";
        addCity(city_name);

        // get the new user's database of cities
        userCities = getUserCities();
        int after_userCitiesSize = userCities.size();

        assertEquals(before_userCitiesSize, after_userCitiesSize);
    }

    @Test
    public void addDuplicateCity() {
        // city to add
        String city_name = "Havana";
        addCity(city_name);

        // get the current amount of cities in the database
        ArrayList<String> userCities = getUserCities();
        int before_userCitiesSize = userCities.size();

        // add the city again
        addCity(city_name);

        // get the new amount of cities in the database
        userCities = getUserCities();
        int after_userCitiesSize = userCities.size();

        assertEquals(before_userCitiesSize, after_userCitiesSize);
    }

    /**
     * @return the TEST_USERNAME cities stored in the database
     */
    private ArrayList<String> getUserCities()
    {
        ArrayList<String> userCities = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(TEST_USERNAME).child("cities")
                .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        System.err.println("error");
                        System.exit(1);
                    } else {
                        Object db_cities = task.getResult().getValue();
                        if (db_cities != null) {
                            userCities.addAll((ArrayList<String>) db_cities);
                        }
                    }
                });
        SystemClock.sleep(one_second);

        return userCities;
    }

    /**
     * Adds the city_name to the user database using the app
     * @param city_name - city to be added on the app screen
     */
    private void addCity(String city_name)
    {
        // MainActivity: Click "ADD A LOCATION" button
        SystemClock.sleep(one_second);
        onView(withId(R.id.buttonAddLocation))
                .perform(click());

        // LocationActivity: Enter city name
        SystemClock.sleep(one_second);
        onView(withId(R.id.edit_city))
                .perform(typeText(city_name), closeSoftKeyboard());

        // LocationActivity: Click "ADD" button
        SystemClock.sleep(one_second);
        onView(withId(R.id.buttonAddLocation))
                .perform(click());
    }
}
