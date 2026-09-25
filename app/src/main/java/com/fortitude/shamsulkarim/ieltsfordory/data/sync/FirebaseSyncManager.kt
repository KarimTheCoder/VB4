package com.fortitude.shamsulkarim.ieltsfordory.data.sync

import android.content.Context
import android.content.SharedPreferences
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.UserPreferencesRepository
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

interface SyncManager {
    fun startSync(userId: String, callback: FirebaseSyncManager.SyncCallback?)
}

/**
 * Kotlin implementation of FirebaseSyncManager.
 * Synchronizes local Room database with remote Firebase Realtime Database
 * using background Coroutines.
 */
open class FirebaseSyncManager(
    private val context: Context,
    private val addChildEventListenerUseCase: AddChildEventListenerUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    private val updateLearnStateUseCase: UpdateLearnStateUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : SyncManager {

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

    override fun startSync(userId: String, callback: SyncCallback?) {
        addChildEventListenerUseCase.execute(userId, object : ChildEventListener {
            private val data = HashMap<String, String>()
            private var askOnce = false

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val state = dataSnapshot.getValue(String::class.java)
                dataSnapshot.key?.let { key ->
                    state?.let { data[key] = it }
                }

                parseData(data)

                if (data.size >= 8 && !askOnce) {
                    askOnce = true
                    if (callback != null) {
                        callback.onCloudDataFound { performInitialSync(callback) }
                    } else {
                        performInitialSync(null)
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val value = dataSnapshot.getValue(String::class.java) ?: return
                val key = dataSnapshot.key ?: return

                scope.launch {
                    if (isFavKey(key)) {
                        syncDatabasesIfFavDataChanged(value, key)
                    } else if (isLearnedKey(key)) {
                        syncDatabasesIfLearnedDataChanged(value, key)
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

    open fun parseData(data: Map<String, String>) {
        greFavorite = data["greFavCount"] ?: data["greFavorite"]
        greLearned = data["greLearnedCount"] ?: data["greLearned"]
        beginnerFavorite = data["ieltsFavCount"] ?: data["beginnerFavCount"] ?: data["beginnerFavorite"]
        beginnerLearned = data["ieltsLearnedCount"] ?: data["beginnerLearnedCount"] ?: data["beginnerLearned"]
        advanceFavorite = data["satFavCount"] ?: data["advanceFavCount"] ?: data["advanceFavorite"]
        advanceLearned = data["satLearnedCount"] ?: data["advanceLearnedCount"] ?: data["advanceLearned"]
        intermediateFavorite = data["toeflFavCount"] ?: data["intermediateFavCount"] ?: data["intermediateFavorite"]
        intermediateLearned = data["toeflLearnedCount"] ?: data["intermediateLearnedCount"] ?: data["intermediateLearned"]

        val userName = data["name"] ?: data["userName"]
        if (!userName.isNullOrBlank()) {
            scope.launch {
                userPreferencesRepository.setUserName(userName)
            }
        }
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
                action(i, list[i] == 1)
            }
        }
    }

    private suspend fun syncLearned(list: List<Int>?, action: suspend (Int, Boolean) -> Unit) {
        if (list != null && list.isNotEmpty()) {
            for (i in list.indices) {
                action(i, list[i] == 1)
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

    open fun builderToNums(string: String): List<Int> {
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

    private fun isFavKey(key: String): Boolean {
        return key.equals("advanceFavCount", ignoreCase = true) ||
               key.equals("intermediateFavCount", ignoreCase = true) ||
               key.equals("beginnerFavCount", ignoreCase = true) ||
               key.equals("ieltsFavCount", ignoreCase = true) ||
               key.equals("toeflFavCount", ignoreCase = true) ||
               key.equals("satFavCount", ignoreCase = true) ||
               key.equals("greFavCount", ignoreCase = true)
    }

    private fun isLearnedKey(key: String): Boolean {
        return key.equals("advanceLearnedCount", ignoreCase = true) ||
               key.equals("intermediateLearnedCount", ignoreCase = true) ||
               key.equals("beginnerLearnedCount", ignoreCase = true) ||
               key.equals("ieltsLearnedCount", ignoreCase = true) ||
               key.equals("toeflLearnedCount", ignoreCase = true) ||
               key.equals("satLearnedCount", ignoreCase = true) ||
               key.equals("greLearnedCount", ignoreCase = true)
    }

    private suspend fun syncDatabasesIfFavDataChanged(newData: String, key: String) {
        val newDataList = builderToNums(newData)
        if (newDataList.isNotEmpty()) {
            when {
                key.equals("ieltsFavCount", ignoreCase = true) || key.equals("beginnerFavCount", ignoreCase = true) -> {
                    syncFavorites(newDataList) { id, state -> updateFavoriteStateUseCase.execute(VocabularySource.IELTS, id, state) }
                }
                key.equals("toeflFavCount", ignoreCase = true) || key.equals("intermediateFavCount", ignoreCase = true) -> {
                    syncFavorites(newDataList) { id, state -> updateFavoriteStateUseCase.execute(VocabularySource.TOEFL, id, state) }
                }
                key.equals("satFavCount", ignoreCase = true) || key.equals("advanceFavCount", ignoreCase = true) -> {
                    syncFavorites(newDataList) { id, state -> updateFavoriteStateUseCase.execute(VocabularySource.SAT, id, state) }
                }
                key.equals("greFavCount", ignoreCase = true) -> {
                    syncFavorites(newDataList) { id, state -> updateFavoriteStateUseCase.execute(VocabularySource.GRE, id, state) }
                }
            }
        }
    }

    private suspend fun syncDatabasesIfLearnedDataChanged(newData: String, key: String) {
        val newDataList = builderToNums(newData)
        if (newDataList.isNotEmpty()) {
            when {
                key.equals("ieltsLearnedCount", ignoreCase = true) || key.equals("beginnerLearnedCount", ignoreCase = true) -> {
                    syncLearned(newDataList) { id, state -> updateLearnStateUseCase.execute(VocabularySource.IELTS, id, state) }
                }
                key.equals("toeflLearnedCount", ignoreCase = true) || key.equals("intermediateLearnedCount", ignoreCase = true) -> {
                    syncLearned(newDataList) { id, state -> updateLearnStateUseCase.execute(VocabularySource.TOEFL, id, state) }
                }
                key.equals("satLearnedCount", ignoreCase = true) || key.equals("advanceLearnedCount", ignoreCase = true) -> {
                    syncLearned(newDataList) { id, state -> updateLearnStateUseCase.execute(VocabularySource.SAT, id, state) }
                }
                key.equals("greLearnedCount", ignoreCase = true) -> {
                    syncLearned(newDataList) { id, state -> updateLearnStateUseCase.execute(VocabularySource.GRE, id, state) }
                }
            }
        }
    }
}
