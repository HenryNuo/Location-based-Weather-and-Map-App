package edu.uiuc.cs427app;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.hasWindowLayoutParams;
import static androidx.test.espresso.matcher.RootMatchers.isSystemAlertWindow;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.icu.text.CaseMap;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    public static final String USERNAME = "nuox3";
    public static final String PASSWORD = "123456";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void emptyUsername()  {
        // Type password text
        onView(withId(R.id.edit_text_pass)).perform(typeText(PASSWORD), closeSoftKeyboard());
        // Click Login
        onView(withId(R.id.letTheUserLogIn)).perform(click());
        onView(withId(R.id.edit_text_username)).check(matches(hasErrorText("Enter Username")));
    }

    @Test
    public void emptyPassword()  {
        // Type username text
        onView(withId(R.id.edit_text_username)).perform(typeText(USERNAME), closeSoftKeyboard());
        // Click Login
        onView(withId(R.id.letTheUserLogIn)).perform(click());
        onView(withId(R.id.edit_text_pass)).check(matches(hasErrorText("Enter Password")));
    }

    @Test
    public void passwordInvalid() {
        String invalidPwd = "invalidpassword";
        // Type username text
        onView(withId(R.id.edit_text_username)).perform(typeText(USERNAME), closeSoftKeyboard());
        // Type invalid password text
        onView(withId(R.id.edit_text_pass)).perform(typeText("invalidpassword"), closeSoftKeyboard());
        onView(withId(R.id.letTheUserLogIn)).perform(click());

        FirebaseDatabase.getInstance().getReference().child("users").child(USERNAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String password1 = dataSnapshot.child("password").getValue(String.class);
                    Assert.assertNotEquals(password1, invalidPwd);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Test
    public void usernameInvalid() {
        String invalidName = "testuser";
        // Type invalid username text
        onView(withId(R.id.edit_text_username)).perform(typeText("testuser"), closeSoftKeyboard());
        // Type password text
        onView(withId(R.id.edit_text_pass)).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.letTheUserLogIn)).perform(click());

        FirebaseDatabase.getInstance().getReference().child("users").child(invalidName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Assert.assertFalse(dataSnapshot.hasChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Test
    public void loginSuccessfully() {
        // Type username text
        onView(withId(R.id.edit_text_username)).perform(typeText(USERNAME), closeSoftKeyboard());
        // Type password text
        onView(withId(R.id.edit_text_pass)).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.letTheUserLogIn)).perform(click());

        FirebaseDatabase.getInstance().getReference().child("users").child(USERNAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Assert.assertTrue(dataSnapshot.hasChildren());
                String password1 = dataSnapshot.child("password").getValue(String.class);
                Assert.assertEquals(password1, PASSWORD);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private <T extends Activity> T getActivity(ActivityScenarioRule<T> activityScenarioRule) {
        AtomicReference<T> activityRef = new AtomicReference<>();
        activityScenarioRule.getScenario().onActivity(activityRef::set);
        return activityRef.get();
    }
}