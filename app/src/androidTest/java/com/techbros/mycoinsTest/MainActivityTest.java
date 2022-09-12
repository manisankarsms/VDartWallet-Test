package com.techbros.mycoinsTest;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void mainActivityTest() {
        ViewInteraction textInputEditText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.empId),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("12345"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(withText("12345"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.empId),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(click());

        ViewInteraction textInputEditText3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.password),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.login), withText("LOGIN"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout9),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                2),
                        isDisplayed()));
        materialButton.perform(click());

        pressBack();

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("YES"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction textInputEditText4 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.empId),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("superadmin"), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.password),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText5.perform(replaceText("superadmin1"), closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.login), withText("LOGIN"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout9),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                2),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction cardView = onView(
                allOf(withId(R.id.cv1),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.core.widget.NestedScrollView")),
                                        0),
                                1),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction appCompatImageView = onView(
                allOf(withClassName(is("androidx.appcompat.widget.AppCompatImageView")), withContentDescription("Search"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withId(R.id.search),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("12345"), closeSoftKeyboard());

        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.lv2),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                2)))
                .atPosition(0);
        constraintLayout.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.materialButton6), withText("ADD"),
                        childAtPosition(
                                allOf(withId(R.id.user_dia),
                                        childAtPosition(
                                                withClassName(is("androidx.core.widget.NestedScrollView")),
                                                0)),
                                3),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction textInputEditText6 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.coinsValue),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText6.perform(replaceText("10"), closeSoftKeyboard());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.materialButton7), withText("CREDIT"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton5.perform(click());
    }

    private static Matcher<View> childAtPosition(
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
}
