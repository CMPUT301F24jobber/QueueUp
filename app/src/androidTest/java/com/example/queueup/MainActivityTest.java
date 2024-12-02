package com.example.queueup;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static java.util.EnumSet.allOf;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.queueup.views.SignUp;
import com.google.android.material.textfield.TextInputEditText;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test class for testing the functionality of the MainActivity in the QueueUp application.
 * This class includes tests for:
 *     UI visibility and navigation</li>
 *     Sign-up functionality for Admin, Organizer, and Attendee roles</li>
 *
 * Note: These tests should be run on an empty database for accurate results.
 *
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    /*
    Method to idle the tests to give time for fragments to load
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    /**
     * Rule to launch the MainActivity for testing.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    //    @Before
//    public void setUp() {
//        Intents.init();
//    }
//
//    @After
//    public void cleanUp() {
//        Intents.release();
//    }

    /**
     * Verifies that the Admin button is displayed in the MainActivity.
     */
    @Test
    public void testAdminButtonExists() {
        onView(withId(R.id.adminButton)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the Organizer button is displayed in the MainActivity.
     */
    @Test
    public void testOrganizerButtonExists() {
        onView(withId(R.id.organizerButton)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the Attendee button is displayed in the MainActivity.
     */
    @Test
    public void testAttendeeButtonExists() {
        onView(withId(R.id.attendeeButton)).check(matches(isDisplayed()));
    }

    /**
     * Tests the navigation when the Admin button is clicked.
     * Ensures that the password input layout is displayed in the next screen.
     */
    @Test
    public void testAdminButtonNavigation() {
        onView(withId(R.id.adminButton)).perform(click());
        //onView(withId(R.id.adminButton)).check(doesNotExist()); // check for activity switch, once any button on activity_main is clicked the SignUp fragment should be displayed
        onView(withId(R.id.passwordInputLayout)).check(matches(isDisplayed()));
    }

    /**
     * Tests the navigation when the Organizer button is clicked.
     * Ensures that the first name input layout is displayed in the next screen.
     */
    @Test
    public void testOrganizerButtonNavigation() {
        onView(withId(R.id.organizerButton)).perform(click());
        onView(withId(R.id.organizerButton)).check(doesNotExist()); // check for activity switch, once any button on activity_main is clicked the SignUp fragment should be displayed
        onView(withId(R.id.firstNameInputLayout)).check(matches(isDisplayed()));
    }

    /**
     * Tests the navigation when the Attendee button is clicked.
     * Ensures that the last name input layout is displayed in the next screen.
     */
    @Test
    public void testAttendeeButtonNavigation() {
        onView(withId(R.id.attendeeButton)).perform(click());
        onView(withId(R.id.attendeeButton)).check(doesNotExist()); // check for activity switch, once any button on activity_main is clicked the SignUp fragment should be displayed
        onView(withId(R.id.lastNameInputLayout)).check(matches(isDisplayed()));
    }

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
                .perform(typeText("Jon"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.lastNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Snow"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.emailInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("jonsnow@test.ca"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.phoneNumberInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("1234567890"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.usernameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("jonsnow"), closeSoftKeyboard());

        onView(allOf(isDescendantOfA(withId(R.id.passwordInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("123456"), closeSoftKeyboard());

        // Click the submit button
        onView(withId(R.id.submitButton)).perform(click());

        onView(isRoot()).perform(waitFor(5000));
        // Verify navigation to the next screen or success message
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }

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

    /**
     * Tests the sign-up process for an Attendee role.
     * Ensures that the Attendee can successfully navigate through the sign-up process.
     */
    @Test
    public void testSignUpForAttendee() {
        onView(withId(R.id.attendeeButton)).perform(click());

        onView(allOf(isDescendantOfA(withId(R.id.firstNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Jon"));

        onView(allOf(isDescendantOfA(withId(R.id.lastNameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Snow"));

        onView(allOf(isDescendantOfA(withId(R.id.emailInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("jonsnow@test.ca"));

        onView(allOf(isDescendantOfA(withId(R.id.phoneNumberInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("1234567890"));

        onView(allOf(isDescendantOfA(withId(R.id.usernameInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("jonsnow"));

        onView(withId(R.id.submitButton)).perform(click());
        onView(withId(R.id.queueup_title)).check(matches(isDisplayed()));
    }

}
