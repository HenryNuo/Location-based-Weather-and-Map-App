package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.SystemClock;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.startsWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WeatherSFFeatureTest {
    @Rule
    public ActivityScenarioRule<WeatherActivity> activityScenarioSFRule
            = new ActivityScenarioRule<>(getSFIntent());

    @Test
    public void testCorrectCitySF() {
        String expectedOutput = "Detailed information about the weather of San Francisco";
        onView(withId(R.id.cityInfo)).check(matches(withText(startsWith(expectedOutput))));

        SystemClock.sleep(1500);
    }

    @Test
    public void testContainsDataSF() {
        String timeText = "Time:";
        String tempText = "Temperature:";
        String weatherText = "Weather:";
        String humidityText = "Humidity:";
        String cloudText = "Cloud:";
        String windText = "Wind:";

        onView(withId(R.id.cityInfo)).check(matches(withText(containsString(timeText))));
        onView(withId(R.id.cityInfo)).check(matches(withText(containsString(tempText))));
        onView(withId(R.id.cityInfo)).check(matches(withText(containsString(weatherText))));
        onView(withId(R.id.cityInfo)).check(matches(withText(containsString(humidityText))));
        onView(withId(R.id.cityInfo)).check(matches(withText(containsString(cloudText))));
        onView(withId(R.id.cityInfo)).check(matches(withText(containsString(windText))));

        SystemClock.sleep(1500);
    }

    private Intent getSFIntent() {
        Intent i = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), WeatherActivity.class);
        i.putExtra("cityName", "San Francisco");
        return i;
    }
}
