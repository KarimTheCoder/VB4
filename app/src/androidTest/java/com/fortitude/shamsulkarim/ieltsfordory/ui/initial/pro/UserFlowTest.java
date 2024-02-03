package com.fortitude.shamsulkarim.ieltsfordory.ui.initial.pro;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.ui.initial.AppLauncher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserFlowTest {

    @Rule
    public ActivityScenarioRule<AppLauncher> mActivityScenarioRule =
            new ActivityScenarioRule<>(AppLauncher.class);

    @Test
    public void userFlowTest() throws InterruptedException {




        Thread.sleep(4000L);


        ViewInteraction fancyButton2 = onView(
                allOf(withId(R.id.word_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        fancyButton2.perform(click());

        ViewInteraction fancyButton3 = onView(
                allOf(withId(R.id.learned_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        fancyButton3.perform(click());

        ViewInteraction fancyButton4 = onView(
                allOf(withId(R.id.favorite_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                3),
                        isDisplayed()));
        fancyButton4.perform(click());

        ViewInteraction fancyButton5 = onView(
                allOf(withId(R.id.profile_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                4),
                        isDisplayed()));
        fancyButton5.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.profile_menu_settings), withContentDescription("Settings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.profile_toolbar),
                                        1),
                                1),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        pressBack();

        ViewInteraction fancyButton6 = onView(
                allOf(withId(R.id.home_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        fancyButton6.perform(click());

        ViewInteraction cardView = onView(
                allOf(withId(R.id.beginner_card_home)));
        cardView.perform(click());

        pressBack();

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.intermediate_card_home)));
        cardView2.perform(click());

        pressBack();

        ViewInteraction cardView3 = onView(
                allOf(withId(R.id.advance_card_home)));
        cardView3.perform(click());

        ViewInteraction fancyButton7 = onView(
                allOf(withId(R.id.no_word_home)));
        fancyButton7.perform(click());

        Thread.sleep(7000L);

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.train_fab),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());
        Thread.sleep(7000L);
        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.train_fab),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                2),
                        isDisplayed()));
        floatingActionButton2.perform(click());
        Thread.sleep(7000L);
        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.train_fab),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                2),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(android.R.id.button1)));
        materialButton.perform(scrollTo(), click());

        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.tf_home),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        floatingActionButton4.perform(click());
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
