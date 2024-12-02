package com.example.queueup;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.queueup.MainActivityTest.waitFor;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.material.textfield.TextInputEditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

public class AdminSignUpAndEditProfileTest {

    static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Tests the sign-up process for an Admin role.
     * Ensures that the Admin can successfully navigate through the sign-up process.
     */
    @Test
    public void testSignUpForAdmin() {
        // Click the admin button to navigate to the SignUp activity
        onView(withId(R.id.adminButton)).perform(click());
        onView(isRoot()).perform(waitFor(5000)); // Wait for 5 seconds for layout to load

        // Fill in the form fields
        onView(allOf(isDescendantOfA(withId(R.id.firstNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Eren"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.lastNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Yeager"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.emailInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("yeager@test.ca"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.phoneNumberInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("8458501000"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.usernameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("eYeager"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.passwordInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("123456"), closeSoftKeyboard());

        // Click the submit button
        onView(withId(R.id.submitButton)).perform(click());

        onView(isRoot()).perform(waitFor(5000));
        // Verify navigation to the next screen or success message
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));

        onView(withId(R.id.bottom_navigation))
                .perform(click());

        // Navigate to profile page
        ViewInteraction bottomNavigationItemView5 = onView(
                allOf(withId(R.id.nav_profile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView5.perform(click());

        onView(withId(R.id.editButton)).perform(click());

        onView(isRoot()).perform(waitFor(5000));

        onView(withId(R.id.editFirstName))
                .perform(replaceText("Mikasa"))
                .perform(closeSoftKeyboard())
                .check(matches(withText("Mikasa")));

        onView(withId(R.id.editLastName))
                .perform(replaceText("Ackerman"))
                .perform(closeSoftKeyboard())
                .check(matches(withText("Ackerman")));

        onView(withId(R.id.saveButton)).perform(click());
        onView(isRoot()).perform(waitFor(5000));
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }
}