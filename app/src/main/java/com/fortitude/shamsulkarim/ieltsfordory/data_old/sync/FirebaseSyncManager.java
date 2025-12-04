package com.fortitude.shamsulkarim.ieltsfordory.data_old.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.AddChildEventListenerUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateFavoriteStateUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateLearnStateUseCase;
import org.koin.java.KoinJavaComponent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manager class to handle Firebase synchronization logic.
 * Orchestrates data flow between FirebaseRepository (Remote) and
 * VocabularyRepository (Local).
 */
public class FirebaseSyncManager {

    private static final String TAG = "FirebaseSyncManager";
    private final AddChildEventListenerUseCase addChildEventListenerUseCase;
    private final UpdateFavoriteStateUseCase updateFavoriteStateUseCase;
    private final UpdateLearnStateUseCase updateLearnStateUseCase;
    private final Context context;
    private final SharedPreferences sp;

    private List<Integer> savedBeginnerFav, savedAdvanceFav, savedIntermediateFav, savedGreFav;
    private List<Integer> savedIeltsLearned, savedToeflLearned, savedSatLearned, savedGreLearned;
    private String ADVANCE_FAVORITE, ADVANCE_LEARNED, BEGINNER_FAVORITE, BEGINNER_LEARNED, INTERMEDIATE_FAVORITE,
            INTERMEDIATE_LEARNED, GRE_FAVORITE, GRE_LEARNED;

    public interface SyncCallback {
        void onCloudDataFound(Runnable confirmSync);

        void onSyncComplete();

        void onSyncError(Exception e);
    }

    public FirebaseSyncManager(Context context, AddChildEventListenerUseCase addChildEventListenerUseCase) {
        this.context = context;
        this.addChildEventListenerUseCase = addChildEventListenerUseCase;
        this.updateFavoriteStateUseCase = KoinJavaComponent.get(UpdateFavoriteStateUseCase.class);
        this.updateLearnStateUseCase = KoinJavaComponent.get(UpdateLearnStateUseCase.class);
        this.sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
    }

