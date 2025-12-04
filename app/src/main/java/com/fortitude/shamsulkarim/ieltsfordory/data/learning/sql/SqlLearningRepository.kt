package com.fortitude.shamsulkarim.ieltsfordory.data.learning.sql

import android.content.Context
import android.database.Cursor
import com.fortitude.shamsulkarim.ieltsfordory.data_old.FavLearnedState
import com.fortitude.shamsulkarim.ieltsfordory.data_old.databases.GREWordDatabase
import com.fortitude.shamsulkarim.ieltsfordory.data_old.databases.IELTSWordDatabase
import com.fortitude.shamsulkarim.ieltsfordory.data_old.databases.JustLearnedDatabaseAdvance
import com.fortitude.shamsulkarim.ieltsfordory.data_old.databases.JustLearnedDatabaseBeginner
import com.fortitude.shamsulkarim.ieltsfordory.data_old.databases.JustLearnedDatabaseIntermediate
import com.fortitude.shamsulkarim.ieltsfordory.data_old.databases.SATWordDatabase
import com.fortitude.shamsulkarim.ieltsfordory.data_old.databases.TOEFLWordDatabase
import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word
import com.fortitude.shamsulkarim.ieltsfordory.data_old.repository.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.JustLearnedSessionData

class SqlLearningRepository(private val context: Context) : LearningRepository {
    private val ielts = IELTSWordDatabase(context)
    private val toefl = TOEFLWordDatabase(context)
    private val sat = SATWordDatabase(context)
    private val gre = GREWordDatabase(context)
    private val vocab = VocabularyRepository(context)
    private val jlBeginner = JustLearnedDatabaseBeginner(context)
    private val jlIntermediate = JustLearnedDatabaseIntermediate(context)
    private val jlAdvance = JustLearnedDatabaseAdvance(context)

    override fun getFavLearnedState(userName: String): FavLearnedState {
        val iFav = StringBuilder()
        val iLearned = StringBuilder()
        val tFav = StringBuilder()
        val tLearned = StringBuilder()
        val sFav = StringBuilder()
        val sLearned = StringBuilder()
        val gFav = StringBuilder()
        val gLearned = StringBuilder()

        process(ielts, iFav, iLearned)
        process(toefl, tFav, tLearned)
        process(sat, sFav, sLearned)
        process(gre, gFav, gLearned)

        return FavLearnedState(
            userName,
            iLearned.toString(),
            tLearned.toString(),
            sLearned.toString(),
            gLearned.toString(),
            iFav.toString(),
            tFav.toString(),
            sFav.toString(),
            gFav.toString()
        )
    }

    private fun process(db: Any, fav: StringBuilder, learned: StringBuilder) {
        var cursor: Cursor? = null
        try {
            cursor = when (db) {
                is IELTSWordDatabase -> db.data
                is TOEFLWordDatabase -> db.data
                is SATWordDatabase -> db.data
                is GREWordDatabase -> db.data
                else -> null
            }
            if (cursor == null) return
            while (cursor.moveToNext()) {
                val favValue = cursor.getString(2)
                fav.append(if ("true".equals(favValue, true)) "1+" else "0+")
                val learnedValue = cursor.getString(3)
                learned.append(if ("true".equals(learnedValue, true)) "1+" else "0+")
            }
        } finally {
            cursor?.close()
            when (db) {
                is IELTSWordDatabase -> db.close()
                is TOEFLWordDatabase -> db.close()
                is SATWordDatabase -> db.close()
                is GREWordDatabase -> db.close()
            }
        }
    }

    override fun fetchSessionWords(level: String, wordsPerSession: Int): List<Word> {
        val words = when {
            level.equals("beginner", true) -> vocab.beginnerUnlearnedWords
            level.equals("intermediate", true) -> vocab.intermediateUnlearnedWords
            else -> vocab.advanceUnlearnedWords
        }
        val limit = wordsPerSession.coerceAtMost(words.size)
        return words.take(limit)
    }

    override fun getAllUnlearnedWords(level: String): List<Word> {
        return when {
            level.equals("beginner", true) -> vocab.beginnerUnlearnedWords
            level.equals("intermediate", true) -> vocab.intermediateUnlearnedWords
            else -> vocab.advanceUnlearnedWords
        }
    }

