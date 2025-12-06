package com.fortitude.shamsulkarim.ieltsfordory.data.vocabulary.room

import android.content.Context
import android.content.SharedPreferences
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.dao.WordProgressDao
import com.fortitude.shamsulkarim.ieltsfordory.data.database.room.entity.WordProgressEntity
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.VocabularyRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularySource
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import kotlinx.coroutines.runBlocking

/**
 * Room-backed implementation of VocabularyRepository.
 * 
 * Word content is loaded from XML string arrays (R.array.*).
 * User progress (learned, favorite) is loaded from Room database.
 */
class RoomVocabularyRepository(
    private val wordProgressDao: WordProgressDao,
    private val context: Context
) : VocabularyRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE
    )

    // Word content arrays for each source (loaded from resources)
    private val wordArrays = mutableMapOf<String, WordArrays>()

    init {
        // Initialize word arrays from resources
        loadWordArrays()
    }

    override fun getVocabulary(level: String): List<VocabularyWord> = runBlocking {
        val words = mutableListOf<VocabularyWord>()
        
        SOURCES.forEach { source ->
            val arrays = wordArrays[source] ?: return@forEach
            val (startIndex, endIndex) = getLevelRange(source, level)
            val progressMap = getProgressMap(source)
            
            for (i in startIndex until endIndex) {
                val progress = progressMap[i]
                words.add(createVocabularyWord(source, i, level, arrays, progress))
            }
        }
        
        words
    }

    override fun getFavoriteWords(): List<VocabularyWord> = runBlocking {
        val words = mutableListOf<VocabularyWord>()
        
        SOURCES.forEach { source ->
            val arrays = wordArrays[source] ?: return@forEach
            val favorites = wordProgressDao.getFavorites(source)
            
            favorites.forEach { progress ->
                if (progress.wordId < arrays.words.size) {
                    val level = determineLevelForIndex(source, progress.wordId)
                    words.add(createVocabularyWord(source, progress.wordId, level, arrays, progress))
                }
            }
        }
        
        words
    }

    override fun getLearnedWords(level: String): List<VocabularyWord> = runBlocking {
        val words = mutableListOf<VocabularyWord>()
        
        SOURCES.forEach { source ->
            val arrays = wordArrays[source] ?: return@forEach
            val (startIndex, endIndex) = getLevelRange(source, level)
            val learned = wordProgressDao.getLearned(source)
            val learnedSet = learned.map { it.wordId }.toSet()
            
            for (i in startIndex until endIndex) {
                if (i in learnedSet) {
                    val progress = learned.find { it.wordId == i }
                    words.add(createVocabularyWord(source, i, level, arrays, progress))
                }
            }
        }
        
        words
    }

    override fun getUnlearnedWords(level: String): List<VocabularyWord> = runBlocking {
        val words = mutableListOf<VocabularyWord>()
        
        SOURCES.forEach { source ->
            val arrays = wordArrays[source] ?: return@forEach
            val (startIndex, endIndex) = getLevelRange(source, level)
            val progressMap = getProgressMap(source)
            
            for (i in startIndex until endIndex) {
                val progress = progressMap[i]
                if (progress == null || !progress.isLearned) {
                    words.add(createVocabularyWord(source, i, level, arrays, progress))
                }
            }
        }
        
        words
    }

    override fun getLearnedCount(level: String): Int = runBlocking {
        var count = 0
        
        SOURCES.forEach { source ->
            val arrays = wordArrays[source] ?: return@forEach
            val (startIndex, endIndex) = getLevelRange(source, level)
            val learned = wordProgressDao.getLearned(source)
            
            count += learned.count { it.wordId in startIndex until endIndex }
        }
        
        count
    }

    override fun getTotalCount(level: String): Int {
        var count = 0
        
        SOURCES.forEach { source ->
            val arrays = wordArrays[source] ?: return@forEach
            val (startIndex, endIndex) = getLevelRange(source, level)
            count += (endIndex - startIndex)
        }
        
        return count
    }

    override fun updateFavorite(source: VocabularySource, wordId: Int, isFavorite: Boolean) = runBlocking {
        val sourceStr = source.name
        ensureProgressExists(sourceStr, wordId)
        wordProgressDao.updateFavorite(sourceStr, wordId, isFavorite)
    }

    override fun updateLearnState(source: VocabularySource, wordId: Int, isLearned: Boolean) = runBlocking {
        val sourceStr = source.name
        ensureProgressExists(sourceStr, wordId)
        wordProgressDao.updateLearned(sourceStr, wordId, isLearned)
    }

    // ========== Private Helper Methods ==========

    private fun loadWordArrays() {
        wordArrays["IELTS"] = WordArrays(
            words = context.resources.getStringArray(R.array.IELTS_words),
            translations = context.resources.getStringArray(R.array.IELTS_translation),
            grammar = context.resources.getStringArray(R.array.IELTS_grammar),
            pronunciation = context.resources.getStringArray(R.array.IELTS_pronunciation),
            example1 = context.resources.getStringArray(R.array.IELTS_example1),
            example2 = context.resources.getStringArray(R.array.IELTS_example2),
            example3 = context.resources.getStringArray(R.array.IELTS_example3),
            level = context.resources.getStringArray(R.array.IELTS_level),
            position = context.resources.getIntArray(R.array.IELTS_position),
            wordsSL = context.resources.getStringArray(R.array.IELTS_words_sp),
            translationsSL = context.resources.getStringArray(R.array.IELTS_translation_sp),
            example1SL = context.resources.getStringArray(R.array.IELTS_example1_sp),
            example2SL = context.resources.getStringArray(R.array.IELTS_example2_sp),
            example3SL = context.resources.getStringArray(R.array.IELTS_example3_sp),
            isActive = prefs.getBoolean("isIELTSActive", true)
        )

        wordArrays["TOEFL"] = WordArrays(
            words = context.resources.getStringArray(R.array.TOEFL_words),
            translations = context.resources.getStringArray(R.array.TOEFL_translation),
            grammar = context.resources.getStringArray(R.array.TOEFL_grammar),
            pronunciation = context.resources.getStringArray(R.array.TOEFL_pronunciation),
            example1 = context.resources.getStringArray(R.array.TOEFL_example1),
            example2 = context.resources.getStringArray(R.array.TOEFL_example2),
            example3 = context.resources.getStringArray(R.array.TOEFL_example3),
            level = context.resources.getStringArray(R.array.TOEFL_level),
            position = context.resources.getIntArray(R.array.TOEFL_position),
            wordsSL = context.resources.getStringArray(R.array.TOEFL_words_sp),
            translationsSL = context.resources.getStringArray(R.array.TOEFL_translation_sp),
            example1SL = context.resources.getStringArray(R.array.TOEFL_example1_sp),
            example2SL = context.resources.getStringArray(R.array.TOEFL_example2_sp),
            example3SL = context.resources.getStringArray(R.array.TOEFL_example3_sp),
            isActive = prefs.getBoolean("isTOEFLActive", true)
        )

        wordArrays["SAT"] = WordArrays(
            words = context.resources.getStringArray(R.array.SAT_words),
            translations = context.resources.getStringArray(R.array.SAT_translation),
            grammar = context.resources.getStringArray(R.array.SAT_grammar),
            pronunciation = context.resources.getStringArray(R.array.SAT_pronunciation),
            example1 = context.resources.getStringArray(R.array.SAT_example1),
            example2 = context.resources.getStringArray(R.array.SAT_example2),
            example3 = context.resources.getStringArray(R.array.SAT_example3),
            level = context.resources.getStringArray(R.array.SAT_level),
            position = context.resources.getIntArray(R.array.SAT_position),
            wordsSL = context.resources.getStringArray(R.array.SAT_words_sp),
            translationsSL = context.resources.getStringArray(R.array.SAT_translation_sp),
            example1SL = context.resources.getStringArray(R.array.SAT_example1_sp),
            example2SL = context.resources.getStringArray(R.array.SAT_example2_sp),
            example3SL = context.resources.getStringArray(R.array.SAT_example3_sp),
            isActive = prefs.getBoolean("isSATActive", true)
        )

        wordArrays["GRE"] = WordArrays(
            words = context.resources.getStringArray(R.array.GRE_words),
            translations = context.resources.getStringArray(R.array.GRE_translation),
            grammar = context.resources.getStringArray(R.array.GRE_grammar),
            pronunciation = context.resources.getStringArray(R.array.GRE_pronunciation),
            example1 = context.resources.getStringArray(R.array.GRE_example1),
            example2 = context.resources.getStringArray(R.array.GRE_example2),
            example3 = context.resources.getStringArray(R.array.GRE_example3),
            level = context.resources.getStringArray(R.array.GRE_level),
            position = context.resources.getIntArray(R.array.GRE_position),
            wordsSL = context.resources.getStringArray(R.array.GRE_words_sp),
            translationsSL = context.resources.getStringArray(R.array.GRE_translation_sp),
            example1SL = context.resources.getStringArray(R.array.GRE_example1_sp),
            example2SL = context.resources.getStringArray(R.array.GRE_example2_sp),
            example3SL = context.resources.getStringArray(R.array.GRE_example3_sp),
            isActive = prefs.getBoolean("isGREActive", true)
        )
    }

    private suspend fun getProgressMap(source: String): Map<Int, WordProgressEntity> {
        return wordProgressDao.getBySource(source).associateBy { it.wordId }
    }

    private fun getLevelRange(source: String, level: String): Pair<Int, Int> {
        val arrays = wordArrays[source] ?: return Pair(0, 0)
        if (!arrays.isActive) return Pair(0, 0)

        val totalSize = arrays.words.size
        val beginnerEnd = getPercentage(30, totalSize)
        val intermediateEnd = beginnerEnd + getPercentage(40, totalSize)

        return when {
            level.equals("beginner", true) -> Pair(0, beginnerEnd)
            level.equals("intermediate", true) -> Pair(beginnerEnd, intermediateEnd)
            else -> Pair(intermediateEnd, totalSize) // advanced
        }
    }

    private fun determineLevelForIndex(source: String, index: Int): String {
        val arrays = wordArrays[source] ?: return "beginner"
        val totalSize = arrays.words.size
        val beginnerEnd = getPercentage(30, totalSize)
        val intermediateEnd = beginnerEnd + getPercentage(40, totalSize)

        return when {
            index < beginnerEnd -> "beginner"
            index < intermediateEnd -> "intermediate"
            else -> "advanced"
        }
    }

    private fun getPercentage(percentage: Int, total: Int): Int {
        return (percentage / 100.0 * total).toInt()
    }

    private fun createVocabularyWord(
        source: String,
        index: Int,
        level: String,
        arrays: WordArrays,
        progress: WordProgressEntity?
    ): VocabularyWord {
        val secondLanguage = prefs.getString("secondlanguage", "english") ?: "english"
        val useSecondLanguage = !secondLanguage.equals("english", ignoreCase = true)

        return VocabularyWord(
            id = arrays.position.getOrNull(index) ?: index,
            word = arrays.words.getOrNull(index) ?: "",
            translation = arrays.translations.getOrNull(index) ?: "",
            pronunciation = arrays.pronunciation.getOrNull(index),
            grammar = arrays.grammar.getOrNull(index),
            example1 = arrays.example1.getOrNull(index),
            example2 = arrays.example2.getOrNull(index),
            example3 = arrays.example3.getOrNull(index),
            source = VocabularySource.valueOf(source),
            level = level,
            isFavorite = progress?.isFavorite == true,
            isLearned = progress?.isLearned == true,
            wordSecondLang = if (useSecondLanguage) arrays.wordsSL.getOrNull(index) else null,
            translationSecondLang = if (useSecondLanguage) arrays.translationsSL.getOrNull(index) else null,
            example1SecondLang = if (useSecondLanguage) arrays.example1SL.getOrNull(index) else null,
            example2SecondLang = if (useSecondLanguage) arrays.example2SL.getOrNull(index) else null,
            example3SecondLang = if (useSecondLanguage) arrays.example3SL.getOrNull(index) else null
        )
    }

    private suspend fun ensureProgressExists(source: String, wordId: Int) {
        val existing = wordProgressDao.getBySourceAndWordId(source, wordId)
        if (existing == null) {
            wordProgressDao.insertAll(listOf(WordProgressEntity.createDefault(wordId, source)))
        }
    }

    companion object {
        private val SOURCES = listOf("IELTS", "TOEFL", "SAT", "GRE")
    }
}

/**
 * Container for word content arrays loaded from resources.
 */
private data class WordArrays(
    val words: Array<String>,
    val translations: Array<String>,
    val grammar: Array<String>,
    val pronunciation: Array<String>,
    val example1: Array<String>,
    val example2: Array<String>,
    val example3: Array<String>,
    val level: Array<String>,
    val position: IntArray,
    val wordsSL: Array<String>,
    val translationsSL: Array<String>,
    val example1SL: Array<String>,
    val example2SL: Array<String>,
    val example3SL: Array<String>,
    val isActive: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WordArrays
        return words.contentEquals(other.words) && isActive == other.isActive
    }

    override fun hashCode(): Int {
        return 31 * words.contentHashCode() + isActive.hashCode()
    }
}