    public void startSync(String userId, SyncCallback callback) {
        addChildEventListenerUseCase.execute(userId, new ChildEventListener() {
            int i = 0;
            final String[] strData = new String[9];
            final HashMap<String, String> data = new HashMap<>();
            boolean askOnce = false;

            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists() && i == 8 && !askOnce) {
                    askOnce = true;
                    if (callback != null) {
                        callback.onCloudDataFound(() -> performInitialSync(callback));
                    }
                }

                String state = dataSnapshot.getValue(String.class);
                strData[i] = state;
                data.put(dataSnapshot.getKey(), state);

                if (strData[8] != null) {
                    parseData(strData);
                }
                i++;
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {
                String data = dataSnapshot.getValue(String.class);
                String key = dataSnapshot.getKey();

                if (key != null) {
                    if (key.equalsIgnoreCase("advanceFavCount") || key.equalsIgnoreCase("intermediateFavCount")
                            || key.equalsIgnoreCase("beginnerFavCount")) {
                        syncDatabasesIfFavDataChanged(data, key);
                    }
                    if (key.equalsIgnoreCase("advanceLearnedCount") || key.equalsIgnoreCase("intermediateLearnedCount")
                            || key.equalsIgnoreCase("beginnerLearnedCount")) {
                        syncSPIfLearnedDataChanged(data, key);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                if (callback != null) {
                    callback.onSyncError(databaseError.toException());
                }
            }
        });
    }

    private void parseData(String[] strData) {
        GRE_FAVORITE = strData[0];
        GRE_LEARNED = strData[1];
        BEGINNER_FAVORITE = strData[2];
        BEGINNER_LEARNED = strData[3];
        sp.edit().putString("userName", strData[4]).apply();
        ADVANCE_FAVORITE = strData[5];
        ADVANCE_LEARNED = strData[6];
        INTERMEDIATE_FAVORITE = strData[7];
        INTERMEDIATE_LEARNED = strData[8];
    }

    private void performInitialSync(SyncCallback callback) {
        addingBuilderToNums();

        new Handler(Looper.getMainLooper()).post(() -> {
            syncSQL();
            if (callback != null) {
                callback.onSyncComplete();
            }
        });
    }

    private void syncSQL() {
        syncFavorites(savedBeginnerFav, this::updateBeginnerFav);
        syncFavorites(savedIntermediateFav, this::updateIntermediateFav);
        syncFavorites(savedAdvanceFav, this::updateAdvanceFav);
        syncFavorites(savedGreFav, this::updateGreFav);

        syncLearned(savedIeltsLearned, this::updateIeltsLearned);
        syncLearned(savedToeflLearned, this::updateToeflLearned);
        syncLearned(savedSatLearned, this::updateSatLearned);
        syncLearned(savedGreLearned, this::updateGreLearned);
    }

    private void syncFavorites(List<Integer> list, UpdateAction action) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                action.update("" + (i + 1), list.get(i) == 1 ? "True" : "False");
            }
        }
    }

    private void syncLearned(List<Integer> list, UpdateAction action) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                action.update("" + (i + 1), list.get(i) == 1 ? "True" : "False");
            }
        }
    }

    @FunctionalInterface
    interface UpdateAction {
        void update(String id, String state);
    }

    private void updateBeginnerFav(String id, String state) {
        updateFavoriteStateUseCase.execute("IELTS", id, state);
    }

    private void updateIntermediateFav(String id, String state) {
        updateFavoriteStateUseCase.execute("TOEFL", id, state);
    }

    private void updateAdvanceFav(String id, String state) {
        updateFavoriteStateUseCase.execute("SAT", id, state);
    }

    private void updateGreFav(String id, String state) {
        updateFavoriteStateUseCase.execute("GRE", id, state);
    }

    private void updateIeltsLearned(String id, String state) {
        updateLearnStateUseCase.execute("IELTS", id, state);
    }

    private void updateToeflLearned(String id, String state) {
        updateLearnStateUseCase.execute("TOEFL", id, state);
    }

    private void updateSatLearned(String id, String state) {
        updateLearnStateUseCase.execute("SAT", id, state);
    }

    private void updateGreLearned(String id, String state) {
        updateLearnStateUseCase.execute("GRE", id, state);
    }

    private void addingBuilderToNums() {
        savedAdvanceFav = builderToNums(new StringBuilder(ADVANCE_FAVORITE));
        savedIntermediateFav = builderToNums(new StringBuilder(INTERMEDIATE_FAVORITE));
        savedBeginnerFav = builderToNums(new StringBuilder(BEGINNER_FAVORITE));
        savedGreFav = builderToNums(new StringBuilder(GRE_FAVORITE));

        savedIeltsLearned = builderToNums(new StringBuilder(BEGINNER_LEARNED));
        savedToeflLearned = builderToNums(new StringBuilder(INTERMEDIATE_LEARNED));
        savedSatLearned = builderToNums(new StringBuilder(ADVANCE_LEARNED));
        savedGreLearned = builderToNums(new StringBuilder(GRE_LEARNED));
    }

    private List<Integer> builderToNums(StringBuilder numBuilder) {
        List<Integer> backToNums = new ArrayList<>();
        String string = numBuilder.toString();
        for (int i = 0; i < string.length();) {
            if (string.substring(i, i + 1).equalsIgnoreCase("1")) {
                backToNums.add(1);
            } else {
                backToNums.add(0);
            }
            i = i + 2;
        }
        return backToNums;
    }

    private void syncDatabasesIfFavDataChanged(String newData, String key) {
        List<Integer> newDataList = builderToNums(new StringBuilder(newData));
        if (newDataList.size() > 0) {
            if (key.equalsIgnoreCase("advanceFavCount")) {
                resetAndSync(R.array.SAT_words, "advance", newDataList, this::updateAdvanceFav);
            } else if (key.equalsIgnoreCase("intermediateFavCount")) {
                resetAndSync(R.array.TOEFL_words, "intermediate", newDataList, this::updateIntermediateFav);
            } else if (key.equalsIgnoreCase("beginnerFavCount")) {
                resetAndSync(R.array.TOEFL_words, "beginner", newDataList, this::updateBeginnerFav);
            }
        }
    }

    private void resetAndSync(int arrayResId, String spKey, List<Integer> newDataList, UpdateAction action) {
        int size = sp.getInt(spKey, context.getResources().getStringArray(arrayResId).length);
        for (int i = 0; i < size; i++) {
            action.update("" + (i + 1), "False");
        }
        for (int k = 0; k < newDataList.size(); k++) {
            action.update("" + (newDataList.get(k) + 1), "True");
        }
    }

    private void syncSPIfLearnedDataChanged(String data, String key) {
        int firebaseSaved = Integer.parseInt(data);
        if (key.equalsIgnoreCase("advanceLearnedCount")) {
            updateSPIfHigher("advance", firebaseSaved);
        } else if (key.equalsIgnoreCase("intermediateLearnedCount")) {
            updateSPIfHigher("intermediate", firebaseSaved);
        } else if (key.equalsIgnoreCase("beginnerLearnedCount")) {
            updateSPIfHigher("beginner", firebaseSaved);
        }
    }

    private void updateSPIfHigher(String key, int firebaseValue) {
        int localValue = sp.getInt(key, 0);
        if (firebaseValue > localValue) {
            sp.edit().putInt(key, firebaseValue).apply();
        }
    }
}
