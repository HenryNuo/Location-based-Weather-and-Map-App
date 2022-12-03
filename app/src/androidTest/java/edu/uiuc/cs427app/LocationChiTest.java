package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocationChiTest {

    public static final String CHICAGO = "Chicago";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes. This is a replacement for @link androidx.test.rule.ActivityTestRule}.
     */
    @Rule public ActivityScenarioRule<LocationActivity> activityScenarioChiRule
            = new ActivityScenarioRule<>(LocationActivity.class);

    @Test
    public void testLocationIsChicago() {
        // Type city name text
        onView(withId(R.id.edit_city))
                .perform(typeText(CHICAGO));

        onView(withId(R.id.buttonAddLocation))
                .perform(click());

        SystemClock.sleep(1500);

        final boolean[] cityCreated = {false};
        FirebaseDatabase.getInstance().getReference().child("users").child("caleb").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    ArrayList<String> cities = (ArrayList<String>) dataSnapshot.child("cities").getValue();
                    // System.out.println("cities "+ cities);
                    if (cities.contains(CHICAGO)) {
                        cityCreated[0] = true;
                    }
                }
            }

            /**
             * Called when having trouble connecting to database
             * @param databaseError
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        SystemClock.sleep(500);
        assert(cityCreated[0]);

    }

//    private Intent getChiIntent() {
//        Intent i = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), LocationActivity.class);
//        i.putExtra("username", "caleb");
//        i.putExtra("cities", CHICAGO);
//        return i;
//    }
}
