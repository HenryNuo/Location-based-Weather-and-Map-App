package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AaSetupTest {
    public static final String USERNAME = "testUser_Cities";
    public static final String PASSWORD = "1234";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginSetupTest() {
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
}
