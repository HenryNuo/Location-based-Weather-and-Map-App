package edu.uiuc.cs427app;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;


import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Basic tests showcasing simple view matchers and actions like {@link ViewMatchers#withId},
 * {@link ViewActions#click} and {@link ViewActions#typeText}.
 * <p>
 * Note that there is no need to tell Espresso that a view is in a different {@link Activity}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterUserTest {

    public static final String USER_THAT_EXISTS = "alex";
    public static final String PASSWORD = "newaccountpass";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes. This is a replacement for @link androidx.test.rule.ActivityTestRule}.
     */
    @Rule public ActivityScenarioRule<RegisterActivity> activityScenarioRule
            = new ActivityScenarioRule<>(RegisterActivity.class);

    @Test
    public void registerUser_AlreadyExists() {
        // Type username text
        onView(withId(R.id.edit_text_username))
                .perform(typeText(USER_THAT_EXISTS), closeSoftKeyboard());
        // Type password text
        onView(withId(R.id.edit_text_pass))
                .perform(typeText(PASSWORD), closeSoftKeyboard());
        // Click blue color option
        onView(withId(R.id.blue))
                .perform(click());

        // Click signup
        onView(withId(R.id.letTheUserSignUp))
                .perform(click());

        // Check for username already exists popup
        onView(withText("Username already exists.")).inRoot(withDecorView(not(is(getActivity(activityScenarioRule).getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void registerUser_NoUsername()  {
        // Type username text
        onView(withId(R.id.edit_text_pass))
                .perform(typeText(PASSWORD), closeSoftKeyboard());

        // Click blue color option
        onView(withId(R.id.blue))
                .perform(click());

        // Click signup
        onView(withId(R.id.letTheUserSignUp))
                .perform(click());

        onView(withId(R.id.edit_text_username)).check(matches(hasErrorText("Enter Username")));
    }

    @Test
    public void registerUser_NoPassword()  {
        // Type username text
        onView(withId(R.id.edit_text_username))
                .perform(typeText(USER_THAT_EXISTS), closeSoftKeyboard());

        // Click blue color option
        onView(withId(R.id.blue))
                .perform(click());

        // Click signup
        onView(withId(R.id.letTheUserSignUp))
                .perform(click());

        onView(withId(R.id.edit_text_pass)).check(matches(hasErrorText("Enter Password")));
    }

    @Test
    public void registerUser_Success() {
        UUID uuid = UUID.randomUUID();
        onView(withId(R.id.edit_text_username))
                .perform(typeText(uuid.toString()), closeSoftKeyboard());

        onView(withId(R.id.edit_text_pass))
                .perform(typeText(PASSWORD), closeSoftKeyboard());

        onView(withId(R.id.blue))
                .perform(click());

        onView(withId(R.id.letTheUserSignUp))
                .perform(click());

        SystemClock.sleep(1500);

        final boolean[] userCreated = {false};
        FirebaseDatabase.getInstance().getReference().child("users").child(uuid.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String password = dataSnapshot.child("password").getValue(String.class);
                    int color = dataSnapshot.child("settings").child("color").getValue(Integer.class);
                    if (username.equals(uuid.toString()) && password.equals(PASSWORD) && color == 2) {
                        userCreated[0] = true;
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
        SystemClock.sleep(100);
        assert(userCreated[0]);
    }

    /**
     * Used to extract activity from activity scenario in order to check for toast popup message
     */
    private <T extends Activity> T getActivity(ActivityScenarioRule<T> activityScenarioRule) {
        AtomicReference<T> activityRef = new AtomicReference<>();
        activityScenarioRule.getScenario().onActivity(activityRef::set);
        return activityRef.get();
    }

    /**
     * Perform action of waiting for a specific view id.
     * @param viewId The id of the view to wait for.
     * @param millis The timeout of until when to wait for.
     */
    private static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

//                .perform(click());

        // Check that the text was changed.
//        onView(withId(R.id.textToBeChanged)).check(matches(withText(STRING_TO_BE_TYPED)));
//
//    @Test
//    public void changeText_newActivity() {
//        // Type text and then press the button.
//        onView(withId(R.id.editTextUserInput)).perform(typeText(STRING_TO_BE_TYPED),
//                closeSoftKeyboard());
//        onView(withId(R.id.activityChangeTextBtn)).perform(click());
//
//        // This view is in a different Activity, no need to tell Espresso.
//        onView(withId(R.id.show_text_view)).check(matches(withText(STRING_TO_BE_TYPED)));
//    }
}