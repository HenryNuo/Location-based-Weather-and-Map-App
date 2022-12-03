package edu.uiuc.cs427app;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.startActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LogoutTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void logoutSuccessfully() {
            Activity activity = getActivity(activityScenarioRule);
            ((MainActivity) activity).logout();
            SharedPreferences prefs = activity.getSharedPreferences("Login", MODE_PRIVATE);
            boolean isLogin = prefs.getBoolean("isLogin",false);
            Assert.assertFalse(isLogin);
        }

        private <T extends Activity> T getActivity(ActivityScenarioRule<T> activityScenarioRule) {
            AtomicReference<T> activityRef = new AtomicReference<>();
            activityScenarioRule.getScenario().onActivity(activityRef::set);
            return activityRef.get();
        }
}
