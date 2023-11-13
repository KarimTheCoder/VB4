package com.fortitude.shamsulkarim.ieltsfordory.ui.initial;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.fortitude.shamsulkarim.ieltsfordory.TestUtils.withRecyclerView;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ScrollToAction;
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
public class FavoriteTest {


    public static final int FAV_RECYCLERVIEW = R.id.recycler_view_favorite_words;
    public static final int FAV_FAV_BUTTON = R.id.favorite_favorite;
    public static final int FAV_BUTTON = R.id.favorite;
    public static final int FAV_TRANS_TEXTVIEW = R.id.favorite_card_translation;
    public static final int WORD_RECYCLERVIEW = R.id.new_recycler_view;

    @Rule
    public ActivityScenarioRule<AppLauncher> mActivityScenarioRule =
            new ActivityScenarioRule<>(AppLauncher.class);

    @Test
    public void favoriteTest() throws InterruptedException {

        Thread.sleep(4000L);

        ViewInteraction fancyButton = onView(
                allOf(withId(R.id.start_trial_button)));
        fancyButton.perform(click());




        //Favorite words from WordFragment
        clickOnAllWordFragment();

        clickOnSpinnerItem(1);
        clickRecyclerViewButton(0,WORD_RECYCLERVIEW,FAV_BUTTON);
        clickRecyclerViewButton(1,WORD_RECYCLERVIEW,FAV_BUTTON);

        clickOnFavoriteFragment();
        assertTextInRecyclerViewText(0,FAV_RECYCLERVIEW,FAV_TRANS_TEXTVIEW,"To make (something, such as a task or action) slow or difficult");
        assertTextInRecyclerViewText(1,FAV_RECYCLERVIEW,FAV_TRANS_TEXTVIEW,"A part that forms when you tie a piece of rope, string, fabric, etc., to itself or to something else");

        clickRecyclerViewButton(0,FAV_RECYCLERVIEW,FAV_FAV_BUTTON);
        clickRecyclerViewButton(1,FAV_RECYCLERVIEW,FAV_FAV_BUTTON);


        clickOnAllWordFragment();
        clickOnSpinnerItem(2);
        clickRecyclerViewButton(0,WORD_RECYCLERVIEW,FAV_BUTTON);
        clickRecyclerViewButton(1,WORD_RECYCLERVIEW,FAV_BUTTON);
        clickOnFavoriteFragment();
        /**/
        assertTextInRecyclerViewText(0,FAV_RECYCLERVIEW,FAV_TRANS_TEXTVIEW,"Without making errors");
        assertTextInRecyclerViewText(1,FAV_RECYCLERVIEW,FAV_TRANS_TEXTVIEW,"To display for the public");
        clickRecyclerViewButton(0,FAV_RECYCLERVIEW,FAV_FAV_BUTTON);
        clickRecyclerViewButton(1,FAV_RECYCLERVIEW,FAV_FAV_BUTTON);



        clickOnAllWordFragment();
        clickOnSpinnerItem(0);
        clickRecyclerViewButton(0,WORD_RECYCLERVIEW,FAV_BUTTON);
        clickRecyclerViewButton(1,WORD_RECYCLERVIEW,FAV_BUTTON);


        clickOnFavoriteFragment();
        assertTextInRecyclerViewText(0,FAV_RECYCLERVIEW,FAV_TRANS_TEXTVIEW,"Protection from harm");
        assertTextInRecyclerViewText(1,FAV_RECYCLERVIEW,FAV_TRANS_TEXTVIEW,"A strong effect");
        clickRecyclerViewButton(0,FAV_RECYCLERVIEW,FAV_FAV_BUTTON);
        clickRecyclerViewButton(1,FAV_RECYCLERVIEW,FAV_FAV_BUTTON);







    }

    private static void clickOnSpinnerItem(int position) {
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
                .atPosition(position);
        appCompatCheckedTextView.perform(click());
    }


    private static void clickOnAllWordFragment() {
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
    }

    private static void assertTextInRecyclerViewText(int position, int recyclerView, int textView, String text) {
        ViewInteraction meaningTextView = onView(withRecyclerView(recyclerView)
                .atPositionOnView(position, textView));
        meaningTextView.check(matches(withText(text)));
    }

    private static void clickOnFavoriteFragment() {
        ViewInteraction fancyButton4 = onView(
                allOf(withId(R.id.favorite_button),
                        childAtPosition(
                                allOf(
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                3),
                        isDisplayed()));
        fancyButton4.perform(click());
    }

    private static void clickRecyclerViewButton(int position,int recyclerviewId, int buttonId) {
        ViewInteraction button = onView(withRecyclerView(recyclerviewId)
                .atPositionOnView(position, buttonId));
        button.perform(click());
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
