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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.queueup.MainActivityTest.waitFor;
import static org.hamcrest.Matchers.allOf;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.Rule;
import org.junit.Test;

public class OrganizerSignUpAndCreateEvent {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Tests the sign-up process for an Organizer role.
     * Ensures that the Organizer can successfully navigate through the sign-up process.
     */
    @Test
    public void testSignUpForOrganizer() {
        // Navigate to the Organizer sign-up screen
        onView(withId(R.id.organizerButton)).perform(click());

        // Wait for the screen to load
        onView(isRoot()).perform(waitFor(5000));
        onView(withId(R.id.firstNameInputLayout)).check(matches(isDisplayed()));

        // Fill out the form fields
        onView(allOf(isDescendantOfA(withId(R.id.firstNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Son"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.lastNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Goku"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.emailInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("sonGoku@test.ca"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.phoneNumberInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("9000000000"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.usernameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("songoku"), closeSoftKeyboard());
        onView(withId(R.id.submitButton)).perform(click());

        onView(withId(R.id.facilityNameInputLayout)).check(matches(isDisplayed()));

        // Fill in facility details
        onView(allOf(isDescendantOfA(withId(R.id.facilityNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("CCIS 1-140"), closeSoftKeyboard());
        onView(withId(R.id.facility_submit_button)).perform(click());

        onView(isRoot()).perform(waitFor(5000));
        onView(withId(R.id.plusButton)).check(matches(isDisplayed()));

        // Create Event
        onView(withId(R.id.plusButton)).perform(click());

        onView(isRoot()).perform(waitFor(5000));
        onView(withId(R.id.eventNameEditText)).check(matches(isDisplayed()));

        onView((withId(R.id.eventNameEditText)))
                .perform(typeText("Test Event"), closeSoftKeyboard());

        // Set Start Date
        onView(withId(R.id.startDateEditText)).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2025, 1, 1));
        onView(withText("OK")).perform(click());

        // Set Start Time
        onView(withId(R.id.startTimeEditText)).perform(click());
        onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(12, 0));
        onView(withText("OK")).perform(click());

        // Set End Date
        onView(withId(R.id.endDateEditText)).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2025, 1, 2));
        onView(withText("OK")).perform(click());

        // Set End Time
        onView(withId(R.id.endTimeEditText)).perform(click());
        onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(16, 0));
        onView(withText("OK")).perform(click());

        onView((withId(R.id.locationEditText)))
                .perform(typeText("Edmonton"), closeSoftKeyboard());

        onView((withId(R.id.descriptionEditText)))
                .perform(typeText("test"), closeSoftKeyboard());

        onView((withId(R.id.attendeeLimitEditText)))  // Going to check unlimited anyways so it will overwite this
                .perform(typeText("10"), closeSoftKeyboard());

        onView(withId(R.id.unlimitedAttendeeCheckBox)).perform(click());

        onView(withId(R.id.geolocationRequiredCheckBox)).perform(click());

        onView(withId(R.id.submitButton)).perform(click());

        onView(isRoot()).perform(waitFor(10000));
        onView(withId(R.id.plusButton)).check(matches(isDisplayed()));
    }
}