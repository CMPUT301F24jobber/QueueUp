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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileTesting {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testProfileAcitivity() {
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

        // Need to write test for profile activity & edit profile


    }
}
