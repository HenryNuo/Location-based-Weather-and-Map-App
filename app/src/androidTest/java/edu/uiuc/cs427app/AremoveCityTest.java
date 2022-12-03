package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.SystemClock;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AremoveCityTest {

    final static String TEST_USERNAME = "testUser_Cities";
    final static int ONE_SECOND = 1000;

    ArrayList<String> cities = new ArrayList<>();
    String cityToRemove;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(CityIntent());

    @Test
    public void removeCity() {
        // DetailsActivity: Click "REMOVE CITY" button
        SystemClock.sleep(ONE_SECOND);
        onView(withId(R.id.removeButton))
                .perform(click());

        // Wait database to Update
        SystemClock.sleep(ONE_SECOND*3);

        // Check city is removed from database
        ArrayList<String> databaseCities = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(TEST_USERNAME).child("cities")
                .get().addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        System.err.println("error");
                        System.exit(1);
                    } else {
                        Object fromDatabase = task.getResult().getValue();
                        if (fromDatabase != null) { // if there is something in db
                            databaseCities.addAll((ArrayList<String>) task.getResult().getValue());
                        }
                    }
                });

        SystemClock.sleep(ONE_SECOND);

        // Check removed
        assertFalse(databaseCities.contains(cityToRemove));

        // Wait for user to check result
        SystemClock.sleep(ONE_SECOND*2);
    }

    /**
     * @return DetailsActivity Intent to perform deletion
     */
    private Intent CityIntent() {
        // Get all cities for test user
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(TEST_USERNAME).child("cities")
                .get().addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        System.err.println("error");
                    } else {
                        cities.addAll((ArrayList<String>) task.getResult().getValue());
                    }
                });

        // Wait database result to fill
        SystemClock.sleep(ONE_SECOND);

        if (cities.size() == 0) {
            System.err.println("cities cannot be empty! Add one city to test this function!");
            System.exit(1);
        }

        // Choose the last city to perform deletion
        cityToRemove = cities.get(cities.size()-1);

        Intent cityIntent = new Intent(InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext(),
                DetailsActivity.class);
        cityIntent.putExtra("cityName", cityToRemove);
        cityIntent.putStringArrayListExtra("cities", cities);

        return cityIntent;
    }
}