    override fun updateLearnedStatus(words: List<Word>) {
        words.forEach { w ->
            when {
                w.vocabularyType.equals("IELTS", true) -> vocab.updateIELTSLearnState((w.position).toString(), "true")
                w.vocabularyType.equals("TOEFL", true) -> vocab.updateTOEFLLearnState((w.position).toString(), "true")
                w.vocabularyType.equals("SAT", true) -> vocab.updateSATLearnState((w.position).toString(), "true")
                w.vocabularyType.equals("GRE", true) -> vocab.updateGRELearnState((w.position).toString(), "true")
            }
        }
    }

    override fun updateJustLearnedStatus(level: String, words: List<Word>, mostMistakenIndex: Int) {
        jlBeginner.removeAll()
        jlIntermediate.removeAll()
        jlAdvance.removeAll()
        var j = 1
        when {
            level.equals("beginner", true) -> {
                words.forEachIndexed { i, w ->
                    insert(jlBeginner, j, w, i == mostMistakenIndex)
                    j++
                }
            }
            level.equals("intermediate", true) -> {
                words.forEachIndexed { i, w ->
                    insert(jlIntermediate, j, w, i == mostMistakenIndex)
                    j++
                }
            }
            else -> {
                words.forEachIndexed { i, w ->
                    insert(jlAdvance, j, w, i == mostMistakenIndex)
                    j++
                }
            }
        }
    }

    private fun insert(db: Any, j: Int, w: Word, mistaken: Boolean) {
        val isFav = w.isFavorite()
        val mistakenStr = if (mistaken) "true" else "false"
        when (db) {
            is JustLearnedDatabaseBeginner -> db.insertData(j, "${w.position}", w.word, w.translation, w.extra, w.pronun, w.grammar, w.example1, w.example2, w.example3, w.vocabularyType, "true", isFav, mistakenStr)
            is JustLearnedDatabaseIntermediate -> db.insertData(j, "${w.position}", w.word, w.translation, w.extra, w.pronun, w.grammar, w.example1, w.example2, w.example3, w.vocabularyType, "true", isFav, mistakenStr)
            is JustLearnedDatabaseAdvance -> db.insertData(j, "${w.position}", w.word, w.translation, w.extra, w.pronun, w.grammar, w.example1, w.example2, w.example3, w.vocabularyType, "true", isFav, mistakenStr)
        }
    }

    override fun updateFavoriteStatus(word: Word, newStatus: String) {
        when {
            word.vocabularyType.equals("IELTS", true) -> ielts.updateFav("${word.position}", newStatus)
            word.vocabularyType.equals("TOEFL", true) -> toefl.updateFav("${word.position}", newStatus)
            word.vocabularyType.equals("SAT", true) -> sat.updateFav("${word.position}", newStatus)
            word.vocabularyType.equals("GRE", true) -> gre.updateFav("${word.position}", newStatus)
        }
    }

    override fun updateLearnedStatus(word: Word, newStatus: String) {
        when {
            word.vocabularyType.equals("IELTS", true) -> ielts.updateLearned("${word.position}", newStatus)
            word.vocabularyType.equals("TOEFL", true) -> toefl.updateLearned("${word.position}", newStatus)
            word.vocabularyType.equals("SAT", true) -> sat.updateLearned("${word.position}", newStatus)
            word.vocabularyType.equals("GRE", true) -> gre.updateLearned("${word.position}", newStatus)
        }
    }

    override fun getJustLearnedSessionData(level: String): JustLearnedSessionData {
        val learned = mutableListOf<Word>()
        var mistaken: Word? = null
        var cursor: Cursor? = null
        try {
            cursor = when {
                level.equals("beginner", true) -> jlBeginner.data
                level.equals("intermediate", true) -> jlIntermediate.data
                else -> jlAdvance.data
            }
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val isLearnedVal = if (level.equals("intermediate", true)) "True" else ""
                    val w = Word(
                        cursor.getString(2) + "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        cursor.getString(10) + "",
                        Integer.parseInt(cursor.getString(1)),
                        isLearnedVal,
                        cursor.getString(12)
                    )
                    if (cursor.getString(13).equals("false", true)) {
                        learned.add(w)
                    } else {
                        mistaken = w
                    }
                }
            }
        } finally {
            cursor?.close()
        }
        return JustLearnedSessionData(learned, mistaken)
    }
}

