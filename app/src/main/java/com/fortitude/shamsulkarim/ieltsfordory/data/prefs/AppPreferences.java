package com.fortitude.shamsulkarim.ieltsfordory.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

public final class AppPreferences {

    public static final String NAME = "com.example.shamsulkarim.vocabulary";

    public static final String KEY_DARK_MODE = "DarkMode";
    public static final String KEY_TRIAL_END_DATE = "trial_end_date";
    public static final String KEY_PURCHASE = "purchase";
    public static final String KEY_PREMIUM = "premium";
    public static final String KEY_HOME = "home";
    public static final String KEY_HOME_FRAGMENT_TRIAL_END = "home_fragment_trail_end";
    public static final String KEY_WORDS_PER_SESSION = "wordsPerSession";
    public static final String KEY_REPEATATION_PER_SESSION = "repeatationPerSession";
    public static final String KEY_SKIP = "skip";
    public static final String KEY_FAVORITE_COUNT_PROFILE = "favoriteCountProfile";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_ADV_FAV = "advanceFavNum";
    public static final String KEY_ADV_LEARNED = "advanceLearnedNum";
    public static final String KEY_BEG_FAV = "beginnerFavNum";
    public static final String KEY_BEG_LEARNED = "beginnerLearnedNum";
    public static final String KEY_INT_FAV = "intermediateFavNum";
    public static final String KEY_INT_LEARNED = "intermediateLearnedNum";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_NAME = "userName";

    private final SharedPreferences sp;

    public static AppPreferences get(Context context) {
        return new AppPreferences(context.getApplicationContext());
    }

    private AppPreferences(Context context) {
        this.sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public boolean contains(String key) { return sp.contains(key); }
    public void remove(String key) { sp.edit().remove(key).apply(); }

    public int getInt(String key, int def) { return sp.getInt(key, def); }
    public void setInt(String key, int value) { sp.edit().putInt(key, value).apply(); }

    public boolean getBool(String key, boolean def) { return sp.getBoolean(key, def); }
    public void setBool(String key, boolean value) { sp.edit().putBoolean(key, value).apply(); }

    public String getString(String key, String def) { return sp.getString(key, def); }
    public void setString(String key, String value) { sp.edit().putString(key, value).apply(); }

    public long getLong(String key, long def) { return sp.getLong(key, def); }
    public void setLong(String key, long value) { sp.edit().putLong(key, value).apply(); }

    public boolean isPremium() { return sp.contains(KEY_PURCHASE) || sp.contains(KEY_PREMIUM); }

    public boolean isTrialActive() {
        if (!sp.contains(KEY_TRIAL_END_DATE)) return false;
        long end = sp.getLong(KEY_TRIAL_END_DATE, 0L);
        long now = Calendar.getInstance().getTime().getTime();
        return (end - now) >= 0;
    }

    public void setTrialEndDateDays(int days) {
        if (!sp.contains(KEY_TRIAL_END_DATE)) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, days);
            Date end = c.getTime();
            sp.edit().putLong(KEY_TRIAL_END_DATE, end.getTime()).apply();
        }
    }

    public static void migrateLegacy(Context context) {
        SharedPreferences legacy = context.getSharedPreferences("com.example.shamsulkarim.vastvocabulary", Context.MODE_PRIVATE);
        SharedPreferences current = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = current.edit();
        if (legacy.contains("beginnerWordCount1")) e.putInt("beginnerWordCount1", legacy.getInt("beginnerWordCount1", 0));
        if (legacy.contains("intermediateWordCount1")) e.putInt("intermediateWordCount1", legacy.getInt("intermediateWordCount1", 0));
        if (legacy.contains("advanceWordCount1")) e.putInt("advanceWordCount1", legacy.getInt("advanceWordCount1", 0));
        if (legacy.contains("GREwordCount1")) e.putInt("GREwordCount1", legacy.getInt("GREwordCount1", 0));
        e.apply();
    }

    public boolean isSignedIn(){ return sp.getBoolean(KEY_IS_SIGNED_IN, false); }
    public void setSignedIn(boolean v){ sp.edit().putBoolean(KEY_IS_SIGNED_IN, v).apply(); }
    public String getUserName(){ return sp.getString(KEY_USER_NAME, "Doggo"); }
    public void setUserName(String name){ sp.edit().putString(KEY_USER_NAME, name).apply(); }
}
