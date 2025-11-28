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
    public static final String KEY_PREV_SELECTION = "prevWordSelection";
    public static final String KEY_FIRST_POS = "wordFirstVisiblePos";
    public static final String KEY_FIRST_OFFSET = "wordFirstOffsetTop";
    public static final String KEY_FAVORITE_SCROLL_POS = "recyclerview_last_pos";
    public static final String KEY_PRACTICE_MODE = "practice";
    public static final String KEY_PREV_LEARNED_SELECTION = "prevLearnedSelection";
    public static final String KEY_DEFAULT_ALARM = "defaultAlarm";
    public static final String KEY_SECOND_LANGUAGE = "secondlanguage";
    public static final String KEY_IS_IELTS_ACTIVE = "isIELTSActive";
    public static final String KEY_IS_TOEFL_ACTIVE = "isTOEFLActive";
    public static final String KEY_IS_SAT_ACTIVE = "isSATActive";
    public static final String KEY_IS_GRE_ACTIVE = "isGREActive";
    public static final String KEY_SOUND_STATE = "soundState";
    public static final String KEY_MISTAKE_FAVORITE = "mistakeFavorite";
    public static final String KEY_FAVORITE_WORD_COUNT = "favoriteWordCount";
    public static final String KEY_MOST_MISTAKEN_WORD = "MostMistakenWord";
    public static final String KEY_TOTAL_CORRECTS = "totalCorrects";
    public static final String KEY_FAVORITE_WRONGS = "favoriteWrongs";
    public static final String KEY_IMAGE_QUALITY = "imageQuality";
    public static final String KEY_PRONUN_STATE = "pronunState";

    private final SharedPreferences sp;

    public static AppPreferences get(Context context) {
        return new AppPreferences(context.getApplicationContext());
    }

    private AppPreferences(Context context) {
        this.sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public boolean contains(String key) {
        return sp.contains(key);
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    public int getInt(String key, int def) {
        return sp.getInt(key, def);
    }

    public void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public boolean getBool(String key, boolean def) {
        return sp.getBoolean(key, def);
    }

    public void setBool(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public String getString(String key, String def) {
        return sp.getString(key, def);
    }

    public void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public long getLong(String key, long def) {
        return sp.getLong(key, def);
    }

    public void setLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public int getPrevWordSelection() {
        return getInt(KEY_PREV_SELECTION, 0);
    }

    public void setPrevWordSelection(int v) {
        setInt(KEY_PREV_SELECTION, v);
    }

    public int getWordFirstVisiblePos() {
        return getInt(KEY_FIRST_POS, 0);
    }

    public void setWordFirstVisiblePos(int v) {
        setInt(KEY_FIRST_POS, v);
    }

    public int getWordFirstOffsetTop() {
        return getInt(KEY_FIRST_OFFSET, 0);
    }

    public void setWordFirstOffsetTop(int v) {
        setInt(KEY_FIRST_OFFSET, v);
    }

    public int getFavoriteScrollPos() {
        return getInt(KEY_FAVORITE_SCROLL_POS, 0);
    }

    public void setFavoriteScrollPos(int v) {
        setInt(KEY_FAVORITE_SCROLL_POS, v);
    }

    public String getPracticeMode() {
        return getString(KEY_PRACTICE_MODE, "");
    }

    public void setPracticeMode(String v) {
        setString(KEY_PRACTICE_MODE, v);
    }

    public int getPrevLearnedSelection() {
        return getInt(KEY_PREV_LEARNED_SELECTION, 0);
    }

    public void setPrevLearnedSelection(int v) {
        setInt(KEY_PREV_LEARNED_SELECTION, v);
    }

    public int getWordsPerSession() {
        return getInt(KEY_WORDS_PER_SESSION, 5);
    }

    public void setWordsPerSession(int v) {
        setInt(KEY_WORDS_PER_SESSION, v);
    }

    public int getRepeatationPerSession() {
        return getInt(KEY_REPEATATION_PER_SESSION, 5);
    }

    public void setRepeatationPerSession(int v) {
        setInt(KEY_REPEATATION_PER_SESSION, v);
    }

    public boolean isDefaultAlarmSet() {
        return getBool(KEY_DEFAULT_ALARM, false);
    }

    public void setDefaultAlarmSet(boolean v) {
        setBool(KEY_DEFAULT_ALARM, v);
    }

    public String getSecondLanguage() {
        return getString(KEY_SECOND_LANGUAGE, "English");
    }

    public void setSecondLanguage(String v) {
        setString(KEY_SECOND_LANGUAGE, v);
    }

    public boolean isIELTSActive() {
        return getBool(KEY_IS_IELTS_ACTIVE, true);
    }

    public void setIELTSActive(boolean v) {
        setBool(KEY_IS_IELTS_ACTIVE, v);
    }

    public boolean isTOEFLActive() {
        return getBool(KEY_IS_TOEFL_ACTIVE, true);
    }

    public void setTOEFLActive(boolean v) {
        setBool(KEY_IS_TOEFL_ACTIVE, v);
    }

    public boolean isSATActive() {
        return getBool(KEY_IS_SAT_ACTIVE, true);
    }

    public void setSATActive(boolean v) {
        setBool(KEY_IS_SAT_ACTIVE, v);
    }

    public boolean isGREActive() {
        return getBool(KEY_IS_GRE_ACTIVE, true);
    }

    public void setGREActive(boolean v) {
        setBool(KEY_IS_GRE_ACTIVE, v);
    }

    public boolean isPremium() {
        return sp.contains(KEY_PURCHASE) || sp.contains(KEY_PREMIUM);
    }

    public boolean isTrialActive() {
        if (!sp.contains(KEY_TRIAL_END_DATE))
            return false;
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

    public long getTrialEndDate() {
        return getLong(KEY_TRIAL_END_DATE, 0);
    }

    public boolean isHomeVisited() {
        return getBool(KEY_HOME, false);
    }

    public void setHomeVisited(boolean v) {
        setBool(KEY_HOME, v);
    }

    public String getLevel() {
        return getString(KEY_LEVEL, "");
    }

    public void setLevel(String v) {
        setString(KEY_LEVEL, v);
    }

    public boolean isHomeFragmentTrialEndShown() {
        return getBool(KEY_HOME_FRAGMENT_TRIAL_END, false);
    }

    public void setHomeFragmentTrialEndShown(boolean v) {
        setBool(KEY_HOME_FRAGMENT_TRIAL_END, v);
    }

    public void setDarkMode(int v) {
        setInt(KEY_DARK_MODE, v);
    }

    public int getDarkMode() {
        return getInt(KEY_DARK_MODE, 0);
    }

    public static void migrateLegacy(Context context) {
        SharedPreferences legacy = context.getSharedPreferences("com.example.shamsulkarim.vastvocabulary",
                Context.MODE_PRIVATE);
        SharedPreferences current = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = current.edit();
        if (legacy.contains("beginnerWordCount1"))
            e.putInt("beginnerWordCount1", legacy.getInt("beginnerWordCount1", 0));
        if (legacy.contains("intermediateWordCount1"))
            e.putInt("intermediateWordCount1", legacy.getInt("intermediateWordCount1", 0));
        if (legacy.contains("advanceWordCount1"))
            e.putInt("advanceWordCount1", legacy.getInt("advanceWordCount1", 0));
        if (legacy.contains("GREwordCount1"))
            e.putInt("GREwordCount1", legacy.getInt("GREwordCount1", 0));
        e.apply();
    }

    public boolean isSignedIn() {
        return sp.getBoolean(KEY_IS_SIGNED_IN, false);
    }

    public void setSignedIn(boolean v) {
        sp.edit().putBoolean(KEY_IS_SIGNED_IN, v).apply();
    }

    public String getUserName() {
        return sp.getString(KEY_USER_NAME, "Doggo");
    }

    public void setUserName(String name) {
        sp.edit().putString(KEY_USER_NAME, name).apply();
    }

    public void setLevelProgress(String level, int value) {
        setInt(level, value);
    }

    public boolean getSoundState() {
        return getBool(KEY_SOUND_STATE, true);
    }

    public void setSoundState(boolean v) {
        setBool(KEY_SOUND_STATE, v);
    }

    public int getMistakeFavorite() {
        return getInt(KEY_MISTAKE_FAVORITE, 0);
    }

    public void setMistakeFavorite(int v) {
        setInt(KEY_MISTAKE_FAVORITE, v);
    }

    public int getFavoriteWordCount() {
        return getInt(KEY_FAVORITE_WORD_COUNT, 0);
    }

    public void setFavoriteWordCount(int v) {
        setInt(KEY_FAVORITE_WORD_COUNT, v);
    }

    public String getMostMistakenWord() {
        return getString(KEY_MOST_MISTAKEN_WORD, "no");
    }

    public void setMostMistakenWord(String v) {
        setString(KEY_MOST_MISTAKEN_WORD, v);
    }

    public int getTotalCorrects() {
        return getInt(KEY_TOTAL_CORRECTS, 0);
    }

    public void setTotalCorrects(int v) {
        setInt(KEY_TOTAL_CORRECTS, v);
    }

    public int getFavoriteWrongs() {
        return getInt(KEY_FAVORITE_WRONGS, 0);
    }

    public void setFavoriteWrongs(int v) {
        setInt(KEY_FAVORITE_WRONGS, v);
    }

    public int getImageQuality() {
        return getInt(KEY_IMAGE_QUALITY, 1);
    }

    public void setImageQuality(int v) {
        setInt(KEY_IMAGE_QUALITY, v);
    }

    public boolean getPronunState() {
        return getBool(KEY_PRONUN_STATE, true);
    }

    public void setPronunState(boolean v) {
        setBool(KEY_PRONUN_STATE, v);
    }
}
