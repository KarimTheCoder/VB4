package com.fortitude.shamsulkarim.ieltsfordory.data.learning.room

import android.content.Context
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.SessionWordDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.WordProgressDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.SessionWordEntity
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity
import com.fortitude.shamsulkarim.ieltsfordory.data_old.FavLearnedState
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.LearningRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.model.JustLearnedSessionData
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.runBlocking

/**
 * Room-backed implementation of LearningRepository.
 * 
 * Uses Room database for:
 * - Word progress (learned/favorite status)
 * - Session words (just learned words during training)
 */
class RoomLearningRepository(
    private val wordProgressDao: WordProgressDao,
    private val sessionWordDao: SessionWordDao,
    private val vocabularyRepository: VocabularyRepository,
    private val context: Context
) : LearningRepository {

    companion object {
        private val SOURCES = listOf("IELTS", "TOEFL", "SAT", "GRE")
    }

    override fun getFavLearnedState(userName: String): FavLearnedState = runBlocking {
        val iFav = buildStateString("IELTS", true)
        val iLearned = buildStateString("IELTS", false)
        val tFav = buildStateString("TOEFL", true)
        val tLearned = buildStateString("TOEFL", false)
        val sFav = buildStateString("SAT", true)
        val sLearned = buildStateString("SAT", false)
        val gFav = buildStateString("GRE", true)
        val gLearned = buildStateString("GRE", false)

        FavLearnedState(
            userName,
            iLearned,
            tLearned,
            sLearned,
            gLearned,
            iFav,
            tFav,
            sFav,
            gFav
        )
    }

    private suspend fun buildStateString(source: String, isFavorite: Boolean): String {
        val allProgress = wordProgressDao.getBySource(source)
        val progressMap = allProgress.associateBy { it.wordId }
        
        val maxWordId = allProgress.maxOfOrNull { it.wordId } ?: 0
        val sb = StringBuilder()
        
        for (i in 0..maxWordId) {
            val progress = progressMap[i]
            val value = if (isFavorite) {
                progress?.isFavorite == true
            } else {
                progress?.isLearned == true
            }
            sb.append(if (value) "1+" else "0+")
        }
        
        return sb.toString()
    }

    override fun fetchSessionWords(level: String, wordsPerSession: Int): List<VocabularyWord> {
        val words = getAllUnlearnedWords(level)
        val limit = wordsPerSession.coerceAtMost(words.size)
        return words.take(limit)
    }

    override fun getAllUnlearnedWords(level: String): List<VocabularyWord> {
        return vocabularyRepository.getUnlearnedWords(level)
    }



    override fun updateLearnedStatus(words: List<VocabularyWord>) = runBlocking {
        words.forEach { word ->
            val source = word.source.name.uppercase()
            val wordId = word.id
            
            ensureProgressExists(source, wordId)
            wordProgressDao.updateLearned(source, wordId, true)
        }
    }

    override fun updateJustLearnedStatus(level: String, words: List<VocabularyWord>, mostMistakenIndex: Int) = runBlocking {
        // Clear existing session words for all levels
        sessionWordDao.clearAll()

        val levelString = normalizeLevel(level)
        
        // Insert new session words
        val entities = words.mapIndexed { index, word ->
            SessionWordEntity(
                id = index + 1,
                wordDatabasePos = word.id.toString(),
                word = word.word,
                translation = word.translation,
                secondTranslation = word.translationSecondLang,
                pronunciation = word.pronunciation,
                grammar = word.grammar,
                example1 = word.example1,
                example2 = word.example2,
                example3 = word.example3,
                vocabularyType = word.source.name,
                level = levelString,
                isLearned = true,
                isFavorite = word.isFavorite,
                isMostMistaken = index == mostMistakenIndex
            )
        }
        
        sessionWordDao.insertAll(entities)
    }

    override fun updateFavoriteStatus(word: VocabularyWord, newStatus: Boolean) = runBlocking {
        val source = word.source.name.uppercase()
        val wordId = word.id
        
        ensureProgressExists(source, wordId)
        wordProgressDao.updateFavorite(source, wordId, newStatus)
    }

    override fun updateLearnedStatus(word: VocabularyWord, newStatus: Boolean) = runBlocking {
        val source = word.source.name.uppercase()
        val wordId = word.id
        
        ensureProgressExists(source, wordId)
        wordProgressDao.updateLearned(source, wordId, newStatus)
    }

    override fun getJustLearnedSessionData(level: String): JustLearnedSessionData = runBlocking {
        val levelString = normalizeLevel(level)
        val sessionWords = sessionWordDao.getByLevel(levelString)
        
        val learnedWords = mutableListOf<VocabularyWord>()
        var mostMistakenWord: VocabularyWord? = null
        
        sessionWords.forEach { entity ->
            val source = try {
                VocabularySource.valueOf(entity.vocabularyType?.uppercase() ?: "IELTS")
            } catch (e: IllegalArgumentException) {
                VocabularySource.IELTS
            }
            
            val word = VocabularyWord(
                id = entity.wordDatabasePos.toIntOrNull() ?: 0,
                word = entity.word,
                translation = entity.translation,
                wordSecondLang = null,
                translationSecondLang = entity.secondTranslation,
                pronunciation = entity.pronunciation,
                grammar = entity.grammar,
                example1 = entity.example1,
                example2 = entity.example2,
                example3 = entity.example3,
                example1SecondLang = null,
                example2SecondLang = null,
                example3SecondLang = null,
                source = source,
                isLearned = entity.isLearned,
                isFavorite = entity.isFavorite
            )
            
            if (entity.isMostMistaken) {
                mostMistakenWord = word
            } else {
                learnedWords.add(word)
            }
        }
        
        JustLearnedSessionData(learnedWords, mostMistakenWord)
    }

    // ========== Private Helper Methods ==========

    private suspend fun ensureProgressExists(source: String, wordId: Int) {
        val existing = wordProgressDao.getBySourceAndWordId(source, wordId)
        if (existing == null) {
            wordProgressDao.insertAll(listOf(WordProgressEntity.createDefault(wordId, source)))
        }
    }

    private fun normalizeLevel(level: String): String {
        return when {
            level.equals("beginner", true) -> "BEGINNER"
            level.equals("intermediate", true) -> "INTERMEDIATE"
            else -> "ADVANCED"
        }
    }
}
