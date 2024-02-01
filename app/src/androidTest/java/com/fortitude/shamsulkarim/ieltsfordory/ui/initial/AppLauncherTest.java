package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.fortitude.shamsulkarim.ieltsfordory.R;

import static org.hamcrest.Matchers.allOf;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppLauncherTest {

    @Rule
    public ActivityScenarioRule<AppLauncher> mActivityScenarioRule =
            new ActivityScenarioRule<>(AppLauncher.class);

    @Test
    public void appLauncherTest() {
        ViewInteraction fancyButton = onView(
                allOf(withId(R.id.start_trial_button), withText("Start now"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        fancyButton.perform(click());

        ViewInteraction cardView = onView(
                allOf(withId(R.id.beginner_card_home),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        3),
                                2),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction fancyButton2 = onView(
                allOf(withId(R.id.no_word_home), withText("Learn"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView8),
                                        0),
                                6),
                        isDisplayed()));
        fancyButton2.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(com.yarolegovich.lovelydialog.R.id.ld_btn_yes), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.intermediate_card_home),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        3),
                                1),
                        isDisplayed()));
        cardView2.perform(click());

        ViewInteraction fancyButton3 = onView(
                allOf(withId(R.id.no_word_home), withText("Learn"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView8),
                                        0),
                                6),
                        isDisplayed()));
        fancyButton3.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(com.yarolegovich.lovelydialog.R.id.ld_btn_yes), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                3),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction cardView3 = onView(
                allOf(withId(R.id.advance_card_home),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        3),
                                0),
                        isDisplayed()));
        cardView3.perform(click());

        ViewInteraction fancyButton4 = onView(
                allOf(withId(R.id.no_word_home), withText("Learn"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cardView8),
                                        0),
                                6),
                        isDisplayed()));
        fancyButton4.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(com.yarolegovich.lovelydialog.R.id.ld_btn_yes), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                3),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction fancyButton5 = onView(
                allOf(withId(R.id.learned_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        fancyButton5.perform(click());

        ViewInteraction fancyButton6 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton6.perform(click());

        ViewInteraction fancyButton7 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton7.perform(click());

        ViewInteraction fancyButton8 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton8.perform(click());

        ViewInteraction fancyButton9 = onView(
                allOf(withId(R.id.favorite_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                3),
                        isDisplayed()));
        fancyButton9.perform(click());

        ViewInteraction fancyButton10 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton10.perform(click());

        ViewInteraction fancyButton11 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton11.perform(click());

        ViewInteraction fancyButton12 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton12.perform(click());

        ViewInteraction fancyButton13 = onView(
                allOf(withId(R.id.learned_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        fancyButton13.perform(click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.word_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        3),
                                0),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        ViewInteraction fancyButton14 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton14.perform(click());

        ViewInteraction fancyButton15 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton15.perform(click());

        ViewInteraction fancyButton16 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton16.perform(click());

        ViewInteraction fancyButton17 = onView(
                allOf(withId(R.id.favorite_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                3),
                        isDisplayed()));
        fancyButton17.perform(click());

        ViewInteraction fancyButton18 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton18.perform(click());

        ViewInteraction fancyButton19 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton19.perform(click());

        ViewInteraction fancyButton20 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton20.perform(click());

        ViewInteraction fancyButton21 = onView(
                allOf(withId(R.id.learned_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        fancyButton21.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.word_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        3),
                                0),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView2.perform(click());

        ViewInteraction fancyButton22 = onView(
                allOf(withId(R.id.favorite_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                3),
                        isDisplayed()));
        fancyButton22.perform(click());

        ViewInteraction fancyButton23 = onView(
                allOf(withId(R.id.learned_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        fancyButton23.perform(click());

        ViewInteraction fancyButton24 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton24.perform(click());

        ViewInteraction fancyButton25 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton25.perform(click());

        ViewInteraction fancyButton26 = onView(
                allOf(withId(R.id.favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton26.perform(click());

        ViewInteraction fancyButton27 = onView(
                allOf(withId(R.id.favorite_button),
                        childAtPosition(
                                allOf(withId(R.id.bottom_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                3),
                        isDisplayed()));
        fancyButton27.perform(click());

        ViewInteraction fancyButton28 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton28.perform(click());

        ViewInteraction fancyButton29 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton29.perform(click());

        ViewInteraction fancyButton30 = onView(
                allOf(withId(R.id.favorite_favorite),
                        childAtPosition(
                                allOf(withId(R.id.top),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        fancyButton30.perform(click());
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
