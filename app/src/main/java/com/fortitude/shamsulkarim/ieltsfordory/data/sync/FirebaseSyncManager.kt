package com.fortitude.shamsulkarim.ieltsfordory.data.sync

import android.content.Context
import android.content.SharedPreferences
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.AddChildEventListenerUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateFavoriteStateUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateLearnStateUseCase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Kotlin implementation of FirebaseSyncManager.
 * Synchronizes local Room database with remote Firebase Realtime Database
 * using background Coroutines.
 */
class FirebaseSyncManager(
    private val context: Context,
    private val addChildEventListenerUseCase: AddChildEventListenerUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    private val updateLearnStateUseCase: UpdateLearnStateUseCase
) {
    private val sp: SharedPreferences = context.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var savedBeginnerFav: List<Int>? = null
    private var savedAdvanceFav: List<Int>? = null
    private var savedIntermediateFav: List<Int>? = null
    private var savedGreFav: List<Int>? = null

    private var savedIeltsLearned: List<Int>? = null
    private var savedToeflLearned: List<Int>? = null
    private var savedSatLearned: List<Int>? = null
    private var savedGreLearned: List<Int>? = null

    private var advanceFavorite: String? = null
    private var advanceLearned: String? = null
    private var beginnerFavorite: String? = null
    private var beginnerLearned: String? = null
    private var intermediateFavorite: String? = null
    private var intermediateLearned: String? = null
    private var greFavorite: String? = null
    private var greLearned: String? = null

    interface SyncCallback {
        fun onCloudDataFound(confirmSync: Runnable)
        fun onSyncComplete()
        fun onSyncError(e: Exception)
    }

    fun startSync(userId: String, callback: SyncCallback?) {
        addChildEventListenerUseCase.execute(userId, object : ChildEventListener {
            private var i = 0
            private val strData = arrayOfNulls<String>(9)
            private val data = HashMap<String, String>()
            private var askOnce = false

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists() && i == 8 && !askOnce) {
                    askOnce = true
                    callback?.onCloudDataFound { performInitialSync(callback) }
                }

                val state = dataSnapshot.getValue(String::class.java)
                strData[i] = state
                dataSnapshot.key?.let { key ->
                    state?.let { data[key] = it }
                }

                if (strData[8] != null) {
                    parseData(strData.filterNotNull().toTypedArray())
                }
                i++
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val value = dataSnapshot.getValue(String::class.java) ?: return
                val key = dataSnapshot.key ?: return

                scope.launch {
                    if (key.equals("advanceFavCount", ignoreCase = true) ||
                        key.equals("intermediateFavCount", ignoreCase = true) ||
                        key.equals("beginnerFavCount", ignoreCase = true)
                    ) {
                        syncDatabasesIfFavDataChanged(value, key)
                    }
                    if (key.equals("advanceLearnedCount", ignoreCase = true) ||
                        key.equals("intermediateLearnedCount", ignoreCase = true) ||
                        key.equals("beginnerLearnedCount", ignoreCase = true)
                    ) {
                        syncSPIfLearnedDataChanged(value, key)
                    }
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {
                callback?.onSyncError(databaseError.toException())
            }
        })
    }

    private fun parseData(strData: Array<String>) {
        if (strData.size < 9) return
        greFavorite = strData[0]
        greLearned = strData[1]
        beginnerFavorite = strData[2]
        beginnerLearned = strData[3]
        sp.edit().putString(AppPreferences.KEY_USER_NAME, strData[4]).apply()
        advanceFavorite = strData[5]
        advanceLearned = strData[6]
        intermediateFavorite = strData[7]
        intermediateLearned = strData[8]
    }

    private fun performInitialSync(callback: SyncCallback?) {
        addingBuilderToNums()

        scope.launch {
            syncSQL()
            withContext(Dispatchers.Main) {
                callback?.onSyncComplete()
            }
        }
    }

    private suspend fun syncSQL() {
        syncFavorites(savedBeginnerFav) { id, state -> updateFavoriteStateUseCase.execute(VocabularySource.IELTS, id, state) }
        syncFavorites(savedIntermediateFav) { id, state -> updateFavoriteStateUseCase.execute(VocabularySource.TOEFL, id, state) }
        syncFavorites(savedAdvanceFav) { id, state -> updateFavoriteStateUseCase.execute(VocabularySource.SAT, id, state) }
        syncFavorites(savedGreFav) { id, state -> updateFavoriteStateUseCase.execute(VocabularySource.GRE, id, state) }

        syncLearned(savedIeltsLearned) { id, state -> updateLearnStateUseCase.execute(VocabularySource.IELTS, id, state) }
        syncLearned(savedToeflLearned) { id, state -> updateLearnStateUseCase.execute(VocabularySource.TOEFL, id, state) }
        syncLearned(savedSatLearned) { id, state -> updateLearnStateUseCase.execute(VocabularySource.SAT, id, state) }
        syncLearned(savedGreLearned) { id, state -> updateLearnStateUseCase.execute(VocabularySource.GRE, id, state) }
    }

    private suspend fun syncFavorites(list: List<Int>?, action: suspend (Int, Boolean) -> Unit) {
        if (list != null && list.isNotEmpty()) {
            for (i in list.indices) {
                action(i + 1, list[i] == 1)
            }
        }
    }

    private suspend fun syncLearned(list: List<Int>?, action: suspend (Int, Boolean) -> Unit) {
        if (list != null && list.isNotEmpty()) {
            for (i in list.indices) {
                action(i + 1, list[i] == 1)
            }
        }
    }

    private fun addingBuilderToNums() {
        savedAdvanceFav = builderToNums(advanceFavorite ?: "")
        savedIntermediateFav = builderToNums(intermediateFavorite ?: "")
        savedBeginnerFav = builderToNums(beginnerFavorite ?: "")
        savedGreFav = builderToNums(greFavorite ?: "")

        savedIeltsLearned = builderToNums(beginnerLearned ?: "")
        savedToeflLearned = builderToNums(intermediateLearned ?: "")
        savedSatLearned = builderToNums(advanceLearned ?: "")
        savedGreLearned = builderToNums(greLearned ?: "")
    }

    private fun builderToNums(string: String): List<Int> {
        val backToNums = mutableListOf<Int>()
        var i = 0
        while (i < string.length) {
            val part = string.substring(i, (i + 1).coerceAtMost(string.length))
            if (part.equals("1", ignoreCase = true)) {
                backToNums.add(1)
            } else {
                backToNums.add(0)
            }
            i += 2
        }
        return backToNums
    }

    private suspend fun syncDatabasesIfFavDataChanged(newData: String, key: String) {
        val newDataList = builderToNums(newData)
        if (newDataList.isNotEmpty()) {
            when {
                key.equals("advanceFavCount", ignoreCase = true) -> {
                    resetAndSync(R.array.SAT_words, "advance", newDataList) { id, state ->
                        updateFavoriteStateUseCase.execute(VocabularySource.SAT, id, state)
                    }
                }
                key.equals("intermediateFavCount", ignoreCase = true) -> {
                    resetAndSync(R.array.TOEFL_words, "intermediate", newDataList) { id, state ->
                        updateFavoriteStateUseCase.execute(VocabularySource.TOEFL, id, state)
                    }
                }
                key.equals("beginnerFavCount", ignoreCase = true) -> {
                    resetAndSync(R.array.TOEFL_words, "beginner", newDataList) { id, state ->
                        updateFavoriteStateUseCase.execute(VocabularySource.IELTS, id, state)
                    }
                }
            }
        }
    }

    private suspend fun resetAndSync(
        arrayResId: Int,
        spKey: String,
        newDataList: List<Int>,
        action: suspend (Int, Boolean) -> Unit
    ) {
        val size = sp.getInt(spKey, context.resources.getStringArray(arrayResId).size)
        for (i in 0 until size) {
            action(i + 1, false)
        }
        for (k in newDataList.indices) {
            action(newDataList[k] + 1, true)
        }
    }

    private fun syncSPIfLearnedDataChanged(data: String, key: String) {
        val firebaseSaved = data.toIntOrNull() ?: return
        when {
            key.equals("advanceLearnedCount", ignoreCase = true) -> updateSPIfHigher("advance", firebaseSaved)
            key.equals("intermediateLearnedCount", ignoreCase = true) -> updateSPIfHigher("intermediate", firebaseSaved)
            key.equals("beginnerLearnedCount", ignoreCase = true) -> updateSPIfHigher("beginner", firebaseSaved)
        }
    }

    private fun updateSPIfHigher(key: String, firebaseValue: Int) {
        val localValue = sp.getInt(key, 0)
        if (firebaseValue > localValue) {
            sp.edit().putInt(key, firebaseValue).apply()
        }
    }
}
