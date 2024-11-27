package com.example.queueup;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static java.util.EnumSet.allOf;
import static org.hamcrest.Matchers.allOf;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.queueup.views.SignUp;
import com.google.android.material.textfield.TextInputEditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// IMPORTANT: Run test on empty database for correct results

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

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
    @Test
    public void testAdminButtonExists() {
        onView(withId(R.id.adminButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testOrganizerButtonExists() {
        onView(withId(R.id.organizerButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testAttendeeButtonExists() {
        onView(withId(R.id.attendeeButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testAdminButtonNavigation() {
        onView(withId(R.id.adminButton)).perform(click());
        //onView(withId(R.id.adminButton)).check(doesNotExist()); // check for activity switch, once any button on activity_main is clicked the SignUp fragment should be displayed
        onView(withId(R.id.passwordInputLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testOrganizerButtonNavigation() {
        onView(withId(R.id.organizerButton)).perform(click());
        onView(withId(R.id.organizerButton)).check(doesNotExist()); // check for activity switch, once any button on activity_main is clicked the SignUp fragment should be displayed
        onView(withId(R.id.firstNameInputLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testAttendeeButtonNavigation() {
        onView(withId(R.id.attendeeButton)).perform(click());
        onView(withId(R.id.attendeeButton)).check(doesNotExist()); // check for activity switch, once any button on activity_main is clicked the SignUp fragment should be displayed
        onView(withId(R.id.lastNameInputLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignUpForAdmin() {
        onView(withId(R.id.adminButton)).perform(click());

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

        onView(allOf(isDescendantOfA(withId(R.id.passwordInputLayout)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("123456"));

        onView(withId(R.id.submitButton)).perform(click());
        //onView(withId(R.id.queueup_title)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInputLayout)).check(doesNotExist());
    }

    @Test
    public void testSignUpForOrganizer() {
        onView(withId(R.id.organizerButton)).perform(click());

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
