package com.example.queueup;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
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

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.Rule;
import org.junit.Test;

public class AttendeeSignUpTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Tests the sign-up process for an Attendee role.
     * Ensures that the Attendee can successfully navigate through the sign-up process.
     */
    @Test
    public void testSignUpForAttendee() {
        onView(withId(R.id.attendeeButton)).perform(click());
        onView(isRoot()).perform(waitFor(5000));
        onView(withId(R.id.firstNameInputLayout)).check(matches(isDisplayed()));

        onView(allOf(isDescendantOfA(withId(R.id.firstNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Itadori"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.lastNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Yuji"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.emailInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("yuji@test.ca"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.phoneNumberInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("0001007000"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.usernameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("kurseKiller"), closeSoftKeyboard());

        onView(withId(R.id.submitButton)).perform(click());

        onView(isRoot()).perform(waitFor(5000));
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));

    }
}