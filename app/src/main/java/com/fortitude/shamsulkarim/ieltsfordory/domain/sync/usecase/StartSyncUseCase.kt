package com.fortitude.shamsulkarim.ieltsfordory.domain.sync.usecase

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.domain.database.usecase.AddChildEventListenerUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateFavoriteStateUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.UpdateLearnStateUseCase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class StartSyncUseCase(
    private val addChildEventListenerUseCase: AddChildEventListenerUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    private val updateLearnStateUseCase: UpdateLearnStateUseCase
) {

    interface SyncCallback {
        fun onCloudDataFound(confirmSync: Runnable)
        fun onSyncComplete()
        fun onSyncError(e: Exception)
    }

    fun execute(context: Context, userId: String, callback: SyncCallback?) {
        val sp: SharedPreferences = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE)

        var ADVANCE_FAVORITE: String? = null
        var ADVANCE_LEARNED: String? = null
        var BEGINNER_FAVORITE: String? = null
        var BEGINNER_LEARNED: String? = null
        var INTERMEDIATE_FAVORITE: String? = null
        var INTERMEDIATE_LEARNED: String? = null
        var GRE_FAVORITE: String? = null
        var GRE_LEARNED: String? = null

        var savedBeginnerFav: List<Int>? = null
        var savedAdvanceFav: List<Int>? = null
        var savedIntermediateFav: List<Int>? = null
        var savedGreFav: List<Int>? = null

        var savedIeltsLearned: List<Int>? = null
        var savedToeflLearned: List<Int>? = null
        var savedSatLearned: List<Int>? = null
        var savedGreLearned: List<Int>? = null

        fun parseData(strData: Array<String?>) {
            GRE_FAVORITE = strData[0]
            GRE_LEARNED = strData[1]
            BEGINNER_FAVORITE = strData[2]
            BEGINNER_LEARNED = strData[3]
            sp.edit().putString("userName", strData[4] ?: "").apply()
            ADVANCE_FAVORITE = strData[5]
            ADVANCE_LEARNED = strData[6]
            INTERMEDIATE_FAVORITE = strData[7]
            INTERMEDIATE_LEARNED = strData[8]
        }

        fun builderToNums(numBuilder: StringBuilder?): List<Int> {
            val backToNums: MutableList<Int> = mutableListOf()
            val string = numBuilder?.toString() ?: ""
            var i = 0
            while (i < string.length) {
                backToNums.add(if (string.substring(i, i + 1).equals("1", true)) 1 else 0)
                i += 2
            }
            return backToNums
        }

        fun addingBuilderToNums() {
            savedAdvanceFav = builderToNums(StringBuilder(ADVANCE_FAVORITE ?: ""))
            savedIntermediateFav = builderToNums(StringBuilder(INTERMEDIATE_FAVORITE ?: ""))
            savedBeginnerFav = builderToNums(StringBuilder(BEGINNER_FAVORITE ?: ""))
            savedGreFav = builderToNums(StringBuilder(GRE_FAVORITE ?: ""))

            savedIeltsLearned = builderToNums(StringBuilder(BEGINNER_LEARNED ?: ""))
            savedToeflLearned = builderToNums(StringBuilder(INTERMEDIATE_LEARNED ?: ""))
            savedSatLearned = builderToNums(StringBuilder(ADVANCE_LEARNED ?: ""))
            savedGreLearned = builderToNums(StringBuilder(GRE_LEARNED ?: ""))
        }

        fun syncFavorites(list: List<Int>?, action: (String, String) -> Unit) {
            if (list != null && list.isNotEmpty()) {
                for (i in list.indices) {
                    action.invoke("${i + 1}", if (list[i] == 1) "True" else "False")
                }
            }
        }

        fun syncLearned(list: List<Int>?, action: (String, String) -> Unit) {
            if (list != null && list.isNotEmpty()) {
                for (i in list.indices) {
                    action.invoke("${i + 1}", if (list[i] == 1) "True" else "False")
                }
            }
        }

        fun syncSQL() {
            syncFavorites(savedBeginnerFav) { id, state -> updateFavoriteStateUseCase.execute("IELTS", id, state) }
            syncFavorites(savedIntermediateFav) { id, state -> updateFavoriteStateUseCase.execute("TOEFL", id, state) }
            syncFavorites(savedAdvanceFav) { id, state -> updateFavoriteStateUseCase.execute("SAT", id, state) }
            syncFavorites(savedGreFav) { id, state -> updateFavoriteStateUseCase.execute("GRE", id, state) }

            syncLearned(savedIeltsLearned) { id, state -> updateLearnStateUseCase.execute("IELTS", id, state) }
            syncLearned(savedToeflLearned) { id, state -> updateLearnStateUseCase.execute("TOEFL", id, state) }
            syncLearned(savedSatLearned) { id, state -> updateLearnStateUseCase.execute("SAT", id, state) }
            syncLearned(savedGreLearned) { id, state -> updateLearnStateUseCase.execute("GRE", id, state) }
        }

        fun resetAndSync(arrayResId: Int, spKey: String, newDataList: List<Int>, action: (String, String) -> Unit) {
            val size = sp.getInt(spKey, context.resources.getStringArray(arrayResId).size)
            for (i in 0 until size) {
                action.invoke("${i + 1}", "False")
            }
            for (k in newDataList.indices) {
                action.invoke("${newDataList[k] + 1}", "True")
            }
        }

        fun syncDatabasesIfFavDataChanged(newData: String, key: String) {
            val newDataList = builderToNums(StringBuilder(newData))
            if (newDataList.isNotEmpty()) {
                when {
                    key.equals("advanceFavCount", true) -> {
                        resetAndSync(R.array.SAT_words, "advance", newDataList) { id, state ->
                            updateFavoriteStateUseCase.execute("SAT", id, state)
                        }
                    }
                    key.equals("intermediateFavCount", true) -> {
                        resetAndSync(R.array.TOEFL_words, "intermediate", newDataList) { id, state ->
                            updateFavoriteStateUseCase.execute("TOEFL", id, state)
                        }
                    }
                    key.equals("beginnerFavCount", true) -> {
                        resetAndSync(R.array.TOEFL_words, "beginner", newDataList) { id, state ->
                            updateFavoriteStateUseCase.execute("IELTS", id, state)
                        }
                    }
                }
            }
        }

        fun updateSPIfHigher(key: String, firebaseValue: Int) {
            val localValue = sp.getInt(key, 0)
            if (firebaseValue > localValue) {
                sp.edit().putInt(key, firebaseValue).apply()
            }
        }

        addChildEventListenerUseCase.execute(userId, object : ChildEventListener {
            var i = 0
            val strData: Array<String?> = arrayOfNulls(9)
            var askOnce = false

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists() && i == 8 && !askOnce) {
                    askOnce = true
                    callback?.onCloudDataFound(Runnable {
                        addingBuilderToNums()
                        Handler(Looper.getMainLooper()).post {
                            syncSQL()
                            callback?.onSyncComplete()
                        }
                    })
                }

                val state = dataSnapshot.getValue(String::class.java)
                strData[i] = state
                if (strData[8] != null) {
                    parseData(strData)
                }
                i++
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val data = dataSnapshot.getValue(String::class.java) ?: return
                val key = dataSnapshot.key
                if (key != null) {
                    if (key.equals("advanceFavCount", true) || key.equals("intermediateFavCount", true)
                        || key.equals("beginnerFavCount", true)
                    ) {
                        syncDatabasesIfFavDataChanged(data, key)
                    }
                    if (key.equals("advanceLearnedCount", true) || key.equals("intermediateLearnedCount", true)
                        || key.equals("beginnerLearnedCount", true)
                    ) {
                        when {
                            key.equals("advanceLearnedCount", true) -> updateSPIfHigher("advance", data.toInt())
                            key.equals("intermediateLearnedCount", true) -> updateSPIfHigher("intermediate", data.toInt())
                            key.equals("beginnerLearnedCount", true) -> updateSPIfHigher("beginner", data.toInt())
                        }
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
}

